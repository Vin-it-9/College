package org.nexus.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class SeminarHall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 20)
    private String Number;

    @Column(nullable = false, length = 100)
    private String Name;

    @Column
    private Double cost;

    @ManyToOne
    @JoinColumn(name = "department_id")
    @JsonIgnoreProperties("seminarhall")
    private Department department;

    @Column
    private Integer capacity;

    @Column
    private Double area;

    @Column(length = 500)
    private String description;

    @Column
    private boolean isActive = true;

    @OneToMany(mappedBy = "SeminarHall", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("SeminarHall")
    @JsonManagedReference
    private Set<InventoryItem> inventoryItems = new HashSet<>();


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String number) {
        Number = number;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Set<InventoryItem> getInventoryItems() {
        return inventoryItems;
    }

    public void setInventoryItems(Set<InventoryItem> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }
}
