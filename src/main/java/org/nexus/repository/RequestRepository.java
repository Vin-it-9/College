package org.nexus.repository;

import org.nexus.entity.Request;
import org.nexus.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {

    List<Request> findByUserOrderByCreatedAtDesc(User user);

    List<Request> findByStatusOrderByCreatedAtDesc(Request.RequestStatus status);

    @Query("SELECT r FROM Request r WHERE r.user.id IN " +
            "(SELECT d.hod.id FROM Department d WHERE d.id = :departmentId) " +
            "ORDER BY r.createdAt DESC")
    List<Request> findByDepartmentIdOrderByCreatedAtDesc(Integer departmentId);

    @Query("SELECT COUNT(r) FROM Request r WHERE r.status = 'PENDING' AND r.user.id IN " +
            "(SELECT d.hod.id FROM Department d WHERE d.id = :departmentId)")
    int countPendingRequestsByDepartmentId(Integer departmentId);

    @Query("SELECT r FROM Request r WHERE EXISTS " +
            "(SELECT ur FROM r.user.roles ur WHERE ur.name = :roleName) " +
            "ORDER BY r.createdAt DESC")
    List<Request> findByUserRoleOrderByCreatedAtDesc(String roleName);

}