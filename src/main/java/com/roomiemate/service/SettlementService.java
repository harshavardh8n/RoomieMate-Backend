package com.roomiemate.service;

import com.roomiemate.dto.payment.PaymentResponse;
import com.roomiemate.dto.payment.RecordPaymentRequest;
import com.roomiemate.dto.payment.SimplifiedSettlementDto;
import com.roomiemate.dto.user.UserSummaryDto;
import com.roomiemate.entity.*;
import com.roomiemate.exception.BadRequestException;
import com.roomiemate.exception.ResourceNotFoundException;
import com.roomiemate.repository.ExpenseRepository;
import com.roomiemate.repository.PaymentRepository;
import com.roomiemate.repository.RoomRepository;
import com.roomiemate.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SettlementService {

    private final PaymentRepository paymentRepository;
    private final ExpenseRepository expenseRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public SettlementService(PaymentRepository paymentRepository,
                             ExpenseRepository expenseRepository,
                             RoomRepository roomRepository,
                             UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.expenseRepository = expenseRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PaymentResponse recordPayment(UUID roomId, User payer, RecordPaymentRequest request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        if (!room.getMembers().contains(payer)) {
            throw new AccessDeniedException("You are not a member of this room.");
        }

        if (payer.getId().equals(request.getRoommateId())) {
            throw new BadRequestException("You cannot record a payment to yourself.");
        }

        User receiver = userRepository.findById(request.getRoommateId())
                .orElseThrow(() -> new ResourceNotFoundException("Roommate not found with ID: " + request.getRoommateId()));

        if (!room.getMembers().contains(receiver)) {
            throw new BadRequestException("The recipient is not a member of this room.");
        }

        BigDecimal paymentAmount = request.getAmount().setScale(2, RoundingMode.HALF_UP);

        Payment payment = Payment.builder()
                .room(room)
                .payer(payer)
                .receiver(receiver)
                .amount(paymentAmount)
                .notes(request.getNotes() != null ? request.getNotes().trim() : null)
                .build();

        Payment saved = paymentRepository.save(payment);
        return PaymentResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getRoomPayments(UUID roomId, User user) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        if (!room.getMembers().contains(user)) {
            throw new AccessDeniedException("You are not a member of this room.");
        }

        return paymentRepository.findAllByRoomId(roomId)
                .stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Min-Cash-Flow Debt Simplification Algorithm.
     * Reduces the N*(N-1) bilateral debts in a room down to at most (N-1) optimal transactions.
     */
    @Transactional(readOnly = true)
    public List<SimplifiedSettlementDto> calculateSimplifiedDebts(UUID roomId, User user) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        if (!room.getMembers().contains(user)) {
            throw new AccessDeniedException("You are not a member of this room.");
        }

        List<Expense> expenses = expenseRepository.findAllWithSplitsByRoomId(roomId);
        List<Payment> payments = paymentRepository.findAllByRoomId(roomId);

        // Map memberId -> overall net balance across the room
        // net > 0: Creditor (they should receive money)
        // net < 0: Debtor (they should pay money)
        Map<UUID, BigDecimal> netMap = new HashMap<>();
        Map<UUID, User> userMap = new HashMap<>();

        for (User m : room.getMembers()) {
            netMap.put(m.getId(), BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
            userMap.put(m.getId(), m);
        }

        // Apply expenses
        for (Expense e : expenses) {
            UUID payerId = e.getPayer().getId();
            netMap.put(payerId, netMap.getOrDefault(payerId, BigDecimal.ZERO).add(e.getAmount()));

            for (ExpenseSplit s : e.getSplits()) {
                UUID splitterId = s.getUser().getId();
                netMap.put(splitterId, netMap.getOrDefault(splitterId, BigDecimal.ZERO).subtract(s.getAmount()));
            }
        }

        // Apply direct payments
        for (Payment p : payments) {
            UUID payerId = p.getPayer().getId();
            UUID receiverId = p.getReceiver().getId();

            netMap.put(payerId, netMap.getOrDefault(payerId, BigDecimal.ZERO).add(p.getAmount()));
            netMap.put(receiverId, netMap.getOrDefault(receiverId, BigDecimal.ZERO).subtract(p.getAmount()));
        }

        // Separate debtors and creditors
        List<BalanceEntry> creditors = new ArrayList<>();
        List<BalanceEntry> debtors = new ArrayList<>();

        for (Map.Entry<UUID, BigDecimal> entry : netMap.entrySet()) {
            BigDecimal val = entry.getValue().setScale(2, RoundingMode.HALF_UP);
            if (val.compareTo(BigDecimal.ZERO) > 0) {
                creditors.add(new BalanceEntry(entry.getKey(), val));
            } else if (val.compareTo(BigDecimal.ZERO) < 0) {
                debtors.add(new BalanceEntry(entry.getKey(), val.abs())); // store absolute debt
            }
        }

        // Greedy matching between largest creditor and largest debtor
        List<SimplifiedSettlementDto> simplifiedTransactions = new ArrayList<>();

        // Priority queues for maximum debtor and maximum creditor
        PriorityQueue<BalanceEntry> maxCreditors = new PriorityQueue<>((a, b) -> b.amount.compareTo(a.amount));
        PriorityQueue<BalanceEntry> maxDebtors = new PriorityQueue<>((a, b) -> b.amount.compareTo(a.amount));

        maxCreditors.addAll(creditors);
        maxDebtors.addAll(debtors);

        while (!maxCreditors.isEmpty() && !maxDebtors.isEmpty()) {
            BalanceEntry creditor = maxCreditors.poll();
            BalanceEntry debtor = maxDebtors.poll();

            BigDecimal settlementAmount = creditor.amount.min(debtor.amount);

            if (settlementAmount.compareTo(BigDecimal.valueOf(0.009)) > 0) {
                simplifiedTransactions.add(SimplifiedSettlementDto.builder()
                        .fromUser(UserSummaryDto.fromEntity(userMap.get(debtor.userId)))
                        .toUser(UserSummaryDto.fromEntity(userMap.get(creditor.userId)))
                        .amount(settlementAmount.setScale(2, RoundingMode.HALF_UP))
                        .build());
            }

            BigDecimal remainingCredit = creditor.amount.subtract(settlementAmount);
            BigDecimal remainingDebt = debtor.amount.subtract(settlementAmount);

            if (remainingCredit.compareTo(BigDecimal.valueOf(0.009)) > 0) {
                maxCreditors.add(new BalanceEntry(creditor.userId, remainingCredit));
            }

            if (remainingDebt.compareTo(BigDecimal.valueOf(0.009)) > 0) {
                maxDebtors.add(new BalanceEntry(debtor.userId, remainingDebt));
            }
        }

        return simplifiedTransactions;
    }

    private static class BalanceEntry {
        UUID userId;
        BigDecimal amount;

        BalanceEntry(UUID userId, BigDecimal amount) {
            this.userId = userId;
            this.amount = amount;
        }
    }
}

