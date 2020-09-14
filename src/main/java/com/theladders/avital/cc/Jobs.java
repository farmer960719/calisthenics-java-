package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.List;

public class Jobs {
    public List<Job> jobs = new ArrayList<Job>();

    public List<List<String>> getElement(String employerName) {
        for (Job job : jobs) {
            if (job.isEqualEmployerName(employerName)) {
                return job.list;
            }
        }
        return new ArrayList<>();
    }

    private Integer position(String employerName) {
        for (Job job : jobs) {
            if (job.isEqualEmployerName(employerName)) {
                return jobs.indexOf(job);
            }
        }
        return -1;
    }

    public void put(Job job) {
        if (job.size() > 1)
            jobs.set(position(job.name), job);
        jobs.add(job);
    }


}
