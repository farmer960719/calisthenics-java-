package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Map.Entry;

public class Application {
    private final Jobs jobs = new Jobs();
    private final HashMap<String, List<List<String>>> applied = new HashMap<>();
    private final List<List<String>> failedApplications = new ArrayList<>();
    private final Applied appliedTemp = new Applied();

    public void execute(String command, Employer employer, Job job, JobSeeker jobSeeker, Resume resume, LocalDate applicationTime) throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
       if(command=="publish")
        executeTemp(Command.PUBLISH, employer, job, jobSeeker, resume, applicationTime);
        if(command=="save")
            executeTemp(Command.SAVE, employer, job, jobSeeker, resume, applicationTime);
        if(command=="apply")
            executeTemp(Command.APPLY, employer, job, jobSeeker, resume, applicationTime);
    }

    public void executeTemp(Command command, Employer employer, Job job, JobSeeker jobSeeker, Resume resume, LocalDate applicationTime) throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
        if (command== Command.PUBLISH) {
            jobs.publishJob(employer, job);
        }
        if (command == Command.SAVE) {
            jobs.saveJob(employer, job);
        }
        if (command== Command.APPLY) {
            applyJob(employer, job, jobSeeker, resume, applicationTime);
        }
    }

    private void applyJob(Employer employer, Job job, JobSeeker jobSeeker, Resume resume, LocalDate applicationTime) throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (job.getType().equals(JobType.J_REQ) && resume.getName() == null) {
            addFailedApplication(employer, job, applicationTime);
            throw new RequiresResumeForJReqJobException();
        }

        if (job.getType().equals(JobType.J_REQ) && !resume.getName().equals(jobSeeker.getName())) {
            throw new InvalidResumeException();
        }
        addApply(employer, job, jobSeeker, applicationTime);
    }

    private void addApply(Employer employer, Job job, JobSeeker jobSeeker, LocalDate applicationTime) {
        List<List<String>> saved = applied.getOrDefault(jobSeeker.getName(), new ArrayList<>());
        saved.add(new ArrayList<String>() {{
            add(job.getName());
            add(job.getType());
            add(applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            add(employer.getName());
        }});
        appliedTemp.addApplyTemp(employer, job, jobSeeker, applicationTime);
        applied.put(jobSeeker.getName(), saved);
    }

    private void addFailedApplication(Employer employer, Job job, LocalDate applicationTime) {
        List<String> failedApplication = new ArrayList<String>() {{
            add(job.getName());
            add(job.getType());
            add(applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            add(employer.getName());
        }};
        failedApplications.add(failedApplication);
    }

    public List<List<String>> getJobs(String employerName, String type) {
        if (type.equals("applied")) {

            return getAppliedJobs(employerName);
        }

        return getJobs(employerName);
    }

    private List<List<String>> getJobs(String employerName) {
        return jobs.getJobs(employerName);
    }

    private List<List<String>> getAppliedJobs(String employerName) {
        return appliedTemp.getAppliedJobs(employerName);
    }


    public List<String> findApplicants(String jobName, String employerName) {
        return findApplicants(jobName, employerName, null);
    }

    public List<String> findApplicants(String jobName, String employerName, LocalDate from) {
        return findApplicants(jobName, employerName, from, null);
    }

    public List<String> findApplicants(String jobName, String employerName, LocalDate from, LocalDate to) {
        if (from == null && to == null) {
            return appliedTemp.findWithJobName(jobName);
        }
        if (jobName == null && to == null) {
            return appliedTemp.findWithFrom(from);
        }
        if (jobName == null && from == null) {
            return appliedTemp.findWithTo(to);

        }
        if (jobName == null) {
            return appliedTemp.findWithFromAndTo(from, to);

        }
        if (to != null) {
            return appliedTemp.findWithJobNameAndTo(jobName, to);
        }
        return appliedTemp.findWithJobNameAndFrom(jobName, from);

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
