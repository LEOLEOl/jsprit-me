package com.ourcode.models.output;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by LEOLEOl on 4/7/2017.
 */

public class OCTransport
{
    // Information about supposed transportation
    @SerializedName("vehicleId")
    private String vehicleId; // (string), Id (plate number) of vehicle

    @SerializedName("jobCodes")
    private ArrayList<String> jobCodes;

    @SerializedName("jobRounds")
    private ArrayList<OCJobRound> jobRounds;




    public void addJobRound(OCJobRound ocJobRound)
    {
        this.jobRounds.add(new OCJobRound(ocJobRound));
    }

    public void addJobCode(String jobCode)
    {
        if (!jobCodes.contains(jobCode))
            jobCodes.add(new String(jobCode));
    }

    // Constructor
    public OCTransport(String vehicleId)
    {
        this.vehicleId = vehicleId;
        this.jobRounds = new ArrayList<>();
        this.jobCodes = new ArrayList<>();
    }

    public OCTransport(OCTransport transport)
    {
        this.vehicleId = transport.vehicleId;
        this.jobCodes = new ArrayList<>();
        for (String jobCode: transport.jobCodes) this.jobCodes.add(new String(jobCode));
        this.jobRounds = new ArrayList<>();
        for (OCJobRound jobRound: transport.jobRounds) this.jobRounds.add(new OCJobRound(jobRound));
    }
}
