package com.roomiemate.service;

import com.roomiemate.dto.room.CreateRoomRequest;
import com.roomiemate.dto.room.RoomResponse;
import com.roomiemate.entity.Invitation;
import com.roomiemate.entity.InvitationStatus;
import com.roomiemate.entity.Room;
import com.roomiemate.entity.User;
import com.roomiemate.exception.BadRequestException;
import com.roomiemate.exception.ResourceNotFoundException;
import com.roomiemate.repository.InvitationRepository;
import com.roomiemate.repository.RoomRepository;
import com.roomiemate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;

    @Transactional
    public RoomResponse createRoom(User creator, CreateRoomRequest request) {
        Room room = Room.builder()
                .name(request.getName().trim())
                .admin(creator)
                .members(new HashSet<>(Collections.singletonList(creator)))
                .build();

        Room savedRoom = roomRepository.save(room);

        // Send invitations if member IDs were provided
        if (request.getInvitedMemberIds() != null && !request.getInvitedMemberIds().isEmpty()) {
            for (UUID memberId : request.getInvitedMemberIds()) {
                if (!memberId.equals(creator.getId())) {
                    userRepository.findById(memberId).ifPresent(receiver -> {
                        Invitation invitation = Invitation.builder()
                                .sender(creator)
                                .receiver(receiver)
                                .room(savedRoom)
                                .status(InvitationStatus.PENDING)
                                .build();
                        invitationRepository.save(invitation);
                    });
                }
            }
        }

        return RoomResponse.fromEntity(savedRoom);
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getUserRooms(User user) {
        return roomRepository.findAllByMemberId(user.getId())
                .stream()
                .map(RoomResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoomResponse getRoomDetails(UUID roomId, User user) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        if (!room.getMembers().contains(user)) {
            throw new AccessDeniedException("You are not a member of this room.");
        }

        return RoomResponse.fromEntity(room);
    }

    @Transactional
    public RoomResponse addMemberDirectly(UUID roomId, UUID userId, User currentUser) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        if (!room.getAdmin().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Only the room admin can directly add members.");
        }

        User userToAdd = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (room.getMembers().contains(userToAdd)) {
            throw new BadRequestException("User is already a member of this room.");
        }

        room.getMembers().add(userToAdd);
        Room updated = roomRepository.save(room);

        return RoomResponse.fromEntity(updated);
    }
}

