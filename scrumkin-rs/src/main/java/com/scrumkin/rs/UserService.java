package com.scrumkin.rs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.scrumkin.api.GroupManager;
import com.scrumkin.api.UserManager;
import com.scrumkin.api.exceptions.UserEmailMismatchException;
import com.scrumkin.api.exceptions.UserInvalidGroupsException;
import com.scrumkin.api.exceptions.UserNotUniqueException;
import com.scrumkin.api.exceptions.UserPasswordMismatchException;
import com.scrumkin.api.exceptions.UserUsernameNotUniqueException;
import com.scrumkin.jpa.GroupEntity;
import com.scrumkin.jpa.UserEntity;
import com.scrumkin.rs.json.UserJSON;

@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class UserService {
	
	@Inject
	private UserManager um;
	@Inject
	private GroupManager gm;
	
	@POST
	@Path("/add")
	public void createUser(@FormParam("username") String username, @FormParam("password") String password, @FormParam("confirmPassword") String confirmPassword, @FormParam("name") String name, @FormParam("email") String email, @FormParam("confirmEmail") String confirmEmail, @FormParam("systemGroupIds") int[] systemGroupIds, @Context HttpServletResponse response) {
		
		List<GroupEntity> groups = new ArrayList<GroupEntity>(systemGroupIds.length);
		for(int i = 0; i < systemGroupIds.length; i++) {
			groups.add(gm.getGroup(systemGroupIds[i]));
		}
		
        try {
            um.addUser(username, password, confirmPassword, name, email, confirmEmail, groups);
        } catch (UserInvalidGroupsException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            try {
                response.getOutputStream().println("Some or all of groups don't exist");
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (UserUsernameNotUniqueException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            try {
                response.getOutputStream().println("Username already taken");
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (UserNotUniqueException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            try {
                response.getOutputStream().println("User already exists");
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (UserPasswordMismatchException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            try {
                response.getOutputStream().println("Passwords don't match");
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (UserEmailMismatchException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            try {
                response.getOutputStream().println("Emails don't match");
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch(Exception e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            try {
                response.getOutputStream().println("Unable to save user");
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
		}
        
        response.setStatus(Response.Status.OK.getStatusCode());
        try {
            response.getOutputStream().println("User successfully saved");
            response.getOutputStream().close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
     
	}
	
    @GET
    @Path("/{id}")
    public UserJSON getUser(@PathParam("id") int id) {
    	
        UserEntity user = um.getUser(id);

        UserJSON userJSON = new UserJSON();
        userJSON.init(user);

        return userJSON;
    }
    
    public class UserJSONList extends ArrayList<UserJSON> {

		private static final long serialVersionUID = 1L;
    	
    }
    
    @GET
    public UserJSONList getUsers() {
    	
    	UserJSONList users = new UserJSONList();
    	
    	List<UserEntity> usersPOJO = um.getUsers();
    	for(UserEntity ue : usersPOJO) {
    		UserJSON userJSON = new UserJSON();
    		userJSON.init(ue);
    		users.add(userJSON);
    	}
    	
    	return users;
    }

}