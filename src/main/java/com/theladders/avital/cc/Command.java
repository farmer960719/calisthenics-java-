package com.theladders.avital.cc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Command {
    abstract void execute(String command, String employerName, String jobName, String jobType, String jobSeekerName, String resumeApplicantName, LocalDate applicationTime);
}

class PublishCommand extends Command{

    @Override
    void execute(String command, String employerName, String jobName, String jobType, String jobSeekerName, String resumeApplicantName, LocalDate applicationTime) {

    }
}