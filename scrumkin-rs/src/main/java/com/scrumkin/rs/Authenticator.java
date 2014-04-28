package com.scrumkin.rs;

import com.scrumkin.api.UserLoginManager;
import com.scrumkin.rs.annotations.Authenticated;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by Matija on 27.4.2014.
 */
@RequestScoped
@Interceptor
@Authenticated
public class Authenticator {

    @Inject
    private UserLoginManager ulm;

    @Inject
    private HttpServletRequest request;

    @AroundInvoke
    public Object authenticate(InvocationContext ic) throws Exception {
        if (!(ic.getTarget() instanceof AuthenticatedRESTService)) {
            return ic.proceed();
        }
        for (Cookie c : request.getCookies()) {
            if (c.getName().equals("login_token")) {
                ((AuthenticatedRESTService) ic.getTarget()).setAuthenticatedUser(ulm.getUserFromToken(c.getValue()));
            }
        }
        return ic.proceed();
    }
}
