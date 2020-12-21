package com.ujiuye.mis.controller;

import com.github.pagehelper.PageInfo;
import com.ujiuye.mis.pojp.Dept;
import com.ujiuye.mis.pojp.Employee;
import com.ujiuye.mis.service.DeptService;
import com.ujiuye.mis.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("dept")
public class DeptController {

    @Autowired
    private DeptService deptService;

    @GetMapping("all")
    public R findAll(){
        List<Dept> list = deptService.findAll();
        return CollectionUtils.isEmpty(list)? R.error().msg("查询失败"):R.ok().data("items",list);
    }

    @GetMapping("{pageNum}/{pageSize}")
    public R page(@PathVariable int pageNum,@PathVariable int pageSize){
        List<Dept> list = deptService.page(pageNum,pageSize);
        return CollectionUtils.isEmpty(list)?R.error().msg("查询失败"):R.ok().data("items",new PageInfo<>(list));
    }

    @PostMapping("save")
    public R save(@RequestBody Dept dept){
        boolean ret  = deptService.save(dept);
        return ret?R.ok():R.error().msg("保存失败");
    }

    @GetMapping("{deptno}")
    public R findById(@PathVariable int deptno){
        Dept dept = deptService.findById(deptno);
        return ObjectUtils.isEmpty(dept)? R.error().msg("查询失败"): R.ok().data("item",dept);
    }

    @PostMapping("update")
    public R update(@RequestBody Dept dept){
        boolean ret = deptService.update(dept);
        return ret?R.ok():R.error().msg("更新失败");
    }

    @PostMapping("{deptno}")
    public R delete(@PathVariable String deptno){
        boolean ret = deptService.delete(deptno);
        return ret?R.ok():R.error().msg("删除失败 ");
    }



}
