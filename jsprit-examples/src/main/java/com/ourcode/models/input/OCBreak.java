package com.ourcode.models.input;

import com.google.gson.annotations.SerializedName;
import com.graphhopper.jsprit.core.problem.job.Break;

import java.util.ArrayList;

/**
 * Created by LEOLEOl on 5/19/2017.
 */

public class OCBreak {
    @SerializedName("breakCode")
    private String breakCode;

    @SerializedName("serviceTime")
    private double serviceTime; // (in hours)

    @SerializedName("timeWindows")
    public ArrayList<OCTimeWindow> timeWindows; // (in hours)

    private Break j_break;

    public Break _getJ_break()
    {
        return j_break;
    }
    public double getServiceTime() {return serviceTime;}
    public void setServiceTime(double serviceTime) {this.serviceTime = serviceTime;}

    public OCBreak(OCBreak ocBreak)
    {
        this.breakCode = ocBreak.breakCode;
        this.serviceTime = ocBreak.serviceTime;
        this.timeWindows = new ArrayList<>();
        for (OCTimeWindow timeWindow: ocBreak.timeWindows)
            this.timeWindows.add(new OCTimeWindow(timeWindow));
    }

    public OCBreak build()
    {
        Break.Builder builder = Break.Builder.newInstance(breakCode);
        builder.setServiceTime(serviceTime);

        for (OCTimeWindow timeWindow: timeWindows)
            builder.addTimeWindow(timeWindow.build()._getJ_timeWindow());

        j_break = builder.build();
        return this;
    }


}
