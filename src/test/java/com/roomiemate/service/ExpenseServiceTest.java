package com.roomiemate.service;

import com.roomiemate.dto.expense.CreateExpenseRequest;
import com.roomiemate.dto.expense.ExpenseResponse;
import com.roomiemate.entity.Expense;
import com.roomiemate.entity.ExpenseCategory;
import com.roomiemate.entity.Room;
import com.roomiemate.entity.User;
import com.roomiemate.repository.ExpenseRepository;
import com.roomiemate.repository.PaymentRepository;
import com.roomiemate.repository.RoomRepository;
import com.roomiemate.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private User user1;
    private User user2;
    private User user3;
    private Room room;

    @BeforeEach
    void setUp() {
        user1 = User.builder().id(UUID.randomUUID()).name("User 1").email("u1@test.com").build();
        user2 = User.builder().id(UUID.randomUUID()).name("User 2").email("u2@test.com").build();
        user3 = User.builder().id(UUID.randomUUID()).name("User 3").email("u3@test.com").build();

        room = Room.builder()
                .id(UUID.randomUUID())
                .name("Test Room")
                .admin(user1)
                .members(new LinkedHashSet<>(Arrays.asList(user1, user2, user3)))
                .build();
    }

    @Test
    void testEqualSplitWithIndivisiblePennyAllocation() {
        // $100 divided among 3 users -> $33.34, $33.33, $33.33 -> Exactly $100.00!
        CreateExpenseRequest request = CreateExpenseRequest.builder()
                .description("Dinner")
                .amount(new BigDecimal("100.00"))
                .category(ExpenseCategory.FOOD)
                .build();

        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        when(expenseRepository.save(any(Expense.class))).thenAnswer(i -> {
            Expense e = i.getArgument(0);
            e.setId(UUID.randomUUID());
            return e;
        });

        ExpenseResponse response = expenseService.createExpense(room.getId(), user1, request);

        assertNotNull(response);
        assertEquals(3, response.getSplits().size());

        BigDecimal sumOfSplits = response.getSplits().stream()
                .map(s -> s.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(new BigDecimal("100.00"), sumOfSplits, "Sum of splits must exactly match $100.00 without loss of pennies");
    }
}

