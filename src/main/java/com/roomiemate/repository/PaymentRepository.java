package com.roomiemate.repository;

import com.roomiemate.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Query("SELECT p FROM Payment p WHERE p.room.id = :roomId ORDER BY p.createdAt DESC")
    List<Payment> findAllByRoomId(@Param("roomId") UUID roomId);
}

