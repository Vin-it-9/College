package org.nexus.controller;

import org.nexus.entity.InventoryCategory;
import org.nexus.service.InventoryCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

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


    @GetMapping("/{id}")
    public ResponseEntity<InventoryCategory> getCategoryById(@PathVariable Integer id) {
        return inventoryCategoryService.findCategoryById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + id));
    }


    @GetMapping("/{id}/with-items")
    public ResponseEntity<InventoryCategory> getCategoryWithItems(@PathVariable Integer id) {
        return inventoryCategoryService.findCategoryByIdWithItems(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found with id: " + id));
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


}