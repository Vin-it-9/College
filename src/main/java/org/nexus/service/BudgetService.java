package org.nexus.service;

import org.nexus.entity.Budget;
import org.nexus.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    public Budget add(Budget budget) {
        return budgetRepository.save(budget);
    }

    public Budget update(Budget budget) {
        return budgetRepository.save(budget);
    }

    public void delete(Integer id) {
        budgetRepository.deleteById(id);
    }

    public Optional<Budget> findById(Integer id) {
        return budgetRepository.findById(id);
    }

    public List<Budget> findAll() {
        return budgetRepository.findAll();
    }
}