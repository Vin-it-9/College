package org.nexus.service;

import org.nexus.entity.*;
import org.nexus.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryCategoryService {

    private final InventoryCategoryRepository inventoryCategoryRepository;
    private final InventoryItemRepository inventoryItemRepository;

    @Autowired
    public InventoryCategoryService(InventoryCategoryRepository inventoryCategoryRepository,
                                    InventoryItemRepository inventoryItemRepository) {
        this.inventoryCategoryRepository = inventoryCategoryRepository;
        this.inventoryItemRepository = inventoryItemRepository;
    }

    public List<InventoryCategory> findAllCategories() {
        return inventoryCategoryRepository.findAll();
    }

    public Page<InventoryCategory> findAllCategories(Pageable pageable) {
        return inventoryCategoryRepository.findAll(pageable);
    }

    public Optional<InventoryCategory> findCategoryById(Integer id) {
        return inventoryCategoryRepository.findById(id);
    }



    public Optional<InventoryCategory> findCategoryByIdWithItems(Integer id) {
        return inventoryCategoryRepository.findByIdWithItems(id);
    }

    public Optional<InventoryCategory> findCategoryByName(String name) {
        return inventoryCategoryRepository.findByName(name);
    }


    public List<InventoryCategory> findCategoriesWithMostItems() {
        return inventoryCategoryRepository.findCategoriesWithMostItems();
    }

    public List<InventoryCategory> findCategoriesByRoom(Integer roomId) {
        return inventoryCategoryRepository.findCategoriesByRoomId(roomId);
    }

    public List<InventoryCategory> findCategoriesByBuilding(Integer buildingId) {
        return inventoryCategoryRepository.findCategoriesByBuildingId(buildingId);
    }

    public Boolean existsByName(String name) {
        return inventoryCategoryRepository.existsByName(name);
    }

    @Transactional
    public InventoryCategory createCategory(InventoryCategory category) {
        validateCategory(category, null);
        return inventoryCategoryRepository.save(category);
    }

    @Transactional
    public InventoryCategory updateCategory(Integer id, InventoryCategory categoryDetails) {
        InventoryCategory existingCategory = inventoryCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        validateCategory(categoryDetails, id);

        // Update fields
        if (categoryDetails.getName() != null && !categoryDetails.getName().isEmpty()) {
            existingCategory.setName(categoryDetails.getName());
        }

        if (categoryDetails.getDescription() != null) {
            existingCategory.setDescription(categoryDetails.getDescription());
        }



        return inventoryCategoryRepository.save(existingCategory);
    }

    @Transactional
    public void deleteCategory(Integer id) {
        InventoryCategory category = inventoryCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + id));

        // Check if category has items
        Long itemCount = inventoryCategoryRepository.countItemsByCategory(id);
        if (itemCount > 0) {
            throw new IllegalStateException("Cannot delete category that has items. Move or delete items first.");
        }

        inventoryCategoryRepository.deleteById(id);
    }

    @Transactional
    public void moveItemsToCategory(Integer sourceCategoryId, Integer targetCategoryId) {
        if (sourceCategoryId.equals(targetCategoryId)) {
            throw new IllegalArgumentException("Source and target categories cannot be the same");
        }

        // Verify both categories exist
        InventoryCategory sourceCategory = inventoryCategoryRepository.findById(sourceCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("Source category not found with id: " + sourceCategoryId));

        InventoryCategory targetCategory = inventoryCategoryRepository.findById(targetCategoryId)
                .orElseThrow(() -> new IllegalArgumentException("Target category not found with id: " + targetCategoryId));

        // Get items in the source category
        List<InventoryItem> items = inventoryItemRepository.findByCategoryId(sourceCategoryId);

        if (items.isEmpty()) {
            return; // No items to move
        }

        // Update each item's category
        for (InventoryItem item : items) {
            item.setCategory(targetCategory);
        }

        inventoryItemRepository.saveAll(items);
    }


    public Long countItemsByCategory(Integer categoryId) {
        return inventoryCategoryRepository.countItemsByCategory(categoryId);
    }

    // Helper methods
    private void validateCategory(InventoryCategory category, Integer categoryId) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }

        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Category name cannot be empty");
        }

        // Check name uniqueness
        Optional<InventoryCategory> existingCategory = inventoryCategoryRepository.findByName(category.getName());
        if (existingCategory.isPresent() && (categoryId == null || !existingCategory.get().getId().equals(categoryId))) {
            throw new IllegalArgumentException("Category name already exists: " + category.getName());
        }

    }

    private void checkCircularReference(Integer parentId, Integer currentId) {
        InventoryCategory parentCategory = inventoryCategoryRepository.findById(parentId).orElse(null);
        if (parentCategory == null) {
            return; // Parent not found, no need to check further
        }

    }

    @Transactional
    public InventoryCategory createCategoryWithParent(String name, String description, Integer parentId) {
        InventoryCategory category = new InventoryCategory();
        category.setName(name);
        category.setDescription(description);

        return createCategory(category);

    }

    @Transactional
    public InventoryCategory moveCategory(Integer categoryId, Integer newParentId) {
        InventoryCategory category = inventoryCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + categoryId));

        if (newParentId == null) {
            // Make it a root category
        } else {
            // Can't move to itself
            if (newParentId.equals(categoryId)) {
                throw new IllegalArgumentException("Category cannot be moved to itself");
            }

            // Check for circular reference
            checkCircularReference(newParentId, categoryId);

        }

        return inventoryCategoryRepository.save(category);
    }

    public boolean hasItems(Integer categoryId) {
        Long itemCount = inventoryCategoryRepository.countItemsByCategory(categoryId);
        return itemCount > 0;
    }
}