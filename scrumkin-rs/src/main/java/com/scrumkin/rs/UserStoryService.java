package com.scrumkin.rs;

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
import com.scrumkin.jpa.*;
import com.scrumkin.rs.json.AcceptanceTestJSON;
import com.scrumkin.rs.json.StoryCommentJSON;
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
    public void createUserStory(UserStoryJSON userStory, @Context HttpServletResponse response) {

        ProjectEntity project = pm.getProject(userStory.project);
        PriorityEntity priority = prm.getPriority(userStory.priority);

        try {
            int userStoryId = usm.addUserStoryToBacklog(project, userStory.title, userStory.story, priority,
                    userStory.bussinessValue, null);
            response.setStatus(Response.Status.OK.getStatusCode());
            HelperClass.exceptionHandler(response, Integer.toString(userStoryId));
        } catch (ProjectInvalidException | UserStoryInvalidPriorityException | UserStoryTitleNotUniqueException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, e.getMessage());
        } catch (UserStoryBusinessValueZeroOrNegative e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, "Business value must be positive");
        }
    }

    @PUT
    @Path("/add/sprint/{id}")
    public void assignUserStories(@PathParam("id") int sprintId, List<UserStoryJSON> userStoriesJSON,
                                  @Context HttpServletResponse response) {

        if (userStoriesJSON.get(0).sprint != 0)
            sprintId = userStoriesJSON.get(0).sprint;
        SprintEntity sprint = sm.getSprint(sprintId);

        List<UserStoryEntity> userStoryEntities = new LinkedList<>();
        for (int i = 0; i < userStoriesJSON.size(); i++) {
            UserStoryEntity userStory = usm.getUserStory(userStoriesJSON.get(0).id);
            userStoryEntities.add(userStory);
        }

        try {
            usm.assignUserStoriesToSprint(sprint, userStoryEntities);
        } catch (UserStoryEstimatedTimeNotSetException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, e.getMessage());
        } catch (UserStoryRealizedException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, e.getMessage());
        } catch (UserStoryInThisSprintException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, e.getMessage());
        } catch (UserStoryInAnotherSprintException e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, e.getMessage());
        }

        response.setStatus(Response.Status.OK.getStatusCode());
        HelperClass.exceptionHandler(response, "User stories successfully assigned to sprint " + sprintId);
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

//        if(userStoryJSON.acceptenceTests != null) {
//            List<AcceptenceTestEntity> acceptanceTests = new LinkedList<>();
//            for (int i : userStoryJSON.acceptenceTests) {
//                acceptanceTests.add(usm.getAcceptanceTest(i));
//            }
//        }

        try {
            usm.updateStory(id, userStoryJSON.title, userStoryJSON.story, prm.getPriority(userStoryJSON.priority),
                    userStoryJSON.bussinessValue, null);
        } catch (UserStoryInvalidPriorityException | UserStoryTitleNotUniqueException |
                UserStoryBusinessValueZeroOrNegative | UserStoryDoesNotExist | UserStoryRealizedException |
                UserStoryAssignedToSprint e) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, e.getMessage());
        }

        try {
            usm.setStoryTime(id, userStoryJSON.estimatedTime);
        } catch (UserStoryEstimatedTimeZeroOrNegative userStoryEstimatedTimeMustBePositive) {
            response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
            HelperClass.exceptionHandler(response, "User story estimated time must not be zero or negative");
        }
    }

    @DELETE
    @Path("/{id}")
    public void deleteStory(@PathParam("id") int id) {
        usm.deleteStory(id);
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

    @PUT
    @Path("/{id}/tests/{test_id}")
    public void updateAcceptanceTest(@PathParam("id") int id, @PathParam("test_id") int testId,
                                     AcceptanceTestJSON test) {
        usm.updateTest(testId, test.test, test.accepted);
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

    @GET
    @Path("/{id}/comments")
    public StoryCommentJSON[] getStoryComments(@PathParam("id") int id) {
        List<StoryCommentEntity> storyCommentEntities = usm.getStoryComments(id);
        StoryCommentJSON[] storyCommentJSONs = new StoryCommentJSON[storyCommentEntities.size()];

        for (int i = 0; i < storyCommentEntities.size(); i++) {
            storyCommentJSONs[i] = new StoryCommentJSON();
            storyCommentJSONs[i].init(storyCommentEntities.get(i));
        }

        return storyCommentJSONs;
    }

    @POST
    @Path("/{id}/comments")
    public void addStoryComment(@PathParam("id") int id, StoryCommentJSON storyCommentJSON) {
        usm.addStoryComment(id, storyCommentJSON.comment, storyCommentJSON.role);
    }

    @PUT
    @Path("/{id}/comments/{storyCommentId}")
    public void editStoryComment(@PathParam("storyCommentId") int storyCommentId, StoryCommentJSON storyCommentJSON) {
        usm.updateStoryComment(storyCommentId, storyCommentJSON.comment, storyCommentJSON.role);
    }

    @DELETE
    @Path("/{id}/comments/{storyCommentId}")
    public void deleteStoryComment(@PathParam("storyCommentId") int storyCommentId) {
        usm.deleteStoryComment(storyCommentId);
    }
}

