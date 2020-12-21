package com.ujiuye.mis.service.impl;

import com.github.pagehelper.PageHelper;
import com.ujiuye.mis.mapper.RoleMapper;
import com.ujiuye.mis.mapper.RoleSourcesMapper;
import com.ujiuye.mis.mapper.SourcesMapper;
import com.ujiuye.mis.pojp.Role;
import com.ujiuye.mis.pojp.RoleSources;
import com.ujiuye.mis.pojp.RoleSourcesExample;
import com.ujiuye.mis.pojp.Sources;
import com.ujiuye.mis.service.RoleService;
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
public class RoleServiceImpl implements RoleService {
    private static final Log log = LogFactory.getLog(RoleSources.class);

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RoleSourcesMapper roleSourcesMapper;

    @Autowired
    private SourcesMapper sourcesMapper;

    @Override
    public List<Role> findAll() {

        return roleMapper.selectByExample(null);
    }

    @Override
    public List<Role> page(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);

        return roleMapper.selectByExample(null);
    }

    @Override
    @Transactional
    public boolean save(Role role, String ids) {

        try {
            roleMapper.insertSelective(role);
            Integer roleid = role.getRoleid();
            List<Integer> sourceIds = Arrays.stream(ids.split("-")).map(id -> Integer.valueOf(id)).collect(Collectors.toList());

//        for (Integer sourceId : sourceIds) {
//            roleSourcesMapper.insertSelective(new RoleSources(sourceId,roleid));
//        }
            sourceIds.stream().forEach(id -> roleSourcesMapper.insertSelective(new RoleSources(id,roleid)));
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            log.info("添加失败,手动回滚");
            //事务手动回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }


    }

    @Override
    public Role findById(int id) {
        //查询指定的角色
        Role role = roleMapper.selectByPrimaryKey(id);
        //查询该角色所对应的权限ID
        RoleSourcesExample roleSourcesExample = new RoleSourcesExample();
        roleSourcesExample.createCriteria().andRoleFkEqualTo(id);
        List<RoleSources> roleSources = roleSourcesMapper.selectByExample(roleSourcesExample);
        //将中间表变成资源id的集合
        List<Integer> sourceIds = roleSources.stream().map(rs -> rs.getResourcesFk()).collect(Collectors.toList());

        List<Sources> sources = sourcesMapper.selectByExample(null);

        //打勾
        for (Sources source : sources) {
            if(sourceIds.contains(source.getId())){
                source.setChecked(true);
            }
        }
        //复制给role
        role.setSourcesList(sources);
        return role;
    }

    @Override
    @Transactional
    public boolean update(Role role, String ids) {
        try {
            //更新role的信息
            roleMapper.updateByPrimaryKey(role);

            //更新中间表的信息
            RoleSourcesExample roleSourcesExample = new RoleSourcesExample();
            roleSourcesExample.createCriteria().andRoleFkEqualTo(role.getRoleid());
            roleSourcesMapper.deleteByExample(roleSourcesExample);

            //修改后的权限ID
            List<Integer> sourceIds = Arrays.stream(ids.split("-")).map(id -> Integer.valueOf(id)).collect(Collectors.toList());
            for (Integer sourceId : sourceIds) {
                roleSourcesMapper.insertSelective(new RoleSources(sourceId,role.getRoleid()));
            }
            return true;
        } catch (Exception e) {
            log.info("更新失败");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return false;
        }

    }

    @Override
    @Transactional
    public boolean delete(String ids) {
        try {
            List<Integer> roleidList = Arrays.stream(ids.split("-")).map(id -> Integer.valueOf(id)).collect(Collectors.toList());
            for (Integer id : roleidList) {
                RoleSourcesExample roleSourcesExample = new RoleSourcesExample();
                roleSourcesExample.createCriteria().andRoleFkEqualTo(id);
                roleSourcesMapper.deleteByExample(roleSourcesExample);

                roleMapper.deleteByPrimaryKey(id);
            }
            return true;
        } catch (Exception e) {
            log.info("删除失败");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            return false;
        }

    }
}
