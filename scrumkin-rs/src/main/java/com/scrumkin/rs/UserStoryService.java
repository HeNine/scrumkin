package com.scrumkin.rs;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.scrumkin.api.AcceptanceTestManager;
import com.scrumkin.api.PriorityManager;
import com.scrumkin.api.ProjectManager;
import com.scrumkin.api.SprintManager;
import com.scrumkin.api.TaskManager;
import com.scrumkin.api.UserStoryManager;
import com.scrumkin.api.exceptions.ProjectInvalidException;
import com.scrumkin.api.exceptions.UserStoryBusinessValueZeroOrNegative;
import com.scrumkin.api.exceptions.UserStoryEstimatedTimeNotSetException;
import com.scrumkin.api.exceptions.UserStoryInAnotherSprintException;
import com.scrumkin.api.exceptions.UserStoryInvalidPriorityException;
import com.scrumkin.api.exceptions.UserStoryRealizedException;
import com.scrumkin.api.exceptions.UserStoryTitleNotUniqueException;
import com.scrumkin.jpa.AcceptenceTestEntity;
import com.scrumkin.jpa.PriorityEntity;
import com.scrumkin.jpa.ProjectEntity;
import com.scrumkin.jpa.SprintEntity;
import com.scrumkin.jpa.TaskEntity;
import com.scrumkin.jpa.UserStoryEntity;
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
    @Inject
    private TaskManager tm;

    @POST
    @Path("/add/backlog")
    public void createUserStory(UserStoryJSON userStory, @Context HttpServletResponse response) {

        ProjectEntity project = pm.getProject(userStory.project);
        PriorityEntity priority = prm.getPriority(2);
        List<AcceptenceTestEntity> acceptenceTests = new ArrayList<AcceptenceTestEntity>(userStory.acceptenceTests.length);
        for (int i = 0; i < userStory.acceptenceTests.length; i++) {
        	acceptenceTests.add(atm.getAcceptanceTest(userStory.acceptenceTests[i]));
        }
        
        try {
            usm.addUserStoryToBacklog(project, userStory.title, userStory.story, priority, userStory.bussinessValue, acceptenceTests);
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
            response.getOutputStream().println("User stories successfully added to a backlog");
            response.getOutputStream().close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    
    @POST
    @Path("/add/sprint")
    public void assignUserStory(@FormParam("sprintID") int sprintID, UserStoryJSON[] userStory, @Context HttpServletResponse response) {

    	SprintEntity sprint = sm.getSprint(sprintID);
    	
        List<UserStoryEntity> userStories = new ArrayList<UserStoryEntity>(userStory.length);
        for (int i = 0; i < userStory.length; i++) {
        	UserStoryEntity use = new UserStoryEntity();
        	use.setBussinessValue(userStory[i].bussinessValue);
        	use.setEstimatedTime(new BigDecimal(userStory[i].estimatedTime));
        	use.setId(userStory[i].id);
        	use.setStory(userStory[i].story);
        	use.setTitle(userStory[i].title);        	
        	use.setPriority(prm.getPriority(userStory[i].priority));
        	use.setProject(pm.getProject(userStory[i].project));
        	use.setSprint(sm.getSprint(userStory[i].sprint));
        	
        	List<AcceptenceTestEntity> ates = new ArrayList<AcceptenceTestEntity>();
            for (int j = 0; j < userStory[i].acceptenceTests.length; i++) {
            	AcceptenceTestEntity ate = atm.getAcceptanceTest(userStory[i].acceptenceTests[j]);
            	ates.add(ate);
            }
        	use.setAcceptenceTests(ates);
        	
        	List<TaskEntity> tes = new ArrayList<TaskEntity>();
            for (int j = 0; j < userStory[i].tasks.length; i++) {
            	TaskEntity te = tm.getTask(userStory[i].tasks[j]);
            	tes.add(te);
            }
        	use.setTasks(tes);
        	
        	userStories.add(use);
        }

        try {
            usm.assignUserStoryToSprint(sprint, userStories);
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
}
