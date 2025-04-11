package org.nexus.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

import java.util.*;


@Entity
@Table(name = "labs")
public class Lab {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 20)
    private String labNumber;

    @Column(nullable = false, length = 100)
    private String labName;

    @ManyToOne
    @JoinColumn(name = "assistant_id")
    private User labAssistant;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User labTeacher;

    @Column
    private Double cost;

    @ManyToOne
    @JoinColumn(name = "department_id")
    @JsonIgnoreProperties("labs")
    private Department department;

    @Column
    private Integer capacity;

    @Column
    private Double area;

    @Column(length = 500)
    private String description;

    @Column
    private Boolean isActive = true;

    @OneToMany(mappedBy = "lab", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("lab")
    @JsonManagedReference
    private Set<InventoryItem> inventoryItems = new HashSet<>();

    @Column(nullable = true , length = 200)
    private String additionalFacility;

    @ManyToOne
    @JoinColumn(name = "floor_id", nullable = true)
    @JsonBackReference(value = "floor-room")
    private Floor floor;

    public String getAdditionalFacility() {
        return additionalFacility;
    }

    public void setAdditionalFacility(String additionalFacility) {
        this.additionalFacility = additionalFacility;
    }

    public String getLabNumber() {
        return labNumber;
    }

    public void setLabNumber(String labNumber) {
        this.labNumber = labNumber;
    }

    public String getLabName() {
        return labName;
    }

    public void setLabName(String labName) {
        this.labName = labName;
    }

    public User getLabAssistant() {
        return labAssistant;
    }

    public void setLabAssistant(User labAssistant) {
        this.labAssistant = labAssistant;
    }

    public User getLabTeacher() {
        return labTeacher;
    }

    public void setLabTeacher(User labTeacher) {
        this.labTeacher = labTeacher;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getRoomName() {
        return labName;
    }

    public void setRoomName(String roomName) {
        this.labName = roomName;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return labNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.labNumber = roomNumber;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public Floor getFloor() {
        return floor;
    }

    public void setFloor(Floor floor) {
        this.floor = floor;
    }

    public Set<InventoryItem> getInventoryItems() {
        return inventoryItems;
    }

    public void setInventoryItems(Set<InventoryItem> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

}