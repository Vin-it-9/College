package org.nexus.controller;

import org.nexus.entity.transferDTO.DepartmentInventorySummary;
import org.nexus.repository.*;
import org.nexus.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class Maincontroller {


    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("")
    public String viewHomePage(Model model) {

        long totalInventory = inventoryItemRepository.count();

        long totalDepartments = departmentRepository.count();

        List<DepartmentInventorySummary> departmentInventories = departmentRepository.getDepartmentsWithInventoryCounts();

        List<InventoryItem> topItems = inventoryItemRepository.findTopItemsByQuantity(
                PageRequest.of(0, 5)
        );

        model.addAttribute("totalInventory", totalInventory);
        model.addAttribute("totalDepartments", totalDepartments);
        model.addAttribute("departmentInventories", departmentInventories);
        model.addAttribute("topInventoryItems", topItems);



        return "index" ;
    }



    @GetMapping("/login")
    public String viewLoginPage() {

        if (!userRepository.existsByEmail("admin@gmail.com")) {
            testCreateRestRoles();
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            User adminUser = new User("admin@gmail.com", encoder.encode("admin"), "admin", "shinde");
            Role roleAdmin = roleRepository.findByName("Admin")
                    .orElseThrow(() -> new RuntimeException("Admin role not found!"));
            adminUser.addRole(roleAdmin);
            adminUser.setEnabled(true);
            userRepository.save(adminUser);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "login";
        }
        return "redirect:/";

    }

    public void testCreateRestRoles() {

        List<Role> roles = List.of(
                new Role("Admin", "manage everything"),
                new Role("Principal", "oversee all college operations, approve major inventory requests"),
                new Role("Director", "supervise specific departments or branches, manage high-level inventory decisions"),
                new Role("HOD", "manage department-specific inventory requests, approvals, and tracking"),
                new Role("Faculty", "request inventory items, view available items, and track order status"),
                new Role("LabAssistant", "manage lab equipment, update inventory status, and report damage or loss"),
                new Role("Teacher", "Teaching")

        );
        roles.forEach(role -> {
            if (!roleRepository.existsByName(role.getName())) {
                roleRepository.save(role);
            }
        });
    }


    @PostMapping("/logout")
    public String logout() {
        return "redirect:/";
    }

}
