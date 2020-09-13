package com.theladders.avital.cc;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Jobs {

    private final HashMap<String, List<List<String>>> jobs = new HashMap<>();

    public List<List<String>> findBy(String employerName) {
        return jobs.get(employerName);
    }

    void publishCommand(String command, String employerName, String jobName, String jobType) throws NotSupportedJobTypeException {
        if (command != "publish") {
            return;
        }
        if (!jobType.equals("JReq") && !jobType.equals("ATS")) {
            throw new NotSupportedJobTypeException();
        }

        List<List<String>> alreadyPublished = jobs.getOrDefault(employerName, new ArrayList<>());

        alreadyPublished.add(new ArrayList<String>() {{
            add(jobName);
            add(jobType);
        }});
        jobs.put(employerName, alreadyPublished);
    }

    void saveCommand(String command, String employerName, String jobName, String jobType) {
        if (command != "save") {
            return;
        }
        List<List<String>> saved = jobs.getOrDefault(employerName, new ArrayList<>());
        saved.add(new JobList(jobName,jobType).list());
        jobs.put(employerName, saved);
    }
}
