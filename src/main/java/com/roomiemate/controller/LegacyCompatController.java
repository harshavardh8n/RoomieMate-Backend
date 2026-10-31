package com.roomiemate.controller;

import com.roomiemate.dto.auth.AuthResponse;
import com.roomiemate.dto.auth.LoginRequest;
import com.roomiemate.dto.auth.RegisterRequest;
import com.roomiemate.dto.expense.BalanceSummaryResponse;
import com.roomiemate.dto.expense.CreateExpenseRequest;
import com.roomiemate.dto.expense.ExpenseResponse;
import com.roomiemate.dto.payment.PaymentResponse;
import com.roomiemate.dto.payment.RecordPaymentRequest;
import com.roomiemate.dto.room.CreateRoomRequest;
import com.roomiemate.dto.room.InvitationResponse;
import com.roomiemate.dto.room.RoomResponse;
import com.roomiemate.dto.user.UserSummaryDto;
import com.roomiemate.entity.ExpenseCategory;
import com.roomiemate.entity.User;
import com.roomiemate.exception.BadRequestException;
import com.roomiemate.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * Compatibility Controller providing 1-to-1 backward compatibility
 * for the legacy React frontend without requiring immediate frontend path changes.
 */
@RestController
@Tag(name = "Legacy React Compatibility", description = "Drop-in compatible endpoints for the existing React.js frontend")
public class LegacyCompatController {

    private final AuthService authService;
    private final UserService userService;
    private final RoomService roomService;
    private final InvitationService invitationService;
    private final ExpenseService expenseService;
    private final SettlementService settlementService;

    public LegacyCompatController(AuthService authService, UserService userService, RoomService roomService, InvitationService invitationService, ExpenseService expenseService, SettlementService settlementService) {
        this.authService = authService;
        this.userService = userService;
        this.roomService = roomService;
        this.invitationService = invitationService;
        this.expenseService = expenseService;
        this.settlementService = settlementService;
    }

    // 1. POST /user/login
    @PostMapping("/user/login")
    @Operation(summary = "Legacy login endpoint compatible with existing React frontend")
    public ResponseEntity<Map<String, Object>> legacyLogin(@RequestBody LoginRequest request) {
        AuthResponse auth = authService.login(request);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("token", auth.getToken());
        resp.put("user", auth.getUser());
        resp.put("roomId", auth.getRoomId());
        return ResponseEntity.ok(resp);
    }

    // 2. GET /user/search?name=...
    @GetMapping("/user/search")
    @Operation(summary = "Legacy search endpoint compatible with existing React frontend")
    public ResponseEntity<Map<String, Object>> legacySearch(@RequestParam(value = "name", defaultValue = "") String name) {
        List<UserSummaryDto> users = userService.searchUsers(name);
        Map<String, Object> resp = new HashMap<>();
        resp.put("users", users);
        return ResponseEntity.ok(resp);
    }

    // 3. POST /api/users (register)
    @PostMapping("/api/users")
    @Operation(summary = "Legacy registration endpoint compatible with existing React frontend")
    public ResponseEntity<Map<String, Object>> legacyRegister(@RequestBody RegisterRequest request) {
        AuthResponse auth = authService.register(request);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "User created successfully!");
        resp.put("user", auth.getUser());
        resp.put("token", auth.getToken());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    // 4. GET /api/idlePeople
    @GetMapping("/api/idlePeople")
    @Operation(summary = "Legacy idle people endpoint compatible with existing React frontend")
    public ResponseEntity<Map<String, Object>> legacyIdlePeople(@AuthenticationPrincipal User currentUser) {
        List<UserSummaryDto> idle = userService.getIdleUsers(currentUser.getId());
        Map<String, Object> resp = new HashMap<>();
        resp.put("idlePeople", idle);
        return ResponseEntity.ok(resp);
    }

    // 5. GET /api/room
    @GetMapping("/api/room")
    @Operation(summary = "Legacy get current user room endpoint compatible with existing React frontend")
    public ResponseEntity<Map<String, Object>> legacyGetRoom(@AuthenticationPrincipal User currentUser) {
        List<RoomResponse> rooms = roomService.getUserRooms(currentUser);
        if (rooms.isEmpty()) {
            throw new BadRequestException("User does not belong to any room.");
        }
        RoomResponse primaryRoom = rooms.get(0);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("roomId", primaryRoom.getId());
        resp.put("members", primaryRoom.getMembers());
        return ResponseEntity.ok(resp);
    }

    // 6. POST /api/rooms
    @PostMapping("/api/rooms")
    @Operation(summary = "Legacy create room endpoint compatible with existing React frontend")
    public ResponseEntity<Map<String, Object>> legacyCreateRoom(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal User currentUser
    ) {
        String name = (String) body.getOrDefault("name", "My Room");
        List<?> rawMembers = (List<?>) body.get("members");
        List<UUID> memberIds = new ArrayList<>();
        if (rawMembers != null) {
            for (Object obj : rawMembers) {
                if (obj instanceof String) {
                    memberIds.add(UUID.fromString((String) obj));
                }
            }
        }

        CreateRoomRequest request = CreateRoomRequest.builder()
                .name(name)
                .invitedMemberIds(memberIds)
                .build();

        RoomResponse room = roomService.createRoom(currentUser, request);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "Room created successfully! Invitations sent.");
        resp.put("roomId", room.getId());
        resp.put("room", room);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    // 7. GET /api/getInvites
    @GetMapping("/api/getInvites")
    @Operation(summary = "Legacy get invitations endpoint compatible with existing React frontend")
    public ResponseEntity<Map<String, Object>> legacyGetInvites(@AuthenticationPrincipal User currentUser) {
        List<InvitationResponse> invites = invitationService.getPendingInvitationsForUser(currentUser);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("invites", invites);
        return ResponseEntity.ok(resp);
    }

