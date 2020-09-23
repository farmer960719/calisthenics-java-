package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.List;

public class Job {
    private String name;
    private JobType type;


    public Job(String name, String type) {
        this.name = name;
        this.type = new JobType(type);
    }

    
    
    public String getName() {
        return name;
    }


    public String getType() {
        return type.getName();
    }

    void isValidType() throws NotSupportedJobTypeException {
        if (!getType().equals(JobType.J_REQ) && !getType().equals(JobType.ATS)) {
            throw new NotSupportedJobTypeException();
        }
    }

    public static List<String> asString(Job job) {
        return new ArrayList<String>(){{
            add(job.getName());
            add(job.getType());
        }};
    }
}
