package com.roomiemate.service;

import com.roomiemate.dto.room.InvitationResponse;
import com.roomiemate.dto.room.RoomResponse;
import com.roomiemate.entity.Invitation;
import com.roomiemate.entity.InvitationStatus;
import com.roomiemate.entity.Room;
import com.roomiemate.entity.User;
import com.roomiemate.exception.BadRequestException;
import com.roomiemate.exception.ConflictException;
import com.roomiemate.exception.ResourceNotFoundException;
import com.roomiemate.repository.InvitationRepository;
import com.roomiemate.repository.RoomRepository;
import com.roomiemate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public InvitationResponse sendInvitation(UUID roomId, UUID receiverId, User sender) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + roomId));

        if (!room.getMembers().contains(sender)) {
            throw new AccessDeniedException("Only room members can send invitations.");
        }

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("User to invite not found with ID: " + receiverId));

        if (room.getMembers().contains(receiver)) {
            throw new BadRequestException("User is already a member of this room.");
        }

        if (invitationRepository.findByRoomIdAndReceiverIdAndStatus(roomId, receiverId, InvitationStatus.PENDING).isPresent()) {
            throw new ConflictException("A pending invitation for this user already exists.");
        }

        Invitation invitation = Invitation.builder()
                .room(room)
                .sender(sender)
                .receiver(receiver)
                .status(InvitationStatus.PENDING)
                .build();

        Invitation saved = invitationRepository.save(invitation);
        return InvitationResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<InvitationResponse> getPendingInvitationsForUser(User user) {
        return invitationRepository.findByReceiverIdAndStatus(user.getId(), InvitationStatus.PENDING)
                .stream()
                .map(InvitationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomResponse acceptInvitation(UUID invitationId, User user) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found with ID: " + invitationId));

        if (!invitation.getReceiver().getId().equals(user.getId())) {
            throw new AccessDeniedException("You cannot accept an invitation addressed to someone else.");
        }

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new BadRequestException("This invitation is already " + invitation.getStatus().name().toLowerCase() + ".");
        }

        Room room = invitation.getRoom();
        room.getMembers().add(user);
        roomRepository.save(room);

        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation.setRespondedAt(LocalDateTime.now());
        invitationRepository.save(invitation);

        // Clean up or delete other pending invitations for this user since they just joined a room
        invitationRepository.deleteAllByReceiverId(user.getId());

        return RoomResponse.fromEntity(room);
    }

    @Transactional
    public void rejectInvitation(UUID invitationId, User user) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ResourceNotFoundException("Invitation not found with ID: " + invitationId));

        if (!invitation.getReceiver().getId().equals(user.getId())) {
            throw new AccessDeniedException("You cannot reject an invitation addressed to someone else.");
        }

        invitation.setStatus(InvitationStatus.REJECTED);
        invitation.setRespondedAt(LocalDateTime.now());
        invitationRepository.save(invitation);
    }
}

