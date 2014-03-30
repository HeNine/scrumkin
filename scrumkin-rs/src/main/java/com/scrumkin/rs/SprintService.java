package com.scrumkin.rs;

import com.scrumkin.api.ProjectManager;
import com.scrumkin.api.SprintManager;
import com.scrumkin.api.exceptions.SprintDatesOutOfOrderException;
import com.scrumkin.api.exceptions.SprintStartDateInThePast;
import com.scrumkin.api.exceptions.SprintTimeSlotNotAvailable;
import com.scrumkin.api.exceptions.SprintVelocityZeroOrNegative;
import com.scrumkin.jpa.ProjectEntity;
import com.scrumkin.jpa.SprintEntity;
import com.scrumkin.rs.json.SprintJSON;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.sql.Date;

/**
 * Created by Matija on 29.3.2014.
 */
@Path("sprint")
@Consumes("application/json")
@Produces("application/json")
@Stateless
public class SprintService {

    @Inject
    private ProjectManager pm;
    @Inject
    private SprintManager sm;

    @POST
    public void createSprint(SprintJSON sprint, @Context HttpServletResponse response) {

        ProjectEntity project = pm.getProject(sprint.projectId);

        try {
            sm.addSprint(project, sprint.startDate, sprint.endDate, sprint.velocity);
        } catch (SprintDatesOutOfOrderException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            try {
                response.getOutputStream().println("Sprint dates out of order");
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (SprintStartDateInThePast sprintStartDateInThePast) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            try {
                response.getOutputStream().println("Sprint start date is in the past");
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (SprintVelocityZeroOrNegative sprintVelocityZeroOrNegative) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            try {
                response.getOutputStream().println("Sprint velocity is zero or negative");
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (SprintTimeSlotNotAvailable sprintTimeSlotNotAvailable) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            try {
                response.getOutputStream().println("Sprint date is not available");
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

    @GET
    @Path("/{id}")
    public SprintJSON getSprint(@PathParam("id") int id) {
        SprintEntity sprint = sm.getSprint(id);

        SprintJSON sprintJSON = new SprintJSON();
        sprintJSON.projectId = sprint.getProject().getId();
        sprintJSON.startDate = sprint.getStartDate();
        sprintJSON.endDate = sprint.getEndDate();
        sprintJSON.velocity = sprint.getVelocity();

        return sprintJSON;
    }

}
