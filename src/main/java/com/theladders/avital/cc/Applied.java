package com.theladders.avital.cc;

import java.time.LocalDate;
import java.util.*;

public class Applied {

    private final HashMap<String, List<Apply>> applied = new HashMap<>();

    private final FailApplicationList failApplicationList=new FailApplicationList();

    public List<List<String>> findBy(String employerName) {
        applied.get(employerName).forEach(item-> ((List<List<String>>) new ArrayList<List<String>>()).add(item.asString()));
        return new ArrayList<>();
    }

    public Iterator<Map.Entry<String, List<Apply>>> getIterator() {
        return this.applied.entrySet().iterator();
    }

    void applyCommand(String command,String employerName,String jobName,String jobType,String jobSeekerName,String resumeApplicantName,LocalDate applicationTime) throws RequiresResumeForJReqJobException, InvalidResumeException {

        applyCommandTemp(command,new Apply(new Job(jobName,jobType),jobSeekerName,resumeApplicantName,applicationTime,employerName));
    }

    void applyCommandTemp(String command,  Apply apply) throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (command != "apply") {
            return;
        }
        if (isRequiresResumeForJReqJob(apply)) {
           failApplicationList.addFailApplication(apply);
            throw new RequiresResumeForJReqJobException();
        }

        if (isInvalidResume(apply)) {
            throw new InvalidResumeException();
        }
        List<Apply> saved = applied.getOrDefault(apply.jobSeekerName,new ArrayList<>());
        saved.add(apply);
        applied.put(apply.jobSeekerName,saved);
    }

    private boolean isInvalidResume(Apply apply) {
        return apply.job.getJobType().equals("JReq") && ! apply.resumeApplicantName .equals(apply.jobSeekerName);
    }

    private boolean isRequiresResumeForJReqJob(Apply apply) {
        return apply.job.getJobType().equals("JReq") && apply.resumeApplicantName == null;
    }
}
