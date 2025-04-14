package org.nexus.repository;

import org.nexus.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Integer>,  PagingAndSortingRepository<User, Integer>  {

    @Query("SELECT u from User u WHERE u.email = :email")
    public User getUserByEmail(@Param("email") String email );

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoles_Name(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ',"
            + " u.lastName) LIKE %?1%")
    public Page<User> findAll(String keyword, Pageable pageable);

    public Long countById(Integer id);

    boolean existsByEmail(String email);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.id != :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Integer id);




}
