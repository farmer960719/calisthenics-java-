package com.theladders.avital.cc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Applied {
    private final HashMap<String, List<List<String>>> applied = new HashMap<>();


    public List<List<String>> getAppliedJobs(String employerName) {
        return applied.get(employerName);
    }

    public void addApply(Employer employer, Job job, JobSeeker jobSeeker, LocalDate applicationTime) {
        List<List<String>> saved = applied.getOrDefault(jobSeeker.getName(),new ArrayList<>());
        saved.add(new ArrayList<String>() {{
            add(job.getName());
            add(job.getType());
            add(applicationTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            add(employer.getName());
        }});
        applied.put(jobSeeker.getName(), saved);

    }
}
