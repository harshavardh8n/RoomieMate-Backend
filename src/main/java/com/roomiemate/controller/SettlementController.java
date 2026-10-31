package com.roomiemate.controller;

import com.roomiemate.dto.common.ApiResponse;
import com.roomiemate.dto.payment.PaymentResponse;
import com.roomiemate.dto.payment.RecordPaymentRequest;
import com.roomiemate.dto.payment.SimplifiedSettlementDto;
import com.roomiemate.entity.User;
import com.roomiemate.service.SettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rooms/{roomId}")
@Tag(name = "Settlements & Payments", description = "Endpoints for direct payments, audit history, and Min-Cash-Flow debt simplification")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @PostMapping("/payments")
    @Operation(summary = "Record a settlement payment from current user to a roommate")
    public ResponseEntity<ApiResponse<PaymentResponse>> recordPayment(
            @PathVariable UUID roomId,
            @Valid @RequestBody RecordPaymentRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        PaymentResponse response = settlementService.recordPayment(roomId, currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Payment recorded and balance settled.", response));
    }

    @GetMapping("/payments")
    @Operation(summary = "Get historical log of all settlement payments in this room")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getRoomPayments(
            @PathVariable UUID roomId,
            @AuthenticationPrincipal User currentUser
    ) {
        List<PaymentResponse> payments = settlementService.getRoomPayments(roomId, currentUser);
        return ResponseEntity.ok(ApiResponse.ok(payments));
    }

    @GetMapping("/settlements/simplified")
    @Operation(summary = "Run Min-Cash-Flow algorithm to simplify debts and minimize transactions across the room")
    public ResponseEntity<ApiResponse<List<SimplifiedSettlementDto>>> getSimplifiedSettlements(
            @PathVariable UUID roomId,
            @AuthenticationPrincipal User currentUser
    ) {
        List<SimplifiedSettlementDto> simplified = settlementService.calculateSimplifiedDebts(roomId, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Optimal settlement transactions calculated.", simplified));
    }
}

