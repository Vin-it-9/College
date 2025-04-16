package org.nexus.entity.classroom;


import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import org.nexus.entity.*;

import java.util.*;

@Entity
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 20)
    private String classroomNumber;

    @Column(nullable = false, length = 100)
    private String classroomName;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User classroomTeacher;

    @Column
    private Double cost;

    @ManyToOne
    @JoinColumn(name = "department_id")
    @JsonIgnoreProperties("classroom")
    private Department department;

    @Column
    private Integer capacity;

    @Column
    private Double area;

    @Column(length = 500)
    private String description;

    @Column
    private boolean isActive = true;

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("classroom")
    @JsonManagedReference
    private Set<InventoryItem> inventoryItems = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClassroomNumber() {
        return classroomNumber;
    }

    public void setClassroomNumber(String classroomNumber) {
        this.classroomNumber = classroomNumber;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public User getClassroomTeacher() {
        return classroomTeacher;
    }

    public void setClassroomTeacher(User classroomTeacher) {
        this.classroomTeacher = classroomTeacher;
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Set<InventoryItem> getInventoryItems() {
        return inventoryItems;
    }

    public void setInventoryItems(Set<InventoryItem> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }
}
