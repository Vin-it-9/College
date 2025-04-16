package org.nexus.controller;

import jakarta.validation.Valid;
import org.nexus.entity.*;
import org.nexus.entity.classroom.Classroom;
import org.nexus.repository.*;
import org.nexus.service.ClassroomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/classrooms")
public class ClassroomController {


    private final ClassroomService classroomService;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    @Autowired
    public ClassroomController(ClassroomService classroomService, UserRepository userRepository, DepartmentRepository departmentRepository) {
        this.classroomService = classroomService;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
    }


    @GetMapping
    public String listClassrooms(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        boolean isHOD = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("HOD"));

        List<Classroom> classrooms;

        if(isHOD) {
            User hod = userRepository.getUserByEmail(email);
            if (hod != null) {
                List<Department> departments = departmentRepository.findByHodId(hod.getId());
                if (!departments.isEmpty()) {
                    Department department = departments.get(0);
                    classrooms = classroomService.findclassroomsByDepartmentId(department.getId());
                } else {
                    classrooms = new ArrayList<>();
                }
            } else {
                classrooms = new ArrayList<>();
            }
        } else {
            classrooms = classroomService.findAllClassrooms();
        }

        model.addAttribute("classrooms", classrooms);
        return "classrooms/list";

    }

    @GetMapping("/{id}")
    public String getClassroomById(@PathVariable Integer id, Model model) {
        Optional<Classroom> classroom = classroomService.findClassroomById(id);
        if (classroom.isPresent()) {
            model.addAttribute("classroom", classroom.get());
            return "classrooms/detail";
        }
        return "redirect:/classrooms";
    }

    @GetMapping("/number/{classroomNumber}")
    public String getClassroomByNumber(@PathVariable String classroomNumber, Model model) {
        Optional<Classroom> classroom = classroomService.findClassroomByNumber(classroomNumber);
        if (classroom.isPresent()) {
            model.addAttribute("classroom", classroom.get());
            return "classrooms/detail";
        }
        return "redirect:/classrooms";
    }


    @GetMapping("/search")
    public String searchClassroomsByName(@RequestParam String name, Model model) {
        List<Classroom> classrooms = classroomService.findclassroomsByName(name);
        model.addAttribute("classrooms", classrooms);
        model.addAttribute("searchTerm", name);
        return "classrooms/search-results";
    }


    @GetMapping("/new")
    public String showCreateForm(Model model) {

        List<User> teachers = userRepository.findByRoles_Name("TEACHER");

        List<Department> departments = departmentRepository.findAll();

        model.addAttribute("classroom", new Classroom());
        model.addAttribute("teachers", teachers);
        model.addAttribute("departments", departments);

        return "classrooms/create-form";

    }

    @PostMapping
    public String createClassroom(
            @Valid @ModelAttribute Classroom classroom,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "classrooms/create-form";
        }

        try {
            Classroom createdClassroom = classroomService.createclassroom(classroom);
            redirectAttributes.addFlashAttribute("success", "Classroom created successfully!");
            return "redirect:/classrooms/" + createdClassroom.getId();
        } catch (IllegalArgumentException e) {
            result.rejectValue("classroomNumber", "error.classroom", e.getMessage());
            return "classrooms/create-form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Optional<Classroom> classroom = classroomService.findClassroomById(id);
        if (classroom.isPresent()) {
            List<User> teachers = userRepository.findByRoles_Name("TEACHER");
            List<Department> departments = departmentRepository.findAll();

            model.addAttribute("classroom", classroom.get());
            model.addAttribute("teachers", teachers);
            model.addAttribute("departments", departments);

            return "classrooms/edit-form";
        }
        return "redirect:/classrooms";
    }

    @PostMapping("/{id}")
    public String updateClassroom(@PathVariable Integer id , @Valid @ModelAttribute Classroom classroom, BindingResult result, RedirectAttributes redirectAttributes)
    {
        if(result.hasErrors()) {
            return "classrooms/edit-form";
        }
        try {
            Classroom updatedClassroom = classroomService.updateClassroom(id, classroom);
            redirectAttributes.addFlashAttribute("success", "Classroom updated successfully!");
            return "redirect:/classrooms/" + updatedClassroom.getId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/classrooms/" + id + "/edit";
        }

    }

    @PostMapping("/{id}/delete")
    public String deleteClassroom(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            classroomService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Classroom deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/classrooms";
    }

    @GetMapping("/count")
    public String getClassroomCount(Model model) {
        long count = classroomService.countclassrooms();
        model.addAttribute("count", count);
        return "classrooms/count";
    }

}
