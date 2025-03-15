package org.nexus.controller;

import org.nexus.entity.Room;
import org.nexus.entity.RoomType;
import org.nexus.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    @Autowired
    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        List<Room> rooms = roomService.findAllRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/paged")
    public ResponseEntity<Map<String, Object>> getAllRoomsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Room> roomsPage = roomService.findAllRooms(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("rooms", roomsPage.getContent());
        response.put("currentPage", roomsPage.getNumber());
        response.put("totalItems", roomsPage.getTotalElements());
        response.put("totalPages", roomsPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getRoomById(@PathVariable Integer id) {
        return roomService.findRoomById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/floor/{floorId}")
    public ResponseEntity<List<Room>> getRoomsByFloor(@PathVariable Integer floorId) {
        List<Room> rooms = roomService.findRoomsByFloor(floorId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/building/{buildingId}")
    public ResponseEntity<List<Room>> getRoomsByBuilding(@PathVariable Integer buildingId) {
        List<Room> rooms = roomService.findRoomsByBuilding(buildingId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/type/{roomType}")
    public ResponseEntity<List<Room>> getRoomsByType(@PathVariable RoomType roomType) {
        List<Room> rooms = roomService.findRoomsByType(roomType);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/building/{buildingId}/type/{roomType}")
    public ResponseEntity<List<Room>> getRoomsByBuildingAndType(
            @PathVariable Integer buildingId,
            @PathVariable RoomType roomType) {
        List<Room> rooms = roomService.findRoomsByBuildingAndType(buildingId, roomType);
        return ResponseEntity.ok(rooms);
    }

    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody Room room) {
        try {
            Room createdRoom = roomService.createRoom(room);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoom(
            @PathVariable Integer id,
            @RequestBody Room roomDetails) {
        try {
            Room updatedRoom = roomService.updateRoom(id, roomDetails);
            return ResponseEntity.ok(updatedRoom);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoom(@PathVariable Integer id) {
        try {
            roomService.deleteRoom(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/floor/{floorId}/count")
    public ResponseEntity<Map<String, Long>> countRoomsByFloor(@PathVariable Integer floorId) {
        Map<String, Long> countInfo = new HashMap<>();
        countInfo.put("roomCount", roomService.countRoomsByFloor(floorId));
        return ResponseEntity.ok(countInfo);
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkRoomExists(
            @RequestParam String roomNumber,
            @RequestParam Integer floorId) {
        Map<String, Boolean> exists = new HashMap<>();
        exists.put("exists", roomService.existsByRoomNumberAndFloor(roomNumber, floorId));
        return ResponseEntity.ok(exists);
    }
}