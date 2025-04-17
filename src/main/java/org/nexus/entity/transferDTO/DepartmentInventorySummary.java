package org.nexus.entity.transferDTO;

public class DepartmentInventorySummary {

    private String departmentCode;
    private String departmentName;
    private Long totalInventory;

    public DepartmentInventorySummary(String departmentCode, String departmentName, Long totalInventory) {
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.totalInventory = totalInventory;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public Long getTotalInventory() {
        return totalInventory;
    }
}