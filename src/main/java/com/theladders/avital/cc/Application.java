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
    private final Applied appliedTemp = new Applied();
    final FailApplicationList failApplications = new FailApplicationList();

    public void execute(String command, String employerName, String jobName, String jobType, String jobSeekerName, String resumeApplicantName, LocalDate applicationTime) throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        jobs.publishJob(command, employerName, new Job(jobName, jobType));
        jobs.saveJob(command, employerName, new Job(jobName, jobType));
        appliedTemp.applyCommand(command, employerName, jobName, jobType, jobSeekerName, resumeApplicantName, applicationTime, this);
    }


    public List<List<String>> getJobs(String employerName, String type) {
        if (type.equals(APPLIED)) {
            return appliedTemp.findBy(employerName);
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
        if (from == null && to == null) {
            List<String> result = new ArrayList<String>() {
            };
            Iterator<Entry<String, List<List<String>>>> iterator = getIterator();
            while (iterator.hasNext()) {
                Entry<String, List<List<String>>> set = iterator.next();
                String applicant = set.getKey();
                List<List<String>> jobs = set.getValue();
                boolean hasAppliedToThisJob = jobs.stream().anyMatch(job -> job.get(0).equals(jobName));
                if (hasAppliedToThisJob) {
                    result.add(applicant);
                }
            }
            return result;
        }
        if (jobName == null && to == null) {
            List<String> result = new ArrayList<String>() {
            };
            Iterator<Entry<String, List<List<String>>>> iterator = getIterator();
            while (iterator.hasNext()) {
                Entry<String, List<List<String>>> set = iterator.next();
                String applicant = set.getKey();
                List<List<String>> jobs = set.getValue();
                boolean isAppliedThisDate = jobs.stream().anyMatch(job ->
                        !from.isAfter(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                if (isAppliedThisDate) {
                    result.add(applicant);
                }
            }
            return result;
        }
        if (jobName == null && from == null) {
            List<String> result = new ArrayList<String>() {
            };
            Iterator<Entry<String, List<List<String>>>> iterator = getIterator();
            while (iterator.hasNext()) {
                Entry<String, List<List<String>>> set = iterator.next();
                String applicant = set.getKey();
                List<List<String>> jobs = set.getValue();
                boolean isAppliedThisDate = jobs.stream().anyMatch(job ->
                        !to.isBefore(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                if (isAppliedThisDate) {
                    result.add(applicant);
                }
            }
            return result;

        }
        if (jobName == null) {
            List<String> result = new ArrayList<String>() {
            };
            Iterator<Entry<String, List<List<String>>>> iterator = getIterator();
            while (iterator.hasNext()) {
                Entry<String, List<List<String>>> set = iterator.next();
                String applicant = set.getKey();
                List<List<String>> jobs = set.getValue();
                boolean isAppliedThisDate = jobs.stream().anyMatch(job -> !from.isAfter(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))) && !to.isBefore(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                if (isAppliedThisDate) {
                    result.add(applicant);
                }
            }
            return result;

        }
        if (to != null) {
            List<String> result = new ArrayList<String>() {
            };
            Iterator<Entry<String, List<List<String>>>> iterator = getIterator();
            while (iterator.hasNext()) {
                Entry<String, List<List<String>>> set = iterator.next();
                String applicant = set.getKey();
                List<List<String>> jobs = set.getValue();
                boolean isAppliedThisDate = jobs.stream().anyMatch(job -> job.get(0).equals(jobName) && !to.isBefore(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
                if (isAppliedThisDate) {
                    result.add(applicant);
                }
            }
            return result;
        }
        List<String> result = new ArrayList<String>() {
        };
        Iterator<Entry<String, List<List<String>>>> iterator = getIterator();
        while (iterator.hasNext()) {
            Entry<String, List<List<String>>> set = iterator.next();
            String applicant = set.getKey();
            List<List<String>> jobs = set.getValue();
            boolean isAppliedThisDate = jobs.stream().anyMatch(job -> job.get(0).equals(jobName) && !from.isAfter(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            if (isAppliedThisDate) {
                result.add(applicant);
            }
        }
        return result;
        
    }

    private Iterator<Entry<String, List<List<String>>>> getIterator() {
        return this.appliedTemp.getIterator();
    }

    public String export(String type, LocalDate date) {
        if (type == "csv") {
            String result = "Employer,Job,Job Type,Applicants,Date" + "\n";
            Iterator<Entry<String, List<List<String>>>> iterator = getIterator();
            while (iterator.hasNext()) {
                Entry<String, List<List<String>>> set = iterator.next();
                String applicant = set.getKey();
                List<List<String>> jobs1 = set.getValue();
                List<List<String>> appliedOnDate = jobs1.stream().filter(job -> job.get(2).equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))).collect(Collectors.toList());

                for (List<String> job : appliedOnDate) {
                    result = result.concat(job.get(3) + "," + job.get(0) + "," + job.get(1) + "," + applicant + "," + job.get(2) + "\n");
                }
            }
            return result;
        } else {
            String content = "";
            Iterator<Entry<String, List<List<String>>>> iterator = getIterator();
            while (iterator.hasNext()) {
                Entry<String, List<List<String>>> set = iterator.next();
                String applicant = set.getKey();
                List<List<String>> jobs1 = set.getValue();
                List<List<String>> appliedOnDate = jobs1.stream().filter(job -> job.get(2).equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))).collect(Collectors.toList());

                for (List<String> job : appliedOnDate) {
                    content = content.concat("<tr>" + "<td>" + job.get(3) + "</td>" + "<td>" + job.get(0) + "</td>" + "<td>" + job.get(1) + "</td>" + "<td>" + applicant + "</td>" + "<td>" + job.get(2) + "</td>" + "</tr>");
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
    }

    public int getSuccessfulApplications(String employerName, String jobName) {
        int result = 0;
        Iterator<Entry<String, List<List<String>>>> iterator = getIterator();
        while (iterator.hasNext()) {
            Entry<String, List<List<String>>> set = iterator.next();
            List<List<String>> jobs = set.getValue();

            result += jobs.stream().anyMatch(job -> job.get(3).equals(employerName) && job.get(0).equals(jobName)) ? 1 : 0;
        }
        return result;
    }

    public int getUnsuccessfulApplications(String employerName, String jobName) {
        return failApplications.count(employerName, jobName);
    }

}