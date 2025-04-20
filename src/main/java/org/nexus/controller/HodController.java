package org.nexus.controller;


import org.nexus.repository.BudgetRepository;
import org.nexus.service.HodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.NumberFormat;
import java.util.Locale;

@Controller
public class HodController {


    @Autowired
    private HodService hodService;

    @Autowired
    private BudgetRepository budgetRepository;


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





}
