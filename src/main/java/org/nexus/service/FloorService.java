package org.nexus.service;

import org.nexus.entity.Building;
import org.nexus.entity.Floor;
import org.nexus.repository.BuildingRepository;
import org.nexus.repository.FloorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class FloorService {

    private final FloorRepository floorRepository;
    private final BuildingRepository buildingRepository;

    @Autowired
    public FloorService(FloorRepository floorRepository, BuildingRepository buildingRepository) {
        this.floorRepository = floorRepository;
        this.buildingRepository = buildingRepository;
    }

    public List<Floor> findAllFloors() {
        return floorRepository.findAll();
    }

    public Page<Floor> findAllFloors(Pageable pageable) {
        return floorRepository.findAll(pageable);
    }

    public Optional<Floor> findFloorById(Integer id) {
        return floorRepository.findById(id);
    }

    public List<Floor> findFloorsByBuilding(Integer buildingId) {
        return floorRepository.findByBuildingIdOrderByFloorNumberAsc(buildingId);
    }

    public Optional<Floor> findFloorByNumberAndBuilding(Integer floorNumber, Integer buildingId) {
        return floorRepository.findByFloorNumberAndBuildingId(floorNumber, buildingId);
    }

    public List<Floor> findFloorsWithMostRooms() {
        return floorRepository.findAllOrderByRoomCountDesc();
    }

    public List<Floor> findEmptyFloors() {
        return floorRepository.findFloorsWithNoRooms();
    }

    @Transactional
    public Floor createFloor(Floor floor) {
        if (floor == null) {
            throw new IllegalArgumentException("Floor cannot be null");
        }

        if (floor.getFloorNumber() == null) {
            throw new IllegalArgumentException("Floor number is required");
        }

        if (floor.getBuilding() == null || floor.getBuilding().getId() == null) {
            throw new IllegalArgumentException("Building is required");
        }

        // Verify building exists
        Building building = buildingRepository.findById(floor.getBuilding().getId())
                .orElseThrow(() -> new RuntimeException("Building not found with id: " + floor.getBuilding().getId()));

        // Check if floor already exists in this building
        if (floorRepository.existsByFloorNumberAndBuildingId(floor.getFloorNumber(), building.getId())) {
            throw new IllegalArgumentException("Floor " + floor.getFloorNumber() + " already exists in building " + building.getBuildingCode());
        }

        floor.setBuilding(building);

        // Initialize rooms if null
        if (floor.getRooms() == null) {
            floor.setRooms(new HashSet<>());
        }

        return floorRepository.save(floor);
    }

    @Transactional
    public Floor updateFloor(Integer id, Floor floorDetails) {
        Floor existingFloor = floorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Floor not found with id: " + id));

        if (floorDetails.getFloorNumber() != null) {
            // Check if changing to a floor number that already exists in the building
            if (!existingFloor.getFloorNumber().equals(floorDetails.getFloorNumber()) &&
                    floorRepository.existsByFloorNumberAndBuildingId(floorDetails.getFloorNumber(), existingFloor.getBuilding().getId())) {
                throw new IllegalArgumentException("Floor " + floorDetails.getFloorNumber() +
                        " already exists in building " + existingFloor.getBuilding().getBuildingCode());
            }
            existingFloor.setFloorNumber(floorDetails.getFloorNumber());
        }

        // Handle building change if specified
        if (floorDetails.getBuilding() != null && floorDetails.getBuilding().getId() != null &&
                !existingFloor.getBuilding().getId().equals(floorDetails.getBuilding().getId())) {

            Building newBuilding = buildingRepository.findById(floorDetails.getBuilding().getId())
                    .orElseThrow(() -> new RuntimeException("Building not found with id: " + floorDetails.getBuilding().getId()));

            // Check if floor number already exists in the new building
            if (floorRepository.existsByFloorNumberAndBuildingId(existingFloor.getFloorNumber(), newBuilding.getId())) {
                throw new IllegalArgumentException("Floor " + existingFloor.getFloorNumber() +
                        " already exists in building " + newBuilding.getBuildingCode());
            }

            existingFloor.setBuilding(newBuilding);
        }

        return floorRepository.save(existingFloor);
    }

    @Transactional
    public void deleteFloor(Integer id) {
        Floor floor = floorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Floor not found with id: " + id));

        // Check if floor has rooms
        if (floor.getRooms() != null && !floor.getRooms().isEmpty()) {
            throw new IllegalStateException("Cannot delete floor that contains rooms. Remove all rooms first.");
        }

        floorRepository.deleteById(id);
    }

    public long countFloorsByBuilding(Integer buildingId) {
        return floorRepository.countByBuildingId(buildingId);
    }

    public boolean existsByFloorNumberAndBuilding(Integer floorNumber, Integer buildingId) {
        return floorRepository.existsByFloorNumberAndBuildingId(floorNumber, buildingId);
    }
}