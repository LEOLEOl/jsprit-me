package com.ourcode.models.input;

import com.google.gson.annotations.SerializedName;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import com.ourcode.exceptions.OurException;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Tung
 * Date: 10/03/17
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */

public class OCTimeWindow
{
    @SerializedName("start")
    public double start; // (double), (in hours), (the time count from optimizing time), Start hour of time window

    @SerializedName("end")
    public double end; // (double), (in hours), (the time count from optimizing time), End hour of time window

    private TimeWindow j_timeWindow;

    public TimeWindow _getJ_timeWindow() {return j_timeWindow;}

    // Constructor
    public OCTimeWindow(double start, double end) {
        this.start = start;
        this.end = end;
    }

    public OCTimeWindow(OCTimeWindow obj)
    {
        if (this != obj) {
            this.start = obj.start;
            this.end = obj.end;
        }
    }

    public OCTimeWindow build()
    {
        j_timeWindow = new TimeWindow(this.start, this.end);
        return this;
    }


    static public ArrayList<OCTimeWindow> changeToRelativeTimeInHour(ArrayList<OCTimeWindow> timeWindows, long timeAtRequest) throws OurException
    {
        if (timeWindows == null) throw new OurException(-1, "-1");

        for (OCTimeWindow timeWindow : timeWindows) {
            timeWindow.start = (timeWindow.start - timeAtRequest) / 3600;
            timeWindow.end = (timeWindow.end - timeAtRequest) / 3600;
        }
        return timeWindows;
    }

    static private boolean isOverlap2TimeWindow(OCTimeWindow a, OCTimeWindow b)
    {
        return !(b.end < a.start || b.start > a.end);
    }

    static private boolean isValidTimeWindow(OCTimeWindow timeWindow, long timeAtRequest)
    {
        return (0 <= timeAtRequest && timeAtRequest <= timeWindow.start && timeWindow.start < timeWindow.end);
    }

    static public int isValidTimeWindows(ArrayList<OCTimeWindow> timeWindows, long timeAtRequest)
    {
        // return 0: valid, return 1: invalid timewindow, return 2: overlapped timewindows
        for (int i = 0; i < timeWindows.size(); ++i) {
            // Check if valid
            if (!isValidTimeWindow(timeWindows.get(i), timeAtRequest)) return 1; // invalid
            // Check if overlapped
            for (int j = i + 1; j < timeWindows.size(); ++j)
                if (isOverlap2TimeWindow(timeWindows.get(i), timeWindows.get(j))) return 2;
        }
        return 0;
    }
}
