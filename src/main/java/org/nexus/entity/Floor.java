package org.nexus.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "floors")
public class Floor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer floorNumber;

    @ManyToOne
    @JoinColumn(name = "building_id", nullable = false)
    @JsonIgnoreProperties("floors")
    private Building building;

    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("floor")
    private Set<Lab> labs  = new HashSet<>();;

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public Set<Lab> getRooms() {
        return labs;
    }

    public void setRooms(Set<Lab> labs) {
        this.labs = labs;
    }
}