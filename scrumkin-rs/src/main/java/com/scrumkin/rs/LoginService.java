package com.scrumkin.rs;

import com.scrumkin.api.UserLoginManager;
import com.scrumkin.jpa.UserEntity;
import com.scrumkin.rs.json.UserJSON;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by Matija on 28.3.2014.
 */
@Path("login")
@Stateless
public class LoginService {

    @Inject
    private UserLoginManager ulm;

    /**
     * GET request to /login returns a user as a JSON object.
     *
     * @param loginToken Injected from request
     * @param response   Injected from context
     * @return User as JSON object
     */
    @GET
    @Produces("application/json")
    public UserJSON getAuthenticatedUser(@CookieParam("login_token") String loginToken,
                                         @Context HttpServletResponse response) {
        UserEntity userEntity = ulm.getUserFromToken(loginToken);

        if (userEntity == null) {
            response.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
            try {
                response.getOutputStream().close();
            } catch (IOException e) {
            }
            return null;
        }

        UserJSON userJSON = new UserJSON();
        userJSON.init(userEntity);

        return userJSON;
    }

    /**
     * Posting username and password to /login sets a cookie that is later used to authenticate the user.
     *
     * @param username User's username
     * @param password User's password in plaintext
     * @param response Drawn from context
     */
    @POST
    public void doAuthenticate(@FormParam("username") String username, @FormParam("password") String password,
                               @Context HttpServletResponse response) {

        String token = ulm.getLoginToken(username, password);

        if (token == null) {
            response.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
            try {
                response.getOutputStream().close();
            } catch (IOException e) {
            }
            return;
        }

        response.setStatus(Response.Status.OK.getStatusCode());

        Cookie tokenCookie = new Cookie("login_token", token);
        tokenCookie.setHttpOnly(true);

        response.addCookie(tokenCookie);
        try {
            response.getOutputStream().close();
        } catch (IOException e) {
        }
    }

    @DELETE
    public void doLogout(@CookieParam("login_token") String login_token, @Context HttpServletResponse response) {
        ulm.doLogout(login_token);

        Cookie tokenCookie = new Cookie("login_token", "");
        tokenCookie.setMaxAge(0);

        response.addCookie(tokenCookie);
        try {
            response.getOutputStream().close();
        } catch (IOException e) {
        }
    }

}
