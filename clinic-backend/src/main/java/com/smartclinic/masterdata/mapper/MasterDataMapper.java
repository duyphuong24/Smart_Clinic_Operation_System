package com.smartclinic.masterdata.mapper;

import com.smartclinic.masterdata.dto.RoomResponse;
import com.smartclinic.masterdata.dto.SpecialtyResponse;
import com.smartclinic.masterdata.entity.Room;
import com.smartclinic.masterdata.entity.Specialty;

public final class MasterDataMapper {

    private MasterDataMapper() {
    }

    public static SpecialtyResponse toResponse(Specialty specialty) {
        return SpecialtyResponse.builder()
                .id(specialty.getId())
                .name(specialty.getName())
                .description(specialty.getDescription())
                .active(specialty.isActive())
                .build();
    }

    public static RoomResponse toResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .roomCode(room.getRoomCode())
                .name(room.getName())
                .floor(room.getFloor())
                .active(room.isActive())
                .build();
    }
}