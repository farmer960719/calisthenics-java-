package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Apply {
    Job job;
    String jobSeekerName;
    String resumeApplicantName;
    LocalDate applicationTime;
    String employerName;

    public Apply(Job job, String jobSeekerName, String resumeApplicantName, LocalDate applicationTime, String employerName) {
        this.job = job;
        this.jobSeekerName = jobSeekerName;
        this.resumeApplicantName = resumeApplicantName;
        this.applicationTime = applicationTime;
        this.employerName = employerName;
    }


    public List<String> asString() {
        return new ArrayList<>() {{
            add(job.getJobName());
            add(job.getJobType());
            add(applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            add(employerName);
        }};
    }

    public boolean isEqualsDate(LocalDate date) {
        return applicationTime.equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    }

    public boolean isEqualsJobName(String jobName) {
        return jobName.equals(job.getJobName());
    }

    public boolean isEqualsEmployerName(String employerName) {
        return employerName.equals(this.employerName);
    }

    public String toStringCsv(String applicant) {
        return employerName + "," + job.getJobName() + "," + job.getJobType() + "," + applicant + "," + applicationTime + "\n";
    }

    public String toStringHtml(String applicant) {
        return "<tr>" + "<td>" + employerName + "</td>" + "<td>" + job.getJobName() + "</td>" + "<td>" +
                job.getJobType() + "</td>" + "<td>" + applicant + "</td>" + "<td>" + applicationTime + "</td>" + "</tr>";
    }
}
