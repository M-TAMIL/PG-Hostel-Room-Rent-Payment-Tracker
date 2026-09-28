package com.renteasy.controller;

import com.renteasy.dto.ApiResponse;
import com.renteasy.dto.RoomRequest;
import com.renteasy.dto.RoomResponse;
import com.renteasy.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@Valid @RequestBody RoomRequest request) {
        RoomResponse response = roomService.createRoom(request);
        return new ResponseEntity<>(
                ApiResponse.success("Room " + response.getRoomNumber() + " created successfully", response),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAllRooms(
            @RequestParam(required = false) Boolean isOccupied) {
        List<RoomResponse> rooms = roomService.getAllRooms(isOccupied);
        return ResponseEntity.ok(
                ApiResponse.success("Rooms retrieved successfully", rooms)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(@PathVariable Long id) {
        RoomResponse response = roomService.getRoomById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Room found", response)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody RoomRequest request) {
        RoomResponse response = roomService.updateRoom(id, request);
        return ResponseEntity.ok(
                ApiResponse.success("Room updated successfully", response)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(
                ApiResponse.success("Room deleted successfully")
        );
    }
}
