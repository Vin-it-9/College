package org.nexus.controller;

import org.nexus.entity.*;
import org.nexus.entity.classroom.Classroom;
import org.nexus.entity.transferDTO.TransferResult;
import org.nexus.entity.transferDTO.TransferValidationResult;
import org.nexus.exception.*;
import org.nexus.repository.ClassroomRepoistory;
import org.nexus.repository.UserRepository;
import org.nexus.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.*;

@Controller
@RequestMapping("/inventory/items")
public class InventoryItemController {

    private final InventoryItemService inventoryItemService;
    private final LabService labService;
    private final InventoryCategoryService categoryService;
    private final UserRepository userRepository;
    private final InventoryItemDetailService inventoryItemDetailService;
    private final ClassroomService classroomService;
    private final ClassroomRepoistory  classroomRepoistory;

    @Autowired
    public InventoryItemController(InventoryItemService inventoryItemService,
                                   LabService labService,
                                   InventoryCategoryService categoryService, UserService userService, UserRepository userRepository, InventoryItemDetailService inventoryItemDetailService, ClassroomService classroomService, ClassroomRepoistory classroomRepoistory) {
        this.inventoryItemService = inventoryItemService;
        this.labService = labService;
        this.categoryService = categoryService;
        this.userRepository = userRepository;
        this.inventoryItemDetailService = inventoryItemDetailService;
        this.classroomService = classroomService;
        this.classroomRepoistory = classroomRepoistory;
    }


    @GetMapping
    public String getAllItems(Model model) {
        List<InventoryItem> items = inventoryItemService.findAllItems();
        model.addAttribute("items", items);
        model.addAttribute("viewType", "all");
        return "inventory/list";
    }

    @GetMapping("/labs")
    public String getLabItems(Model model) {
        List<InventoryItem> labItems = inventoryItemService.findItemsByLab();
        model.addAttribute("items", labItems);
        model.addAttribute("viewType", "labs");
        model.addAttribute("title", "Lab Inventory Items");
        return "inventory/list";
    }

    @GetMapping("/classrooms")
    public String getClassroomItems(Model model) {
        List<InventoryItem> classroomItems = inventoryItemService.findItemsByClassroom();
        model.addAttribute("items", classroomItems);
        model.addAttribute("viewType", "classrooms");
        model.addAttribute("title", "Classroom Inventory Items");
        return "inventory/list";
    }

    @GetMapping("/items/lab/{labId}")
    public String getItemsForLab(@PathVariable Integer labId, Model model) {
        List<InventoryItem> labItems = inventoryItemService.findItemsByLabId(labId);
        model.addAttribute("items", labItems);
        model.addAttribute("viewType", "specificLab");
        model.addAttribute("labId", labId);
        return "inventory/list";
    }

    @GetMapping("/items/classroom/{classroomId}")
    public String getItemsForClassroom(@PathVariable Integer classroomId, Model model) {
        List<InventoryItem> classroomItems = inventoryItemService.findItemsByClassroomId(classroomId);
        model.addAttribute("items", classroomItems);
        model.addAttribute("viewType", "specificClassroom");
        model.addAttribute("classroomId", classroomId);
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
        if (!model.containsAttribute("item")) {
            model.addAttribute("item", new InventoryItem());
        }
        List<Lab> labs = labService.findAllLabs();
        List<InventoryCategory> categories = categoryService.findAllCategories();
        model.addAttribute("labs", labs);
        model.addAttribute("categories", categories);
        model.addAttribute("statuses", InventoryStatus.values());
        model.addAttribute("formType", "lab");

        return "inventory/create-form";
    }

    @GetMapping("/new/classroom")
    public String showCreateFormClassroom(Model model) {
        if (!model.containsAttribute("item")) {
            model.addAttribute("item", new InventoryItem());
        }
        List<Classroom> classrooms = classroomService.findAllClassrooms();
        List<InventoryCategory> categories = categoryService.findAllCategories();
        model.addAttribute("classrooms", classrooms);
        model.addAttribute("categories", categories);
        model.addAttribute("statuses", InventoryStatus.values());
        model.addAttribute("formType", "classroom");

        return "inventory/create-form";
    }

