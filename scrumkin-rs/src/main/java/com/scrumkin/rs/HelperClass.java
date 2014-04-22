package com.scrumkin.rs;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by Uporabnik on 22.4.2014.
 */
public class HelperClass {

    public static void exceptionHandler(HttpServletResponse response, String message) {
        try {
            response.getOutputStream().println(message);
            response.getOutputStream().close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
