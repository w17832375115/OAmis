package com.ujiuye.mis.controller;

import com.ujiuye.mis.mapper.SourcesMapper;
import com.ujiuye.mis.pojp.Sources;
import com.ujiuye.mis.service.SourcesService;
import com.ujiuye.mis.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("source")
public class SourcesController {

    @Autowired
    private SourcesService sourcesService;

    @GetMapping("list")
    public R list(){
        Sources root = sourcesService.findRoot();
        return ObjectUtils.isEmpty(root)?R.error().msg("查询失败"):R.ok().data("item",root);
    }

    @PostMapping("save")
    public R save(@RequestBody Sources sources){
        boolean ret = sourcesService.save(sources);
        return ret?R.ok():R.error().msg("保存失败");
    }

    @GetMapping("all")
    public R findAll(){
        List<Sources> list = sourcesService.findAll();
        return CollectionUtils.isEmpty(list)?R.error().msg("查询失败"):R.ok().data("items",list);
    }
    @GetMapping("byPid")
    public R findByPid(){
        List<Sources> list =  sourcesService.findByPid();
        return ObjectUtils.isEmpty(list)?R.error().msg("查询失败"):R.ok().data("items",list);
    }

    @GetMapping("{id}")
    public R findById(@PathVariable int id){
        Sources sources = sourcesService.findById(id);
        return ObjectUtils.isEmpty(sources)?R.error().msg("查询失败"):R.ok().data("item",sources);
    }

    @PostMapping("update")
    public R update(@RequestBody Sources sources){
        boolean ret = sourcesService.update(sources);
        return ret?R.ok():R.error().msg("操作失败");
    }

    @PostMapping("{id}")
    public R delete(@PathVariable int id){
        boolean ret = sourcesService.delete(id);
        return ret?R.ok():R.error().msg("操作失败");
    }

    @GetMapping("own")
    public R own(HttpSession session){
        //TODO(角色ID)
        int eid = (Integer) session.getAttribute("active");
        List<Sources> list = sourcesService.findByEid(eid);
        return CollectionUtils.isEmpty(list)?R.error().msg("查询失败"):R.ok().data("items",list);
    }

}
