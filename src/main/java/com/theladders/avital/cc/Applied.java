package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Applied {
    private final HashMap<String, List<List<String>>> applied = new HashMap<>();


    public List<List<String>> getAppliedJobs(String employerName) {
        return applied.get(employerName);
    }

    public void addApplyTemp(Employer employer, Job job, JobSeeker jobSeeker, LocalDate applicationTime) {
        List<List<String>> saved = applied.getOrDefault(jobSeeker.getName(),new ArrayList<>());
        saved.add(new ArrayList<String>() {{
            add(job.getName());
            add(job.getType());
            add(applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            add(employer.getName());
        }});
        applied.put(jobSeeker.getName(), saved);

    }

    List<String> findWithJobNameAndFrom(String jobName, LocalDate from) {
        List<String> result = new ArrayList<String>() {
        };
        Iterator<Map.Entry<String, List<List<String>>>> iterator = applied.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<List<String>>> set = iterator.next();
            String applicant = set.getKey();
            List<List<String>> jobs = set.getValue();
            boolean isAppliedThisDate = jobs.stream().anyMatch(job -> job.get(0).equals(jobName) && !from.isAfter(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            isAppliedThisDate(result, applicant, isAppliedThisDate);
        }
        return result;
    }

    void isAppliedThisDate(List<String> result, String applicant, boolean isAppliedThisDate) {
        if (isAppliedThisDate) {
            result.add(applicant);
        }
    }

    List<String> findWithJobName(String jobName) {
        List<String> result = new ArrayList<String>() {
        };
        Iterator<Map.Entry<String, List<List<String>>>> iterator = applied.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<List<String>>> set = iterator.next();
            String applicant = set.getKey();
            List<List<String>> jobs = set.getValue();
            boolean hasAppliedToThisJob = jobs.stream().anyMatch(job -> job.get(0).equals(jobName));
            isAppliedThisDate(result, applicant, hasAppliedToThisJob);
        }
        return result;
    }

    List<String> findWithJobNameAndTo(String jobName, LocalDate to) {
        List<String> result = new ArrayList<String>() {
        };
        Iterator<Map.Entry<String, List<List<String>>>> iterator = applied.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<List<String>>> set = iterator.next();
            String applicant = set.getKey();
            List<List<String>> jobs = set.getValue();
            boolean isAppliedThisDate = jobs.stream().anyMatch(job -> job.get(0).equals(jobName) && !to.isBefore(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            isAppliedThisDate(result, applicant, isAppliedThisDate);
        }
        return result;
    }

    List<String> findWithFromAndTo(LocalDate from, LocalDate to) {
        List<String> result = new ArrayList<String>() {
        };
        Iterator<Map.Entry<String, List<List<String>>>> iterator = applied.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<List<String>>> set = iterator.next();
            String applicant = set.getKey();
            List<List<String>> jobs = set.getValue();
            boolean isAppliedThisDate = jobs.stream().anyMatch(job -> !from.isAfter(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))) && !to.isBefore(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            isAppliedThisDate(result, applicant, isAppliedThisDate);
        }
        return result;
    }

    List<String> findWithTo(LocalDate to) {
        List<String> result = new ArrayList<String>() {
        };
        Iterator<Map.Entry<String, List<List<String>>>> iterator = applied.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<List<String>>> set = iterator.next();
            String applicant = set.getKey();
            List<List<String>> jobs = set.getValue();
            boolean isAppliedThisDate = jobs.stream().anyMatch(job ->
                    !to.isBefore(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            isAppliedThisDate(result, applicant, isAppliedThisDate);
        }
        return result;
    }

    List<String> findWithFrom(LocalDate from) {
        List<String> result = new ArrayList<String>() {
        };
        Iterator<Map.Entry<String, List<List<String>>>> iterator = applied.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<List<String>>> set = iterator.next();
            String applicant = set.getKey();
            List<List<String>> jobs = set.getValue();
            boolean isAppliedThisDate = jobs.stream().anyMatch(job ->
                    !from.isAfter(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            isAppliedThisDate(result, applicant, isAppliedThisDate);
        }
        return result;
    }
}
