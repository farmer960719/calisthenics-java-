package com.theladders.avital.cc;

import java.time.LocalDate;
import java.util.List;

public class Job {
    String name;
    List<List<String>> list;

    public Job(String name, List<List<String>> list) {
        this.name = name;
        this.list = list;
    }

    public Job() {
    }
}
