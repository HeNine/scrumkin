package com.scrumkin.rs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.scrumkin.api.AcceptanceTestManager;
import com.scrumkin.api.PriorityManager;
import com.scrumkin.api.ProjectManager;
import com.scrumkin.api.SprintManager;
import com.scrumkin.api.UserStoryManager;
import com.scrumkin.api.exceptions.*;
import com.scrumkin.jpa.AcceptenceTestEntity;
import com.scrumkin.jpa.PriorityEntity;
import com.scrumkin.jpa.ProjectEntity;
import com.scrumkin.jpa.SprintEntity;
import com.scrumkin.jpa.UserStoryEntity;
import com.scrumkin.rs.json.AcceptanceTestJSON;
import com.scrumkin.rs.json.UserStoryJSON;

@Path("userStories")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class UserStoryService {

    @Inject
    private UserStoryManager usm;
    @Inject
    private ProjectManager pm;
    @Inject
    private PriorityManager prm;
    @Inject
    private AcceptanceTestManager atm;
    @Inject
    private SprintManager sm;

    @POST
    @Path("/add/backlog")
    public void createUserStory(UserStoryJSON userStory, /*@FormParam("acceptanceTest") String[] acceptanceTests,
            @FormParam("acceptanceTestAccepted") boolean[] acceptanceTestsAccepted,*/
                                @Context HttpServletResponse response) {

        ProjectEntity project = pm.getProject(userStory.project);
        PriorityEntity priority = prm.getPriority(userStory.priority);

        int userStoryID = userStory.id;
        UserStoryEntity use = usm.getUserStory(userStoryID);
//		List<AcceptenceTestEntity> acceptenceTests = atm.addAcceptanceTests(use, acceptanceTests,
//				acceptanceTestsAccepted);

        try {
//			usm.addUserStoryToBacklog(project, userStory.title, userStory.story, priority, userStory.bussinessValue,
//					acceptenceTests);
            userStoryID = usm.addUserStoryToBacklog(project, userStory.title, userStory.story, priority,
                    userStory.bussinessValue,
                    null);
        } catch (ProjectInvalidException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            try {
                response.getOutputStream().println("Project doesn't exist");
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (UserStoryInvalidPriorityException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            try {
                response.getOutputStream().println("Priority name " + priority.getName() + " is invalid");
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (UserStoryTitleNotUniqueException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            try {
                response.getOutputStream().println("User story with name " + userStory.title + " already exists");
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (UserStoryBusinessValueZeroOrNegative e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            try {
                response.getOutputStream().println("Business value must be positive");
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        response.setStatus(Response.Status.OK.getStatusCode());
        try {
            response.getOutputStream().println(Integer.toString(userStoryID));
            response.getOutputStream().close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

    }

    @PUT
    @Path("/add/sprint/{id}")
    public void assignUserStories(@PathParam("id") int sprintId, List<UserStoryJSON> userStoriesJSON,
                                @Context HttpServletResponse response) {

        if(userStoriesJSON.get(0).sprint != 0)
            sprintId = userStoriesJSON.get(0).sprint;
        SprintEntity sprint = sm.getSprint(sprintId);

        List<UserStoryEntity> userStoryEntities = new LinkedList<>();
        for(int i = 0; i < userStoriesJSON.size(); i++) {
            UserStoryEntity userStory = usm.getUserStory(userStoriesJSON.get(0).id);
            userStoryEntities.add(userStory);
        }

        try {
            usm.assignUserStoriesToSprint(sprint, userStoryEntities);
        } catch (UserStoryEstimatedTimeNotSetException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            try {
                response.getOutputStream().println("Estimated time not set");
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (UserStoryRealizedException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            try {
                response.getOutputStream().println("Some use stories are already realized");
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (UserStoryInThisSprintException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            try {
                response.getOutputStream().println("Some user stories are already assigned to this sprint");
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (UserStoryInAnotherSprintException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            try {
                response.getOutputStream().println("Some user stories are already assigned to some other sprints");
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        response.setStatus(Response.Status.OK.getStatusCode());
        try {
            response.getOutputStream().println("User stories successfully assigned to a sprint");
            response.getOutputStream().close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    @GET
    @Path("/{id}")
    public UserStoryJSON getUserStory(@PathParam("id") int id) {

        UserStoryEntity use = usm.getUserStory(id);

        UserStoryJSON userStoryJSON = new UserStoryJSON();
        userStoryJSON.init(use);

        return userStoryJSON;
    }

    @PUT
    @Path("/{id}")
    public void updateUserStory(UserStoryJSON userStoryJSON, @PathParam("id") int id,
                                @Context HttpServletResponse response) {
        try {
            usm.setStoryTime(id, userStoryJSON.estimatedTime);
        } catch (UserStoryEstimatedTimeMustBePositive userStoryEstimatedTimeMustBePositive) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            try {
                response.getOutputStream().println("User story estimated time must be positive");
                response.getOutputStream().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @GET
    @Path("/{id}/tests/{test_id}")
    public AcceptanceTestJSON getStoryTest(@PathParam("id") int id, @PathParam("test_id") int testId) {
        UserStoryEntity storyEntity = usm.getUserStory(id);

        for (AcceptenceTestEntity t : storyEntity.getAcceptenceTests()) {
            if (t.getId() == testId) {
                AcceptanceTestJSON acceptanceTestJSON = new AcceptanceTestJSON();
                acceptanceTestJSON.init(t);
                return acceptanceTestJSON;
            }
        }

        return null;
    }

    @POST
    @Path("/{id}/tests")
    public void addTestsToStory(AcceptanceTestJSON acceptanceTestJSON, @PathParam("id") int id) {
        AcceptenceTestEntity acceptenceTestEntity = new AcceptenceTestEntity();
        acceptenceTestEntity.setTest(acceptanceTestJSON.test);
        acceptenceTestEntity.setAccepted(null);
        acceptenceTestEntity.setUserStory(usm.getUserStory(id));

        usm.addTestToStory(acceptenceTestEntity);
    }
}
