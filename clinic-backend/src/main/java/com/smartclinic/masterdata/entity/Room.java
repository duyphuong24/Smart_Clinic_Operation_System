package com.smartclinic.masterdata.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "rooms",
        uniqueConstraints = @UniqueConstraint(name = "uq_rooms_room_code", columnNames = "room_code")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_code", nullable = false, length = 50)
    private String roomCode;

    @Column(name = "name", nullable = false, length = 100, national = true)
    private String name;

    @Column(name = "floor", length = 30)
    private String floor;

    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;
}