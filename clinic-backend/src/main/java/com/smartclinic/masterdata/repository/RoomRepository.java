package com.smartclinic.masterdata.repository;

import com.smartclinic.masterdata.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByRoomCodeIgnoreCase(String roomCode);

    java.util.Optional<Room> findByRoomCodeIgnoreCase(String roomCode);
}