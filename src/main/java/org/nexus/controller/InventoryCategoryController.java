package org.nexus.controller;

import org.nexus.entity.InventoryCategory;
import org.nexus.service.InventoryCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/inventory/categories")
public class InventoryCategoryController {

    private final InventoryCategoryService inventoryCategoryService;

    @Autowired
    public InventoryCategoryController(InventoryCategoryService inventoryCategoryService) {
        this.inventoryCategoryService = inventoryCategoryService;
    }

    @GetMapping
    public ResponseEntity<List<InventoryCategory>> getAllCategories() {
        return ResponseEntity.ok(inventoryCategoryService.findAllCategories());
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<InventoryCategory>> getPagedCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(inventoryCategoryService.findAllCategories(pageable));
    }

    @GetMapping("/root")
    public ResponseEntity<List<InventoryCategory>> getRootCategories() {
        return ResponseEntity.ok(inventoryCategoryService.findRootCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryCategory> getCategoryById(@PathVariable Integer id) {
        return inventoryCategoryService.findCategoryById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + id));
    }

    @GetMapping("/{id}/with-children")
    public ResponseEntity<InventoryCategory> getCategoryWithChildren(@PathVariable Integer id) {
        return inventoryCategoryService.findCategoryByIdWithChildren(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + id));
    }

    @GetMapping("/{id}/with-items")
    public ResponseEntity<InventoryCategory> getCategoryWithItems(@PathVariable Integer id) {
        return inventoryCategoryService.findCategoryByIdWithItems(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + id));
    }

    @GetMapping("/subcategories/{parentId}")
    public ResponseEntity<List<InventoryCategory>> getSubcategories(@PathVariable Integer parentId) {
        return ResponseEntity.ok(inventoryCategoryService.findSubcategories(parentId));
    }

    @GetMapping("/by-name/{name}")
    public ResponseEntity<InventoryCategory> getCategoryByName(@PathVariable String name) {
        return inventoryCategoryService.findCategoryByName(name)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with name: " + name));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<InventoryCategory>> getCategoriesByRoom(@PathVariable Integer roomId) {
        return ResponseEntity.ok(inventoryCategoryService.findCategoriesByRoom(roomId));
    }

    @GetMapping("/building/{buildingId}")
    public ResponseEntity<List<InventoryCategory>> getCategoriesByBuilding(@PathVariable Integer buildingId) {
        return ResponseEntity.ok(inventoryCategoryService.findCategoriesByBuilding(buildingId));
    }

    @GetMapping("/most-items")
    public ResponseEntity<List<InventoryCategory>> getCategoriesWithMostItems() {
        return ResponseEntity.ok(inventoryCategoryService.findCategoriesWithMostItems());
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> getCategoryStatus(@PathVariable Integer id) {
        if (!inventoryCategoryService.findCategoryById(id).isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + id);
        }

        Map<String, Object> status = new HashMap<>();
        status.put("categoryId", id);
        status.put("hasSubcategories", inventoryCategoryService.hasSubcategories(id));
        status.put("hasItems", inventoryCategoryService.hasItems(id));
        status.put("itemCount", inventoryCategoryService.countItemsByCategory(id));

        return ResponseEntity.ok(status);
    }

    @PostMapping
    public ResponseEntity<InventoryCategory> createCategory(@RequestBody InventoryCategory category) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(inventoryCategoryService.createCategory(category));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/with-parent")
    public ResponseEntity<InventoryCategory> createCategoryWithParent(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer parentId) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(inventoryCategoryService.createCategoryWithParent(name, description, parentId));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryCategory> updateCategory(
            @PathVariable Integer id,
            @RequestBody InventoryCategory category) {
        try {
            return ResponseEntity.ok(inventoryCategoryService.updateCategory(id, category));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}/move")
    public ResponseEntity<InventoryCategory> moveCategory(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer newParentId) {
        try {
            return ResponseEntity.ok(inventoryCategoryService.moveCategory(id, newParentId));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        try {
            inventoryCategoryService.deleteCategory(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{sourceId}/move-items/{targetId}")
    public ResponseEntity<Void> moveItemsToCategory(
            @PathVariable Integer sourceId,
            @PathVariable Integer targetId) {
        try {
            inventoryCategoryService.moveItemsToCategory(sourceId, targetId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/{id}/move-subcategories-to-parent")
    public ResponseEntity<Void> moveSubcategoriesToParent(@PathVariable Integer id) {
        try {
            inventoryCategoryService.moveSubcategoriesToParent(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}