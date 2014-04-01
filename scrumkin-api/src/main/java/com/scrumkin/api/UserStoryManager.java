package com.scrumkin.api;

import java.util.Collection;
import java.util.List;

import com.scrumkin.api.exceptions.ProjectInvalidException;
import com.scrumkin.api.exceptions.UserStoryBusinessValueZeroOrNegative;
import com.scrumkin.api.exceptions.UserStoryInvalidPriorityException;
import com.scrumkin.api.exceptions.UserStoryTitleNotUniqueException;
import com.scrumkin.jpa.AcceptenceTestEntity;
import com.scrumkin.jpa.PriorityEntity;
import com.scrumkin.jpa.ProjectEntity;
import com.scrumkin.jpa.SprintEntity;
import com.scrumkin.jpa.UserStoryEntity;

public interface UserStoryManager {
	
    /**
     * Add new user story to project {@code project}.
     *
     * @param project  		  Project user story belongs to
     * @param title 	 	  Story title
     * @param story      	  User story description
     * @param priority 		  User story priority
     * @param businessValue   User story business value
     * @param acceptanceTests Acceptance tests for user story
     * @throws ProjectInvalidException              if project doesn't exist
     * @throws UserStoryInvalidPriorityException    if user story has invalid priority
     * @throws UserStoryTitleNotUniqueException     if user story with same title already exists
     * @throws UserStoryBusinessValueZeroOrNegative if user story business value is invalid (zero or less)
     */
	public void addUserStoryToBacklog(ProjectEntity project, String title, String story, PriorityEntity priority, int businessValue, Collection<AcceptenceTestEntity> acceptanceTests) throws ProjectInvalidException, UserStoryInvalidPriorityException, UserStoryTitleNotUniqueException, UserStoryBusinessValueZeroOrNegative;
	
    /**
     * Assign new user story to sprint {@code sprint}.
     *
     * @param sprint  	  Sprint to which user stories belongs to
     * @param userStories User stories to be added to sprint
     */
	public void assignUserStoryToSprint(SprintEntity sprint, List<UserStoryEntity> userStories);
	
    /**
     * Gets user story by id.
     *
     * @param id User story id
     */
    public UserStoryEntity getUserStory(int id);
    
    /**
     * Gets valid priorities.
     */
    public List<PriorityEntity> getValidPriorities();
}
