package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Map.Entry;

public class Application {
    public static final String APPLIED = "applied";
    private final Jobs jobs = new Jobs();
    private final Applied applied = new Applied();
    final FailApplicationList failApplications = new FailApplicationList();

    public void execute(String command, String employerName, String jobName, String jobType, String jobSeekerName, String resumeApplicantName, LocalDate applicationTime) throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        jobs.publishJob(command, employerName, new Job(jobName, jobType));
        jobs.saveJob(command, employerName, new Job(jobName, jobType));
        applied.applyCommand(command, employerName, jobName, jobType, jobSeekerName, resumeApplicantName, applicationTime);
    }


    public List<List<String>> getJobs(String employerName, String type) {
        if (type.equals(APPLIED)) {
            return applied.findBy(employerName);
        }
        return jobs.findBy(employerName);
    }

    public List<String> findApplicants(String jobName, String employerName) {
        return findApplicants(jobName, employerName, null);
    }

    public List<String> findApplicants(String jobName, String employerName, LocalDate from) {
        return findApplicants(jobName, employerName, from, null);
    }

    public List<String> findApplicants(String jobName, String employerName, LocalDate from, LocalDate to) {
        List<String> result = new ArrayList<>();
        Iterator<Entry<String, List<Apply>>> iterator = getIterator();
        while (iterator.hasNext()) {
            Entry<String, List<Apply>> set = iterator.next();
            String applicant = set.getKey();
            List<Apply> jobs = set.getValue();
            boolean hasAppliedToThisJob;
            if (from == null && to == null) {
                hasAppliedToThisJob = jobs.stream().anyMatch(job -> job.isEqualsJobName(jobName));
            }
            if (jobName == null && to == null) {
                hasAppliedToThisJob = jobs.stream().anyMatch(job ->
                        !from.isAfter(LocalDate.parse(job.applicationTime.toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            }
            if (jobName == null && from == null) {
                hasAppliedToThisJob = jobs.stream().anyMatch(job ->
                        !to.isBefore(job.applicationTime));
            }
            if (jobName == null) {
                hasAppliedToThisJob = jobs.stream().anyMatch(job -> !from.isAfter(job.applicationTime) && !to.isBefore(job.applicationTime));
            }
            if (to != null) {
                hasAppliedToThisJob = jobs.stream().anyMatch(job -> job.isEqualsJobName(jobName) && !to.isBefore(job.applicationTime));
            }
            hasAppliedToThisJob = jobs.stream().anyMatch(job -> job.isEqualsJobName(jobName) && !from.isAfter(job.applicationTime));
            if (hasAppliedToThisJob) {
                result.add(applicant);

            }

        }
        return result;
    }

    private Iterator<Entry<String, List<Apply>>> getIterator() {
        return this.applied.getIterator();
    }

    public String export(String type, LocalDate date) {
        if (type == "csv") {
            return exportCsv(date);
        }
        return exportHtml(date);

    }

    private String exportCsv(LocalDate date) {
        String result = "Employer,Job,Job Type,Applicants,Date" + "\n";
        Iterator<Entry<String, List<Apply>>> iterator = getIterator();
        while (iterator.hasNext()) {
            Entry<String, List<Apply>> set = iterator.next();
            String applicant = set.getKey();
            List<Apply> jobs = set.getValue();
            List<Apply> appliedOnDate = jobs.stream().filter(job -> job.isEqualsDate(date)).collect(Collectors.toList());

            for (Apply apply : appliedOnDate) {
                result = result.concat(apply.toStringCsv(applicant));
            }
        }
        return result;
    }

    private String exportHtml(LocalDate date) {
        String content = "";
        Iterator<Entry<String, List<Apply>>> iterator = getIterator();
        while (iterator.hasNext()) {
            Entry<String, List<Apply>> set = iterator.next();
            String applicant = set.getKey();
            List<Apply> jobs = set.getValue();
            List<Apply> appliedOnDate = jobs.stream().filter(job -> job.isEqualsDate(date)).collect(Collectors.toList());

            for (Apply apply : appliedOnDate) {
                content = content.concat(apply.toStringHtml(applicant));
            }
        }

        return "<!DOCTYPE html>"
                + "<body>"
                + "<table>"
                + "<thead>"
                + "<tr>"
                + "<th>Employer</th>"
                + "<th>Job</th>"
                + "<th>Job Type</th>"
                + "<th>Applicants</th>"
                + "<th>Date</th>"
                + "</tr>"
                + "</thead>"
                + "<tbody>"
                + content
                + "</tbody>"
                + "</table>"
                + "</body>"
                + "</html>";
    }

    public int getSuccessfulApplications(String employerName, String jobName) {
        int result = 0;
        Iterator<Entry<String, List<Apply>>> iterator = getIterator();
        while (iterator.hasNext()) {
            Entry<String, List<Apply>> set = iterator.next();
            List<Apply> jobs = set.getValue();

            result += jobs.stream().anyMatch(job -> job.isEqualsEmployerName(employerName) && job.isEqualsJobName(jobName)) ? 1 : 0;
        }
        return result;
    }

    public int getUnsuccessfulApplications(String employerName, String jobName) {
        return failApplications.count(employerName, jobName);
    }

}