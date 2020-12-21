package com.ujiuye.mis.service.impl;

import com.ujiuye.mis.mapper.EmpRoleMapper;
import com.ujiuye.mis.mapper.RoleSourcesMapper;
import com.ujiuye.mis.mapper.SourcesMapper;
import com.ujiuye.mis.pojp.*;
import com.ujiuye.mis.service.SourcesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SourcesServiceImpl implements SourcesService {

    @Autowired
    private SourcesMapper sourcesMapper;

    @Autowired
    private EmpRoleMapper empRoleMapper;

    @Autowired
    private RoleSourcesMapper roleSourcesMapper;


    @Override
    public Sources findRoot() {

        SourcesExample sourcesExample = new SourcesExample();
        sourcesExample.createCriteria().andPidEqualTo(0);
        Sources root = sourcesMapper.selectByExample(sourcesExample).get(0);

        setChildren(root);

        return root;
    }

    @Override
    public boolean save(Sources sources) {
        return sourcesMapper.insertSelective(sources) > 0;
    }

    @Override
    public List<Sources> findAll() {
        return sourcesMapper.selectByExample(null);
    }

    @Override
    public List<Sources> findByPid() {
        SourcesExample sourcesExample = new SourcesExample();
        sourcesExample.createCriteria().andPidEqualTo(1);
        return sourcesMapper.selectByExample(sourcesExample);
    }

    @Override
    public Sources findById(int id) {
        return sourcesMapper.selectByPrimaryKey(id);
    }

    @Override
    public boolean update(Sources sources) {
        return sourcesMapper.updateByPrimaryKeySelective(sources) > 0;
    }

    @Override
    public boolean delete(int id) {
        return sourcesMapper.deleteByPrimaryKey(id) > 0;
    }

    @Override
    public List<Sources> findByEid(int eid) {
        // 查询当前用户对应的role的id
        EmpRoleExample empRoleExample = new EmpRoleExample();
        empRoleExample.createCriteria().andEmpFkEqualTo(eid);
        EmpRole empRole = empRoleMapper.selectByExample(empRoleExample).get(0);

        //根据role的id查询用户所拥有的权限的id
        RoleSourcesExample roleSourcesExample = new RoleSourcesExample();
        RoleSourcesExample.Criteria criteria = roleSourcesExample.createCriteria();
        criteria.andRoleFkEqualTo(empRole.getRoleFk());
        List<RoleSources> roleSources = roleSourcesMapper.selectByExample(roleSourcesExample);
        // 权限id
        List<Integer> sourceIds = roleSources.stream().map(rs -> rs.getResourcesFk()).collect(Collectors.toList());
        // 查找对应的所有的权限
        SourcesExample sourcesExample = new SourcesExample();
        sourcesExample.createCriteria().andIdIn(sourceIds);
        List<Sources> list = sourcesMapper.selectByExample(sourcesExample);

        // 筛选出顶级权限
//        Sources root = list.stream().filter(s -> s.getPid() == 0).collect(Collectors.toList()).get(0);
        Sources root = null;
        for (Sources sources : list) {
            if (sources.getPid()==0){
                root = sources;
            }
        }

        // 筛选出二级权限
//        List<Sources> son = list.stream().filter(s -> s.getPid() == root.getId()).collect(Collectors.toList());
        List<Sources> sons = new ArrayList<>();
        for (Sources sources : list) {
            if (sources.getPid()== root.getId()){
                sons.add(sources);
            }
        }
        //为二级菜单设置对应子菜单
        for (Sources son : sons) {
            for (Sources sources : list) {
                if (sources.getPid()==son.getId()){
                    son.getChildren().add(sources);
                }
            }
        }

        return sons;
    }


    private void setChildren(Sources root) {
        SourcesExample sourcesExample2 = new SourcesExample();
        sourcesExample2.createCriteria().andPidEqualTo(root.getId());
        List<Sources> sources = sourcesMapper.selectByExample(sourcesExample2);
       if (sources != null && sources.size()>0){
           root.setChildren(sources);
           for (Sources source : sources) {//亿点点
               setChildren(source);
           }
       }

    }
}
