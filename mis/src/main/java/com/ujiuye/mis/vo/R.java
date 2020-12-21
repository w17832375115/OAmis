package com.ujiuye.mis.vo;

import java.util.HashMap;
import java.util.Map;

public class R {

    private boolean status;
    private String msg;
    private Map<String,Object> data = new HashMap<>();

    public static R ok(){
         return new R(){{
            setStatus(true);
        }};
    }

    public static R error(){
        return new R (){{
           setStatus(false);
        }};
    }

    public R msg(String msg){
        this.msg = msg;
        return this;
    }

    public R data(Map<String,Object> map){
        this.data = map;
        return this;
    }

    public R data(String key,Object value){
        this.data.put(key,value);
        return this;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
