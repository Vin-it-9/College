package org.nexus.repository;

import org.nexus.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Integer> {

    Optional<Building> findByBuildingCode(String buildingCode);

    List<Building> findByDepartmentId(Integer departmentId);

    boolean existsByBuildingCode(String buildingCode);

    long countByDepartmentId(Integer departmentId);


}