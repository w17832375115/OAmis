package com.ujiuye.mis.service.impl;

import com.github.pagehelper.PageHelper;
import com.ujiuye.mis.mapper.DeptMapper;
import com.ujiuye.mis.pojp.Dept;
import com.ujiuye.mis.service.DeptService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeptServiceImpl implements DeptService {

    private static final Log LOG = LogFactory.getLog(EmployeeServiceImpl.class);

    @Autowired
    private DeptMapper deptMapper;

    @Override
    public List<Dept> findAll() {
        return deptMapper.selectByExample(null);
    }

    @Override
    public List<Dept> page(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);

        return deptMapper.selectByExample(null);
    }

    @Override
    public boolean save(Dept dept) {
        return deptMapper.insertSelective(dept)>0;
    }

    @Override
    public Dept findById(int deptno) {
        return deptMapper.selectByPrimaryKey(deptno);
    }

    @Override
    public boolean update(Dept dept) {
        return deptMapper.updateByPrimaryKeySelective(dept)>0;
    }

    @Override
    public boolean delete(String deptno) {
        try {
            List<Integer> deptnos = Arrays.stream(deptno.split("-")).map(id -> Integer.valueOf(id)).collect(Collectors.toList());
            deptnos.stream().forEach(id -> deptMapper.deleteByPrimaryKey(id));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("删除失败,手动回滚");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
    }
}
