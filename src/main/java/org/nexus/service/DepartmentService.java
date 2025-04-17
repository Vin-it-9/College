package org.nexus.service;

import org.nexus.entity.Department;
import org.nexus.entity.User;
import org.nexus.repository.DepartmentRepository;
import org.nexus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;


    @Autowired
    public DepartmentService(DepartmentRepository departmentRepository, UserRepository userRepository) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    public List<Department> findAllDepartments() {
        return departmentRepository.findAll();
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

        Department existing = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));

        String newCode = departmentDetails.getDepartmentCode();
        String newName = departmentDetails.getDepartmentName();
        if (newCode == null || newCode.isBlank()) {
            throw new IllegalArgumentException("Department code cannot be null or empty");
        }
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Department name cannot be null or empty");
        }

        if (!existing.getDepartmentCode().equals(newCode)
                && departmentRepository.existsByDepartmentCode(newCode)) {
            throw new IllegalArgumentException("Department code " + newCode + " is already in use");
        }

        existing.setDepartmentCode(newCode);
        existing.setDepartmentName(newName);

        if (departmentDetails.getHod() == null || departmentDetails.getHod().getId() == null) {
            throw new IllegalArgumentException("Please select a valid HOD");
        }
        User hod = userRepository.findById(departmentDetails.getHod().getId())
                .orElseThrow(() -> new IllegalArgumentException("Selected HOD not found"));
        existing.setHod(hod);

        existing.setFacultyCount(
                departmentDetails.getFacultyCount() != null
                        ? departmentDetails.getFacultyCount()
                        : existing.getFacultyCount()
        );
        existing.setEstablishment(
                departmentDetails.getEstablishment() != null
                        ? departmentDetails.getEstablishment()
                        : existing.getEstablishment()
        );
        existing.setDepartmentDescription(departmentDetails.getDepartmentDescription());

        return departmentRepository.save(existing);
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