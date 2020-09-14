package com.theladders.avital.cc;


import java.util.HashMap;
import java.util.List;

public class Jobs {

    public static final String SAVE = "save";
    public static final String PUBLISH = "publish";
    private final HashMap<Object, JobList> jobs = new HashMap<>();


    public List<List<String>> findBy(String employerName) {
        return jobs.get(employerName).asList();
    }

    void publishJob(String command, String employerName, Job job) throws NotSupportedJobTypeException {
        if (command != PUBLISH) {
            return;
        }
        job.vailType();
        jobs.put(employerName, JobList.storeJob(job));
    }

    void saveJob(String command, String employerName, Job job) {
        if (command != SAVE) {
            return;
        }
        jobs.put(employerName, JobList.storeJob(job));

    }

}
