package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Application {
    private final Jobs jobs = new Jobs();
    private final HashMap<String, List<List<String>>> applied = new HashMap<>();
    private final List<List<String>> failedApplications = new ArrayList<>();
    final Applied appliedTemp = new Applied();

    public void execute(String command, Employer employer, Job job, JobSeeker jobSeeker, Resume resume, LocalDate applicationTime) throws NotSupportedJobTypeException, RequiresResumeForJReqJobException, InvalidResumeException {
       if(command=="publish")
           publishJob(employer, job);
        if(command=="save")
            saveJob(employer, job);
        if(command=="apply")
            applyJobs(employer, job, jobSeeker, resume, applicationTime);
    }


    private void applyJobs(Employer employer, Job job, JobSeeker jobSeeker, Resume resume, LocalDate applicationTime) throws RequiresResumeForJReqJobException, InvalidResumeException {
        applyJob(employer, job, jobSeeker, resume, applicationTime);
    }

    private void saveJob(Employer employer, Job job) {
        jobs.saveJob(employer, job);
    }

    private void publishJob(Employer employer, Job job) throws NotSupportedJobTypeException {
        jobs.publishJob(employer, job);
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


    public int getUnsuccessfulApplications(String employerName, String jobName) {
        return (int) failedApplications.stream().filter(job -> job.get(0).equals(jobName) && job.get(3).equals(employerName)).count();
    }
}
