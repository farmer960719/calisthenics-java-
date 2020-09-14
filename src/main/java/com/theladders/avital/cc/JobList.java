package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.List;

public class JobList {

    List<List<String>> jobList;

    public List<List<String>> single(Job job) {
        jobList.add(job.asList());
        return jobList;
    }

}
