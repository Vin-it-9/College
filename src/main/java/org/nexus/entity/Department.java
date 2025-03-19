package org.nexus.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 10)
    private String departmentCode;

    @ManyToOne
    @JoinColumn(name = "hod_id", referencedColumnName = "id")
    private User hod;

    @Column(nullable = false, length = 100)
    private String departmentName;

    private Integer facultyCount;

    @Column(nullable = true, length = 100)
    private String departmentDescription;

    @OneToMany(mappedBy = "department")
    @JsonIgnoreProperties("department")
    private Set<Building> buildings;

    @OneToMany(mappedBy = "department")
    @JsonIgnoreProperties("department")
    @Column(nullable = true)
    private Set<Lab> labs =  new HashSet<>();;

    private Integer establishment;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public User getHod() {
        return hod;
    }

    public void setHod(User hod) {
        this.hod = hod;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Integer getFacultyCount() {
        return facultyCount;
    }

    public void setFacultyCount(Integer facultyCount) {
        this.facultyCount = facultyCount;
    }

    public String getDepartmentDescription() {
        return departmentDescription;
    }

    public void setDepartmentDescription(String departmentDescription) {
        this.departmentDescription = departmentDescription;
    }

    public Set<Building> getBuildings() {
        return buildings;
    }

    public void setBuildings(Set<Building> buildings) {
        this.buildings = buildings;
    }

    public Set<Lab> getLabs() {
        return labs;
    }

    public void setLabs(Set<Lab> labs) {
        this.labs = labs;
    }

    public Integer getEstablishment() {
        return establishment;
    }

    public void setEstablishment(Integer establishment) {
        this.establishment = establishment;
    }


}