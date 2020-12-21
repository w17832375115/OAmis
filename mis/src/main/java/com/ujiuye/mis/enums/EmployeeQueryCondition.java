package com.ujiuye.mis.enums;

public enum EmployeeQueryCondition {

    EMPLOYEE_ENAM(1,"员工姓名"),
    DEPT_DNAME(2,"部门名称");

    private Integer condition;
    private String description;

    public Integer getCondition() {
        return condition;
    }

    public String getDescription() {
        return description;
    }

    EmployeeQueryCondition() {
    }

    EmployeeQueryCondition(Integer condition, String description) {
        this.condition = condition;
        this.description = description;
    }
}
