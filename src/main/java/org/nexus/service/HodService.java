package org.nexus.service;
import org.nexus.entity.Department;
import org.nexus.entity.InventoryItem;
import org.nexus.entity.User;
import org.nexus.repository.BudgetRepository;
import org.nexus.repository.DepartmentRepository;
import org.nexus.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HodService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryItemService inventoryItemService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private LabService labService;

    @Autowired
    private BudgetRepository budgetRepository;


    public int getTotalLabsForHod() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User hod = userRepository.getUserByEmail(email);

        if (hod != null) {
            List<Department> departments = departmentRepository.findByHodId(hod.getId());
            if (!departments.isEmpty()) {
                Department department = departments.get(0);
                return labService.findLabsByDepartmentId(department.getId()).size();
            }
        }

        return 0;
    }

    public Double getTotalBudget() {
        return budgetRepository.getTotalBudgetAmount();
    }

    public int getTotalInventoryForHod() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User hod = userRepository.getUserByEmail(email);

        if (hod != null) {
            List<Department> departments = departmentRepository.findByHodId(hod.getId());
            if (!departments.isEmpty()) {
                Department department = departments.get(0);
                return inventoryItemService.findInventoryByDepartmentId(department.getId()).size();
            }
        }

        return 0;
    }

    public int getPendingRequestsForHod() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User hod = userRepository.getUserByEmail(email);

        if (hod != null) {
            List<InventoryItem> unapprovedItems = inventoryItemService.getAllUnapprovedItemsForHod(hod);
            return unapprovedItems.size();
        }

        return 0;
    }

    public String getDepartmentNameForHod() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User hod = userRepository.getUserByEmail(email);

        if (hod != null) {
            Optional<Department> department = departmentRepository.findByHod(hod);
            if (department.isPresent()) {
                return department.get().getDepartmentName();
            }
        }

        return "No Department Assigned";
    }
}