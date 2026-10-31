package com.roomiemate.service;

import com.roomiemate.dto.expense.*;
import com.roomiemate.dto.user.UserSummaryDto;
import com.roomiemate.entity.*;
import com.roomiemate.exception.BadRequestException;
import com.roomiemate.exception.ResourceNotFoundException;
import com.roomiemate.repository.ExpenseRepository;
import com.roomiemate.repository.PaymentRepository;
import com.roomiemate.repository.RoomRepository;
import com.roomiemate.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final PaymentRepository paymentRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public ExpenseService(ExpenseRepository expenseRepository, PaymentRepository paymentRepository, RoomRepository roomRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.paymentRepository = paymentRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ExpenseResponse createExpense(UUID roomId, User payer, CreateExpenseRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        if (!room.getMembers().contains(payer)) {
            throw new AccessDeniedException("You are not a member of this room.");
        }

        BigDecimal totalAmount = request.getAmount().setScale(2, RoundingMode.HALF_UP);

        Expense expense = Expense.builder()
                .description(request.getDescription().trim())
                .amount(totalAmount)
                .category(request.getCategory() != null ? request.getCategory() : ExpenseCategory.OTHER)
                .room(room)
                .payer(payer)
                .splits(new ArrayList<>())
                .build();

        // Check whether custom splits or equal split is requested
        if (request.getCustomSplits() != null && !request.getCustomSplits().isEmpty()) {
            BigDecimal sum = BigDecimal.ZERO;
            for (CreateExpenseRequest.CustomSplitItem item : request.getCustomSplits()) {
                User splitUser = userRepository.findById(item.getUserId())
                        .orElseThrow(() -> new ResourceNotFoundException("Split user not found: " + item.getUserId()));

                if (!room.getMembers().contains(splitUser)) {
                    throw new BadRequestException("User " + splitUser.getName() + " is not a member of this room.");
                }

                BigDecimal itemAmount = item.getAmount().setScale(2, RoundingMode.HALF_UP);
                sum = sum.add(itemAmount);

                ExpenseSplit split = ExpenseSplit.builder()
                        .expense(expense)
                        .user(splitUser)
                        .amount(itemAmount)
                        .build();
                expense.getSplits().add(split);
            }

            if (sum.compareTo(totalAmount) != 0) {
                throw new BadRequestException("Sum of custom splits (" + sum + ") must exactly match total expense amount (" + totalAmount + ").");
            }
        } else {
            // Determine splitting members
            Set<User> splitters = new HashSet<>();
            if (request.getSplittersIds() != null && !request.getSplittersIds().isEmpty()) {
                for (UUID uid : request.getSplittersIds()) {
                    User u = userRepository.findById(uid)
                            .orElseThrow(() -> new ResourceNotFoundException("Split user not found: " + uid));
                    if (!room.getMembers().contains(u)) {
                        throw new BadRequestException("User " + u.getName() + " is not in this room.");
                    }
                    splitters.add(u);
                }
            } else {
                // Default: all room members share
                splitters.addAll(room.getMembers());
            }

            if (splitters.isEmpty()) {
                throw new BadRequestException("Cannot create an expense without splitters.");
            }

            // Distribute pennies accurately
            long totalCents = totalAmount.movePointRight(2).longValueExact();
            int count = splitters.size();
            long baseShareCents = totalCents / count;
            long remainderCents = totalCents % count;

            int index = 0;
            for (User member : splitters) {
                long centsForMember = baseShareCents + (index < remainderCents ? 1 : 0);
                BigDecimal memberShare = BigDecimal.valueOf(centsForMember).movePointLeft(2);

                ExpenseSplit split = ExpenseSplit.builder()
                        .expense(expense)
                        .user(member)
                        .amount(memberShare)
                        .build();
                expense.getSplits().add(split);
                index++;
            }
        }

        Expense savedExpense = expenseRepository.save(expense);
        return ExpenseResponse.fromEntity(savedExpense);
    }

    @Transactional(readOnly = true)
    public Page<ExpenseResponse> getRoomExpenses(UUID roomId, User user, Pageable pageable) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        if (!room.getMembers().contains(user)) {
            throw new AccessDeniedException("You are not a member of this room.");
        }

        return expenseRepository.findByRoomIdOrderByCreatedAtDesc(roomId, pageable)
                .map(ExpenseResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getAllRoomExpenses(UUID roomId, User user) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        if (!room.getMembers().contains(user)) {
            throw new AccessDeniedException("You are not a member of this room.");
        }

        return expenseRepository.findAllWithSplitsByRoomId(roomId)
                .stream()
                .map(ExpenseResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BalanceSummaryResponse calculateNetBalances(UUID roomId, User currentUser) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        if (!room.getMembers().contains(currentUser)) {
            throw new AccessDeniedException("You are not a member of this room.");
        }

        List<Expense> expenses = expenseRepository.findAllWithSplitsByRoomId(roomId);
        List<Payment> payments = paymentRepository.findAllByRoomId(roomId);

        // Map roommateId -> net balance relative to currentUser
        Map<UUID, BigDecimal> balanceMap = new HashMap<>();

        // Initialize all other room members with 0
        for (User member : room.getMembers()) {
            if (!member.getId().equals(currentUser.getId())) {
                balanceMap.put(member.getId(), BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            }
        }

        // Factor in expenses
        for (Expense expense : expenses) {
            UUID payerId = expense.getPayer().getId();

            if (payerId.equals(currentUser.getId())) {
                // Current user paid: any other roommate who was split owes currentUser
                for (ExpenseSplit split : expense.getSplits()) {
                    UUID splitterId = split.getUser().getId();
                    if (!splitterId.equals(currentUser.getId()) && balanceMap.containsKey(splitterId)) {
                        balanceMap.put(splitterId, balanceMap.get(splitterId).add(split.getAmount()));
                    }
                }
            } else if (balanceMap.containsKey(payerId)) {
                // Roommate payerId paid: check if currentUser was a splitter
                for (ExpenseSplit split : expense.getSplits()) {
                    if (split.getUser().getId().equals(currentUser.getId())) {
                        balanceMap.put(payerId, balanceMap.get(payerId).subtract(split.getAmount()));
                    }
                }
            }
        }

        // Factor in payments (settlements)
        for (Payment payment : payments) {
            UUID payerId = payment.getPayer().getId();
            UUID receiverId = payment.getReceiver().getId();

            if (payerId.equals(currentUser.getId()) && balanceMap.containsKey(receiverId)) {
                // Current user paid roommate: debt is reduced (balance increases towards positive)
                balanceMap.put(receiverId, balanceMap.get(receiverId).add(payment.getAmount()));
            } else if (receiverId.equals(currentUser.getId()) && balanceMap.containsKey(payerId)) {
                // Roommate paid current user: credit is settled (balance decreases)
                balanceMap.put(payerId, balanceMap.get(payerId).subtract(payment.getAmount()));
            }
        }

        List<RoommateBalanceDto> roommateBalances = new ArrayList<>();
        BigDecimal totalNet = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

        for (User member : room.getMembers()) {
            if (!member.getId().equals(currentUser.getId())) {
                BigDecimal bal = balanceMap.getOrDefault(member.getId(), BigDecimal.ZERO);
                totalNet = totalNet.add(bal);

                roommateBalances.add(RoommateBalanceDto.builder()
                        .userId(member.getId())
                        .name(member.getName())
                        .email(member.getEmail())
                        .netBalance(bal)
                        .build());
            }
        }

        return BalanceSummaryResponse.builder()
                .roomId(roomId)
                .currentUserId(currentUser.getId())
                .userTotalNetBalance(totalNet)
                .roommateBalances(roommateBalances)
                .build();
    }

    @Transactional(readOnly = true)
    public RoomAnalyticsResponse getRoomAnalytics(UUID roomId, User user) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        if (!room.getMembers().contains(user)) {
            throw new AccessDeniedException("You are not a member of this room.");
        }

        List<Expense> expenses = expenseRepository.findAllWithSplitsByRoomId(roomId);

        BigDecimal totalExpenses = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        Map<ExpenseCategory, BigDecimal> categoryMap = new EnumMap<>(ExpenseCategory.class);
        Map<UUID, BigDecimal> memberPaidMap = new HashMap<>();
        Map<UUID, BigDecimal> memberShareMap = new HashMap<>();

        for (User m : room.getMembers()) {
            memberPaidMap.put(m.getId(), BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            memberShareMap.put(m.getId(), BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        }

        for (Expense e : expenses) {
            totalExpenses = totalExpenses.add(e.getAmount());
            categoryMap.put(e.getCategory(), categoryMap.getOrDefault(e.getCategory(), BigDecimal.ZERO).add(e.getAmount()));

            UUID payerId = e.getPayer().getId();
            memberPaidMap.put(payerId, memberPaidMap.getOrDefault(payerId, BigDecimal.ZERO).add(e.getAmount()));

            for (ExpenseSplit s : e.getSplits()) {
                UUID splitUserId = s.getUser().getId();
                memberShareMap.put(splitUserId, memberShareMap.getOrDefault(splitUserId, BigDecimal.ZERO).add(s.getAmount()));
            }
        }

        List<RoomAnalyticsResponse.MemberSpendingDto> memberList = room.getMembers().stream()
                .map(m -> {
                    BigDecimal paid = memberPaidMap.getOrDefault(m.getId(), BigDecimal.ZERO);
                    BigDecimal owed = memberShareMap.getOrDefault(m.getId(), BigDecimal.ZERO);
                    BigDecimal net = paid.subtract(owed);
                    return RoomAnalyticsResponse.MemberSpendingDto.builder()
                            .user(UserSummaryDto.fromEntity(m))
                            .totalPaid(paid)
                            .totalShareOwed(owed)
                            .netPosition(net)
                            .build();
                })
                .collect(Collectors.toList());

        return RoomAnalyticsResponse.builder()
                .roomId(roomId)
                .totalExpenses(totalExpenses)
                .totalExpenseCount(expenses.size())
                .spendingByCategory(categoryMap)
                .memberSpending(memberList)
                .build();
    }
}

