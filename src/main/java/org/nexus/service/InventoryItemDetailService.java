package org.nexus.service;

import org.nexus.entity.InventoryItem;
import org.nexus.entity.InventoryItemDetail;
import org.nexus.repository.InventoryItemDetailRepository;
import org.nexus.repository.InventoryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InventoryItemDetailService {

    private final InventoryItemDetailRepository inventoryItemDetailRepository;
    private final InventoryItemRepository inventoryItemRepository;

    @Autowired
    public InventoryItemDetailService(InventoryItemDetailRepository inventoryItemDetailRepository,
                                      InventoryItemRepository inventoryItemRepository) {
        this.inventoryItemDetailRepository = inventoryItemDetailRepository;
        this.inventoryItemRepository = inventoryItemRepository;
    }

    public List<InventoryItemDetail> findDetailsByItemId(Integer itemId) {
        return inventoryItemDetailRepository.findByInventoryItemId(itemId);
    }

    public Optional<InventoryItemDetail> findDetailById(Integer detailId) {
        return inventoryItemDetailRepository.findById(detailId);
    }

    public Optional<InventoryItemDetail> findDetailByItemIdAndKey(Integer itemId, String keyName) {
        return inventoryItemDetailRepository.findByInventoryItemIdAndKeyName(itemId, keyName);
    }

    public List<InventoryItemDetail> findDetailsByKeyName(String keyName) {
        return inventoryItemDetailRepository.findByKeyName(keyName);
    }

    public List<InventoryItemDetail> findDetailsByKeyAndValue(String keyName, String value) {
        return inventoryItemDetailRepository.findByKeyNameAndValueContaining(keyName, value);
    }

    public List<InventoryItemDetail> findDetailsByRoomAndKey(Integer roomId, String keyName) {
        return inventoryItemDetailRepository.findByRoomIdAndKeyName(roomId, keyName);
    }

    public List<String> findDistinctKeysByCategory(Integer categoryId) {
        return inventoryItemDetailRepository.findDistinctKeyNamesByCategory(categoryId);
    }

    public Map<String, Long> getMostCommonAttributes() {
        List<Object[]> results = inventoryItemDetailRepository.findMostCommonAttributes();
        return results.stream()
                .collect(Collectors.toMap(
                        result -> (String) result[0],
                        result -> ((Number) result[1]).longValue(),
                        (v1, v2) -> v1,
                        java.util.LinkedHashMap::new
                ));
    }

    @Transactional
    public InventoryItemDetail createDetail(Integer itemId, String keyName, String value) {
        // Validate inputs
        if (keyName == null || keyName.trim().isEmpty()) {
            throw new IllegalArgumentException("Detail key cannot be empty");
        }

        if (value == null) {
            throw new IllegalArgumentException("Detail value cannot be null");
        }

        // Find the inventory item
        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found with id: " + itemId));

        // Check if key already exists for this item
        Optional<InventoryItemDetail> existingDetail =
                inventoryItemDetailRepository.findByInventoryItemIdAndKeyName(itemId, keyName);

        if (existingDetail.isPresent()) {
            throw new IllegalArgumentException("Detail with key '" + keyName + "' already exists for this item");
        }

        // Create and save the detail
        InventoryItemDetail detail = new InventoryItemDetail();
        detail.setKeyName(keyName);
        detail.setValue(value);
        detail.setInventoryItem(item);

        return inventoryItemDetailRepository.save(detail);
    }

    @Transactional
    public InventoryItemDetail updateDetail(Integer detailId, String value) {
        // Find the detail
        InventoryItemDetail detail = inventoryItemDetailRepository.findById(detailId)
                .orElseThrow(() -> new IllegalArgumentException("Detail not found with id: " + detailId));

        // Update the value
        if (value == null) {
            throw new IllegalArgumentException("Detail value cannot be null");
        }

        detail.setValue(value);
        return inventoryItemDetailRepository.save(detail);
    }

    @Transactional
    public InventoryItemDetail updateDetailByItemIdAndKey(Integer itemId, String keyName, String value) {
        // Find the detail
        InventoryItemDetail detail = inventoryItemDetailRepository.findByInventoryItemIdAndKeyName(itemId, keyName)
                .orElseThrow(() -> new IllegalArgumentException("Detail not found with key: " + keyName));

        // Update the value
        if (value == null) {
            throw new IllegalArgumentException("Detail value cannot be null");
        }

        detail.setValue(value);
        return inventoryItemDetailRepository.save(detail);
    }

    @Transactional
    public void deleteDetail(Integer detailId) {
        if (!inventoryItemDetailRepository.existsById(detailId)) {
            throw new IllegalArgumentException("Detail not found with id: " + detailId);
        }

        inventoryItemDetailRepository.deleteById(detailId);
    }

    @Transactional
    public void deleteDetailByItemIdAndKey(Integer itemId, String keyName) {
        // Check if the detail exists
        Optional<InventoryItemDetail> detail =
                inventoryItemDetailRepository.findByInventoryItemIdAndKeyName(itemId, keyName);

        if (!detail.isPresent()) {
            throw new IllegalArgumentException("Detail not found with key: " + keyName);
        }

        inventoryItemDetailRepository.delete(detail.get());
    }

    @Transactional
    public void deleteAllDetailsByItemId(Integer itemId) {
        // Check if the item exists
        if (!inventoryItemRepository.existsById(itemId)) {
            throw new IllegalArgumentException("Inventory item not found with id: " + itemId);
        }

        inventoryItemDetailRepository.deleteByInventoryItemId(itemId);
    }

    @Transactional
    public void bulkCreateDetails(Integer itemId, Map<String, String> details) {
        if (details == null || details.isEmpty()) {
            return;
        }

        // Find the inventory item
        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found with id: " + itemId));

        // Get existing keys for this item
        List<InventoryItemDetail> existingDetails = inventoryItemDetailRepository.findByInventoryItemId(itemId);
        Set<String> existingKeys = existingDetails.stream()
                .map(InventoryItemDetail::getKeyName)
                .collect(Collectors.toSet());

        // Create and save details
        List<InventoryItemDetail> detailsToSave = details.entrySet().stream()
                .filter(entry -> !existingKeys.contains(entry.getKey()))
                .map(entry -> {
                    InventoryItemDetail detail = new InventoryItemDetail();
                    detail.setKeyName(entry.getKey());
                    detail.setValue(entry.getValue());
                    detail.setInventoryItem(item);
                    return detail;
                })
                .collect(Collectors.toList());

        if (!detailsToSave.isEmpty()) {
            inventoryItemDetailRepository.saveAll(detailsToSave);
        }
    }

    @Transactional
    public void bulkUpdateDetails(Integer itemId, Map<String, String> details) {
        if (details == null || details.isEmpty()) {
            return;
        }

        // Check if the item exists
        if (!inventoryItemRepository.existsById(itemId)) {
            throw new IllegalArgumentException("Inventory item not found with id: " + itemId);
        }

        // Get existing details for this item
        List<InventoryItemDetail> existingDetails = inventoryItemDetailRepository.findByInventoryItemId(itemId);
        Map<String, InventoryItemDetail> detailsByKey = existingDetails.stream()
                .collect(Collectors.toMap(InventoryItemDetail::getKeyName, detail -> detail));

        // Process each detail in the update request
        for (Map.Entry<String, String> entry : details.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (detailsByKey.containsKey(key)) {
                // Update existing detail
                InventoryItemDetail detail = detailsByKey.get(key);
                detail.setValue(value);
                inventoryItemDetailRepository.save(detail);
            }
        }
    }

    public Long countDetailsByItemId(Integer itemId) {
        return inventoryItemDetailRepository.countByInventoryItemId(itemId);
    }

    @Transactional
    public void copyDetailsFromItem(Integer sourceItemId, Integer targetItemId) {
        // Check if both items exist
        if (!inventoryItemRepository.existsById(sourceItemId)) {
            throw new IllegalArgumentException("Source item not found with id: " + sourceItemId);
        }

        InventoryItem targetItem = inventoryItemRepository.findById(targetItemId)
                .orElseThrow(() -> new IllegalArgumentException("Target item not found with id: " + targetItemId));

        // Get source item details
        List<InventoryItemDetail> sourceDetails = inventoryItemDetailRepository.findByInventoryItemId(sourceItemId);
        if (sourceDetails.isEmpty()) {
            return; // No details to copy
        }

        // Get existing keys for target item to avoid duplicates
        List<InventoryItemDetail> targetDetails = inventoryItemDetailRepository.findByInventoryItemId(targetItemId);
        Set<String> existingKeys = targetDetails.stream()
                .map(InventoryItemDetail::getKeyName)
                .collect(Collectors.toSet());

        // Create and save new details for target item
        List<InventoryItemDetail> detailsToSave = sourceDetails.stream()
                .filter(detail -> !existingKeys.contains(detail.getKeyName()))
                .map(detail -> {
                    InventoryItemDetail newDetail = new InventoryItemDetail();
                    newDetail.setKeyName(detail.getKeyName());
                    newDetail.setValue(detail.getValue());
                    newDetail.setInventoryItem(targetItem);
                    return newDetail;
                })
                .collect(Collectors.toList());

        if (!detailsToSave.isEmpty()) {
            inventoryItemDetailRepository.saveAll(detailsToSave);
        }
    }
}