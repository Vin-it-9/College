package org.nexus.controller;


import org.nexus.service.HodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HodController {


    @Autowired
    private HodService hodService;


    @GetMapping("/dashboard/hod")
    public String showHodDashboard(Model model) {

        int totalLabs = hodService.getTotalLabsForHod();
        int totalInventory = hodService.getTotalInventoryForHod();
        int pendingRequests = hodService.getPendingRequestsForHod();
        String departmentName = hodService.getDepartmentNameForHod();

        model.addAttribute("totalLabs", totalLabs);
        model.addAttribute("totalInventory", totalInventory);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("departmentName", departmentName);

        return "dashboard/hod";
    }





}
