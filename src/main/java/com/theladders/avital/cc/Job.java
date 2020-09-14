package com.theladders.avital.cc;

import java.util.ArrayList;
import java.util.List;

public class Job {

    public static final String J_REQ = "JReq";
    public static final String ATS = "ATS";
    private final String jobName;
    private final String jobType;

    public Job(String jobName, String jobType) {
        this.jobName = jobName;
        this.jobType = jobType;
    }

    public List<String> asString() {
        return new ArrayList<String>() {{
            add(jobName);
            add(jobType);
        }};

    }

    public void vailType() throws NotSupportedJobTypeException {
        if (!jobType.equals(J_REQ) && !jobType.equals(ATS)) {
            throw new NotSupportedJobTypeException();
        }
    }
}
