package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Applied {
    private final HashMap<String, List<List<String>>> applied = new HashMap<>();


    public List<List<String>> findBy(String employerName) {
        return applied.get(employerName);
    }

    public Iterator<Map.Entry<String, List<List<String>>>> getIterator() {
        return this.applied.entrySet().iterator();
    }

    public List<List<String>> getOrDefault(String jobSeekerName) {
        return applied.getOrDefault(jobSeekerName, new ArrayList<>());
    }

    public void store(String jobSeekerName, List<List<String>> saved) {
        applied.put(jobSeekerName,saved);
    }

    void applyCommand(String command, String employerName, String jobName, String jobType, String jobSeekerName, String resumeApplicantName, LocalDate applicationTime, Application application) throws RequiresResumeForJReqJobException, InvalidResumeException {
        if (command != "apply") {
            return;
        }
        if (jobType.equals("JReq") && resumeApplicantName == null) {
            application.failApplications.addFailApplication(employerName, jobName, jobType, applicationTime);
            throw new RequiresResumeForJReqJobException();
        }

        if (jobType.equals("JReq") && !resumeApplicantName.equals(jobSeekerName)) {
            throw new InvalidResumeException();
        }
        List<List<String>> saved = getOrDefault(jobSeekerName);
        saved.add(new ArrayList<String>() {{
            add(jobName);
            add(jobType);
            add(applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            add(employerName);
        }});
        store(jobSeekerName, saved);

    }
}
