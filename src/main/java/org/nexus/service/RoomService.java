package org.nexus.service;

import org.nexus.entity.Floor;
import org.nexus.entity.Room;
import org.nexus.entity.RoomType;
import org.nexus.repository.FloorRepository;
import org.nexus.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final FloorRepository floorRepository;

    @Autowired
    public RoomService(RoomRepository roomRepository, FloorRepository floorRepository) {
        this.roomRepository = roomRepository;
        this.floorRepository = floorRepository;
    }

    public List<Room> findAllRooms() {
        return roomRepository.findAll();
    }

    public Page<Room> findAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    public Optional<Room> findRoomById(Integer id) {
        return roomRepository.findById(id);
    }

    public List<Room> findRoomsByFloor(Integer floorId) {
        return roomRepository.findByFloorId(floorId);
    }

    public List<Room> findRoomsByBuilding(Integer buildingId) {
        return roomRepository.findByFloorBuildingId(buildingId);
    }

    public List<Room> findRoomsByType(RoomType roomType) {
        return roomRepository.findByRoomType(roomType);
    }

    public List<Room> findRoomsByBuildingAndType(Integer buildingId, RoomType roomType) {
        return roomRepository.findByBuildingIdAndRoomType(buildingId, roomType);
    }

    @Transactional
    public Room createRoom(Room room) {
        // Validate room inputs
        if (room.getRoomNumber() == null || room.getRoomNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Room number cannot be empty");
        }
        if (room.getRoomType() == null) {
            throw new IllegalArgumentException("Room type must be specified");
        }
        if (room.getFloor() == null || room.getFloor().getId() == null) {
            throw new IllegalArgumentException("Floor must be specified");
        }

        // Check if floor exists
        Floor floor = floorRepository.findById(room.getFloor().getId())
                .orElseThrow(() -> new RuntimeException("Floor not found with id: " + room.getFloor().getId()));
        room.setFloor(floor);

        // Check if room with this number already exists on the floor
        if (roomRepository.existsByRoomNumberAndFloorId(room.getRoomNumber(), floor.getId())) {
            throw new IllegalArgumentException("Room " + room.getRoomNumber() + " already exists on floor " + floor.getFloorNumber());
        }

        return roomRepository.save(room);
    }

    @Transactional
    public Room updateRoom(Integer id, Room roomDetails) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));

        // Update basic properties
        if (roomDetails.getRoomNumber() != null && !roomDetails.getRoomNumber().trim().isEmpty()) {
            existingRoom.setRoomNumber(roomDetails.getRoomNumber());
        }

        if (roomDetails.getRoomType() != null) {
            existingRoom.setRoomType(roomDetails.getRoomType());
        }

        if (roomDetails.getCapacity() != null) {
            existingRoom.setCapacity(roomDetails.getCapacity());
        }

        if (roomDetails.getDescription() != null) {
            existingRoom.setDescription(roomDetails.getDescription());
        }

        if (roomDetails.getIsActive() != null) {
            existingRoom.setIsActive(roomDetails.getIsActive());
        }

        // Update floor if provided
        if (roomDetails.getFloor() != null && roomDetails.getFloor().getId() != null) {
            // Check if attempting to change floor
            if (!existingRoom.getFloor().getId().equals(roomDetails.getFloor().getId())) {
                // Verify the floor exists
                Floor newFloor = floorRepository.findById(roomDetails.getFloor().getId())
                        .orElseThrow(() -> new RuntimeException("Floor not found with id: " + roomDetails.getFloor().getId()));

                // Check if room number already exists on new floor
                if (roomRepository.existsByRoomNumberAndFloorId(existingRoom.getRoomNumber(), newFloor.getId())) {
                    throw new IllegalArgumentException("Room " + existingRoom.getRoomNumber() +
                            " already exists on floor " + newFloor.getFloorNumber());
                }

                existingRoom.setFloor(newFloor);
            }
        }

        return roomRepository.save(existingRoom);
    }

    @Transactional
    public void deleteRoom(Integer id) {
        if (!roomRepository.existsById(id)) {
            throw new RuntimeException("Room not found with id: " + id);
        }
        roomRepository.deleteById(id);
    }

    public long countRoomsByFloor(Integer floorId) {
        return roomRepository.countByFloorId(floorId);
    }

    public boolean existsByRoomNumberAndFloor(String roomNumber, Integer floorId) {
        return roomRepository.existsByRoomNumberAndFloorId(roomNumber, floorId);
    }

}