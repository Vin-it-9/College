package org.nexus.repository;

import org.nexus.entity.InventoryItemDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryItemDetailRepository extends JpaRepository<InventoryItemDetail, Integer> {

    List<InventoryItemDetail> findByInventoryItemId(Integer itemId);

    List<InventoryItemDetail> findByKeyName(String keyName);

    Optional<InventoryItemDetail> findByInventoryItemIdAndKeyName(Integer itemId, String keyName);

    @Query("SELECT d FROM InventoryItemDetail d WHERE d.keyName = :keyName AND d.value LIKE %:value%")
    List<InventoryItemDetail> findByKeyNameAndValueContaining(String keyName, String value);

    @Query("SELECT DISTINCT d.keyName FROM InventoryItemDetail d WHERE d.inventoryItem.category.id = :categoryId")
    List<String> findDistinctKeyNamesByCategory(Integer categoryId);

    @Query("SELECT d FROM InventoryItemDetail d WHERE d.inventoryItem.room.id = :roomId AND d.keyName = :keyName")
    List<InventoryItemDetail> findByRoomIdAndKeyName(Integer roomId, String keyName);

    @Query("SELECT COUNT(d) FROM InventoryItemDetail d WHERE d.inventoryItem.id = :itemId")
    Long countByInventoryItemId(Integer itemId);

    void deleteByInventoryItemId(Integer itemId);

    void deleteByInventoryItemIdAndKeyName(Integer itemId, String keyName);

    @Query(nativeQuery = true, value =
            "SELECT d.key_name, COUNT(DISTINCT d.inventory_item_id) as usage_count " +
                    "FROM inventory_item_details d " +
                    "GROUP BY d.key_name " +
                    "ORDER BY usage_count DESC")
    List<Object[]> findMostCommonAttributes();


}