package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.*;

public class Application {
    public static final String APPLY = "apply";
    public static final String SAVE = "save";
    public static final String PUBLISH = "publish";
    public static final String J_REQ = "JReq";
    public static final String ATS = "ATS";
    private Jobs jobs =new Jobs();
    private final HashMap<String, List<List<String>>> applied = new HashMap<>();
    private Applied appliedTemp=new Applied();
    private final List<List<String>> failedApplications = new ArrayList<>();

    public void execute(String command, String employerName, String jobName, String jobType, String jobSeekerName, String resumeApplicantName, LocalDate applicationTime) throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        publishCommand(command, employerName, jobName, jobType);
        saveCommand(command, employerName, jobName, jobType);
        applyCommand(command, employerName, jobName, jobType, jobSeekerName, resumeApplicantName, applicationTime);
    }

    private void applyCommand(String command, String employerName, String jobName, String jobType, String jobSeekerName, String resumeApplicantName, LocalDate applicationTime) throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (command != APPLY) {
            return;
        }
        if (jobType.equals(J_REQ) && resumeApplicantName == null) {
            addFailApplication(employerName, jobName, jobType, applicationTime);
            throw new RequiresResumeForJReqJobException();
        }

        if (jobType.equals(J_REQ) && !resumeApplicantName.equals(jobSeekerName)) {
            throw new InvalidResumeException();
        }
        addApply(employerName, jobName, jobType, jobSeekerName, applicationTime);

    }

    private void saveCommand(String command, String employerName, String jobName, String jobType) {
        if (command != SAVE) {
            return;
        }
        addSave(employerName, jobName, jobType);
    }

    private void publishCommand(String command, String employerName, String jobName, String jobType) throws NotSupportedJobTypeException {
        if (command != PUBLISH) {
            return;
        }
        if (!jobType.equals(J_REQ) && !jobType.equals(ATS)) {
            throw new NotSupportedJobTypeException();
        }
        addPublish(employerName, jobName, jobType);
    }

    private void addApply(String employerName, String jobName, String jobType, String jobSeekerName, LocalDate applicationTime) {
        List<List<String>> saved = appliedTemp.getElement(employerName);
        saved.add(new ArrayList<String>() {{
            add(jobName);
            add(jobType);
            add(applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            add(employerName);
        }});
        appliedTemp.replace(new Job(jobSeekerName, saved));
    }

    private void addFailApplication(String employerName, String jobName, String jobType, LocalDate applicationTime) {
        List<String> failedApplication = new ArrayList<String>() {{
            add(jobName);
            add(jobType);
            add(applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            add(employerName);
        }};
        failedApplications.add(failedApplication);
    }

    private void addSave(String employerName, String jobName, String jobType) {
        List<List<String>> saved = jobs.getElement(employerName);
        saved.add(new ArrayList<String>() {{
            add(jobName);
            add(jobType);
        }});

        jobs.put(new Job(employerName,saved));
    }

    private void addPublish(String employerName, String jobName, String jobType) {
        List<List<String>> alreadyPublished = jobs.getElement(employerName);
        alreadyPublished.add(new ArrayList<String>() {{
            add(jobName);
            add(jobType);
        }});
        jobs.put(new Job(employerName, alreadyPublished));
    }

    public List<List<String>> getJobs(String employerName, String type) {
        if (type.equals("applied")) {
            return appliedTemp.getElement(employerName);
        }

        return jobs.getElement(employerName);
    }

    public List<String> findApplicants(String jobName, String employerName) {
        return findApplicants(jobName, employerName, null);
    }

    public List<String> findApplicants(String jobName, String employerName, LocalDate from) {
        return findApplicants(jobName, employerName, from, null);
    }

    public List<String> findApplicants(String jobName, String employerName, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return fromIsNullAndToIsNull(jobName);
        }
        if (jobName == null && to == null) {
            return jobNameIsNullAndToIsNull(from);
        }
        if (jobName == null && from == null) {
            return jobNameIsNullAndFromIsNull(to);
        }
        if (jobName == null) {
            return jobNameIsNull(from, to);
        }
        if (to != null) {
            return formIsNull(jobName, to);
        }
        return toIsNull(jobName, from);

    }

    private List<String> toIsNull(String jobName, LocalDate from) {
        List<String> result = new ArrayList<String>() {
        };
        Iterator<Job> iteratorTemp = this.appliedTemp.applied.iterator();
        while (iteratorTemp.hasNext()) {
            Job set = iteratorTemp.next();
            String applicant = set.name;
            List<List<String>> jobs = set.list;
            boolean isAppliedThisDate = jobs.stream().anyMatch(job -> job.get(0).equals(jobName) && !from.isAfter(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            if (isAppliedThisDate) {
                result.add(applicant);
            }
        }
        return result;
    }

    private List<String> formIsNull(String jobName, LocalDate to) {
        List<String> result = new ArrayList<String>() {
        };
        Iterator<Entry<String, List<List<String>>>> iterator = this.applied.entrySet().iterator();
        Iterator<Job> iteratorTemp = this.appliedTemp.applied.iterator();
        while (iteratorTemp.hasNext()) {
            Job set = iteratorTemp.next();
            String applicant = set.name;
            List<List<String>> jobs = set.list;
            boolean isAppliedThisDate = jobs.stream().anyMatch(job -> job.get(0).equals(jobName) && !to.isBefore(LocalDate.parse(job.get(2), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
            if (isAppliedThisDate) {
                result.add(applicant);
            }
        }
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

    private List<String> jobNameIsNull(LocalDate from, LocalDate to) {
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

    private List<String> jobNameIsNullAndFromIsNull(LocalDate to) {
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

    private List<String> jobNameIsNullAndToIsNull(LocalDate from) {
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

    private List<String> fromIsNullAndToIsNull(String jobName) {
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
        } else {
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
