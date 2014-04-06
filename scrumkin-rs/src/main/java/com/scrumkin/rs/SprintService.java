package com.scrumkin.rs;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.scrumkin.api.ProjectManager;
import com.scrumkin.api.SprintManager;
import com.scrumkin.api.exceptions.SprintDatesOutOfOrderException;
import com.scrumkin.api.exceptions.SprintStartDateInThePast;
import com.scrumkin.api.exceptions.SprintTimeSlotNotAvailable;
import com.scrumkin.api.exceptions.SprintVelocityZeroOrNegative;
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
