package com.ourcode.models.output;

import com.google.gson.annotations.SerializedName;
import com.ourcode.models.algorithms.Floyd;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by LEOLEOl on 4/14/2017.
 */
public class OCJobRound {
    // Information about a package that supposed to be executed.
    @SerializedName("srcLocationId")
    private String srcLocationId; // (string), id of source location that need to be pickup

    @SerializedName("desLocationId")
    private String desLocationId; // (string), id of destination location that need to be dropoff

    @SerializedName("departTime")
    private long departTime; // (long), (in epoch seconds), the expected departure time

    @SerializedName("arriveTime")
    private long arriveTime; // (long), (in epoch seconds), the expected arrival time

    @SerializedName("jobsAtLocation")
    private ArrayList<OCJobAtLocation> jobsAtLocation;

    // Getter and Setter method
    public String getSrcLocationId() {return srcLocationId;}
    public String getDesLocationId() {return desLocationId;}
    public long getDepartTime() {return departTime;}
    public void setDepartTime(long departTime) {this.departTime = departTime;}
    public void setArriveTime(long arriveTime) {this.arriveTime = arriveTime;}
    public long getArriveTime() {return arriveTime;}
    public ArrayList<OCJobAtLocation> getJobsAtLocation()
    {
        ArrayList<OCJobAtLocation> ret = new ArrayList<>();
        for (OCJobAtLocation jobAtLocation: jobsAtLocation) ret.add(new OCJobAtLocation(jobAtLocation));
        return ret;
    }

    public void setJobsAtLocation(ArrayList<OCJobAtLocation> jobsAtLocation) {
        this.jobsAtLocation.clear();
        for (OCJobAtLocation jobAtLocation: jobsAtLocation) this.jobsAtLocation.add(new OCJobAtLocation(jobAtLocation));
    }

    public void addJobAtLocation(OCJobAtLocation ocJobAtLocation)
    {
        jobsAtLocation.add(new OCJobAtLocation(ocJobAtLocation));
    }

    // Constructor
    public OCJobRound(String srcLocationId, String desLocationId, long departTime, long arriveTime)
    {
        this.srcLocationId = srcLocationId;
        this.desLocationId = desLocationId;
        this.departTime = departTime;
        this.arriveTime = arriveTime;
        jobsAtLocation = new ArrayList<>();
    }

    public OCJobRound(OCJobRound jobRound)
    {
        this.srcLocationId = jobRound.srcLocationId;
        this.desLocationId = jobRound.desLocationId;
        this.departTime = jobRound.departTime;
        this.arriveTime = jobRound.arriveTime;
        this.jobsAtLocation = new ArrayList<>();
        for (OCJobAtLocation jobAtLocation: jobRound.jobsAtLocation)
            this.jobsAtLocation.add(new OCJobAtLocation(jobAtLocation));

    }

    public static ArrayList<OCJobRound> getRealJobRounds(OCJobRound jobRound, Hashtable<Pair, Double> hashTableMatrix, Hashtable<Pair, String> hashTableTrace)
    {
        String s = jobRound.getSrcLocationId(), f = jobRound.getDesLocationId(), ss = s;
        double sf = hashTableMatrix.get(new Pair(s, f));

        ArrayList<OCJobRound> ret = new ArrayList<>();

        if (sf == Floyd.maxC)
            return null;
        else {
            // System.out.printf("Distance from %d to %d: %f\n", s, f, sf);
            do {
                Pair tPair = new Pair(s, f);
                OCJobRound jobRound1 = new OCJobRound(s, hashTableTrace.get(tPair), -1, -1); // Alway set -1, -1

                if (s.equals(ss)) jobRound1.setDepartTime(jobRound.getDepartTime()); // If it is startJobRound, real departTime
                if (hashTableTrace.get(tPair).equals(f)) jobRound1.setArriveTime(jobRound.getArriveTime()); // If it is endJobRound, real arriveTime

                // setJobsAtLocation
                jobRound1.setJobsAtLocation(jobRound.getJobsAtLocation());

                // Add "pre-" to previous jobRounds to the real jobRound
                ArrayList<OCJobAtLocation> jobsAtLocationTemp = jobRound1.jobsAtLocation;
                if (jobRound1.getArriveTime() == -1)
                    for (OCJobAtLocation jobAtLocation: jobsAtLocationTemp)
                        jobAtLocation.setJobType("pre-" + jobAtLocation.getJobType());


                //System.out.printf("%s -> ", s);
                s = hashTableTrace.get(tPair); // trace to get next location
                ret.add(jobRound1); // add subJobRounds to result
            } while (!s.equals(f));
            //System.out.printf("%s\n", f);
            return ret;
        }
    }
}
