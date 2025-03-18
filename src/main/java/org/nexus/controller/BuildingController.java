package org.nexus.controller;

import org.nexus.entity.Building;
import org.nexus.repository.DepartmentRepository;
import org.nexus.service.BuildingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
@RequestMapping("/buildings")
public class BuildingController {

    private final BuildingService buildingService;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public BuildingController(BuildingService buildingService, DepartmentRepository departmentRepository) {
        this.buildingService = buildingService;
        this.departmentRepository = departmentRepository;
    }

//    done
    @GetMapping
    public String getAllBuildings(Model model) {
        List<Building> buildings = buildingService.findAllBuildings();
        model.addAttribute("buildings", buildings);
        return "buildings/list";
    }

    @GetMapping("/{id}")
    public String getBuildingById(@PathVariable Integer id, Model model) {
        return buildingService.findBuildingById(id)
                .map(building -> {
                    model.addAttribute("building", building);
                    return "buildings/detail";
                })
                .orElse("error/404");
    }

    @GetMapping("/code/{code}")
    public String getBuildingByCode(@PathVariable String code, Model model) {
        return buildingService.findBuildingByCode(code)
                .map(building -> {
                    model.addAttribute("building", building);
                    return "buildings/detail";
                })
                .orElse("error/404");
    }


    @GetMapping("/department/{departmentId}")
    public String getBuildingsByDepartment(@PathVariable Integer departmentId, Model model) {
        List<Building> buildings = buildingService.findBuildingsByDepartment(departmentId);
        model.addAttribute("buildings", buildings);
        return "buildings/list";
    }

    // Done
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("building", new Building());
        model.addAttribute("departments", departmentRepository.findAll()); // Add departments to the model

        return "buildings/create";
    }

    // Done
    @PostMapping
    public String createBuilding(@ModelAttribute Building building, Model model) {
        if (buildingService.existsByBuildingCode(building.getBuildingCode())) {
            model.addAttribute("error", "Building code already exists");
            model.addAttribute("departments", departmentRepository.findAll()); // Re-add departments if error
            return "buildings/create";
        }
        buildingService.createBuilding(building);
        return "redirect:/buildings";
    }

    // Done
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        return buildingService.findBuildingById(id)
                .map(building -> {
                    model.addAttribute("building", building);
                    model.addAttribute("departments", departmentRepository.findAll()); // Add departments to the model
                    return "buildings/edit";
                })
                .orElse("error/404");
    }

   //Done
    @PostMapping("/{id}")
    public String updateBuilding(@PathVariable Integer id, @ModelAttribute Building buildingDetails, Model model) {
        try {
            buildingService.updateBuilding(id, buildingDetails);
            return "redirect:/buildings";
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Building not found")) {
                return "error/404";
            }
            model.addAttribute("error", e.getMessage());
            model.addAttribute("departments", departmentRepository.findAll()); // Re-add departments on error
            return "buildings/edit";
        }
    }

    // Done
    @PostMapping("/{id}/delete")
    public String deleteBuilding(@PathVariable Integer id) {
        if (buildingService.findBuildingById(id).isPresent()) {
            buildingService.deleteBuilding(id);
            return "redirect:/buildings";
        } else {
            return "error/404";
        }
    }

    // Done
    @GetMapping("/count")
    public String getBuildingCount(Model model) {
        model.addAttribute("count", buildingService.countBuildings());
        return "buildings/count";
    }


    @GetMapping("/count/department/{departmentId}")
    public String getBuildingCountByDepartment(@PathVariable Integer departmentId, Model model) {
        model.addAttribute("count", buildingService.countBuildingsByDepartment(departmentId));
        return "buildings/count";
    }
}