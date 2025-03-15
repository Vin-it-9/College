package org.nexus.controller;

import org.nexus.entity.Building;
import org.nexus.service.BuildingService;
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
@RequestMapping("/api/buildings")
public class BuildingController {

    private final BuildingService buildingService;

    @Autowired
    public BuildingController(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    @GetMapping
    public ResponseEntity<List<Building>> getAllBuildings() {
        List<Building> buildings = buildingService.findAllBuildings();
        return ResponseEntity.ok(buildings);
    }

    @GetMapping("/paged")
    public ResponseEntity<Map<String, Object>> getAllBuildingsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Building> buildingsPage = buildingService.findAllBuildings(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("buildings", buildingsPage.getContent());
        response.put("currentPage", buildingsPage.getNumber());
        response.put("totalItems", buildingsPage.getTotalElements());
        response.put("totalPages", buildingsPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Building> getBuildingById(@PathVariable Integer id) {
        return buildingService.findBuildingById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Building> getBuildingByCode(@PathVariable String code) {
        return buildingService.findBuildingByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<Building>> getBuildingsByDepartment(@PathVariable Integer departmentId) {
        List<Building> buildings = buildingService.findBuildingsByDepartment(departmentId);
        return ResponseEntity.ok(buildings);
    }

    @PostMapping
    public ResponseEntity<?> createBuilding(@RequestBody Building building) {
        try {
            if (buildingService.existsByBuildingCode(building.getBuildingCode())) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", "Building code already exists"));
            }
            Building createdBuilding = buildingService.createBuilding(building);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdBuilding);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBuilding(
            @PathVariable Integer id,
            @RequestBody Building buildingDetails) {
        try {
            Building updatedBuilding = buildingService.updateBuilding(id, buildingDetails);
            return ResponseEntity.ok(updatedBuilding);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Building not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBuilding(@PathVariable Integer id) {
        try {
            if (buildingService.findBuildingById(id).isPresent()) {
                buildingService.deleteBuilding(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{buildingId}/department/{departmentId}")
    public ResponseEntity<?> assignDepartmentToBuilding(
            @PathVariable Integer buildingId,
            @PathVariable Integer departmentId) {
        try {
            Building updatedBuilding = buildingService.assignDepartmentToBuilding(buildingId, departmentId);
            return ResponseEntity.ok(updatedBuilding);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getBuildingCount() {
        Map<String, Long> countInfo = new HashMap<>();
        countInfo.put("totalBuildings", buildingService.countBuildings());
        return ResponseEntity.ok(countInfo);
    }

    @GetMapping("/count/department/{departmentId}")
    public ResponseEntity<Map<String, Long>> getBuildingCountByDepartment(@PathVariable Integer departmentId) {
        Map<String, Long> countInfo = new HashMap<>();
        countInfo.put("buildingCount", buildingService.countBuildingsByDepartment(departmentId));
        return ResponseEntity.ok(countInfo);
    }

}