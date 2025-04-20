package org.nexus.controller;


import org.nexus.entity.*;
import org.nexus.repository.InventoryItemRepository;
import org.nexus.repository.LabRepository;
import org.nexus.repository.UserRepository;
import org.nexus.service.HodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HodController {


    @Autowired
    private HodService hodService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabRepository labRepository;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @GetMapping("/dashboard/labassitant")
    public String showLabassitantDashboard(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User labAssistant = userRepository.getUserByEmail(email);

        if (labAssistant != null) {
            List<Lab> assignedLabs = labRepository.findByLabAssistant(labAssistant);
            model.addAttribute("labs", assignedLabs);
            model.addAttribute("totalLabs", assignedLabs.size());

            if (assignedLabs.size() == 1) {
                model.addAttribute("labName", assignedLabs.get(0).getLabName());
            }

            Set<Integer> labIds = assignedLabs.stream().map(Lab::getId).collect(Collectors.toSet());
            List<InventoryItem> inventoryItems = new ArrayList<>();

            if (!labIds.isEmpty()) {
                inventoryItems = inventoryItemRepository.findByLabIdIn(labIds);
            }

            model.addAttribute("totalInventory", inventoryItems.size());

            LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
            LocalDate today = LocalDate.now();

            long warrantyExpiringCount = inventoryItems.stream()
                    .filter(item -> item.getWarrantyExpiryDate() != null &&
                            !item.getWarrantyExpiryDate().isBefore(today) &&
                            item.getWarrantyExpiryDate().isBefore(thirtyDaysFromNow))
                    .count();
            model.addAttribute("warrantyExpiringCount", warrantyExpiringCount);

            Set<InventoryCategory> categories = inventoryItems.stream()
                    .map(InventoryItem::getCategory)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            model.addAttribute("categoriesCount", categories.size());

            Map<InventoryStatus, Long> statusCounts = inventoryItems.stream()
                    .collect(Collectors.groupingBy(InventoryItem::getStatus, Collectors.counting()));

            model.addAttribute("availableCount", statusCounts.getOrDefault(InventoryStatus.AVAILABLE, 0L));
            model.addAttribute("inUseCount", statusCounts.getOrDefault(InventoryStatus.IN_USE, 0L));
            model.addAttribute("maintenanceCount", statusCounts.getOrDefault(InventoryStatus.UNDER_MAINTENANCE, 0L));
            model.addAttribute("damagedCount", statusCounts.getOrDefault(InventoryStatus.DAMAGED, 0L));
            model.addAttribute("retiredCount", statusCounts.getOrDefault(InventoryStatus.RETIRED, 0L));
            model.addAttribute("lostCount", statusCounts.getOrDefault(InventoryStatus.LOST, 0L));
        } else {
            model.addAttribute("labs", new ArrayList<Lab>());
            model.addAttribute("totalLabs", 0);
            model.addAttribute("totalInventory", 0);
            model.addAttribute("warrantyExpiringCount", 0);
            model.addAttribute("categoriesCount", 0);
            model.addAttribute("availableCount", 0);
            model.addAttribute("inUseCount", 0);
            model.addAttribute("maintenanceCount", 0);
            model.addAttribute("damagedCount", 0);
            model.addAttribute("retiredCount", 0);
            model.addAttribute("lostCount", 0);
        }

        return "dashboard/labassitant";
    }

    @GetMapping("/dashboard/teacher")
    public String showLabInchargeDashboard(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User labIncharge = userRepository.getUserByEmail(email);

        if (labIncharge != null) {
            List<Lab> managedLabs = labRepository.findByLabTeacher(labIncharge);
            model.addAttribute("labs", managedLabs);
            model.addAttribute("totalLabs", managedLabs.size());

            if (managedLabs.size() == 1) {
                model.addAttribute("labName", managedLabs.get(0).getLabName());
            }

            Set<Integer> labIds = managedLabs.stream().map(Lab::getId).collect(Collectors.toSet());
            List<InventoryItem> inventoryItems = new ArrayList<>();

            if (!labIds.isEmpty()) {
                inventoryItems = inventoryItemRepository.findByLabIdIn(labIds);
            }

            model.addAttribute("totalInventory", inventoryItems.size());

            LocalDate thirtyDaysFromNow = LocalDate.now().plusDays(30);
            LocalDate today = LocalDate.now();

            long warrantyExpiringCount = inventoryItems.stream()
                    .filter(item -> item.getWarrantyExpiryDate() != null &&
                            !item.getWarrantyExpiryDate().isBefore(today) &&
                            item.getWarrantyExpiryDate().isBefore(thirtyDaysFromNow))
                    .count();
            model.addAttribute("warrantyExpiringCount", warrantyExpiringCount);

            Set<InventoryCategory> categories = inventoryItems.stream()
                    .map(InventoryItem::getCategory)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            model.addAttribute("categoriesCount", categories.size());

            Map<InventoryStatus, Long> statusCounts = inventoryItems.stream()
                    .collect(Collectors.groupingBy(InventoryItem::getStatus, Collectors.counting()));

            model.addAttribute("availableCount", statusCounts.getOrDefault(InventoryStatus.AVAILABLE, 0L));
            model.addAttribute("inUseCount", statusCounts.getOrDefault(InventoryStatus.IN_USE, 0L));
            model.addAttribute("maintenanceCount", statusCounts.getOrDefault(InventoryStatus.UNDER_MAINTENANCE, 0L));
            model.addAttribute("damagedCount", statusCounts.getOrDefault(InventoryStatus.DAMAGED, 0L));
            model.addAttribute("retiredCount", statusCounts.getOrDefault(InventoryStatus.RETIRED, 0L));
            model.addAttribute("lostCount", statusCounts.getOrDefault(InventoryStatus.LOST, 0L));
        } else {
            model.addAttribute("labs", new ArrayList<Lab>());
            model.addAttribute("totalLabs", 0);
            model.addAttribute("totalInventory", 0);
            model.addAttribute("warrantyExpiringCount", 0);
            model.addAttribute("categoriesCount", 0);
            model.addAttribute("availableCount", 0);
            model.addAttribute("inUseCount", 0);
            model.addAttribute("maintenanceCount", 0);
            model.addAttribute("damagedCount", 0);
            model.addAttribute("retiredCount", 0);
            model.addAttribute("lostCount", 0);
        }

        return "dashboard/teacher";
    }



    @GetMapping("/dashboard/hod")
    public String showHodDashboard(Model model) {

        int totalLabs = hodService.getTotalLabsForHod();
        int totalInventory = hodService.getTotalInventoryForHod();
        int pendingRequests = hodService.getPendingRequestsForHod();
        String departmentName = hodService.getDepartmentNameForHod();

        Double totalBudgetValue = hodService.getTotalBudget();


        String totalBudget;
        if (totalBudgetValue >= 100000) {
            double lakhValue = totalBudgetValue / 100000.0;
            if (lakhValue % 1 == 0) {
                totalBudget = "₹ " + (int)lakhValue + "L";
            } else {
                totalBudget = "₹ " + String.format("%.1f", lakhValue) + "L";
            }
        } else {
            totalBudget = "₹ " + NumberFormat.getNumberInstance(new Locale("en", "IN"))
                    .format(totalBudgetValue);
        }


        model.addAttribute("totalLabs", totalLabs);
        model.addAttribute("totalBudget", totalBudget);
        model.addAttribute("totalInventory", totalInventory);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("departmentName", departmentName);

        return "dashboard/hod";
    }


    @GetMapping("/seminar-halls")
    public String showSeminarHalls(Model model) {
        return "seminarhall/list";
    }

}
