package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    public int getSuccessfulApplications(String employerName, String jobName) {
        int result = 0;
        Iterator<Map.Entry<String, List<List<String>>>> iterator = applied.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<List<String>>> set = iterator.next();
            List<List<String>> jobs = set.getValue();

            result += jobs.stream().anyMatch(job -> job.get(3).equals(employerName) && job.get(0).equals(jobName)) ? 1 : 0;
        }
        return result;
    }

    String getHtmlContent(LocalDate date, String content) {
        Iterator<Map.Entry<String, List<List<String>>>> iterator = applied.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<List<String>>> set = iterator.next();
            String applicant = set.getKey();
            List<List<String>> jobs1 = set.getValue();
            List<List<String>> appliedOnDate = jobs1.stream().filter(job -> job.get(2).equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))).collect(Collectors.toList());

            for (List<String> job : appliedOnDate) {
                content = content.concat("<tr>" + "<td>" + job.get(3) + "</td>" + "<td>" + job.get(0) + "</td>" + "<td>" + job.get(1) + "</td>" + "<td>" + applicant + "</td>" + "<td>" + job.get(2) + "</td>" + "</tr>");
            }
        }
        return content;
    }

    String exportCsv(LocalDate date) {
        return getCsvContent(date, "Employer,Job,Job Type,Applicants,Date" + "\n");
    }

    private String getCsvContent(LocalDate date, String result) {
        Iterator<Map.Entry<String, List<List<String>>>> iterator =applied.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<List<String>>> set = iterator.next();
            String applicant = set.getKey();
            List<List<String>> jobs1 = set.getValue();
            List<List<String>> appliedOnDate = jobs1.stream().filter(job -> job.get(2).equals(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))).collect(Collectors.toList());

            for (List<String> job : appliedOnDate) {
                result = result.concat(job.get(3) + "," + job.get(0) + "," + job.get(1) + "," + applicant + "," + job.get(2) + "\n");
            }
        }
        return result;
    }

    public String export(String type, LocalDate date) {
        if (type == "csv") {
            return exportCsv(date);
        }
        return exportHtml(date);
    }

    private String exportHtml(LocalDate date) {

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
                + getHtmlContent(date, "")
                + "</tbody>"
                + "</table>"
                + "</body>"
                + "</html>";
    }
}
