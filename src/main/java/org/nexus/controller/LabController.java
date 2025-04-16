package org.nexus.controller;

import org.nexus.entity.Department;
import org.nexus.entity.Lab;
import org.nexus.entity.Floor;
import org.nexus.entity.User;
import org.nexus.repository.UserRepository;
import org.nexus.repository.DepartmentRepository;
import org.nexus.repository.FloorRepository;
import org.nexus.service.LabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/labs")
public class LabController {

    private final LabService labService;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final FloorRepository floorRepository;


    @Autowired
    public LabController(LabService labService, UserRepository userRepository,
                         DepartmentRepository departmentRepository, FloorRepository floorRepository) {
        this.labService = labService;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.floorRepository = floorRepository;
    }

    @GetMapping
    public String listLabs(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        boolean isHOD = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("HOD"));

        List<Lab> labs;

        if (isHOD) {
            User hod = userRepository.getUserByEmail(email);
            if (hod != null) {
                List<Department> departments = departmentRepository.findByHodId(hod.getId());
                if (!departments.isEmpty()) {
                    Department department = departments.get(0);
                    labs = labService.findLabsByDepartmentId(department.getId());
                } else {
                    labs = new ArrayList<>();
                }
            } else {
                labs = new ArrayList<>();
            }
        } else {
            labs = labService.findAllLabs();
        }

        model.addAttribute("labs", labs);
        return "labs/list";
    }

    @GetMapping("/{id}")
    public String getLabById(@PathVariable Integer id, Model model) {
        Optional<Lab> lab = labService.findLabById(id);
        if (lab.isPresent()) {
            model.addAttribute("lab", lab.get());
            return "labs/detail";
        }
        return "redirect:/labs";
    }

    @GetMapping("/number/{labNumber}")
    public String getLabByNumber(@PathVariable String labNumber, Model model) {
        Optional<Lab> lab = labService.findLabByLabNumber(labNumber);
        if (lab.isPresent()) {
            model.addAttribute("lab", lab.get());
            return "labs/detail";
        }
        return "redirect:/labs";
    }

    @GetMapping("/search")
    public String searchLabsByName(@RequestParam String name, Model model) {
        List<Lab> labs = labService.findLabsByLabName(name);
        model.addAttribute("labs", labs);
        model.addAttribute("searchTerm", name);
        return "labs/search-results";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {

        List<User> assistants = userRepository.findByRoles_Name("LabAssistant");
        List<User> teachers = userRepository.findByRoles_Name("Teacher");

        List<Department> departments = departmentRepository.findAll();
        List<Floor> floors = floorRepository.findAll();

        model.addAttribute("lab", new Lab());
        model.addAttribute("assistants", assistants);
        model.addAttribute("teachers", teachers);
        model.addAttribute("departments", departments);
        model.addAttribute("floors", floors);

        return "labs/create-form";

    }

    @PostMapping
    public String createLab(@Valid @ModelAttribute Lab lab,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "labs/create-form";
        }

        try {
            Lab createdLab = labService.createLab(lab);
            redirectAttributes.addFlashAttribute("success", "Lab created successfully!");
            return "redirect:/labs/" + createdLab.getId();
        } catch (IllegalArgumentException e) {
            result.rejectValue("labNumber", "error.lab", e.getMessage());
            return "labs/create-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Optional<Lab> lab = labService.findLabById(id);
        if (lab.isPresent()) {
            List<User> assistants = userRepository.findByRoles_Name("LABASSISTANT");
            List<User> teachers = userRepository.findByRoles_Name("TEACHER");
            String additionalFacility = lab.get().getAdditionalFacility();
            List<Department> departments = departmentRepository.findAll();
            List<Floor> floors = floorRepository.findAll();

            model.addAttribute("lab", lab.get());
            model.addAttribute("additionalFacility" , additionalFacility);
            model.addAttribute("assistants", assistants);
            model.addAttribute("teachers", teachers);
            model.addAttribute("departments", departments);
            model.addAttribute("floors", floors);

            return "labs/edit-form";
        }
        return "redirect:/labs";
    }

    @PostMapping("/{id}")
    public String updateLab(@PathVariable Integer id,
                            @Valid @ModelAttribute Lab labDetails,
                            BindingResult result,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "labs/edit-form";
        }

        try {
            Lab updatedLab = labService.updateLab(id, labDetails);
            redirectAttributes.addFlashAttribute("success", "Lab updated successfully!");
            return "redirect:/labs/" + updatedLab.getId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/labs/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteLab(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            labService.deleteLab(id);
            redirectAttributes.addFlashAttribute("success", "Lab deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/labs";
    }

    @GetMapping("/count")
    public String getLabCount(Model model) {
        long count = labService.countLabs();
        model.addAttribute("count", count);
        return "labs/count";
    }
}