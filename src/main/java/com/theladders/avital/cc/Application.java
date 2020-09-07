package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.*;

public class Application {
    public static final String PUBLISH = "publish";
    public static final String J_REQ = "JReq";
    public static final String ATS = "ATS";
    public static final String SAVE = "save";
    public static final String APPLY = "apply";
    public static final String APPLIED = "applied";
    private final HashMap<String, List<List<String>>> jobs = new HashMap<>();
    private final HashMap<String, List<List<String>>> applied = new HashMap<>();
    private final List<List<String>> failedApplications = new ArrayList<>();

    public void execute(String command, String employerName, String jobName, String jobType, String jobSeekerName, String resumeApplicantName, LocalDate applicationTime) throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        publishCommand(command, employerName, jobName, jobType);
        saveCommand(command, employerName, jobName, jobType);
        applyCommand(command, employerName, jobName, jobType, jobSeekerName, resumeApplicantName, applicationTime);
    }

    private void applyCommand(String command, String employerName, String jobName, String jobType, String jobSeekerName, String resumeApplicantName, LocalDate applicationTime) throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (!isApply(command)) {
            return;
        }
        if (jobType.equals(J_REQ) && resumeApplicantName == null) {
            List<String> failedApplication = new ArrayList<String>() {{
                add(jobName);
                add(jobType);
                add(applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                add(employerName);
            }};
            failedApplications.add(failedApplication);
            throw new RequiresResumeForJReqJobException();
        }

        if (jobType.equals(J_REQ) && !resumeApplicantName.equals(jobSeekerName)) {
            throw new InvalidResumeException();
        }
        List<List<String>> saved = this.applied.getOrDefault(jobSeekerName, new ArrayList<>());

        saved.add(new ArrayList<String>() {{
            add(jobName);
            add(jobType);
            add(applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            add(employerName);
        }});
        applied.put(jobSeekerName, saved);
    }

    private boolean isApply(String command) {
        return APPLY.equals(command);
    }

    private void saveCommand(String command, String employerName, String jobName, String jobType) {
        if (!isSave(command)) {
            return;
        }
        List<List<String>> saved = jobs.getOrDefault(employerName, new ArrayList<>());

        saved.add(new ArrayList<String>() {{
            add(jobName);
            add(jobType);
        }});
        jobs.put(employerName, saved);
    }

    private boolean isSave(String command) {
        return SAVE.equals(command);
    }

    private void publishCommand(String command, String employerName, String jobName, String jobType) throws NotSupportedJobTypeException {
        if (!isPublish(command)) {
            return;
        }
        if (!jobType.equals(J_REQ) && !jobType.equals(ATS)) {
            throw new NotSupportedJobTypeException();
        }

        List<List<String>> alreadyPublished = jobs.getOrDefault(employerName, new ArrayList<>());

        alreadyPublished.add(new ArrayList<String>() {{
            add(jobName);
            add(jobType);
        }});
        jobs.put(employerName, alreadyPublished);
    }


    private boolean isPublish(String command) {
        return PUBLISH.equals(command);
    }

    public List<List<String>> getJobs(String employerName, String type) {
        if (isApplied(type)) {
            return applied.get(employerName);
        }

        return jobs.get(employerName);
    }

    private boolean isApplied(String type) {
        return type.equals(APPLIED);
    }

    public List<String> findApplicants(String jobName, String employerName) {
        return findApplicants(jobName, employerName, null);
    }

    public List<String> findApplicants(String jobName, String employerName, LocalDate from) {
        return findApplicants(jobName, employerName, from, null);
    }

    public List<String> findApplicants(String jobName, String employerName, LocalDate from, LocalDate to) {
        List<String> result1 = method1(jobName, from, to);
        if (result1 != null) return result1;
        List<String> result2 = method2(jobName, from, to);
        if (result2 != null) return result2;
        List<String> result3 = method3(jobName, from, to);
        if (result3 != null) return result3;
        List<String> result4 = method4(jobName, from, to);
        if (result4 != null) return result4;
        List<String> result5 = method5(jobName, to);
        if (result5 != null) return result5;
        return method6(jobName, from);

    }

    private List<String> method6(String jobName, LocalDate from) {
        List<String> result = new ArrayList<String>() {
        };
        Iterator<Entry<String, List<List<String>>>> iterator = this.applied.entrySet().iterator();
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

    private List<String> method5(String jobName, LocalDate to) {
        if (to != null) {
            List<String> result = new ArrayList<String>() {
            };
            Iterator<Entry<String, List<List<String>>>> iterator = this.applied.entrySet().iterator();
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
        return null;
    }

    private List<String> method4(String jobName, LocalDate from, LocalDate to) {
        if (jobName == null) {
            List<String> result = new ArrayList<String>() {
            };
            Iterator<Entry<String, List<List<String>>>> iterator = this.applied.entrySet().iterator();
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
        return null;
    }

    private List<String> method3(String jobName, LocalDate from, LocalDate to) {
        if (jobName == null && from == null) {
            List<String> result = new ArrayList<String>() {
            };
            Iterator<Entry<String, List<List<String>>>> iterator = this.applied.entrySet().iterator();
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
        return null;
    }

    private List<String> method2(String jobName, LocalDate from, LocalDate to) {
        if (jobName == null && to == null) {
            List<String> result = new ArrayList<String>() {
            };
            Iterator<Entry<String, List<List<String>>>> iterator = this.applied.entrySet().iterator();
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
        return null;
    }

    private List<String> method1(String jobName, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            List<String> result = new ArrayList<String>() {
            };
            Iterator<Entry<String, List<List<String>>>> iterator = this.applied.entrySet().iterator();
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
        return null;
    }

    public String export(String type, LocalDate date) {
        if (type == "csv") {
            String result = "Employer,Job,Job Type,Applicants,Date" + "\n";
            Iterator<Entry<String, List<List<String>>>> iterator = this.applied.entrySet().iterator();
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
        }
        String content = "";
        Iterator<Entry<String, List<List<String>>>> iterator = this.applied.entrySet().iterator();
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

    public int getSuccessfulApplications(String employerName, String jobName) {
        int result = 0;
        Iterator<Entry<String, List<List<String>>>> iterator = this.applied.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, List<List<String>>> set = iterator.next();
            List<List<String>> jobs = set.getValue();

            result += jobs.stream().anyMatch(job -> job.get(3).equals(employerName) && job.get(0).equals(jobName)) ? 1 : 0;
        }
        return result;
    }

    public int getUnsuccessfulApplications(String employerName, String jobName) {
        return (int) failedApplications.stream().filter(job -> job.get(0).equals(jobName) && job.get(3).equals(employerName)).count();
    }
}
