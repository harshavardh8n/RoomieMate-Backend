package com.roomiemate.controller;

import com.roomiemate.dto.common.ApiResponse;
import com.roomiemate.dto.user.UserSummaryDto;
import com.roomiemate.entity.User;
import com.roomiemate.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for searching users, finding idle users, and profile")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get currently authenticated user's profile")
    public ResponseEntity<ApiResponse<UserSummaryDto>> getMe(@AuthenticationPrincipal User currentUser) {
        return ResponseEntity.ok(ApiResponse.ok(UserSummaryDto.fromEntity(currentUser)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users by name or email")
    public ResponseEntity<ApiResponse<List<UserSummaryDto>>> searchUsers(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "name", required = false) String name
    ) {
        String searchTerm = query != null ? query : name;
        List<UserSummaryDto> users = userService.searchUsers(searchTerm);
        return ResponseEntity.ok(ApiResponse.ok(users));
    }

    @GetMapping("/idle")
    @Operation(summary = "Get list of idle users (not currently in any room)")
    public ResponseEntity<ApiResponse<List<UserSummaryDto>>> getIdleUsers(@AuthenticationPrincipal User currentUser) {
        List<UserSummaryDto> idleUsers = userService.getIdleUsers(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(idleUsers));
    }

    @GetMapping("/available-for-room/{roomId}")
    @Operation(summary = "Get users available to be invited to a specific room")
    public ResponseEntity<ApiResponse<List<UserSummaryDto>>> getUsersNotInRoom(
            @PathVariable UUID roomId,
            @AuthenticationPrincipal User currentUser
    ) {
        List<UserSummaryDto> users = userService.getUsersNotInRoom(roomId, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(users));
    }
}

