package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.List;

public class JobList {
    public List<List<String>> jobList=new ArrayList<>();

    public JobList(String jobName, String jobType) {
        jobList.add(new ArrayList<String>() {{
            add(jobName);
            add(jobType);
        }});
    }

    public List<String> list() {
        return jobList.get(0);
    }
}
