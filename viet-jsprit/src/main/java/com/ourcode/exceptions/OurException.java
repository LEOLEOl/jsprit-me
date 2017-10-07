package com.ourcode.exceptions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import scala.util.parsing.combinator.testing.Str;

/**
 * Created by LEOLEOl on 4/21/2017.
 */
public class OurException extends Exception {
    @Expose
    @SerializedName("errorCode")
    public int errorCode;

    @Expose
    @SerializedName("details")
    public String details;

    public OurException(int errorCode, String detailMessage) {
        super(detailMessage);
        this.errorCode = errorCode;
        this.details = detailMessage;
    }
}
