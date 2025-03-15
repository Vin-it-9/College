package org.nexus.service;

import org.nexus.entity.Department;
import org.nexus.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public List<Department> findAllDepartments() {
        return departmentRepository.findAll();
    }

    public Page<Department> findAllDepartments(Pageable pageable) {
        return departmentRepository.findAll(pageable);
    }

    public Optional<Department> findDepartmentById(Integer id) {
        return departmentRepository.findById(id);
    }

    public Optional<Department> findDepartmentByCode(String departmentCode) {
        return departmentRepository.findByDepartmentCode(departmentCode);
    }

    public List<Department> findDepartmentsByName(String name) {
        return departmentRepository.findByDepartmentNameContainingIgnoreCase(name);
    }

    @Transactional
    public Department saveDepartment(Department department) {
        // Validate the department before saving
        if (department.getDepartmentCode() == null || department.getDepartmentCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Department code cannot be null or empty");
        }
        if (department.getDepartmentName() == null || department.getDepartmentName().trim().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be null or empty");
        }
        return departmentRepository.save(department);
    }

    @Transactional
    public Department createDepartment(Department department) {
        // Validate the department before creating
        if (department.getDepartmentCode() == null || department.getDepartmentCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Department code cannot be null or empty");
        }
        if (department.getDepartmentName() == null || department.getDepartmentName().trim().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be null or empty");
        }
        if (departmentRepository.existsByDepartmentCode(department.getDepartmentCode())) {
            throw new IllegalArgumentException("Department with code " + department.getDepartmentCode() + " already exists");
        }
        return departmentRepository.save(department);
    }

    @Transactional
    public Department updateDepartment(Integer id, Department departmentDetails) {
        if (id == null) {
            throw new IllegalArgumentException("Department ID cannot be null");
        }

        return departmentRepository.findById(id)
                .map(existingDepartment -> {
                    // Validate the inputs
                    if (departmentDetails.getDepartmentCode() == null || departmentDetails.getDepartmentCode().trim().isEmpty()) {
                        throw new IllegalArgumentException("Department code cannot be null or empty");
                    }
                    if (departmentDetails.getDepartmentName() == null || departmentDetails.getDepartmentName().trim().isEmpty()) {
                        throw new IllegalArgumentException("Department name cannot be null or empty");
                    }

                    // If department code is being changed, check for duplicates
                    if (!existingDepartment.getDepartmentCode().equals(departmentDetails.getDepartmentCode()) &&
                            departmentRepository.existsByDepartmentCode(departmentDetails.getDepartmentCode())) {
                        throw new IllegalArgumentException("Department code " + departmentDetails.getDepartmentCode() + " is already in use");
                    }

                    existingDepartment.setDepartmentCode(departmentDetails.getDepartmentCode());
                    existingDepartment.setDepartmentName(departmentDetails.getDepartmentName());

                    return departmentRepository.save(existingDepartment);
                })
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
    }

    @Transactional
    public void deleteDepartment(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Department ID cannot be null");
        }

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        // Check if buildings are associated with this department
        if (department.getBuildings() != null && !department.getBuildings().isEmpty()) {
            throw new IllegalStateException("Cannot delete department that has buildings assigned to it");
        }

        departmentRepository.deleteById(id);
    }

    public boolean existsByDepartmentCode(String departmentCode) {
        if (departmentCode == null) {
            return false;
        }
        return departmentRepository.existsByDepartmentCode(departmentCode);
    }

    public long countDepartments() {
        return departmentRepository.count();
    }
}