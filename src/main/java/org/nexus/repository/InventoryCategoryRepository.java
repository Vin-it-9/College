package org.nexus.repository;

import org.nexus.entity.InventoryCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryCategoryRepository extends JpaRepository<InventoryCategory, Integer> {

    Optional<InventoryCategory> findByName(String name);

    List<InventoryCategory> findByParentCategoryId(Integer parentId);

    List<InventoryCategory> findByParentCategoryIsNull();

    boolean existsByName(String name);

    @Query("SELECT c FROM InventoryCategory c LEFT JOIN FETCH c.childCategories WHERE c.id = :id")
    Optional<InventoryCategory> findByIdWithChildren(Integer id);

    @Query("SELECT c FROM InventoryCategory c LEFT JOIN FETCH c.items WHERE c.id = :id")
    Optional<InventoryCategory> findByIdWithItems(Integer id);

    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.category.id = :categoryId")
    Long countItemsByCategory(Integer categoryId);

    @Query("SELECT c FROM InventoryCategory c WHERE SIZE(c.items) > 0 ORDER BY SIZE(c.items) DESC")
    List<InventoryCategory> findCategoriesWithMostItems();

    @Query("SELECT DISTINCT c FROM InventoryCategory c JOIN c.items i JOIN i.lab r WHERE r.id = :roomId")
    List<InventoryCategory> findCategoriesByRoomId(Integer roomId);

    @Query("SELECT DISTINCT c FROM InventoryCategory c JOIN c.items i JOIN i.lab r JOIN r.floor f WHERE f.building.id = :buildingId")
    List<InventoryCategory> findCategoriesByBuildingId(Integer buildingId);
}