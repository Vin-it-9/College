package org.nexus.service;

import org.nexus.entity.*;
import org.nexus.entity.transferDTO.TransferResult;
import org.nexus.entity.transferDTO.TransferValidationResult;
import org.nexus.exception.*;
import org.nexus.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;
    private final InventoryItemDetailRepository inventoryItemDetailRepository;
    private final InventoryCategoryRepository inventoryCategoryRepository;
    private final LabRepository roomRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public InventoryItemService(InventoryItemRepository inventoryItemRepository,
                                InventoryItemDetailRepository inventoryItemDetailRepository,
                                InventoryCategoryRepository inventoryCategoryRepository,
                                LabRepository roomRepository, DepartmentRepository departmentRepository) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryItemDetailRepository = inventoryItemDetailRepository;
        this.inventoryCategoryRepository = inventoryCategoryRepository;
        this.roomRepository = roomRepository;
        this.departmentRepository = departmentRepository;
    }

    public List<InventoryItem> findAllItems() {
        return inventoryItemRepository.findAll();
    }

    public Page<InventoryItem> findAllItems(Pageable pageable) {
        return inventoryItemRepository.findAll(pageable);
    }

    public Optional<InventoryItem> findItemById(Integer id) {
        return inventoryItemRepository.findByIdWithDetails(id);
    }

    public List<InventoryItem> findItemsByRoom(Integer labId) {
        return inventoryItemRepository.findByLabId(labId);
    }

    public List<InventoryItem> findItemsByCategory(Integer categoryId) {
        return inventoryItemRepository.findByCategoryId(categoryId);
    }

    public List<InventoryItem> findItemsByStatus(InventoryStatus status) {
        return inventoryItemRepository.findByStatus(status);
    }

    public List<InventoryItem> findItemsByFloor(Integer floorId) {
        return inventoryItemRepository.findByFloorId(floorId);
    }

    public List<InventoryItem> findItemsByBuilding(Integer buildingId) {
        return inventoryItemRepository.findByBuildingId(buildingId);
    }

    public List<InventoryItem> findItemsByName(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return inventoryItemRepository.searchItemsByAnyField(searchTerm.trim());
    }

    public Optional<InventoryItem> findItemBySerialNumber(String serialNumber) {
        return inventoryItemRepository.findBySerialNumber(serialNumber);
    }

    public List<InventoryItem> findItemsByDetailKeyAndValue(String key, String value) {
        return inventoryItemRepository.findByItemDetailKeyAndValue(key, value);
    }

    public List<InventoryItem> findItemsWithWarrantyExpiringBetween(LocalDate startDate, LocalDate endDate) {
        return inventoryItemRepository.findByWarrantyExpiringBetween(startDate, endDate);
    }

    public Double calculateTotalValueByRoom(Integer roomId) {
        Double value = inventoryItemRepository.calculateTotalValueByRoom(roomId);
        return value != null ? value : 0.0;
    }

    public Long countItemsByRoom(Integer roomId) {
        return inventoryItemRepository.countByRoomId(roomId);
    }

    public Map<String, Long> getMostCommonAttributes() {
        List<Object[]> results = inventoryItemDetailRepository.findMostCommonAttributes();
        Map<String, Long> attributeUsage = new LinkedHashMap<>();

        for (Object[] result : results) {
            String keyName = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            attributeUsage.put(keyName, count);
        }

        return attributeUsage;
    }

    @Transactional
    public InventoryItem createItem(InventoryItem item, Map<String, String> details) {
        validateItem(item);

        InventoryItem savedItem = inventoryItemRepository.save(item);
        if (details != null && !details.isEmpty()) {
            for (Map.Entry<String, String> entry : details.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (key != null && !key.trim().isEmpty() && value != null) {
                    InventoryItemDetail detail = new InventoryItemDetail();
                    detail.setKeyName(key);
                    detail.setValue(value);
                    detail.setInventoryItem(savedItem);
                    inventoryItemDetailRepository.save(detail);
                }
            }
        }

        return inventoryItemRepository.findByIdWithDetails(savedItem.getId()).orElse(savedItem);
    }

    @Transactional
    public InventoryItem updateItem(Integer id, InventoryItem itemDetails, Map<String, String> details) {
        InventoryItem existingItem = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found with id: " + id));

        // Update basic properties
        if (itemDetails.getName() != null && !itemDetails.getName().trim().isEmpty()) {
            existingItem.setName(itemDetails.getName());
        }

        if (itemDetails.getSerialNumber() != null) {
            // Check if serial number is being changed and if it already exists
            if (!itemDetails.getSerialNumber().equals(existingItem.getSerialNumber())) {
                Optional<InventoryItem> itemWithSameSerial = inventoryItemRepository.findBySerialNumber(itemDetails.getSerialNumber());
                if (itemWithSameSerial.isPresent() && !itemWithSameSerial.get().getId().equals(id)) {
                    throw new IllegalArgumentException("Another item with this serial number already exists");
                }
            }
            existingItem.setSerialNumber(itemDetails.getSerialNumber());
        }

        if (itemDetails.getQuantity() != null) {
            existingItem.setQuantity(itemDetails.getQuantity());
        }

        if (itemDetails.getUnitCost() != null) {
            existingItem.setUnitCost(itemDetails.getUnitCost());
        }

        if (itemDetails.getPurchaseDate() != null) {
            existingItem.setPurchaseDate(itemDetails.getPurchaseDate());
        }

        if (itemDetails.getWarrantyExpiryDate() != null) {
            existingItem.setWarrantyExpiryDate(itemDetails.getWarrantyExpiryDate());
        }

        if (itemDetails.getStatus() != null) {
            existingItem.setStatus(itemDetails.getStatus());
        }

        // Update category if provided
        if (itemDetails.getCategory() != null && itemDetails.getCategory().getId() != null) {
            InventoryCategory category = inventoryCategoryRepository.findById(itemDetails.getCategory().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Category not found with id: " + itemDetails.getCategory().getId()));
            existingItem.setCategory(category);
        }

        // Update room if provided
        if (itemDetails.getLab() != null && itemDetails.getLab().getId() != null) {
            Lab lab = roomRepository.findById(itemDetails.getLab().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Lab not found with id: " + itemDetails.getLab().getId()));
            existingItem.setLab(lab);
        }

        // Update details if provided
        if (details != null) {
            // Get existing details for comparison
            Map<String, InventoryItemDetail> existingDetails = existingItem.getDetails().stream()
                    .collect(Collectors.toMap(InventoryItemDetail::getKeyName, detail -> detail));

            // Process each detail in the update request
            for (Map.Entry<String, String> entry : details.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (key != null && !key.trim().isEmpty()) {
                    if (existingDetails.containsKey(key)) {
                        // Update existing detail
                        InventoryItemDetail detail = existingDetails.get(key);
                        if (value != null) {
                            detail.setValue(value);
                            inventoryItemDetailRepository.save(detail);
                        } else {
                            // Remove detail if value is null
                            inventoryItemDetailRepository.delete(detail);
                            existingItem.getDetails().remove(detail);
                        }
                    } else if (value != null) {
                        // Add new detail
                        InventoryItemDetail detail = new InventoryItemDetail();
                        detail.setKeyName(key);
                        detail.setValue(value);
                        detail.setInventoryItem(existingItem);
                        inventoryItemDetailRepository.save(detail);
                        existingItem.getDetails().add(detail);
                    }
                }
            }
        }

        return inventoryItemRepository.save(existingItem);
    }

    @Transactional
    public void deleteItem(Integer id) {
        if (!inventoryItemRepository.existsById(id)) {
            throw new IllegalArgumentException("Inventory item not found with id: " + id);
        }

        // Details will be deleted automatically due to cascade settings
        inventoryItemRepository.deleteById(id);
    }

    @Transactional
    public void addItemDetail(Integer itemId, String key, String value) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Detail key cannot be empty");
        }

        if (value == null) {
            throw new IllegalArgumentException("Detail value cannot be null");
        }

        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory item not found with id: " + itemId));

        // Check if key already exists
        Optional<InventoryItemDetail> existingDetail =
                inventoryItemDetailRepository.findByInventoryItemIdAndKeyName(itemId, key);

        if (existingDetail.isPresent()) {
            InventoryItemDetail detail = existingDetail.get();
            detail.setValue(value);
            inventoryItemDetailRepository.save(detail);
        } else {
            InventoryItemDetail detail = new InventoryItemDetail();
            detail.setKeyName(key);
            detail.setValue(value);
            detail.setInventoryItem(item);
            inventoryItemDetailRepository.save(detail);

            // Ensure the item's detail collection is updated
            if (item.getDetails() == null) {
                item.setDetails(new HashSet<>());
            }
            item.getDetails().add(detail);
            inventoryItemRepository.save(item);
        }
    }

    @Transactional
    public void removeItemDetail(Integer itemId, String key) {
        if (key == null || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Detail key cannot be empty");
        }

        Optional<InventoryItemDetail> detail =
                inventoryItemDetailRepository.findByInventoryItemIdAndKeyName(itemId, key);

        if (detail.isPresent()) {
            inventoryItemDetailRepository.delete(detail.get());
        } else {
            throw new IllegalArgumentException("Detail not found with key: " + key);
        }
    }

    public List<InventoryItem> findItemsByLabId(Integer labId) {
        return inventoryItemRepository.findByLabId(labId);
    }

    // New method to find all available items
    public List<InventoryItem> findAllAvailableItems() {
        return inventoryItemRepository.findByStatus(InventoryStatus.AVAILABLE);
    }

    @Transactional
    public TransferResult transferItems(Integer fromRoomId, Integer toRoomId, List<Integer> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) {
            throw new IllegalArgumentException("No items specified for transfer");
        }

        // Get source and destination labs
        Lab fromLab = roomRepository.findById(fromRoomId)
                .orElseThrow(() -> new LabNotFoundException("Source room not found with id: " + fromRoomId));

        Lab toLab = roomRepository.findById(toRoomId)
                .orElseThrow(() -> new LabNotFoundException("Destination room not found with id: " + toRoomId));

        List<InventoryItem> items = inventoryItemRepository.findAllById(itemIds);
        if (items.size() != itemIds.size()) {
            throw new ItemNotFoundException("One or more items not found");
        }

        // Check if all items are from the same room
        Set<Integer> uniqueRoomIds = items.stream()
                .map(item -> item.getLab() != null ? item.getLab().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (!uniqueRoomIds.isEmpty() && (uniqueRoomIds.size() > 1 || !uniqueRoomIds.contains(fromRoomId))) {
            throw new TransferValidationException("Items must all be from the source room");
        }

        List<String> warnings = new ArrayList<>();

        // Transfer items
        for (InventoryItem item : items) {
            item.setLab(toLab);
        }

        inventoryItemRepository.saveAll(items);

        return new TransferResult(
                items.size(),
                fromLab.getLabName(),
                toLab.getLabName(),
                warnings
        );

    }

    // New method to validate transfer
    public TransferValidationResult validateTransfer(Integer fromLabId, Integer toLabId, List<Integer> itemIds) {
        List<String> messages = new ArrayList<>();
        boolean isValid = true;

        try {
            // Check if labs exist
            Lab fromLab = roomRepository.findById(fromLabId)
                    .orElseThrow(() -> new LabNotFoundException("Source lab not found"));
            Lab toLab = roomRepository.findById(toLabId)
                    .orElseThrow(() -> new LabNotFoundException("Destination lab not found"));

            // Check if items exist and are in the source lab
            List<InventoryItem> items = inventoryItemRepository.findAllById(itemIds);

            if (items.size() != itemIds.size()) {
                isValid = false;
                messages.add("One or more items not found");
            }

            // Validate items are in source lab
            List<InventoryItem> invalidItems = items.stream()
                    .filter(item -> !fromLabId.equals(item.getLab().getId()))
                    .collect(Collectors.toList());

            if (!invalidItems.isEmpty()) {
                isValid = false;
                messages.add("Some items are not in the source lab");
            }

            return new TransferValidationResult(isValid, messages, items);

        } catch (Exception e) {
            return new TransferValidationResult(false,
                    Collections.singletonList(e.getMessage()),
                    Collections.emptyList());
        }

    }

    private void validateItem(InventoryItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Inventory item cannot be null");
        }

        if (item.getName() == null || item.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Inventory item name cannot be empty");
        }

        if (item.getQuantity() == null || item.getQuantity() < 1) {
            throw new IllegalArgumentException("Inventory item quantity must be at least 1");
        }

        if (item.getCategory() == null || item.getCategory().getId() == null) {
            throw new IllegalArgumentException("Inventory item must be assigned to a category");
        } else {
            // Verify category exists
            if (!inventoryCategoryRepository.existsById(item.getCategory().getId())) {
                throw new IllegalArgumentException("Category not found with id: " + item.getCategory().getId());
            }
        }

        if (item.getLab() == null || item.getLab().getId() == null) {
            throw new IllegalArgumentException("Inventory item must be assigned to a room");
        } else {
            // Verify room exists
            if (!roomRepository.existsById(item.getLab().getId())) {
                throw new IllegalArgumentException("Lab not found with id: " + item.getLab().getId());
            }
        }

        // Check serial number uniqueness if provided
        if (item.getSerialNumber() != null && !item.getSerialNumber().trim().isEmpty()) {
            Optional<InventoryItem> existingItem = inventoryItemRepository.findBySerialNumber(item.getSerialNumber());
            if (existingItem.isPresent()) {
                throw new IllegalArgumentException("An item with this serial number already exists");
            }
        }
    }

    public List<InventoryItem> getAllUnapprovedItems() {
        return inventoryItemRepository.findByApprovedFalse();
    }

    public InventoryItem approveItem(Integer id) {
        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item with id " + id + " not found"));
        item.setApproved(true);
        return inventoryItemRepository.save(item);
    }
    public List<InventoryItem> getAllUnapprovedItemsForHod(User user) {
        Department department = departmentRepository.findByHod(user)
                .orElseThrow(() -> new AccessDeniedException("User is not an HOD of any department"));

        return inventoryItemRepository.findByApprovedFalseAndLabDepartmentId(department.getId());
    }

    public List<InventoryItem> findInventoryByDepartmentId(Integer departmentId) {
        return inventoryItemRepository.findByLabDepartmentId(departmentId);
    }

    public boolean isHod(User user) {
        if (user == null || user.getRoles() == null) {
            return false;
        }

        return user.getRoles().stream()
                .anyMatch(role -> "HOD".equals(role.getName()));
    }

    public InventoryItem approveItem(Integer id, User user) {
        if (!isHod(user)) {
            throw new AccessDeniedException("Only HODs can approve inventory items");
        }
        Department hodDepartment = departmentRepository.findByHod(user)
                .orElseThrow(() -> new AccessDeniedException("User is not an HOD of any department"));

        InventoryItem item = inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item with id " + id + " not found"));

        if (item.getLab() == null ||
                item.getLab().getDepartment() == null ||
                !item.getLab().getDepartment().getId().equals(hodDepartment.getId())) {
            throw new AccessDeniedException("You can only approve items from labs in your department");
        }

        item.setApproved(true);
        return inventoryItemRepository.save(item);
    }


}