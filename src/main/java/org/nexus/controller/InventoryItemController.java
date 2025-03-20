package org.nexus.controller;

import org.nexus.entity.*;
import org.nexus.service.InventoryItemService;
import org.nexus.service.LabService;
import org.nexus.service.InventoryCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/inventory/items")
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;
    private final LabService labService;
    private final InventoryCategoryService categoryService;

    @Autowired
    public InventoryItemController(InventoryItemService inventoryItemService,
                                   LabService labService,
                                   InventoryCategoryService categoryService) {
        this.inventoryItemService = inventoryItemService;
        this.labService = labService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String getAllItems(Model model) {
        List<InventoryItem> items = inventoryItemService.findAllItems();
        model.addAttribute("items", items);
        return "inventory/list";
    }

    @GetMapping("/paged")
    public String getPagedItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<InventoryItem> items = inventoryItemService.findAllItems(pageable);
        model.addAttribute("items", items);
        return "inventory/paged-list";
    }

    @GetMapping("/{id}")
    public String getItemById(@PathVariable Integer id, Model model) {
        Optional<InventoryItem> item = inventoryItemService.findItemById(id);
        if (item.isPresent()) {
            model.addAttribute("item", item.get());
            return "inventory/detail";
        }
        return "redirect:/inventory/items";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {

        List<Lab> labs = labService.findAllLabs();

        List<InventoryCategory> categories = categoryService.findAllCategories();

        model.addAttribute("item", new InventoryItem());
        model.addAttribute("labs", labs);
        model.addAttribute("categories", categories);
        model.addAttribute("statuses", InventoryStatus.values());

        return "inventory/create-form";

    }

    @PostMapping
    public String createItem(@Valid @ModelAttribute InventoryItem item,
                             @RequestParam Map<String, String> details,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "inventory/create-form";
        }

        try {
            // Remove non-detail parameters from the map
            details.remove("_csrf");
            List<String> fieldsToRemove = List.of("name", "serialNumber", "quantity",
                    "unitCost", "purchaseDate", "warrantyExpiryDate", "status", "categoryId", "roomId");
            fieldsToRemove.forEach(details::remove);

            InventoryItem createdItem = inventoryItemService.createItem(item, details);
            redirectAttributes.addFlashAttribute("success", "Inventory item created successfully!");
            return "redirect:/inventory/items/" + createdItem.getId();
        } catch (IllegalArgumentException e) {
            result.rejectValue("serialNumber", "error.item", e.getMessage());
            return "inventory/create-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Optional<InventoryItem> item = inventoryItemService.findItemById(id);
        if (item.isPresent()) {
            InventoryItem inventoryItem = item.get();

            // Format dates if they exist
            if (inventoryItem.getPurchaseDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                model.addAttribute("formattedPurchaseDate",
                        inventoryItem.getPurchaseDate().format(formatter));
            }

            if (inventoryItem.getWarrantyExpiryDate() != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                model.addAttribute("formattedWarrantyDate",
                        inventoryItem.getWarrantyExpiryDate().format(formatter));
            }

            List<Lab> labs = labService.findAllLabs();
            List<InventoryCategory> categories = categoryService.findAllCategories();

            model.addAttribute("item", inventoryItem);
            model.addAttribute("labs", labs);
            model.addAttribute("categories", categories);
            model.addAttribute("statuses", InventoryStatus.values());

            return "inventory/edit-form";
        }
        return "redirect:/inventory/items";
    }

    @PostMapping("/{id}")
    public String updateItem(@PathVariable Integer id,
                             @Valid @ModelAttribute InventoryItem itemDetails,
                             @RequestParam Map<String, String> details,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "inventory/edit-form";
        }

        try {
            // Remove non-detail parameters from the map
            details.remove("_csrf");
            List<String> fieldsToRemove = List.of("name", "serialNumber", "quantity",
                    "unitCost", "purchaseDate", "warrantyExpiryDate", "status", "categoryId", "roomId");
            fieldsToRemove.forEach(details::remove);

            InventoryItem updatedItem = inventoryItemService.updateItem(id, itemDetails, details);
            redirectAttributes.addFlashAttribute("success", "Inventory item updated successfully!");
            return "redirect:/inventory/items/" + updatedItem.getId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/inventory/items/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteItem(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            inventoryItemService.deleteItem(id);
            redirectAttributes.addFlashAttribute("success", "Inventory item deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inventory/items";
    }

    @GetMapping("/search")
    public String searchItems(@RequestParam String query, Model model) {
        List<InventoryItem> items = inventoryItemService.findItemsByName(query);
        model.addAttribute("items", items);
        model.addAttribute("searchTerm", query);
        return "inventory/search-results";
    }

    @GetMapping("/transfer")
    public String showTransferForm(Model model) {
        List<Lab> labs = labService.findAllLabs();
        model.addAttribute("labs", labs);
        return "inventory/transfer-form";
    }

    @PostMapping("/transfer")
    public String transferItems(@RequestParam Integer fromRoomId,
                                @RequestParam Integer toRoomId,
                                @RequestParam List<Integer> itemIds,
                                RedirectAttributes redirectAttributes) {
        try {
            inventoryItemService.transferItems(fromRoomId, toRoomId, itemIds);
            redirectAttributes.addFlashAttribute("success", "Items transferred successfully!");
            return "redirect:/inventory/items";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/inventory/items/transfer";
        }
    }

    @GetMapping("/warranty-expiring")
    public String getItemsWithWarrantyExpiring(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            Model model) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = LocalDate.now().plusMonths(3);
        }

        List<InventoryItem> items = inventoryItemService
                .findItemsWithWarrantyExpiringBetween(startDate, endDate);

        model.addAttribute("items", items);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "inventory/warranty-expiring";

    }

    @GetMapping("/status/{status}")
    public String getItemsByStatus(@PathVariable InventoryStatus status, Model model) {
        List<InventoryItem> items = inventoryItemService.findItemsByStatus(status);
        model.addAttribute("items", items);
        model.addAttribute("currentStatus", status);
        model.addAttribute("allStatuses", InventoryStatus.values());
        return "inventory/status-list";
    }

}