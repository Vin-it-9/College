package org.nexus.controller;

import org.nexus.entity.Budget;
import org.nexus.repository.BudgetRepository;
import org.nexus.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/budget")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @GetMapping
    public String budget(Model model) {
        model.addAttribute("budgets", budgetService.findAll());
        model.addAttribute("budget", new Budget());
        return "budget/budget";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute Budget budget) {
        budgetService.add(budget);
        return "redirect:/budget";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("id") Integer id) {
        budgetService.delete(id);
        return "redirect:/budget";
    }

    @GetMapping("/edit")
    public String edit(@RequestParam("id") Integer id, Model model) {
        Budget budget = budgetService.findById(id).orElse(new Budget());
        model.addAttribute("budget", budget);
        return "budget/edit";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute Budget budget) {
        budgetService.update(budget);
        return "redirect:/budget";
    }
}