package org.nexus.repository;

import org.nexus.entity.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Integer> {

    @Query("SELECT i FROM InventoryItem i ORDER BY i.quantity DESC")
    List<InventoryItem> findTopItemsByQuantity(Pageable pageable);

    List<InventoryItem> findByCategoryId(Integer categoryId);

    List<InventoryItem> findByStatus(InventoryStatus status);

    Optional<InventoryItem> findBySerialNumber(String serialNumber);

    List<InventoryItem> findByApprovedFalse();

    @Query("SELECT DISTINCT i FROM InventoryItem i " +
            "LEFT JOIN i.category c " +
            "LEFT JOIN i.lab r " +
            "LEFT JOIN i.classroom cl " +
            "WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(i.serialNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR (r IS NOT NULL AND LOWER(r.labName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "OR (cl IS NOT NULL AND LOWER(cl.classroomNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "OR (cl IS NOT NULL AND LOWER(cl.classroomName) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<InventoryItem> searchItemsByAnyField(@Param("searchTerm") String searchTerm);

    List<InventoryItem> findByNameContainingIgnoreCase(String name);

    @Query("SELECT i FROM InventoryItem i WHERE i.lab.floor.id = :floorId")
    List<InventoryItem> findByFloorId(Integer floorId);

    @Query("SELECT i FROM InventoryItem i WHERE i.lab.floor.building.id = :buildingId")
    List<InventoryItem> findByBuildingId(Integer buildingId);

    @Query("SELECT i FROM InventoryItem i LEFT JOIN FETCH i.details WHERE i.id = :id")
    Optional<InventoryItem> findByIdWithDetails(Integer id);

    @Query("SELECT i FROM InventoryItem i WHERE i.warrantyExpiryDate BETWEEN :startDate AND :endDate")
    List<InventoryItem> findByWarrantyExpiringBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(i.unitCost * i.quantity) FROM InventoryItem i WHERE i.lab.id = :roomId")
    Double calculateTotalValueByRoom(Integer roomId);

    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.lab.id = :roomId")
    Long countByRoomId(Integer roomId);

    @Query("SELECT i FROM InventoryItem i WHERE EXISTS (SELECT d FROM InventoryItemDetail d WHERE d.inventoryItem = i AND d.keyName = :key AND d.value = :value)")
    List<InventoryItem> findByItemDetailKeyAndValue(String key, String value);

    List<InventoryItem> lab(Lab lab);

    @Query("SELECT i FROM InventoryItem i WHERE i.lab.id = :labId")
    List<InventoryItem> findByLabId(Integer labId);
    
    @Query("SELECT i FROM InventoryItem i WHERE i.approved = false AND i.lab.id IN :labIds")
    List<InventoryItem> findByApprovedFalseAndLabIdIn(Collection<Integer> labIds);

    @Query("SELECT i FROM InventoryItem i WHERE i.approved = false AND i.lab.department.id = :departmentId")
    List<InventoryItem> findByApprovedFalseAndLabDepartmentId(Integer departmentId);

    List<InventoryItem> findByLabDepartmentId(Integer departmentId);

    List<InventoryItem> findByLabIsNotNull();
    List<InventoryItem> findByClassroomIsNotNull();
    List<InventoryItem> findByClassroomId(Integer classroomId);


}