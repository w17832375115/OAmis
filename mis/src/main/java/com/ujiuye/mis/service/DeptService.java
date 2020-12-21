package com.ujiuye.mis.service;

import com.ujiuye.mis.pojp.Dept;

import java.util.List;

public interface DeptService {
    List<Dept> findAll();

    List<Dept> page(int pageNum, int pageSize);

    boolean save(Dept dept);

    Dept findById(int deptno);

    boolean update(Dept dept);

    boolean delete(String deptno);
}
