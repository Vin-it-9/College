package org.nexus.repository;

import org.nexus.entity.Lab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabRepository extends JpaRepository<Lab, Integer> {

    Optional<Lab> findByLabNumber(String labNumber);
    List<Lab> findByLabNameContainingIgnoreCase(String labName);
    boolean existsByLabNumber(String labNumber);

}
