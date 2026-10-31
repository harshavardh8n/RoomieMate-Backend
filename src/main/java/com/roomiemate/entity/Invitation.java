package com.roomiemate.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "invitations")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "rooms"})
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "rooms"})
    private User receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "members"})
    private Room room;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvitationStatus status = InvitationStatus.PENDING;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    public Invitation() {}

    public Invitation(UUID id, User sender, User receiver, Room room, InvitationStatus status, LocalDateTime createdAt, LocalDateTime respondedAt) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.room = room;
        this.status = status != null ? status : InvitationStatus.PENDING;
        this.createdAt = createdAt;
        this.respondedAt = respondedAt;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public User getReceiver() { return receiver; }
    public void setReceiver(User receiver) { this.receiver = receiver; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public InvitationStatus getStatus() { return status; }
    public void setStatus(InvitationStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Invitation that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public static InvitationBuilder builder() {
        return new InvitationBuilder();
    }

    public static class InvitationBuilder {
        private UUID id;
        private User sender;
        private User receiver;
        private Room room;
        private InvitationStatus status = InvitationStatus.PENDING;
        private LocalDateTime createdAt;
        private LocalDateTime respondedAt;

        public InvitationBuilder id(UUID id) { this.id = id; return this; }
        public InvitationBuilder sender(User sender) { this.sender = sender; return this; }
        public InvitationBuilder receiver(User receiver) { this.receiver = receiver; return this; }
        public InvitationBuilder room(Room room) { this.room = room; return this; }
        public InvitationBuilder status(InvitationStatus status) { this.status = status; return this; }
        public InvitationBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public InvitationBuilder respondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; return this; }

        public Invitation build() {
            return new Invitation(id, sender, receiver, room, status, createdAt, respondedAt);
        }
    }
}
