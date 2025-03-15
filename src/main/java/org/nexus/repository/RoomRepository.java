package org.nexus.repository;

import org.nexus.entity.Room;
import org.nexus.entity.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.*;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    List<Room> findByFloorId(Integer floorId);

    List<Room> findByFloorBuildingId(Integer buildingId);

    List<Room> findByRoomType(RoomType roomType);

    Optional<Room> findByRoomNumberAndFloorId(String roomNumber, Integer floorId);

    boolean existsByRoomNumberAndFloorId(String roomNumber, Integer floorId);

    @Query("SELECT r FROM Room r JOIN r.floor f JOIN f.building b WHERE b.id = :buildingId AND r.roomType = :roomType")
    List<Room> findByBuildingIdAndRoomType(Integer buildingId, RoomType roomType);

    @Query("SELECT COUNT(r) FROM Room r WHERE r.floor.id = :floorId")
    long countByFloorId(Integer floorId);


}
