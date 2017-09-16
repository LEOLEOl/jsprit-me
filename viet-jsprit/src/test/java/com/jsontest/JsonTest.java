package com.jsontest;

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

    @Test
    public void testOverallPassedJson()
    {
        final File folder = new File("src/main/java/com/jsontest/PassedJson");
        File[] listFiles = folder.listFiles();
        if (listFiles == null) return;
        for (final File fileEntry: listFiles)
            if (fileEntry.isFile()) {
                System.out.printf(fileEntry.getName() + "\n");
                int errCode = testJson(fileEntry.getAbsolutePath());
                System.out.printf(Integer.toString(errCode));
                assert errCode == 0;
            }
    }

    @Test
    public void testOverallHandledErrorJson()
    {
        final File folder = new File("src/main/java/com/jsontest/handlederrorjson");
        File[] listFiles = folder.listFiles();
        if (listFiles == null) return;
        for (final File fileEntry: listFiles)
            if (fileEntry.isFile()) {
                System.out.printf(fileEntry.getName() + "\n");
                int errCode = testJson(fileEntry.getAbsolutePath());
                assert errCode != 0;
            }
    }

}
