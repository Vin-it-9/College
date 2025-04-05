package org.nexus.repository;

import org.nexus.entity.Department;
import org.nexus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    Optional<Department> findByDepartmentCode(String departmentCode);

    boolean existsByDepartmentCode(String departmentCode);

    List<Department> findByDepartmentNameContainingIgnoreCase(String name);

    Optional<Department> findByHod(User hod);


}