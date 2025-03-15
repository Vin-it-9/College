package org.nexus.controller;

import org.nexus.entity.InventoryCategory;
import org.nexus.entity.InventoryItem;
import org.nexus.entity.InventoryStatus;
import org.nexus.entity.Room;
import org.nexus.service.InventoryItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventory/items")
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;

    @Autowired
    public InventoryItemController(InventoryItemService inventoryItemService) {
        this.inventoryItemService = inventoryItemService;
    }

    @GetMapping
    public ResponseEntity<List<InventoryItem>> getAllItems() {
        return ResponseEntity.ok(inventoryItemService.findAllItems());
    }

    @GetMapping("/paged")
    public ResponseEntity<Page<InventoryItem>> getPagedItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return ResponseEntity.ok(inventoryItemService.findAllItems(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItem> getItemById(@PathVariable Integer id) {
        return inventoryItemService.findItemById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory item not found with id: " + id));
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<InventoryItem>> getItemsByRoom(@PathVariable Integer roomId) {
        return ResponseEntity.ok(inventoryItemService.findItemsByRoom(roomId));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<InventoryItem>> getItemsByCategory(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(inventoryItemService.findItemsByCategory(categoryId));
    }

    @GetMapping("/floor/{floorId}")
    public ResponseEntity<List<InventoryItem>> getItemsByFloor(@PathVariable Integer floorId) {
        return ResponseEntity.ok(inventoryItemService.findItemsByFloor(floorId));
    }

    @GetMapping("/building/{buildingId}")
    public ResponseEntity<List<InventoryItem>> getItemsByBuilding(@PathVariable Integer buildingId) {
        return ResponseEntity.ok(inventoryItemService.findItemsByBuilding(buildingId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<InventoryItem>> getItemsByStatus(@PathVariable InventoryStatus status) {
        return ResponseEntity.ok(inventoryItemService.findItemsByStatus(status));
    }

    @GetMapping("/search")
    public ResponseEntity<List<InventoryItem>> searchItems(@RequestParam String name) {
        return ResponseEntity.ok(inventoryItemService.findItemsByName(name));
    }

    @GetMapping("/serial/{serialNumber}")
    public ResponseEntity<InventoryItem> getItemBySerialNumber(@PathVariable String serialNumber) {
        return inventoryItemService.findItemBySerialNumber(serialNumber)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found with serial number: " + serialNumber));
    }

    @GetMapping("/attribute")
    public ResponseEntity<List<InventoryItem>> getItemsByDetailKeyAndValue(
            @RequestParam String key,
            @RequestParam String value) {
        return ResponseEntity.ok(inventoryItemService.findItemsByDetailKeyAndValue(key, value));
    }

    @GetMapping("/warranty-expiry")
    public ResponseEntity<List<InventoryItem>> getItemsWithWarrantyExpiring(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(inventoryItemService.findItemsWithWarrantyExpiringBetween(startDate, endDate));
    }

    @GetMapping("/room/{roomId}/value")
    public ResponseEntity<Map<String, Object>> getTotalValueByRoom(@PathVariable Integer roomId) {
        Double value = inventoryItemService.calculateTotalValueByRoom(roomId);
        Map<String, Object> result = new HashMap<>();
        result.put("roomId", roomId);
        result.put("totalValue", value);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/room/{roomId}/count")
    public ResponseEntity<Map<String, Object>> getItemCountByRoom(@PathVariable Integer roomId) {
        Long count = inventoryItemService.countItemsByRoom(roomId);
        Map<String, Object> result = new HashMap<>();
        result.put("roomId", roomId);
        result.put("itemCount", count);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<InventoryItem> createItem(
            @RequestBody Map<String, Object> payload) {
        try {
            // Extract item properties
            InventoryItem item = new InventoryItem();

            if (payload.containsKey("name")) {
                item.setName((String) payload.get("name"));
            }

            if (payload.containsKey("serialNumber")) {
                item.setSerialNumber((String) payload.get("serialNumber"));
            }

            if (payload.containsKey("quantity")) {
                if (payload.get("quantity") instanceof Integer) {
                    item.setQuantity((Integer) payload.get("quantity"));
                } else {
                    item.setQuantity(Integer.valueOf(payload.get("quantity").toString()));
                }
            }

            if (payload.containsKey("unitCost")) {
                if (payload.get("unitCost") instanceof Double) {
                    item.setUnitCost((Double) payload.get("unitCost"));
                } else {
                    item.setUnitCost(Double.valueOf(payload.get("unitCost").toString()));
                }
            }

            if (payload.containsKey("purchaseDate")) {
                String dateStr = (String) payload.get("purchaseDate");
                item.setPurchaseDate(LocalDate.parse(dateStr));
            }

            if (payload.containsKey("warrantyExpiryDate")) {
                String dateStr = (String) payload.get("warrantyExpiryDate");
                item.setWarrantyExpiryDate(LocalDate.parse(dateStr));
            }

            if (payload.containsKey("status")) {
                String statusStr = (String) payload.get("status");
                item.setStatus(InventoryStatus.valueOf(statusStr));
            } else {
                item.setStatus(InventoryStatus.AVAILABLE);
            }

            // Set category
            if (payload.containsKey("categoryId")) {
                InventoryCategory category = new InventoryCategory();
                Integer categoryId;
                if (payload.get("categoryId") instanceof Integer) {
                    categoryId = (Integer) payload.get("categoryId");
                } else {
                    categoryId = Integer.valueOf(payload.get("categoryId").toString());
                }
                category.setId(categoryId);
                item.setCategory(category);
            }

            // Set room
            if (payload.containsKey("roomId")) {
                Room room = new Room();
                Integer roomId;
                if (payload.get("roomId") instanceof Integer) {
                    roomId = (Integer) payload.get("roomId");
                } else {
                    roomId = Integer.valueOf(payload.get("roomId").toString());
                }
                room.setId(roomId);
                item.setRoom(room);
            }

            // Extract details
            Map<String, String> details = new HashMap<>();
            if (payload.containsKey("details") && payload.get("details") instanceof Map) {
                Map<String, Object> detailsMap = (Map<String, Object>) payload.get("details");
                for (Map.Entry<String, Object> entry : detailsMap.entrySet()) {
                    if (entry.getValue() != null) {
                        details.put(entry.getKey(), entry.getValue().toString());
                    }
                }
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(inventoryItemService.createItem(item, details));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryItem> updateItem(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> payload) {
        try {
            // Extract item properties
            InventoryItem item = new InventoryItem();
            item.setId(id);

            if (payload.containsKey("name")) {
                item.setName((String) payload.get("name"));
            }

            if (payload.containsKey("serialNumber")) {
                item.setSerialNumber((String) payload.get("serialNumber"));
            }

            if (payload.containsKey("quantity")) {
                if (payload.get("quantity") instanceof Integer) {
                    item.setQuantity((Integer) payload.get("quantity"));
                } else {
                    item.setQuantity(Integer.valueOf(payload.get("quantity").toString()));
                }
            }

            if (payload.containsKey("unitCost")) {
                if (payload.get("unitCost") instanceof Double) {
                    item.setUnitCost((Double) payload.get("unitCost"));
                } else {
                    item.setUnitCost(Double.valueOf(payload.get("unitCost").toString()));
                }
            }

            if (payload.containsKey("purchaseDate")) {
                String dateStr = (String) payload.get("purchaseDate");
                item.setPurchaseDate(LocalDate.parse(dateStr));
            }

            if (payload.containsKey("warrantyExpiryDate")) {
                String dateStr = (String) payload.get("warrantyExpiryDate");
                item.setWarrantyExpiryDate(LocalDate.parse(dateStr));
            }

            if (payload.containsKey("status")) {
                String statusStr = (String) payload.get("status");
                item.setStatus(InventoryStatus.valueOf(statusStr));
            }

            // Set category
            if (payload.containsKey("categoryId")) {
                InventoryCategory category = new InventoryCategory();
                Integer categoryId;
                if (payload.get("categoryId") instanceof Integer) {
                    categoryId = (Integer) payload.get("categoryId");
                } else {
                    categoryId = Integer.valueOf(payload.get("categoryId").toString());
                }
                category.setId(categoryId);
                item.setCategory(category);
            }

            // Set room
            if (payload.containsKey("roomId")) {
                Room room = new Room();
                Integer roomId;
                if (payload.get("roomId") instanceof Integer) {
                    roomId = (Integer) payload.get("roomId");
                } else {
                    roomId = Integer.valueOf(payload.get("roomId").toString());
                }
                room.setId(roomId);
                item.setRoom(room);
            }

            // Extract details
            Map<String, String> details = null;
            if (payload.containsKey("details") && payload.get("details") instanceof Map) {
                details = new HashMap<>();
                Map<String, Object> detailsMap = (Map<String, Object>) payload.get("details");
                for (Map.Entry<String, Object> entry : detailsMap.entrySet()) {
                    if (entry.getValue() != null) {
                        details.put(entry.getKey(), entry.getValue().toString());
                    } else {
                        details.put(entry.getKey(), null);
                    }
                }
            }

            return ResponseEntity.ok(inventoryItemService.updateItem(id, item, details));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Integer id) {
        try {
            inventoryItemService.deleteItem(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> transferItems(
            @RequestParam Integer fromRoomId,
            @RequestParam Integer toRoomId,
            @RequestBody List<Integer> itemIds) {
        try {
            inventoryItemService.transferItems(fromRoomId, toRoomId, itemIds);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<InventoryItem> updateItemStatus(
            @PathVariable Integer id,
            @RequestParam InventoryStatus status) {
        try {
            InventoryItem item = new InventoryItem();
            item.setId(id);
            item.setStatus(status);
            return ResponseEntity.ok(inventoryItemService.updateItem(id, item, null));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/attributes/most-common")
    public ResponseEntity<Map<String, Long>> getMostCommonAttributes() {
        return ResponseEntity.ok(inventoryItemService.getMostCommonAttributes());
    }
}