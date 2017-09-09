package com.graphhopper.jsprit.examples;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ourcode.exceptions.OurException;
import com.ourcode.models.output.OCVRP;
import com.ourcode.models.utilities.Utilities;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by LEOLEOl on 6/6/2017.
 */
public class JsonTest extends TestCase{
    private int testJson(String stringPath)
    {
        int ret;
        try {
            String jsonStringInput = Utilities.getAllTextUTF8(stringPath), jsonStringOutput;
            Object obj;
            Gson gson;

            try {
                obj = OCVRP.solve(jsonStringInput);
                gson = new GsonBuilder().create();
                ret = 0;
            }
            catch (OurException e) {
                obj = e;
                gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
                ret = e.errorCode;
                System.out.printf(e.errorCode + " " + e.details);
            }
            // System.out.printf(jsonStringOutput);

            jsonStringOutput = gson.toJson(obj);
        } catch (IOException e) {
            //e.printStackTrace();
            ret = -2; // UnhandledError
        }

        return ret;
    }

    @Test public void test()
    {
        int errCode = testJson("src/main/java/com/json/HandledErrorJson/break-ship-ship.json");
        System.out.printf("%d", errCode);
    }

    @Test
    public void testOverallPassedJson()
    {
        final File folder = new File("src/main/java/com/json/PassedJson");
        for (final File fileEntry: folder.listFiles())
            if (fileEntry.isFile()) {
                System.out.printf(fileEntry.getName() + "\n");
                int errCode = testJson(fileEntry.getAbsolutePath());
                // assert errCode == 0;
            }
    }

    @Test
    public void testOverallHandledErrorJson()
    {
        final File folder = new File("src/main/java/com/json/HandledErrorJson");
        for (final File fileEntry: folder.listFiles())
            if (fileEntry.isFile()) {
                System.out.printf(fileEntry.getName() + "\n");
                int errCode = testJson(fileEntry.getAbsolutePath());
                assert errCode != 0;
            }
    }

}
