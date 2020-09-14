package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JobList {

    private final List<Job> jobList ;

    public JobList() {
        jobList = new ArrayList<>();
    }

    public static JobList storeJob(Job job) {
        JobList jobList=new JobList();
        jobList.jobList.add(job);
        return jobList;
    }


    public List<List<String>> asList() {
        return jobList.stream()
                .map(Job::asString)
                .collect(Collectors.toList());
    }

}
