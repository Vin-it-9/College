package org.nexus.controller;

import jakarta.validation.Valid;
import org.nexus.entity.*;
import org.nexus.repository.*;
import org.nexus.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@RequestMapping("/requests")
public class RequestController {

    @Autowired
    private RequestService requestService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;


    @GetMapping("/new")
    public String showNewRequestForm(Model model) {

        addCommonAttributes(model);
        model.addAttribute("request", new Request());
        return "requests/new-request";
    }

    @PostMapping
    public String createRequest(@Valid @ModelAttribute("request") Request requestForm,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        if (bindingResult.hasErrors()) {
            addCommonAttributes(model);
            return "requests/new-request";
        }

        Request createdRequest = requestService.createRequest(
                requestForm.getSubject(),
                requestForm.getMessage()
        );

        redirectAttributes.addFlashAttribute("message", "Request has been submitted successfully!");
        return "redirect:/requests";
    }


    @GetMapping
    public String listRequests(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.getUserByEmail(email);

        Optional<Department> hodDepartment = departmentRepository.findByHod(currentUser);
        boolean isHod = hodDepartment.isPresent();

        List<Request> requests;
        if (isHod) {
            Department department = hodDepartment.get();
            requests = requestService.getRequestsByDepartment(department.getId());
            model.addAttribute("isHod", true);
            model.addAttribute("departmentName", department.getDepartmentName());
        } else {
            requests = requestService.getCurrentUserRequests();
            model.addAttribute("isHod", false);
        }

        model.addAttribute("requests", requests);
        addCommonAttributes(model);

        return "requests/list-requests";
    }

    @GetMapping("/{id}")
    public String viewRequest(@PathVariable("id") Integer id, Model model) {
        Request request = requestService.getRequestById(id);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.getUserByEmail(email);

        Optional<Department> hodDepartment = departmentRepository.findByHod(currentUser);
        boolean isHod = hodDepartment.isPresent();

        boolean isFromHodDepartment = false;
        if (isHod && request.getUser() != null) {

            Department requesterDepartment = null;
            List<Department> departments = departmentRepository.findByHodId(currentUser.getId());
            if (!departments.isEmpty()) {
                requesterDepartment = departments.get(0);
            }

            if (requesterDepartment != null) {
                isFromHodDepartment = true;
            }
        }

        boolean isAuthorized = request.getUser().getId().equals(currentUser.getId()) ||
                (isHod && isFromHodDepartment);

        if (!isAuthorized) {
            return "error/403";
        }

        model.addAttribute("request", request);
        model.addAttribute("isHod", isHod);
        addCommonAttributes(model);

        return "requests/view-request";
    }

    @GetMapping("/pending-approvals")
    public String pendingApprovals(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User currentUser = userRepository.getUserByEmail(email);

        Optional<Department> hodDepartment = departmentRepository.findByHod(currentUser);
        boolean isHod = hodDepartment.isPresent();

        if (!isHod) {
            return "error/403";
        }

        Department department = hodDepartment.get();
        List<Request> pendingRequests = requestService.getPendingRequestsByDepartment(department.getId());

        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("departmentName", department.getDepartmentName());
        addCommonAttributes(model);

        return "requests/pending-approvals";
    }

    @PostMapping("/{id}/approve")
    public String approveRequest(@PathVariable("id") Integer id,
                                 @RequestParam("comments") String comments,
                                 RedirectAttributes redirectAttributes) {
        requestService.approveRequest(id, comments);
        redirectAttributes.addFlashAttribute("message", "Request has been approved successfully!");
        return "redirect:/requests/pending-approvals";
    }

    @PostMapping("/{id}/reject")
    public String rejectRequest(@PathVariable("id") Integer id,
                                @RequestParam("comments") String comments,
                                RedirectAttributes redirectAttributes) {
        requestService.rejectRequest(id, comments);
        redirectAttributes.addFlashAttribute("message", "Request has been rejected.");
        return "redirect:/requests/pending-approvals";
    }


    private void addCommonAttributes(Model model) {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userLogin = auth.getName();

        model.addAttribute("currentDateTime", formattedDateTime);
        model.addAttribute("userLogin", userLogin);
    }
}