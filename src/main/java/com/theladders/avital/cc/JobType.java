package com.theladders.avital.cc;

public class JobType {

    public static final String J_REQ = "JReq";
    public static final String ATS = "ATS";

    private String name;

    public JobType(String name) {
        this.name = name;
    }

    public String getName() {


        return name;
    }
}
