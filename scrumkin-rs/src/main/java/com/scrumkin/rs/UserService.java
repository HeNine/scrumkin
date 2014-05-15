package com.scrumkin.rs;

import java.io.IOException;
import java.util.*;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.scrumkin.api.*;
import com.scrumkin.api.exceptions.*;
import com.scrumkin.jpa.GroupEntity;
import com.scrumkin.jpa.ProjectEntity;
import com.scrumkin.jpa.TaskEntity;
import com.scrumkin.jpa.UserEntity;
import com.scrumkin.rs.annotations.Authenticated;
import com.scrumkin.rs.json.ProjectJSON;
import com.scrumkin.rs.json.TaskJSON;
import com.scrumkin.rs.json.UserJSON;

@Path("users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
@Authenticated
@LocalBean
public class UserService implements AuthenticatedRESTService {

    @Inject
    private UserManager um;
    @Inject
    private GroupManager gm;
    @Inject
    private ProjectManager pm;
    @Inject
    private UserLoginManager ulm;
    @Inject
    private TaskManager tm;

    private UserEntity authenticatedUser;

    @POST
    @Path("/add")
    public void createUser(UserJSON user, @Context HttpServletResponse response) {

        List<GroupEntity> groups = new ArrayList<GroupEntity>(user.groups.length);
        for (int i = 0; i < user.groups.length; i++) {
            groups.add(gm.getGroup(user.groups[i]));
        }

        try {
            um.addUser(user.username, user.password, user.password, user.name, user.email, user.email, groups);
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
        } catch (Exception e) {
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

    @GET
    public UserJSON[] getUsers() {

        List<UserJSON> users = new LinkedList<>();
        List<UserEntity> usersPOJO = um.getUsers();
        for (UserEntity ue : usersPOJO) {
            UserJSON userJSON = new UserJSON();
            userJSON.init(ue);
            users.add(userJSON);
        }

        return users.toArray(new UserJSON[0]);
    }

    @GET
    @Path("{id}/projects")
    public ProjectJSON[] getUserProjects(@PathParam("id") int id) {
        Collection<ProjectEntity> projectEntities = um.getUserProject(id);

        ProjectJSON[] projectJSONs = new ProjectJSON[projectEntities.size()];

        Iterator<ProjectEntity> pIt = projectEntities.iterator();

        for (int i = 0; i < projectJSONs.length; i++) {
            ProjectJSON projectJSON = new ProjectJSON();
            projectJSON.init(pIt.next(), pm);
            projectJSONs[i] = projectJSON;
        }

        return projectJSONs;
    }

    @GET
    @Path("{id}/tasks")
    public TaskJSON[] getUserTasks(@PathParam("id") int id) {
        Collection<TaskEntity> taskEntities = tm.getUserTasks(id);

        TaskJSON[] taskJSONs = new TaskJSON[taskEntities.size()];

        Iterator<TaskEntity> tIt = taskEntities.iterator();

        for (int i = 0; i < taskJSONs.length; i++) {
            TaskJSON taskJSON = new TaskJSON();
            taskJSON.init(tIt.next());
            taskJSONs[i] = taskJSON;
        }

        return taskJSONs;
    }

    @Override
    public UserEntity getAuthenticatedUser() {
        return authenticatedUser;
    }

    @Override
    public void setAuthenticatedUser(UserEntity user) {
        this.authenticatedUser = user;
    }

    @DELETE
    @Path("{id}/projects/{projectID}")
    public void removeUserFromProject(@PathParam("id") int id, @PathParam("projectID") int projectID,
                                      @Context HttpServletResponse response) {
        try {
            um.deleteUserFromProject(id, projectID);
        } catch (UserNotInProject e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, e.getMessage());
        }
    }

    @POST
    @Path("{id}/groups/add")
    public void addGroupsToUser(@PathParam("id") int id, UserJSON userJSON,
                                      @Context HttpServletResponse response) {
        try {
            um.addUserGroups(id, userJSON.groups);
        } catch (UserInGroup e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, e.getMessage());
        }
    }

    @POST
    @Path("{id}/groups/remove")
    public void removeGroupsFromUser(@PathParam("id") int id, UserJSON userJSON,
                                      @Context HttpServletResponse response) {
        try {
            um.deleteUserGroups(id, userJSON.groups);
        } catch (UserNotInGroup e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, e.getMessage());
        }
    }
}