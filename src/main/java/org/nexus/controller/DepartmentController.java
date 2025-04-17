package org.nexus.controller;

import org.nexus.entity.Department;
import org.nexus.entity.User;
import org.nexus.repository.UserRepository;
import org.nexus.service.DepartmentService;
import org.nexus.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/departments")
public class DepartmentController {

    private final DepartmentService departmentService;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public DepartmentController(DepartmentService departmentService, UserRepository userRepository, UserService userService) {
        this.departmentService = departmentService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    // Done
    @GetMapping
    public String getAllDepartments(Model model) {
        List<Department> departments = departmentService.findAllDepartments();
        model.addAttribute("departments", departments);
        return "departments/list";
    }

    // Done
    @GetMapping("/{id}")
    public String getDepartmentById(@PathVariable Integer id, Model model) {
        Optional<Department> department = departmentService.findDepartmentById(id);
        if (department.isPresent()) {
            model.addAttribute("department", department.get());
            return "departments/detail";
        }
        return "redirect:/departments";
    }

    @GetMapping("/code/{code}")
    public String getDepartmentByCode(@PathVariable String code, Model model) {
        Optional<Department> department = departmentService.findDepartmentByCode(code);
        if (department.isPresent()) {
            model.addAttribute("department", department.get());
            return "departments/detail";
        }
        return "redirect:/departments";
    }

    @GetMapping("/search")
    public String searchDepartmentsByName(@RequestParam String name, Model model) {
        List<Department> departments = departmentService.findDepartmentsByName(name);
        model.addAttribute("departments", departments);
        model.addAttribute("searchTerm", name);
        return "departments/search-results";
    }

    // Done
    @GetMapping("/new")
    public String showCreateForm(Model model) {

        List<User> hodUsers = userRepository.findByRoles_Name("HOD");

        model.addAttribute("department", new Department());

        model.addAttribute("hodUsers", hodUsers);

        return "departments/create-form";
    }

    // Done
    @PostMapping
    public String createDepartment(@Valid @ModelAttribute Department department,
                                   BindingResult result,
                                   RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "departments/create-form";
        }

        try {
            Department createdDepartment = departmentService.createDepartment(department);
            redirectAttributes.addFlashAttribute("success", "Department created successfully!");
            return "redirect:/departments/" + createdDepartment.getId();
        } catch (IllegalArgumentException e) {
            result.rejectValue("departmentCode", "error.department", e.getMessage());
            return "departments/create-form";
        }
    }

    // Done
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Optional<Department> department = departmentService.findDepartmentById(id);
        if (department.isPresent()) {
            List<User> hodUsers = userRepository.findByRoles_Name("HOD");
            model.addAttribute("hodUsers", hodUsers);
            model.addAttribute("department", department.get());
            return "departments/edit-form";
        }
        return "redirect:/departments";
    }

    // Done
    @PostMapping("/{id}")
    public String updateDepartment(
            @PathVariable Integer id,
            @Valid @ModelAttribute("department") Department departmentDetails,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttrs
    ) {
        if (result.hasErrors()) {
            List<User> hodUsers = userRepository.findByRoles_Name("HOD");
            model.addAttribute("hodUsers", hodUsers);
            return "departments/edit-form";
        }

        try {
            Department updated = departmentService.updateDepartment(id, departmentDetails);
            redirectAttrs.addFlashAttribute("success", "Department updated successfully!");
            return "redirect:/departments/" + updated.getId();
        } catch (IllegalArgumentException ex) {
            redirectAttrs.addFlashAttribute("error", ex.getMessage());
            return "redirect:/departments/" + id + "/edit";
        }
    }

    // Done
    @PostMapping("/{id}/delete")
    public String deleteDepartment(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.deleteDepartment(id);
            redirectAttributes.addFlashAttribute("success", "Department deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/departments";
    }

    // Done
    @GetMapping("/count")
    public String getDepartmentCount(Model model) {
        long count = departmentService.countDepartments();
        model.addAttribute("count", count);
        return "departments/count";
    }
}