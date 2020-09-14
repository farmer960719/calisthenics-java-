package com.theladders.avital.cc;

import java.util.List;

public class Job {
    public String name;
    public List<List<String>> list;

    public Job(String name, List<List<String>> list) {
        this.name = name;
        this.list = list;
    }

    public Job() {
    }

    Integer size(){
        return list.size();
    }

    boolean isEqualEmployerName(String employerName) {
        return name == employerName;
    }
}
