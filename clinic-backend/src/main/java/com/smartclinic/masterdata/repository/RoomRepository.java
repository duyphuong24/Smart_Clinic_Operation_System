package com.smartclinic.masterdata.repository;

import com.smartclinic.masterdata.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByRoomCodeIgnoreCase(String roomCode);
}