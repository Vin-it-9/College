package org.nexus.repository;


import org.nexus.entity.classroom.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRepoistory extends JpaRepository<Classroom, Integer> {


    Optional<Classroom> findByClassroomNumber(String labNumber);

    List<Classroom> findByClassroomNameContainingIgnoreCase(String name);

    boolean existsByClassroomNumber(String labNumber);

    List<Classroom> findByDepartmentId(int departmentId);

    Integer id(Integer id);
}
