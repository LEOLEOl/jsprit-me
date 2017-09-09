package com.ourcode.models.output;

import com.google.gson.annotations.SerializedName;

/**
 * Created by LEOLEOl on 4/14/2017.
 */
public class OCJobAtLocation {
    @SerializedName("jobCode")
    private String jobCode;

    @SerializedName("jobType")
    private String jobType;

    @SerializedName("weight")
    private double weight;

    @SerializedName("volume")
    private double volume;


    //public String getJobIndex() {return jobIndex;}
    public String getJobCode() {return jobCode;}
    public String getJobType() {return jobType;}
    public double getWeight() {return weight;}
    public double getVolume() {return volume;}

    public void setJobType(String newJobType) {this.jobType = newJobType;}

    public OCJobAtLocation(String jobCode, String jobType, double weight, double volume)
    {
        this.jobCode = jobCode;
        this.jobType = jobType;
        this.weight = weight;
        this.volume = volume;
    }

    public OCJobAtLocation(OCJobAtLocation jobAtLocation)
    {
        this.jobType = jobAtLocation.jobType;
        this.jobCode = jobAtLocation.jobCode;
        this.weight = jobAtLocation.weight;
        this.volume = jobAtLocation.volume;
    }
}
