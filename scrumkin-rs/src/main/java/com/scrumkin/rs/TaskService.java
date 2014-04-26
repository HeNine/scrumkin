package com.scrumkin.rs;

import com.scrumkin.api.*;
import com.scrumkin.api.exceptions.*;
import com.scrumkin.jpa.*;
import com.scrumkin.rs.json.AcceptanceTestJSON;
import com.scrumkin.rs.json.TaskJSON;
import com.scrumkin.rs.json.UserStoryJSON;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Path("tasks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class TaskService {

    @Inject
    private TaskManager tm;

    @POST
    @Path("/sprint/{id}")
    public void addTaskToStory(TaskJSON taskJSON, @PathParam("id") int id, @Context HttpServletResponse response) {
        try {
            tm.addTaskToStory(id, taskJSON.userStoryID, taskJSON.description, taskJSON.estimatedTime,
                    taskJSON.assigneeID);
        } catch (SprintNotActiveException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, e.getMessage());
        } catch (UserStoryRealizedException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, e.getMessage());
        } catch (TaskEstimatedTimeMustBePositive e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, e.getMessage());
        }

        response.setStatus(Response.Status.OK.getStatusCode());
        HelperClass.exceptionHandler(response, "Task successfully assigned to sprint with id " + id);
    }

    @GET
    @Path("/{id}")
    public TaskJSON getTask(@PathParam("id") int id) {

        TaskEntity task = tm.getTask(id);

        TaskJSON taskJSON = new TaskJSON();
        taskJSON.init(task);

        return taskJSON;
    }
}