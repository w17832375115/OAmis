package com.ujiuye.mis.service;

import com.ujiuye.mis.pojp.Employee;
import com.ujiuye.mis.qo.EmployeeQuery;

import java.util.List;

public interface EmployeeService {
    List<Employee> findByPageQuery(int pageNum, int pageSize, EmployeeQuery employeeQuery);

    boolean save(Employee employee, int roleid);

    Employee findById(int eid);

    boolean update(Employee employee, int roleid);

    boolean delete(String ids);

    List<Employee> findAll();

    Employee login(String username, String password);
}