    @PostMapping
    public String createItem(@Valid @ModelAttribute("item") InventoryItem item,
                             @RequestParam(value = "formType", required = false) String formType,
                             @RequestParam(value = "detailKeys[]", required = false) String[] detailKeys,
                             @RequestParam(value = "detailValues[]", required = false) String[] detailValues,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("item", item);
            model.addAttribute("statuses", InventoryStatus.values());
            model.addAttribute("formType", formType);

            List<InventoryCategory> categories = categoryService.findAllCategories();
            model.addAttribute("categories", categories);

            if ("classroom".equals(formType)) {
                List<Classroom> classrooms = classroomService.findAllClassrooms();
                model.addAttribute("classrooms", classrooms);
            } else {
                List<Lab> labs = labService.findAllLabs();
                model.addAttribute("labs", labs);
            }

            return "inventory/create-form";
        }

        try {
            if ("classroom".equals(formType)) {
                item.setLab(null);
            } else {
                item.setClassroom(null);
            }

            Map<String, String> detailsMap = new HashMap<>();
            if (detailKeys != null && detailValues != null) {
                int minLength = Math.min(detailKeys.length, detailValues.length);
                for (int i = 0; i < minLength; i++) {
                    if (detailKeys[i] != null && !detailKeys[i].trim().isEmpty()) {
                        detailsMap.put(detailKeys[i], detailValues[i]);
                    }
                }
            }

            InventoryItem createdItem = inventoryItemService.createItem(item, detailsMap);

            redirectAttributes.addFlashAttribute("success", "Inventory item created successfully!");
            return "redirect:/inventory/items/" + createdItem.getId();
        } catch (IllegalArgumentException e) {
            result.rejectValue("serialNumber", "error.item", e.getMessage());

            model.addAttribute("item", item);
            model.addAttribute("statuses", InventoryStatus.values());
            model.addAttribute("formType", formType);

            List<InventoryCategory> categories = categoryService.findAllCategories();
            model.addAttribute("categories", categories);

            if ("classroom".equals(formType)) {
                List<Classroom> classrooms = classroomService.findAllClassrooms();
                model.addAttribute("classrooms", classrooms);
            } else {
                List<Lab> labs = labService.findAllLabs();
                model.addAttribute("labs", labs);
            }

            return "inventory/create-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Optional<InventoryItem> item = inventoryItemService.findItemById(id);

        if (item.isPresent()) {
            InventoryItem inventoryItem = item.get();

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

            List<InventoryItemDetail> itemDetails = inventoryItemDetailService.findDetailsByItemId(id);

            List<Lab> labs = labService.findAllLabs();
            List<InventoryCategory> categories = categoryService.findAllCategories();

            model.addAttribute("item", inventoryItem);
            model.addAttribute("itemDetails", itemDetails);
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
                             @RequestParam(value = "detailKeys[]", required = false) String[] detailKeys,
                             @RequestParam(value = "detailValues[]", required = false) String[] detailValues,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "inventory/edit-form";
        }

        try {
            Map<String, String> detailsMap = new HashMap<>();

            if (detailKeys != null && detailValues != null) {
                int minLength = Math.min(detailKeys.length, detailValues.length);

                for (int i = 0; i < minLength; i++) {
                    if (detailKeys[i] != null && !detailKeys[i].trim().isEmpty()) {
                        detailsMap.put(detailKeys[i], detailValues[i]);
                    }
                }
            }

            InventoryItem updatedItem = inventoryItemService.updateItem(id, itemDetails, detailsMap);
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
        try {

            List<Lab> labs = labService.findAllLabs();
            if (labs.isEmpty()) {
                model.addAttribute("error", "No labs available for transfer");
                return "inventory/transfer-form";
            }

            List<InventoryItem> availableItems = inventoryItemService.findAllAvailableItems();

            model.addAttribute("labs", labs);
            model.addAttribute("availableItems", availableItems);
            model.addAttribute("currentDate", LocalDateTime.now());

            return "inventory/transfer-form";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading transfer form: " + e.getMessage());
            return "inventory/transfer-form";
        }
    }

    @GetMapping("/transfer/items")
    @ResponseBody
    public ResponseEntity<?> getItemsByLab(@RequestParam Integer labId) {
        try {
            List<InventoryItem> items = inventoryItemService.findItemsByLabId(labId);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "Error fetching items: " + e.getMessage()));
        }
    }

    @PostMapping("/transfer")
    public String transferItems(
            @RequestParam Integer fromLabId,
            @RequestParam Integer toLabId,
            @RequestParam(required = false) List<Integer> itemIds,
            RedirectAttributes redirectAttributes) {

        // Validation
        if (fromLabId.equals(toLabId)) {
            redirectAttributes.addFlashAttribute("error", "Source and destination labs cannot be the same");
            return "redirect:/inventory/items/transfer";
        }

        if (itemIds == null || itemIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No items selected for transfer");
            return "redirect:/inventory/items/transfer";
        }

        try {
            TransferResult result = inventoryItemService.transferItems(fromLabId, toLabId, itemIds);

            redirectAttributes.addFlashAttribute("success",
                    String.format("Successfully transferred %d items from %s to %s",
                            result.getTransferredCount(),
                            result.getFromLabName(),
                            result.getToLabName()));

            if (result.hasWarnings()) {
                redirectAttributes.addFlashAttribute("warnings", result.getWarnings());
            }

            return "redirect:/inventory/items";

        } catch (LabNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Lab not found: " + e.getMessage());
            return "redirect:/inventory/items/transfer";
        } catch (ItemNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", "Item not found: " + e.getMessage());
            return "redirect:/inventory/items/transfer";
        } catch (TransferValidationException e) {
            redirectAttributes.addFlashAttribute("error", "Transfer validation failed: " + e.getMessage());
            return "redirect:/inventory/items/transfer";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "An unexpected error occurred: " + e.getMessage());
            return "redirect:/inventory/items/transfer";
        }
    }

    @PostMapping("/transfer/validate")
    @ResponseBody
    public ResponseEntity<?> validateTransfer(
            @RequestParam Integer fromLabId,
            @RequestParam Integer toLabId,
            @RequestParam List<Integer> itemIds) {

        try {

            TransferValidationResult validationResult =  inventoryItemService.validateTransfer(fromLabId, toLabId, itemIds);

            return ResponseEntity.ok(validationResult);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", e.getMessage()));
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

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("errorMessage", "You do not have permission to access this page.");
        return "inventory/access-denied";
    }

    @GetMapping("/pending-approvals")
    public String showPendingApprovals(Model model, Principal principal) {
        User currentUser = userRepository.getUserByEmail(principal.getName());
        if (!inventoryItemService.isHod(currentUser)) {
            return "redirect:access-denied";
        }

        try {
            List<InventoryItem> unapprovedItems = inventoryItemService.getAllUnapprovedItemsForHod(currentUser);
            model.addAttribute("items", unapprovedItems);
            model.addAttribute("currentTimestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            model.addAttribute("currentUser", currentUser);
            return "inventory/pending-approvals";
        } catch (AccessDeniedException e) {
            return "redirect:access-denied";
        }
    }

    @PostMapping("/approve/{id}")
    public String approveInventoryItem(@PathVariable Integer id, RedirectAttributes redirectAttributes, Principal principal) {

        User currentUser = userRepository.getUserByEmail(principal.getName());

        try {
            inventoryItemService.approveItem(id, currentUser);
            redirectAttributes.addFlashAttribute("successMessage", "Item approved successfully");
        } catch (AccessDeniedException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Access denied: " + e.getMessage());
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Item not found: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }

        return "redirect:/inventory/items/pending-approvals";
    }


}