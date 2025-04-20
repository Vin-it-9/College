package org.nexus.repository;

import org.nexus.entity.SeminarHall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeminarhallRepository extends JpaRepository<SeminarHall,Integer> {


}
