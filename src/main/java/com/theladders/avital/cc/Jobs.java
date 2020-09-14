package com.theladders.avital.cc;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Jobs {

    private final HashMap<String, List<List<String>>> jobs = new HashMap<>();
    private final JobList jobList = new JobList();

    public List<List<String>> findBy(String employerName) {
        return jobs.get(employerName);
    }

    void publishJob(String command, String employerName, String jobName, String jobType) throws NotSupportedJobTypeException {
        if (command != "publish") {
            return;
        }
        if (!jobType.equals("JReq") && !jobType.equals("ATS")) {
            throw new NotSupportedJobTypeException();
        }

        List<List<String>> alreadyPublished = jobs.getOrDefault(employerName, new ArrayList<>());

        alreadyPublished.add(new ArrayList<>() {{
            add(jobName);
            add(jobType);
        }});
        jobs.put(employerName, alreadyPublished);
    }

    void saveJob(String command, String employerName, Job job) {
        if (command != "save") {
            return;
        }
        jobList.jobList= jobs.getOrDefault(employerName, new ArrayList<>());
        jobs.put(employerName, jobList.single(job));
    }
    void saveCommand(String command, String employerName, String jobName, String jobType) {
       saveJob(command,employerName,new Job(jobName,jobType));
    }
}
