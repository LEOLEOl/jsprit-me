package com.ourcode.models.output;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by LEOLEOl on 4/10/2017.
 */

public class OCOutput {
    @SerializedName("transports")
    private ArrayList<OCTransport> transports;

    public OCOutput()
    {
        transports = new ArrayList<>();
    }

    public void addTransport(OCTransport ocTransport)
    {
        this.transports.add(new OCTransport(ocTransport));
    }

}
