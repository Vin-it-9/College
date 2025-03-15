package org.nexus.repository;

import org.nexus.entity.InventoryItem;
import org.nexus.entity.InventoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Integer> {

    List<InventoryItem> findByRoomId(Integer roomId);

    List<InventoryItem> findByCategoryId(Integer categoryId);

    List<InventoryItem> findByStatus(InventoryStatus status);

    Optional<InventoryItem> findBySerialNumber(String serialNumber);

    List<InventoryItem> findByNameContainingIgnoreCase(String name);

    @Query("SELECT i FROM InventoryItem i WHERE i.room.floor.id = :floorId")
    List<InventoryItem> findByFloorId(Integer floorId);

    @Query("SELECT i FROM InventoryItem i WHERE i.room.floor.building.id = :buildingId")
    List<InventoryItem> findByBuildingId(Integer buildingId);

    @Query("SELECT i FROM InventoryItem i LEFT JOIN FETCH i.details WHERE i.id = :id")
    Optional<InventoryItem> findByIdWithDetails(Integer id);

    @Query("SELECT i FROM InventoryItem i WHERE i.warrantyExpiryDate BETWEEN :startDate AND :endDate")
    List<InventoryItem> findByWarrantyExpiringBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(i.unitCost * i.quantity) FROM InventoryItem i WHERE i.room.id = :roomId")
    Double calculateTotalValueByRoom(Integer roomId);

    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.room.id = :roomId")
    Long countByRoomId(Integer roomId);

    @Query("SELECT i FROM InventoryItem i WHERE EXISTS (SELECT d FROM InventoryItemDetail d WHERE d.inventoryItem = i AND d.keyName = :key AND d.value = :value)")
    List<InventoryItem> findByItemDetailKeyAndValue(String key, String value);

    @Query("SELECT DISTINCT i.room FROM InventoryItem i WHERE i.category.id = :categoryId")
    List<Object[]> findRoomsByCategory(Integer categoryId);

    @Query(nativeQuery = true, value =
            "SELECT COUNT(i.id) as item_count, r.room_number, r.room_name, r.id as room_id " +
                    "FROM inventory_items i " +
                    "JOIN rooms r ON i.room_id = r.id " +
                    "GROUP BY r.id " +
                    "ORDER BY item_count DESC")
    List<Object[]> findRoomsWithMostInventoryItems();
}