package com.roomiemate.controller;

import com.roomiemate.dto.common.ApiResponse;
import com.roomiemate.dto.expense.BalanceSummaryResponse;
import com.roomiemate.dto.expense.CreateExpenseRequest;
import com.roomiemate.dto.expense.ExpenseResponse;
import com.roomiemate.dto.expense.RoomAnalyticsResponse;
import com.roomiemate.entity.User;
import com.roomiemate.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rooms/{roomId}")
@Tag(name = "Expenses & Balances", description = "Endpoints for creating expenses, calculating balances, and analytics")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping("/expenses")
    @Operation(summary = "Add an expense to a room with equal or custom split among members")
    public ResponseEntity<ApiResponse<ExpenseResponse>> addExpense(
            @PathVariable UUID roomId,
            @Valid @RequestBody CreateExpenseRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        ExpenseResponse response = expenseService.createExpense(roomId, currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Expense recorded successfully.", response));
    }

    @GetMapping("/expenses")
    @Operation(summary = "Get paginated expenses for a room")
    public ResponseEntity<ApiResponse<Page<ExpenseResponse>>> getExpenses(
            @PathVariable UUID roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @AuthenticationPrincipal User currentUser
    ) {
        Sort.Direction dir = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortBy));
        Page<ExpenseResponse> expenses = expenseService.getRoomExpenses(roomId, currentUser, pageable);
        return ResponseEntity.ok(ApiResponse.ok(expenses));
    }

    @GetMapping("/expenses/all")
    @Operation(summary = "Get all expenses for a room")
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getAllExpenses(
            @PathVariable UUID roomId,
            @AuthenticationPrincipal User currentUser
    ) {
        List<ExpenseResponse> expenses = expenseService.getAllRoomExpenses(roomId, currentUser);
        return ResponseEntity.ok(ApiResponse.ok(expenses));
    }

    @GetMapping("/balances")
    @Operation(summary = "Calculate the logged-in user's net balance and bilateral balances with every roommate")
    public ResponseEntity<ApiResponse<BalanceSummaryResponse>> getBalances(
            @PathVariable UUID roomId,
            @AuthenticationPrincipal User currentUser
    ) {
        BalanceSummaryResponse balances = expenseService.calculateNetBalances(roomId, currentUser);
        return ResponseEntity.ok(ApiResponse.ok(balances));
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get statistical breakdown of room spending by category and member")
    public ResponseEntity<ApiResponse<RoomAnalyticsResponse>> getAnalytics(
            @PathVariable UUID roomId,
            @AuthenticationPrincipal User currentUser
    ) {
        RoomAnalyticsResponse analytics = expenseService.getRoomAnalytics(roomId, currentUser);
        return ResponseEntity.ok(ApiResponse.ok(analytics));
    }
}

