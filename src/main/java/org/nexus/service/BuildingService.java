package org.nexus.service;

import org.nexus.entity.*;
import org.nexus.repository.BuildingRepository;
import org.nexus.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public BuildingService(BuildingRepository buildingRepository, DepartmentRepository departmentRepository) {
        this.buildingRepository = buildingRepository;
        this.departmentRepository = departmentRepository;
    }

    public List<Building> findAllBuildings() {
        return buildingRepository.findAll();
    }

    public Page<Building> findAllBuildings(Pageable pageable) {
        return buildingRepository.findAll(pageable);
    }

    public Optional<Building> findBuildingById(Integer id) {
        return buildingRepository.findById(id);
    }

    public Optional<Building> findBuildingByCode(String buildingCode) {
        return buildingRepository.findByBuildingCode(buildingCode);
    }

    public List<Building> findBuildingsByDepartment(Integer departmentId) {
        return buildingRepository.findByDepartmentId(departmentId);
    }

    @Transactional
    public Building saveBuilding(Building building) {
        return buildingRepository.save(building);
    }

    @Transactional
    public Building createBuilding(Building building) {
        // Generate floors automatically based on the number of floors specified
        if (building.getNumberOfFloors() > 0 && (building.getFloors() == null || building.getFloors().isEmpty())) {
            Set<Floor> floors = new HashSet<>();
            for (int i = 1; i <= building.getNumberOfFloors(); i++) {
                Floor floor = new Floor();
                floor.setFloorNumber(i);
                floor.setBuilding(building);
                floors.add(floor);
            }
            building.setFloors(floors);
        }

        return buildingRepository.save(building);

    }

    @Transactional
    public Building updateBuilding(Integer id, Building buildingDetails) {
        return buildingRepository.findById(id)
            .map(existingBuilding -> {
                existingBuilding.setBuildingCode(buildingDetails.getBuildingCode());
                existingBuilding.setBuildingName(buildingDetails.getBuildingName());
                existingBuilding.setNumberOfFloors(buildingDetails.getNumberOfFloors());

                // Update department if provided
                if (buildingDetails.getDepartment() != null) {
                    existingBuilding.setDepartment(buildingDetails.getDepartment());
                }

                return buildingRepository.save(existingBuilding);
            })
            .orElseThrow(() -> new RuntimeException("Building not found with id: " + id));
    }

    @Transactional
    public void deleteBuilding(Integer id) {
        buildingRepository.deleteById(id);
    }

    @Transactional
    public Building assignDepartmentToBuilding(Integer buildingId, Integer departmentId) {
        Building building = buildingRepository.findById(buildingId)
            .orElseThrow(() -> new RuntimeException("Building not found with id: " + buildingId));

        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new RuntimeException("Department not found with id: " + departmentId));

        building.setDepartment(department);
        return buildingRepository.save(building);
    }

    public boolean existsByBuildingCode(String buildingCode) {
        return buildingRepository.existsByBuildingCode(buildingCode);
    }

    public long countBuildings() {
        return buildingRepository.count();
    }

    public long countBuildingsByDepartment(Integer departmentId) {
        return buildingRepository.countByDepartmentId(departmentId);
    }

}