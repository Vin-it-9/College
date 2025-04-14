package org.nexus.entity.transferDTO;

public class DepartmentInventorySummary {

    private Integer departmentId;
    private String departmentName;
    private Long totalInventory;

    public DepartmentInventorySummary(Integer departmentId, String departmentName, Long totalInventory) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.totalInventory = totalInventory;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public Long getTotalInventory() {
        return totalInventory;
    }
}