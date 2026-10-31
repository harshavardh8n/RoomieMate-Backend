package com.roomiemate.controller;

import com.roomiemate.dto.common.ApiResponse;
import com.roomiemate.dto.room.CreateRoomRequest;
import com.roomiemate.dto.room.InviteUserRequest;
import com.roomiemate.dto.room.RoomResponse;
import com.roomiemate.entity.User;
import com.roomiemate.service.RoomService;
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
@RequestMapping("/api/v1/rooms")
@Tag(name = "Rooms", description = "Endpoints for creating and managing rooms and members")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    @Operation(summary = "Create a new room and send invitations to specified users")
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(
            @Valid @RequestBody CreateRoomRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        RoomResponse response = roomService.createRoom(currentUser, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Room created successfully.", response));
    }

    @GetMapping
    @Operation(summary = "Get all rooms that the current user belongs to")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getUserRooms(@AuthenticationPrincipal User currentUser) {
        List<RoomResponse> rooms = roomService.getUserRooms(currentUser);
        return ResponseEntity.ok(ApiResponse.ok(rooms));
    }

    @GetMapping("/{roomId}")
    @Operation(summary = "Get details and members of a specific room")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomDetails(
            @PathVariable UUID roomId,
            @AuthenticationPrincipal User currentUser
    ) {
        RoomResponse response = roomService.getRoomDetails(roomId, currentUser);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/{roomId}/members")
    @Operation(summary = "Directly add a member to the room (Admin only)")
    public ResponseEntity<ApiResponse<RoomResponse>> addMember(
            @PathVariable UUID roomId,
            @Valid @RequestBody InviteUserRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        RoomResponse response = roomService.addMemberDirectly(roomId, request.getUserId(), currentUser);
        return ResponseEntity.ok(ApiResponse.ok("Member added successfully.", response));
    }
}

