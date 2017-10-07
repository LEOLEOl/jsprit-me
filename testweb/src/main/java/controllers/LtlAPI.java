package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ourcode.exceptions.OurException;
import com.ourcode.models.process.OCVRP;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 * User: Tung
 * Date: 15/04/17
 * Time: 11:39 PM
 * To change this template use File | Settings | File Templates.
 */

//@MultipartConfig(fileSizeThreshold=1024*1024*10,    // 10 MB
//    maxFileSize=1024*1024*50,          // 50 MB
//    maxRequestSize=1024*1024*100)      // 100 MB
@WebServlet(name = "LtlAPI", urlPatterns = "/LtlAPI")
public class LtlAPI extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/jsontest;charset=utf-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.getWriter().write("GET\nWelcome to OCVehicleRoutingProblem!\n Ha Duc Viet");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonInput = ShareInfo.extractPostRequestBody(req), jsonOutput;
        Object obj;
        Gson gson;

        try {
            obj = new OCVRP().solveJson(jsonInput);
            gson = new GsonBuilder().create();
        }
        catch (OurException e) {
            obj = e;
            gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        }

        jsonOutput = gson.toJson(obj);

        // Set output contents and properties
        resp.setContentType("application/jsontest;charset=utf-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        PrintWriter writer = resp.getWriter();
        writer.printf(jsonOutput);
    }
}





