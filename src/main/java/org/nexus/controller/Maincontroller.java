package org.nexus.controller;

import org.nexus.repository.RoleRepository;
import org.nexus.repository.UserRepository;
import org.nexus.entity.Role;
import org.nexus.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class Maincontroller {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping("")
    public String viewHomePage() {
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
                new Role("Lab Assistant", "manage lab equipment, update inventory status, and report damage or loss"),
                new Role("Accountant", "manage purchase orders, payments, and inventory budgets"),
                new Role("Maintenance Staff", "track maintenance-related items and update their status"),
                new Role("TNP Officer", "manage inventory related to placement activities, seminars, and workshops"),
                new Role("Management Staff", "oversee administrative supplies and resources, handle inventory reports"),
                new Role("Library Staff", "manage books, journals, and other educational materials inventory"),
                new Role("IT Support", "manage technological equipment inventory like computers, routers, etc."),
                new Role("Clerk", "assist in managing departmental inventory records and documentation")
        );


        roles.forEach(role -> {
            if (!roleRepository.existsByName(role.getName())) {
                roleRepository.save(role);
            }
        });
    }

    @PostMapping("/logout")
    public String logout() {
        return "redirect:/login";
    }

}
