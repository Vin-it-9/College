package org.nexus.repository;

import org.nexus.entity.Floor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface FloorRepository extends JpaRepository<Floor, Integer> {

    List<Floor> findByBuildingId(Integer buildingId);

    Optional<Floor> findByFloorNumberAndBuildingId(Integer floorNumber, Integer buildingId);

    boolean existsByFloorNumberAndBuildingId(Integer floorNumber, Integer buildingId);

    @Query("SELECT f FROM Floor f WHERE f.building.id = :buildingId ORDER BY f.floorNumber ASC")
    List<Floor> findByBuildingIdOrderByFloorNumberAsc(Integer buildingId);

    @Query("SELECT f FROM Floor f JOIN f.rooms r GROUP BY f.id ORDER BY COUNT(r) DESC")
    List<Floor> findAllOrderByRoomCountDesc();

    @Query("SELECT COUNT(f) FROM Floor f WHERE f.building.id = :buildingId")
    long countByBuildingId(Integer buildingId);

    @Query("SELECT f FROM Floor f WHERE SIZE(f.rooms) = 0")
    List<Floor> findFloorsWithNoRooms();

}
