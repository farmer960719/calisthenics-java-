package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FailApplicationList {
    private final List<List<String>> failedApplications = new ArrayList<>();
    private final List<Apply> failedApplicationsTemp = new ArrayList<>();


    public Integer count(String employerName, String jobName) {
        return (int) failedApplications.stream().filter(job -> job.get(0).equals(jobName) && job.get(3).equals(employerName)).count();
    }


    void addFailApplication(String employerName, String jobName, String jobType, LocalDate applicationTime) {
        List<String> failedApplication = new ArrayList<String>() {{
            add(jobName);
            add(jobType);
            add(applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            add(employerName);
        }};
        failedApplications.add(failedApplication);
    }
    void addFailApplicationTemp(Apply apply) {
        failedApplicationsTemp.add(apply);
    }
}
