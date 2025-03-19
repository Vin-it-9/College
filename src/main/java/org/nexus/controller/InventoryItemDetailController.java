//package org.nexus.controller;
//
//import org.nexus.entity.InventoryItemDetail;
//import org.nexus.service.InventoryItemDetailService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/inventory/details")
//public class InventoryItemDetailController {
//
//    private final InventoryItemDetailService inventoryItemDetailService;
//
//    @Autowired
//    public InventoryItemDetailController(InventoryItemDetailService inventoryItemDetailService) {
//        this.inventoryItemDetailService = inventoryItemDetailService;
//    }
//
//    @GetMapping("/item/{itemId}")
//    public ResponseEntity<List<InventoryItemDetail>> getDetailsByItemId(@PathVariable Integer itemId) {
//        return ResponseEntity.ok(inventoryItemDetailService.findDetailsByItemId(itemId));
//    }
//
//    @GetMapping("/{detailId}")
//    public ResponseEntity<InventoryItemDetail> getDetailById(@PathVariable Integer detailId) {
//        return inventoryItemDetailService.findDetailById(detailId)
//                .map(ResponseEntity::ok)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detail not found with id: " + detailId));
//    }
//
//    @GetMapping("/item/{itemId}/key/{keyName}")
//    public ResponseEntity<InventoryItemDetail> getDetailByItemIdAndKey(
//            @PathVariable Integer itemId,
//            @PathVariable String keyName) {
//        return inventoryItemDetailService.findDetailByItemIdAndKey(itemId, keyName)
//                .map(ResponseEntity::ok)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
//                        "Detail not found with key: " + keyName + " for item: " + itemId));
//    }
//
//    @GetMapping("/key/{keyName}")
//    public ResponseEntity<List<InventoryItemDetail>> getDetailsByKeyName(@PathVariable String keyName) {
//        return ResponseEntity.ok(inventoryItemDetailService.findDetailsByKeyName(keyName));
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<List<InventoryItemDetail>> getDetailsByKeyAndValue(
//            @RequestParam String keyName,
//            @RequestParam String value) {
//        return ResponseEntity.ok(inventoryItemDetailService.findDetailsByKeyAndValue(keyName, value));
//    }
//
//    @GetMapping("/room/{roomId}/key/{keyName}")
//    public ResponseEntity<List<InventoryItemDetail>> getDetailsByRoomAndKey(
//            @PathVariable Integer roomId,
//            @PathVariable String keyName) {
//        return ResponseEntity.ok(inventoryItemDetailService.findDetailsByRoomAndKey(roomId, keyName));
//    }
//
//    @GetMapping("/category/{categoryId}/keys")
//    public ResponseEntity<List<String>> getDistinctKeysByCategory(@PathVariable Integer categoryId) {
//        return ResponseEntity.ok(inventoryItemDetailService.findDistinctKeysByCategory(categoryId));
//    }
//
//    @GetMapping("/most-common")
//    public ResponseEntity<Map<String, Long>> getMostCommonAttributes() {
//        return ResponseEntity.ok(inventoryItemDetailService.getMostCommonAttributes());
//    }
//
//    @PostMapping("/item/{itemId}")
//    public ResponseEntity<InventoryItemDetail> createDetail(
//            @PathVariable Integer itemId,
//            @RequestParam String keyName,
//            @RequestParam String value) {
//        try {
//            return ResponseEntity.status(HttpStatus.CREATED)
//                    .body(inventoryItemDetailService.createDetail(itemId, keyName, value));
//        } catch (IllegalArgumentException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
//        }
//    }
//
//    @PostMapping("/item/{itemId}/bulk")
//    public ResponseEntity<Void> bulkCreateDetails(
//            @PathVariable Integer itemId,
//            @RequestBody Map<String, String> details) {
//        try {
//            inventoryItemDetailService.bulkCreateDetails(itemId, details);
//            return ResponseEntity.status(HttpStatus.CREATED).build();
//        } catch (IllegalArgumentException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
//        }
//    }
//
//    @PutMapping("/{detailId}")
//    public ResponseEntity<InventoryItemDetail> updateDetail(
//            @PathVariable Integer detailId,
//            @RequestParam String value) {
//        try {
//            return ResponseEntity.ok(inventoryItemDetailService.updateDetail(detailId, value));
//        } catch (IllegalArgumentException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
//        }
//    }
//
//    @PutMapping("/item/{itemId}/key/{keyName}")
//    public ResponseEntity<InventoryItemDetail> updateDetailByItemIdAndKey(
//            @PathVariable Integer itemId,
//            @PathVariable String keyName,
//            @RequestParam String value) {
//        try {
//            return ResponseEntity.ok(inventoryItemDetailService.updateDetailByItemIdAndKey(itemId, keyName, value));
//        } catch (IllegalArgumentException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
//        }
//    }
//
//    @PutMapping("/item/{itemId}/bulk")
//    public ResponseEntity<Void> bulkUpdateDetails(
//            @PathVariable Integer itemId,
//            @RequestBody Map<String, String> details) {
//        try {
//            inventoryItemDetailService.bulkUpdateDetails(itemId, details);
//            return ResponseEntity.ok().build();
//        } catch (IllegalArgumentException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
//        }
//    }
//
//    @DeleteMapping("/{detailId}")
//    public ResponseEntity<Void> deleteDetail(@PathVariable Integer detailId) {
//        try {
//            inventoryItemDetailService.deleteDetail(detailId);
//            return ResponseEntity.noContent().build();
//        } catch (IllegalArgumentException e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
//        }
//    }
//
//    @DeleteMapping("/item/{itemId}/key/{keyName}")
//    public ResponseEntity<Void> deleteDetailByItemIdAndKey(
//            @PathVariable Integer itemId,
//            @PathVariable String keyName) {
//        try {
//            inventoryItemDetailService.deleteDetailByItemIdAndKey(itemId, keyName);
//            return ResponseEntity.noContent().build();
//        } catch (IllegalArgumentException e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
//        }
//    }
//
//    @DeleteMapping("/item/{itemId}/all")
//    public ResponseEntity<Void> deleteAllDetailsByItemId(@PathVariable Integer itemId) {
//        try {
//            inventoryItemDetailService.deleteAllDetailsByItemId(itemId);
//            return ResponseEntity.noContent().build();
//        } catch (IllegalArgumentException e) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
//        }
//    }
//
//    @PostMapping("/copy")
//    public ResponseEntity<Void> copyDetailsFromItem(
//            @RequestParam Integer sourceItemId,
//            @RequestParam Integer targetItemId) {
//        try {
//            inventoryItemDetailService.copyDetailsFromItem(sourceItemId, targetItemId);
//            return ResponseEntity.ok().build();
//        } catch (IllegalArgumentException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
//        }
//    }
//
//    @GetMapping("/item/{itemId}/count")
//    public ResponseEntity<Map<String, Object>> countDetailsByItemId(@PathVariable Integer itemId) {
//        try {
//            Long count = inventoryItemDetailService.countDetailsByItemId(itemId);
//            Map<String, Object> result = new HashMap<>();
//            result.put("itemId", itemId);
//            result.put("detailCount", count);
//            return ResponseEntity.ok(result);
//        } catch (IllegalArgumentException e) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
//        }
//    }
//}