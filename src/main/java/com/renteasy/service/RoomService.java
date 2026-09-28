package com.renteasy.service;

import com.renteasy.dto.RoomRequest;
import com.renteasy.dto.RoomResponse;
import com.renteasy.entity.Room;
import com.renteasy.exception.InvalidOperationException;
import com.renteasy.exception.ResourceNotFoundException;
import com.renteasy.repository.RoomRepository;
import com.renteasy.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final TenantRepository tenantRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository, TenantRepository tenantRepository) {
        this.roomRepository = roomRepository;
        this.tenantRepository = tenantRepository;
    }

    public RoomResponse createRoom(RoomRequest request) {
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new InvalidOperationException("Room number " + request.getRoomNumber() + " already exists!");
        }

        Room room = new Room(
                request.getRoomNumber().trim().toUpperCase(),
                request.getRoomType(),
                request.getCapacity(),
                request.getMonthlyRent()
        );

        Room saved = roomRepository.save(room);
        return RoomResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getAllRooms(Boolean isOccupied) {
        List<Room> rooms;
        if (isOccupied != null) {
            rooms = roomRepository.findByIsOccupied(isOccupied);
        } else {
            rooms = roomRepository.findAll();
        }
        return rooms.stream().map(RoomResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RoomResponse getRoomById(Long id) {
        Room room = findRoomEntityById(id);
        return RoomResponse.fromEntity(room);
    }

    public Room findRoomEntityById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with ID: " + id));
    }

    public RoomResponse updateRoom(Long id, RoomRequest request) {
        Room room = findRoomEntityById(id);

        if (!room.getRoomNumber().equalsIgnoreCase(request.getRoomNumber().trim())
                && roomRepository.existsByRoomNumber(request.getRoomNumber().trim())) {
            throw new InvalidOperationException("Room number " + request.getRoomNumber() + " is already taken by another room!");
        }

        if (request.getCapacity() < room.getCurrentOccupancy()) {
            throw new InvalidOperationException("Capacity cannot be reduced below current occupancy of " + room.getCurrentOccupancy() + " tenants!");
        }

        room.setRoomNumber(request.getRoomNumber().trim().toUpperCase());
        room.setRoomType(request.getRoomType());
        room.setCapacity(request.getCapacity());
        room.setMonthlyRent(request.getMonthlyRent());
        room.updateOccupiedStatus();

        Room updated = roomRepository.save(room);
        return RoomResponse.fromEntity(updated);
    }

    public void deleteRoom(Long id) {
        Room room = findRoomEntityById(id);
        if (room.getCurrentOccupancy() > 0) {
            throw new InvalidOperationException("Cannot delete room " + room.getRoomNumber() + " because it currently has active tenants!");
        }
        roomRepository.delete(room);
    }
}
