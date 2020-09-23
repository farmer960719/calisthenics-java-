package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FailApplicationList {


    private final List<Apply> failedApplications = new ArrayList<>();


    public Integer count(String employerName, String jobName) {
        return (int) failedApplications.stream().filter(apply -> apply.isEqualsJobName(jobName)
                && apply.isEqualsEmployerName(employerName)).count();
    }



    void addFailApplication(Apply apply) {
        failedApplications.add(apply);
    }
}
