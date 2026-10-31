package com.roomiemate.controller;

import com.roomiemate.dto.common.ApiResponse;
import com.roomiemate.dto.room.InvitationResponse;
import com.roomiemate.dto.room.InviteUserRequest;
import com.roomiemate.dto.room.RoomResponse;
import com.roomiemate.entity.User;
import com.roomiemate.service.InvitationService;
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
@RequestMapping("/api/v1/invitations")
@Tag(name = "Invitations", description = "Endpoints for room invitations and responses")
public class InvitationController {

    private final InvitationService invitationService;

    public InvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @PostMapping("/rooms/{roomId}")
    @Operation(summary = "Send an invitation to a user to join a room")
    public ResponseEntity<ApiResponse<InvitationResponse>> sendInvitation(
            @PathVariable UUID roomId,
            @Valid @RequestBody InviteUserRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        InvitationResponse response = invitationService.sendInvitation(roomId, request.getUserId(), currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Invitation sent successfully.", response));
    }

    @GetMapping
    @Operation(summary = "Get all pending invitations received by the current user")
    public ResponseEntity<ApiResponse<List<InvitationResponse>>> getMyPendingInvitations(
            @AuthenticationPrincipal User currentUser
    ) {
        List<InvitationResponse> invites = invitationService.getPendingInvitationsForUser(currentUser);
        return ResponseEntity.ok(ApiResponse.ok(invites));
    }

    @PostMapping("/{invitationId}/accept")
    @Operation(summary = "Accept an invitation to join a room")
    public ResponseEntity<ApiResponse<RoomResponse>> acceptInvitation(
            @PathVariable UUID invitationId,
            @AuthenticationPrincipal User currentUser
    ) {
        RoomResponse room = invitationService.acceptInvitation(invitationId, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Invitation accepted. You joined the room.", room));
    }

    @PostMapping("/{invitationId}/reject")
    @Operation(summary = "Reject an invitation to join a room")
    public ResponseEntity<ApiResponse<Void>> rejectInvitation(
            @PathVariable UUID invitationId,
            @AuthenticationPrincipal User currentUser
    ) {
        invitationService.rejectInvitation(invitationId, currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Invitation rejected.", null));
    }
}

