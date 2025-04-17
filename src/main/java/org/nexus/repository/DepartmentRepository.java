package org.nexus.repository;

import org.nexus.entity.Department;
import org.nexus.entity.User;
import org.nexus.entity.transferDTO.DepartmentInventorySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    Optional<Department> findByDepartmentCode(String departmentCode);

    boolean existsByDepartmentCode(String departmentCode);

    List<Department> findByDepartmentNameContainingIgnoreCase(String name);

    Optional<Department> findByHod(User hod);

    List<Department> findByHodId(Integer hodId);

    @Query("SELECT new org.nexus.entity.transferDTO.DepartmentInventorySummary(d.departmentCode, d.departmentName, COUNT(i)) " +
            "FROM Department d " +
            "LEFT JOIN d.labs l " +
            "LEFT JOIN l.inventoryItems i " +
            "GROUP BY d.departmentCode, d.departmentName " +
            "ORDER BY d.departmentName")
    List<DepartmentInventorySummary> getDepartmentsWithInventoryCounts();


}