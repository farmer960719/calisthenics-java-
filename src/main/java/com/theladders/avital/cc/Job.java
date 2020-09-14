package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.List;

public class Job {

    private final String jobName;
    private final String jobType;

    public Job(String jobName, String jobType) {
        this.jobName = jobName;
        this.jobType = jobType;
    }

    public List<String> asList() {
        return new ArrayList<>() {{
            add(jobName);
            add(jobType);
        }};

    }
}
