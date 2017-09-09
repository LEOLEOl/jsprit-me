package controllers;

import com.google.gson.annotations.SerializedName;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by LEOLEOl on 3/28/2017.
 */
public class ShareInfo {

    public static String extractPostRequestBody(HttpServletRequest request) throws IOException
    {
        return org.apache.commons.io.IOUtils.toString(request.getReader());
    }
}
