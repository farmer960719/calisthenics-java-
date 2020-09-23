package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Jobs {
    private final HashMap<String, List<Job>> jobs = new HashMap<>();

    public List<List<String>> getJobs(String employerName) {
        List<Job> jobList = jobs.get(employerName);
        return jobList.stream().map(job -> new ArrayList<String>() {{
            add(job.getName());
            add(job.getType());
        }}).collect(Collectors.toList());
    }


    void saveJob(Employer employer, Job job) {
        List<Job> jobList = jobs.getOrDefault(employer.getName(), new ArrayList<>());
        jobList.add(job);
        jobs.put(employer.getName(), jobList);
    }


    void publishJob(Employer employer, Job job) throws NotSupportedJobTypeException {
        job.isValidType();
        saveJob(employer, job);
    }
}
