package org.nexus.repository;

import org.nexus.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {




}
