package com.ujiuye.mis.service.impl;

import com.github.pagehelper.PageHelper;
import com.ujiuye.mis.enums.EmployeeQueryCondition;
import com.ujiuye.mis.mapper.DeptMapper;
import com.ujiuye.mis.mapper.EmpRoleMapper;
import com.ujiuye.mis.mapper.EmployeeMapper;
import com.ujiuye.mis.mapper.RoleMapper;
import com.ujiuye.mis.pojp.*;
import com.ujiuye.mis.qo.EmployeeQuery;
import com.ujiuye.mis.service.EmployeeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Log LOG = LogFactory.getLog(EmployeeServiceImpl.class);

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private DeptMapper deptMapper;

    @Autowired
    private EmpRoleMapper empRoleMapper;
    @Autowired
    public RoleMapper roleMapper;

    @Override
    public List<Employee> findByPageQuery(int pageNum, int pageSize, EmployeeQuery employeeQuery) {

        EmployeeExample employeeExample = new EmployeeExample();
        EmployeeExample.Criteria criteria = employeeExample.createCriteria();



        //根据员工姓名进行模糊查询
        if(employeeQuery != null && employeeQuery.getCondition() == EmployeeQueryCondition.EMPLOYEE_ENAM.getCondition()){
            criteria.andEnameLike("%"+employeeQuery.getKeyword()+"%");
        }

        //根据部门名称对部门进行模糊查询,然后根据查询到的结果ID查询部门中的员工
        if(employeeQuery != null && employeeQuery.getCondition() == EmployeeQueryCondition.DEPT_DNAME.getCondition()){
            DeptExample deptExample = new DeptExample();
            deptExample.createCriteria().andDnameLike("%"+employeeQuery.getKeyword()+"%");
            List<Dept> depts = deptMapper.selectByExample(deptExample);
            List<Integer> deptIds = depts.stream().map(dept -> dept.getDeptno()).collect(Collectors.toList());

            criteria.andDfkIn(deptIds);
        }

        PageHelper.startPage(pageNum,pageSize);
        List<Employee> employees = employeeMapper.selectByExample(employeeExample);
        employees.stream().forEach(emp -> emp.setDept(deptMapper.selectByPrimaryKey(emp.getDfk())));

        return employees;
    }

    @Override
    @Transactional
    public boolean save(Employee employee, int roleid) {
        try {
            employeeMapper.insertSelective(employee);

            EmpRole empRole = new EmpRole();
            empRole.setEmpFk(employee.getEid());
            empRole.setRoleFk(roleid);

            empRoleMapper.insertSelective(empRole);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("添加失败");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }

    }

    @Override
    public Employee findById(int eid) {
        Employee employee = employeeMapper.selectByPrimaryKey(eid);

        EmpRoleExample empRoleExample = new EmpRoleExample();
        empRoleExample.createCriteria().andEmpFkEqualTo(employee.getEid());
        EmpRole empRole = empRoleMapper.selectByExample(empRoleExample).get(0);

        Role role = roleMapper.selectByPrimaryKey(empRole.getRoleFk());
        employee.setRole(role);

        return employee;
    }

    @Override
    @Transactional
    public boolean update(Employee employee, int roleid) {
        try {
            employeeMapper.updateByPrimaryKeySelective(employee);

            EmpRoleExample empRoleExample = new EmpRoleExample();
            empRoleExample.createCriteria().andEmpFkEqualTo(employee.getEid());
            empRoleMapper.deleteByExample(empRoleExample);

            EmpRole empRole = new EmpRole();
            empRole.setEmpFk(employee.getEid());
            empRole.setRoleFk(roleid);
            empRoleMapper.insertSelective(empRole);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("更新失败");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }

    @Override
    public boolean delete(String ids) {

        try {
            List<Integer> empIds = Arrays.stream(ids.split("-")).map(id -> Integer.valueOf(id)).collect(Collectors.toList());

            for (Integer empId : empIds) {
                EmpRoleExample empRoleExample = new EmpRoleExample();
                empRoleExample.createCriteria().andEmpFkEqualTo(empId);
                empRoleMapper.deleteByExample(empRoleExample);
            }
            empIds.stream().forEach(id -> employeeMapper.deleteByPrimaryKey(id));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("删除失败,手动回滚");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }


    }

    @Override
    @Transactional
    public List<Employee> findAll() {
        List<Employee> employees = employeeMapper.selectByExample(null);
        employees.stream().forEach(e ->e.setDept(deptMapper.selectByPrimaryKey(e.getEid())));

        return employees;
    }

    @Override
    public Employee login(String username, String password) {
        EmployeeExample employeeExample = new EmployeeExample();
        employeeExample.createCriteria().andUsernameEqualTo(username).andPasswordEqualTo(password);
        List<Employee> employees = employeeMapper.selectByExample(employeeExample);
        if (CollectionUtils.isEmpty(employees)){
            return null;
        }
        return employees.get(0);
    }
}
