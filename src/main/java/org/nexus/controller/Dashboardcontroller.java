package org.nexus.controller;


import org.nexus.entity.Department;
import org.nexus.entity.InventoryItem;
import org.nexus.entity.Lab;
import org.nexus.repository.DepartmentRepository;
import org.nexus.repository.InventoryItemRepository;
import org.nexus.repository.LabRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller("/dashboard")
@RequestMapping("/dashboard")
public class Dashboardcontroller {
    
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private LabRepository labRepository;

    public void getdata(Model model) {
        List<Department> departments = departmentRepository.findAll();
        List<InventoryItem> inventoryItems = inventoryItemRepository.findAll();
        List<Lab> labs = labRepository.findAll();

        model.addAttribute("departments", departments);
        model.addAttribute("inventoryItems", inventoryItems);
        model.addAttribute("labs", labs);

    }

    @GetMapping("/director")
    public String director(Model model) {
        getdata(model);
        return "dashboard/director";
    }

    @GetMapping("/principal")
    public String principle(Model model) {
        getdata(model);
        return "dashboard/principle";
    }

    @GetMapping("/overview")
    public String overview(Model model) {
        getdata(model);
        return "dashboard/overview/overview";
    }

    @GetMapping("/{id}/labs")
    public String labs(@PathVariable int id, Model model) {
        List<Lab> labs = labRepository.findByDepartmentId(id);
        model.addAttribute("labs", labs);
        return "dashboard/overview/list";
    }

    @GetMapping("{id}/inventory")
    public String inventory(@PathVariable int id, Model model) {
        List<InventoryItem> inventoryItems = inventoryItemRepository.findByLabId(id);
        model.addAttribute("items", inventoryItems);
        return "dashboard/overview/inventory";
    }





}
