package com.ujiuye.mis.service;

import com.ujiuye.mis.pojp.Sources;

import java.util.List;

public interface SourcesService {


    Sources findRoot();

    boolean save(Sources sources);

    List<Sources> findAll();

    List<Sources> findByPid();

    Sources findById(int id);

    boolean update(Sources sources);

    boolean delete(int id);

    List<Sources> findByEid(int eid);
}
