package com.roomiemate.repository;

import com.roomiemate.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String name, String email);

    @Query("SELECT u FROM User u WHERE u.id != :currentUserId AND u.id NOT IN (SELECT m.id FROM Room r JOIN r.members m)")
    List<User> findIdleUsers(@Param("currentUserId") UUID currentUserId);

    @Query("SELECT u FROM User u WHERE u.id != :currentUserId AND u.id NOT IN (SELECT m.id FROM Room r JOIN r.members m WHERE r.id = :roomId)")
    List<User> findUsersNotInRoom(@Param("roomId") UUID roomId, @Param("currentUserId") UUID currentUserId);
}

