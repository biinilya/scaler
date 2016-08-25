package com.toprater.sync.scaler.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class StartMessage implements Serializable {
    public List<Map<String,Object>> getList() {
        return list;
    }

    public void setList(List<Map<String,Object>> list) {
        this.list = list;
    }

    private List<Map<String,Object>> list = new ArrayList<>();

}
