package com.scrumkin.rs;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.scrumkin.api.ProjectManager;
import com.scrumkin.api.SprintManager;
import com.scrumkin.api.exceptions.*;
import com.scrumkin.jpa.ProjectEntity;
import com.scrumkin.jpa.SprintEntity;
import com.scrumkin.jpa.UserStoryEntity;
import com.scrumkin.rs.json.SprintJSON;
import com.scrumkin.rs.json.UserStoryJSON;

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
    public SprintJSON[] getAllSprints() {
        Collection<SprintEntity> sprintEntities = sm.getAllSprints();
        Iterator<SprintEntity> sIt = sprintEntities.iterator();
        SprintJSON[] sprintJSONs = new SprintJSON[sprintEntities.size()];

        for (int i = 0; i < sprintJSONs.length; i++) {
            SprintJSON sprintJSON = new SprintJSON();
            sprintJSON.init(sIt.next());
            sprintJSONs[i] = sprintJSON;
        }

        return sprintJSONs;
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

    @PUT
    @Path("/{id}")
    public void updateSprint(SprintJSON sprint, @PathParam("id") int id, @Context HttpServletResponse response) {
        try {
            sm.updateSprint(sprint.id == 0 ? sm.getSprint(id).getId() : sprint.id, sprint.startDate, sprint.endDate,
                    sprint.velocity, sprint.stories);
        } catch (SprintDatesOutOfOrderException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, "Sprint dates out of order");
        } catch (SprintStartDateInThePast e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, "Sprint start date is in the past");
        } catch (SprintVelocityZeroOrNegative e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, "Sprint velocity is zero or negative");
        } catch (SprintTimeSlotNotAvailable e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, "Sprint date is not available");
        } catch (SprintOverlap e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, e.getMessage());
        }

    }

    @DELETE
    @Path("/{id}")
    public void deleteSprint(@PathParam("id") int id) {
        sm.deleteSprint(id);
    }

    @GET
    @Path("/{id}/stories")
    public UserStoryJSON[] getSprintStories(@PathParam("id") int id) {
        Collection<UserStoryEntity> stories = sm.getSprintStories(id);

        List<UserStoryJSON> storiesJSON = new LinkedList<>();
        for (UserStoryEntity us : stories) {
            UserStoryJSON story = new UserStoryJSON();
            story.init(us);
            storiesJSON.add(story);
        }

        return storiesJSON.toArray(new UserStoryJSON[0]);
    }
}
