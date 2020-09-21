package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.Entry;

public class Application {
    private final HashMap<String, List<List<String>>> jobs = new HashMap<>();
    private final HashMap<String, List<List<String>>> applied = new HashMap<>();
    private final List<List<String>> failedApplications = new ArrayList<>();

    public void execute(String command, Employer employer, Job job, JobSeeker jobSeeker, Resume resume, LocalDate applicationTime) throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        if (command == Command.PUBLISH) {
            publishJob(new Employer(employer.getName()),new Job(job.getName(), job.getType()));
        }
        if (command == Command.SAVE) {
            saveJob(new Employer(employer.getName()),new Job(job.getName(), job.getType()));
        }
        if (command == Command.APPLY) {
            applyJob(new Employer(employer.getName()),new Job(job.getName(), job.getType()),new JobSeeker(jobSeeker.getName()),new Resume(resume.getName()), applicationTime);
        }
    }
    private void applyJob(Employer employer, Job job, JobSeeker jobSeeker, Resume resume, LocalDate applicationTime) throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (job.getType().equals(JobType.J_REQ) && resume.getName() == null) {
            List<String> failedApplication = new ArrayList<String>() {{
                add(job.getName());
                add(job.getType());
                add(applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                add(employer.getName());
            }};
            failedApplications.add(failedApplication);
            throw new RequiresResumeForJReqJobException();
        }

        if (job.getType().equals(JobType.J_REQ) && !resume.getName().equals(jobSeeker.getName())) {
            throw new InvalidResumeException();
        }
        List<List<String>> saved = this.applied.getOrDefault(jobSeeker.getName(), new ArrayList<>());

        saved.add(new ArrayList<String>() {{
            add(job.getName());
            add(job.getType());
            add(applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            add(employer.getName());
        }});
        applied.put(jobSeeker.getName(), saved);
    }

    private void saveJob(Employer employer, Job job) {
        List<List<String>> saved = jobs.getOrDefault(employer.getName(), new ArrayList<>());

        saved.add(new ArrayList<String>() {{
            add(job.getName());
            add(job.getType());
        }});
        jobs.put(employer.getName(), saved);
    }

    private void publishJob(Employer employer, Job job) throws NotSupportedJobTypeException {
        if (!job.getType().equals(JobType.J_REQ) && !job.getType().equals(JobType.ATS)) {
            throw new NotSupportedJobTypeException();
        }

        List<List<String>> alreadyPublished = jobs.getOrDefault(employer.getName(), new ArrayList<>());

        alreadyPublished.add(new ArrayList<String>() {{
            add(job.getName());
            add(job.getType());
        }});
        jobs.put(employer.getName(), alreadyPublished);
    }

    public List<List<String>> getJobs(String employerName, String type) {
        if (type.equals("applied")) {
            return applied.get(employerName);
        }

        return jobs.get(employerName);
    }

    public List<String> findApplicants(String jobName, String employerName) {
        return findApplicants(jobName, employerName, null);
    }

    public List<String> findApplicants(String jobName, String employerName, LocalDate from) {
        return findApplicants(jobName, employerName, from, null);
    }

    public List<String> findApplicants(String jobName, String employerName, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return findWithJobName(jobName);
        }
        if (jobName == null && to == null) {
            return findWithFrom(from);
        }
        if (jobName == null && from == null) {
            return findWithTo(to);

        }
        if (jobName == null) {
            return findWithFromAndTo(from, to);

        }
        if (to != null) {
            return findWithJobNameAndTo(jobName, to);
        }
        return findWithJobNameAndFrom(jobName, from);

    }

    private List<String> findWithJobNameAndFrom(String jobName, LocalDate from) {
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

    private List<String> findWithJobNameAndTo(String jobName, LocalDate to) {
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

    private List<String> findWithFromAndTo(LocalDate from, LocalDate to) {
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

    private List<String> findWithTo(LocalDate to) {
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

    private List<String> findWithFrom(LocalDate from) {
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

    private List<String> findWithJobName(String jobName) {
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
            return expotCsv(date);
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

    private String getHtmlContent(LocalDate date, String content) {
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
        return content;
    }


    private String expotCsv(LocalDate date) {
        return getCsvContent(date, "Employer,Job,Job Type,Applicants,Date" + "\n");
    }

    private String getCsvContent(LocalDate date, String result) {
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
