package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.List;

public class Applied {
    public List<Job> applied =new ArrayList<Job>();

    public List<List<String>> getElement(String employerName) {
        for (Job job : applied) {
            if (job.isEqualEmployerName(employerName)) {
                return job.list;
            }
        }
        return new ArrayList<>();
    }

    private Integer position(String employerName) {
        for (Job job : applied) {
            if (job.isEqualEmployerName(employerName)) {
                return applied.indexOf(job);
            }
        }
        return -1;
    }

    public void put(Job job) {
        if (job.size() > 1)
            applied.set(position(job.name), job);
        applied.add(job);
    }
}
