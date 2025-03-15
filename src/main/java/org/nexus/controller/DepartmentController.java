package org.nexus.controller;

import org.nexus.entity.Department;
import org.nexus.service.DepartmentService;
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
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    @Autowired
    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments() {
        List<Department> departments = departmentService.findAllDepartments();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/paged")
    public ResponseEntity<Map<String, Object>> getAllDepartmentsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        // Validate page and size parameters
        if (page < 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Page number cannot be negative"));
        }
        if (size <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Page size must be greater than 0"));
        }

        // Validate and sanitize sort parameters
        List<String> allowedSortFields = List.of("id", "departmentCode", "departmentName");
        if (!allowedSortFields.contains(sortBy)) {
            sortBy = "id"; // Default to sorting by ID if invalid sort field
        }

        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ?
                Sort.Direction.DESC : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        Page<Department> departmentsPage = departmentService.findAllDepartments(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("departments", departmentsPage.getContent());
        response.put("currentPage", departmentsPage.getNumber());
        response.put("totalItems", departmentsPage.getTotalElements());
        response.put("totalPages", departmentsPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Integer id) {
        return departmentService.findDepartmentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<Department> getDepartmentByCode(@PathVariable String code) {
        return departmentService.findDepartmentByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Department>> searchDepartmentsByName(@RequestParam String name) {
        List<Department> departments = departmentService.findDepartmentsByName(name);
        return ResponseEntity.ok(departments);
    }

    @PostMapping
    public ResponseEntity<?> createDepartment(@RequestBody Department department) {
        try {
            // Basic validation
            if (department == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Department cannot be null"));
            }
            if (department.getDepartmentCode() == null || department.getDepartmentCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Department code is required"));
            }
            if (department.getDepartmentName() == null || department.getDepartmentName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Department name is required"));
            }

            Department createdDepartment = departmentService.createDepartment(department);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdDepartment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(
            @PathVariable Integer id,
            @RequestBody Department departmentDetails) {
        try {
            // Basic validation
            if (id == null || id <= 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid department ID"));
            }
            if (departmentDetails == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Department details cannot be null"));
            }
            if (departmentDetails.getDepartmentCode() == null || departmentDetails.getDepartmentCode().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Department code is required"));
            }
            if (departmentDetails.getDepartmentName() == null || departmentDetails.getDepartmentName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Department name is required"));
            }

            Department updatedDepartment = departmentService.updateDepartment(id, departmentDetails);
            return ResponseEntity.ok(updatedDepartment);
        } catch (RuntimeException e) {
            if (e instanceof IllegalArgumentException) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
            }
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An error occurred: " + e.getMessage()));
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable Integer id) {
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            if (e instanceof IllegalStateException) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("error", e.getMessage()));
            }
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getDepartmentCount() {
        Map<String, Long> countInfo = new HashMap<>();
        countInfo.put("totalDepartments", departmentService.countDepartments());
        return ResponseEntity.ok(countInfo);
    }

    @GetMapping("/exists/{code}")
    public ResponseEntity<Map<String, Boolean>> checkDepartmentExists(@PathVariable String code) {
        Map<String, Boolean> exists = new HashMap<>();
        exists.put("exists", departmentService.existsByDepartmentCode(code));
        return ResponseEntity.ok(exists);
    }



}