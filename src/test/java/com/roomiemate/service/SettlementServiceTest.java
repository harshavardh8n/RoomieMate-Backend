package com.roomiemate.service;

import com.roomiemate.dto.payment.SimplifiedSettlementDto;
import com.roomiemate.entity.*;
import com.roomiemate.repository.ExpenseRepository;
import com.roomiemate.repository.PaymentRepository;
import com.roomiemate.repository.RoomRepository;
import com.roomiemate.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SettlementServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SettlementService settlementService;

    private User alice;
    private User bob;
    private User charlie;
    private Room room;

    @BeforeEach
    void setUp() {
        alice = User.builder().id(UUID.randomUUID()).name("Alice").email("alice@test.com").build();
        bob = User.builder().id(UUID.randomUUID()).name("Bob").email("bob@test.com").build();
        charlie = User.builder().id(UUID.randomUUID()).name("Charlie").email("charlie@test.com").build();

        room = Room.builder()
                .id(UUID.randomUUID())
                .name("Apartment 101")
                .admin(alice)
                .members(new HashSet<>(Arrays.asList(alice, bob, charlie)))
                .build();
    }

    @Test
    void testMinCashFlowAlgorithmSimplifiesTransitiveDebts() {
        // Scenario:
        // Alice pays $60 for groceries, split equally among Alice, Bob, Charlie ($20 each).
        // Bob owes Alice $20, Charlie owes Alice $20.
        // Later, Bob pays $30 for WiFi, split equally among Alice, Bob, Charlie ($10 each).
        // Alice owes Bob $10, Charlie owes Bob $10.
        //
        // Net positions:
        // Alice: paid $60, share owed $30 -> Net = +$30 (Creditor)
        // Bob: paid $30, share owed $30 -> Net = $0 (Settled)
        // Charlie: paid $0, share owed $30 -> Net = -$30 (Debtor)
        //
        // Without simplification:
        // Bob pays Alice $10 ($20 - $10). Charlie pays Alice $20. Charlie pays Bob $10. (3 transactions)
        // WITH Min-Cash-Flow Simplification:
        // Charlie pays Alice $30 directly! (1 transaction!) Bob does nothing!

        Expense expense1 = Expense.builder()
                .id(UUID.randomUUID())
                .room(room)
                .payer(alice)
                .amount(new BigDecimal("60.00"))
                .splits(Arrays.asList(
                        ExpenseSplit.builder().user(alice).amount(new BigDecimal("20.00")).build(),
                        ExpenseSplit.builder().user(bob).amount(new BigDecimal("20.00")).build(),
                        ExpenseSplit.builder().user(charlie).amount(new BigDecimal("20.00")).build()
                ))
                .build();

        Expense expense2 = Expense.builder()
                .id(UUID.randomUUID())
                .room(room)
                .payer(bob)
                .amount(new BigDecimal("30.00"))
                .splits(Arrays.asList(
                        ExpenseSplit.builder().user(alice).amount(new BigDecimal("10.00")).build(),
                        ExpenseSplit.builder().user(bob).amount(new BigDecimal("10.00")).build(),
                        ExpenseSplit.builder().user(charlie).amount(new BigDecimal("10.00")).build()
                ))
                .build();

        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        when(expenseRepository.findAllWithSplitsByRoomId(room.getId())).thenReturn(Arrays.asList(expense1, expense2));
        when(paymentRepository.findAllByRoomId(room.getId())).thenReturn(Collections.emptyList());

        List<SimplifiedSettlementDto> simplified = settlementService.calculateSimplifiedDebts(room.getId(), alice);

        assertNotNull(simplified);
        assertEquals(1, simplified.size(), "Should simplify 3 bilateral debts down to exactly 1 transaction");

        SimplifiedSettlementDto singleTx = simplified.get(0);
        assertEquals(charlie.getId(), singleTx.getFromUser().getId(), "Charlie should be the debtor");
        assertEquals(alice.getId(), singleTx.getToUser().getId(), "Alice should be the creditor");
        assertEquals(new BigDecimal("30.00"), singleTx.getAmount(), "Charlie should pay Alice $30.00");
    }
}

