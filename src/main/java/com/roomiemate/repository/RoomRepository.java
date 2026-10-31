package com.roomiemate.repository;

import com.roomiemate.entity.Room;
import com.roomiemate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {

    @Query("SELECT r FROM Room r JOIN r.members m WHERE m.id = :userId")
    List<Room> findAllByMemberId(@Param("userId") UUID userId);

    @Query("SELECT r FROM Room r JOIN r.members m WHERE r.id = :roomId AND m.id = :userId")
    Optional<Room> findByIdAndMemberId(@Param("roomId") UUID roomId, @Param("userId") UUID userId);

    boolean existsByIdAndMembersContaining(UUID roomId, User user);
}

