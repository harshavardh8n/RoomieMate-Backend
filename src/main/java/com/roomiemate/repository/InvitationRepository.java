package com.roomiemate.repository;

import com.roomiemate.entity.Invitation;
import com.roomiemate.entity.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, UUID> {

    List<Invitation> findByReceiverIdAndStatus(UUID receiverId, InvitationStatus status);

    Optional<Invitation> findByRoomIdAndReceiverIdAndStatus(UUID roomId, UUID receiverId, InvitationStatus status);

    @Modifying
    @Query("DELETE FROM Invitation i WHERE i.receiver.id = :userId")
    void deleteAllByReceiverId(@Param("userId") UUID userId);
}

