package com.graphhopper.jsprit.util;

/**
 * Created with IntelliJ IDEA.
 * User: Tung
 * Date: 10/03/17
 * Time: 12:03 PM
 * To change this template use File | Settings | File Templates.
 */
/*
 * Copyright (c) SkedGo 2013
 */
public class TimeUtils {

    public static long calculateTimeFromNowInSecs (long futureSecs) {
        return (futureSecs - nowMillis()/1000);
    }

    public static long nowMillis () {
        return System.currentTimeMillis();

    }



}