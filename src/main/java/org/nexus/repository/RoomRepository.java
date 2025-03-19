package org.nexus.repository;

import org.nexus.entity.Lab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.*;

@Repository
public interface RoomRepository extends JpaRepository<Lab, Integer> {


}
