package com.roomiemate.repository;

import com.roomiemate.entity.Expense;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, UUID> {

    @Query("SELECT DISTINCT e FROM Expense e LEFT JOIN FETCH e.splits s LEFT JOIN FETCH s.user WHERE e.room.id = :roomId ORDER BY e.createdAt DESC")
    List<Expense> findAllWithSplitsByRoomId(@Param("roomId") UUID roomId);

    Page<Expense> findByRoomIdOrderByCreatedAtDesc(UUID roomId, Pageable pageable);
}

