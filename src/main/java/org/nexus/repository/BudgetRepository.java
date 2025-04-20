package org.nexus.repository;

import org.nexus.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {

    @Query("SELECT COALESCE(SUM(b.money), 0) FROM Budget b")
    Double getTotalBudgetAmount();





}
