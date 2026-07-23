package com.smartclinic.masterdata.service;

import com.smartclinic.common.exception.DuplicateResourceException;
import com.smartclinic.common.exception.ResourceNotFoundException;
import com.smartclinic.masterdata.dto.RoomRequest;
import com.smartclinic.masterdata.dto.RoomResponse;
import com.smartclinic.masterdata.entity.Room;
import com.smartclinic.masterdata.mapper.MasterDataMapper;
import com.smartclinic.masterdata.repository.RoomRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> findAll() {
        return roomRepository.findAll().stream()
                .map(MasterDataMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse getById(Long id) {
        return MasterDataMapper.toResponse(findRoom(id));
    }

    @Override
    public RoomResponse create(RoomRequest request) {
        if (roomRepository.existsByRoomCodeIgnoreCase(request.getRoomCode())) {
            throw new DuplicateResourceException("Room code already exists");
        }
        Room room = Room.builder()
                .roomCode(request.getRoomCode().trim())
                .name(request.getName().trim())
                .floor(request.getFloor())
                .active(request.isActive())
                .build();
        return MasterDataMapper.toResponse(roomRepository.save(room));
    }

    @Override
    public RoomResponse update(Long id, RoomRequest request) {
        Room room = findRoom(id);
        if (!room.getRoomCode().equalsIgnoreCase(request.getRoomCode())
                && roomRepository.existsByRoomCodeIgnoreCase(request.getRoomCode())) {
            throw new DuplicateResourceException("Room code already exists");
        }
        room.setRoomCode(request.getRoomCode().trim());
        room.setName(request.getName().trim());
        room.setFloor(request.getFloor());
        room.setActive(request.isActive());
        return MasterDataMapper.toResponse(roomRepository.save(room));
    }

    @Override
    public void deactivate(Long id) {
        Room room = findRoom(id);
        room.setActive(false);
        roomRepository.save(room);
    }

    private Room findRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    }
}