    // 8. POST /api/acceptInvitations
    @PostMapping("/api/acceptInvitations")
    @Operation(summary = "Legacy accept invitation endpoint compatible with existing React frontend")
    public ResponseEntity<Map<String, Object>> legacyAcceptInvitation(
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal User currentUser
    ) {
        String invitationIdStr = body.get("invitationId");
        if (invitationIdStr == null) {
            throw new BadRequestException("invitationId is required.");
        }
        RoomResponse room = invitationService.acceptInvitation(UUID.fromString(invitationIdStr), currentUser);
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", true);
        resp.put("message", "Invitation accepted successfully! You have been added to the room.");
        resp.put("room", room);
        return ResponseEntity.ok(resp);
    }

    // 9. POST /expenses/add
    @PostMapping("/expenses/add")
    @Operation(summary = "Legacy add expense endpoint compatible with existing React frontend")
    public ResponseEntity<Map<String, Object>> legacyAddExpense(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal User currentUser
    ) {
        Object amountObj = body.get("amount");
        BigDecimal amount = new BigDecimal(amountObj.toString());
        String description = (String) body.get("expenseDesc");
        String roomIdStr = (String) body.get("roomId");
        UUID roomId = UUID.fromString(roomIdStr);

        List<?> splittersRaw = (List<?>) body.get("splittersIds");
        List<UUID> splitters = new ArrayList<>();
        if (splittersRaw != null) {
            for (Object item : splittersRaw) {
                splitters.add(UUID.fromString(item.toString()));
            }
        }

        CreateExpenseRequest req = CreateExpenseRequest.builder()
                .amount(amount)
                .description(description)
                .splittersIds(splitters)
                .category(ExpenseCategory.OTHER)
                .build();

        expenseService.createExpense(roomId, currentUser, req);
        BalanceSummaryResponse balances = expenseService.calculateNetBalances(roomId, currentUser);

        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Expense added successfully");
        resp.put("updatedBalances", balances.getRoommateBalances());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    // 10. GET /expenses/room
    @GetMapping("/expenses/room")
    @Operation(summary = "Legacy get room expenses endpoint compatible with existing React frontend")
    public ResponseEntity<List<ExpenseResponse>> legacyGetRoomExpenses(@AuthenticationPrincipal User currentUser) {
        List<RoomResponse> rooms = roomService.getUserRooms(currentUser);
        if (rooms.isEmpty()) {
            throw new BadRequestException("User does not belong to any room.");
        }
        UUID roomId = rooms.get(0).getId();
        List<ExpenseResponse> expenses = expenseService.getAllRoomExpenses(roomId, currentUser);
        return ResponseEntity.ok(expenses);
    }

    // 11. GET /expenses/net-balance
    @GetMapping("/expenses/net-balance")
    @Operation(summary = "Legacy get net balance endpoint compatible with existing React frontend")
    public ResponseEntity<BalanceSummaryResponse> legacyNetBalance(@AuthenticationPrincipal User currentUser) {
        List<RoomResponse> rooms = roomService.getUserRooms(currentUser);
        if (rooms.isEmpty()) {
            throw new BadRequestException("User does not belong to any room.");
        }
        UUID roomId = rooms.get(0).getId();
        BalanceSummaryResponse balances = expenseService.calculateNetBalances(roomId, currentUser);
        return ResponseEntity.ok(balances);
    }

    // 12. POST /expenses/pay-roommate
    @PostMapping("/expenses/pay-roommate")
    @Operation(summary = "Legacy pay roommate endpoint compatible with existing React frontend")
    public ResponseEntity<Map<String, Object>> legacyPayRoommate(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal User currentUser
    ) {
        List<RoomResponse> rooms = roomService.getUserRooms(currentUser);
        if (rooms.isEmpty()) {
            throw new BadRequestException("User does not belong to any room.");
        }
        UUID roomId = rooms.get(0).getId();

        UUID roommateId = UUID.fromString((String) body.get("roommateId"));
        BigDecimal amount = new BigDecimal(body.get("amount").toString());

        RecordPaymentRequest req = RecordPaymentRequest.builder()
                .roommateId(roommateId)
                .amount(amount)
                .notes("Settled via legacy interface")
                .build();

        PaymentResponse payment = settlementService.recordPayment(roomId, currentUser, req);
        BalanceSummaryResponse balances = expenseService.calculateNetBalances(roomId, currentUser);

        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Payment successful");
        resp.put("updatedBalances", balances.getRoommateBalances());
        resp.put("payment", payment);
        return ResponseEntity.ok(resp);
    }
}

