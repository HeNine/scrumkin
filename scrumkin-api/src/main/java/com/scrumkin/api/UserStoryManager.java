package com.scrumkin.api;

import java.util.Collection;
import java.util.List;

import com.scrumkin.api.exceptions.*;
import com.scrumkin.jpa.*;

import javax.ejb.Local;

@Local
public interface UserStoryManager {


    /**
     * Add new user story to project {@code project}.
     *
     * @param project         Project to which the user story belongs to
     * @param title           Story title
     * @param story           User story description
     * @param priority        User story priority
     * @param businessValue   User story business value
     * @param acceptanceTests Acceptance tests for user story
     * @throws ProjectInvalidException              if project doesn't exist
     * @throws UserStoryInvalidPriorityException    if user story has invalid priority
     * @throws UserStoryTitleNotUniqueException     if user story with same title already exists
     * @throws UserStoryBusinessValueZeroOrNegative if user story business value is invalid (zero or less)
     */
    public int addUserStoryToBacklog(ProjectEntity project, String title, String story, PriorityEntity priority,
                                     int businessValue, Collection<AcceptenceTestEntity> acceptanceTests) throws
            ProjectInvalidException, UserStoryInvalidPriorityException, UserStoryTitleNotUniqueException,
            UserStoryBusinessValueZeroOrNegative;

    /**
     * Check if user story is realized.
     *
     * @param userStory User story to be checked
     * @return True if user story is realized, otherwise false
     */
    public boolean isUserStoryRealized(UserStoryEntity userStory);

    /**
     * Assign new user stories to existing ones in sprint {@code sprint}.
     *
     * @param sprint      Sprint to which user stories belongs to
     * @param userStories User stories to be added to sprint
     */
    public void assignUserStoriesToSprint(SprintEntity sprint, List<UserStoryEntity> userStories) throws
            UserStoryEstimatedTimeNotSetException, UserStoryRealizedException, UserStoryInThisSprintException,
            UserStoryInAnotherSprintException;

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

    /**
     * Add acceptance test to story.
     *
     * @param acceptenceTestEntity Test to be added
     */
    public void addTestToStory(AcceptenceTestEntity acceptenceTestEntity);

    /**
     * Set story time estimate.
     *
     * @param id   Story id
     * @param time Estimated time
     */
    public void setStoryTime(int id, double time) throws UserStoryEstimatedTimeZeroOrNegative;

    /**
     * Update a story test
     *
     * @param id       Test id
     * @param test     Test text
     * @param accepted Test completion status
     */
    public void updateTest(int id, String test, Boolean accepted);

    /**
     * Change test completions status.
     *
     * @param id       Test id
     * @param accepted Test completion status
     */
    public void updateTestCompletion(int id, Boolean accepted);

    /**
     * Deletes a test
     *
     * @param id test ID
     */
    public void deleteTest(int id);

    /**
     * Delete the story
     *
     * @param id Story id
     */
    public void deleteStory(int id);

    /**
     * Updates a story with new information. Any argument can be null to signify no change.
     *
     * @param id              Story id
     * @param title           Story title
     * @param story           User story description
     * @param priority        User story priority
     * @param businessValue   Business value of the story
     * @param acceptanceTests Collection of acceptance tests
     * @throws ProjectInvalidException
     * @throws UserStoryInvalidPriorityException
     * @throws UserStoryTitleNotUniqueException
     * @throws UserStoryBusinessValueZeroOrNegative
     */
    public void updateStory(int id, String title, String story, PriorityEntity priority,
                            Integer businessValue, Collection<AcceptenceTestEntity> acceptanceTests) throws
            UserStoryInvalidPriorityException, UserStoryTitleNotUniqueException,
            UserStoryBusinessValueZeroOrNegative, UserStoryDoesNotExist, UserStoryRealizedException,
            UserStoryAssignedToSprint;

    /**
     * Gets acceptance tests by id
     *
     * @param id Acceptance test id
     */
    public AcceptenceTestEntity getAcceptanceTest(int id);

    /**
     * Add a comment to a story
     *
     * @param id      Story id
     * @param comment Comment text
     * @param role    Commenter role
     */
    public void addStoryComment(int id, String comment, int role);

    /**
     * Updates story comment
     *
     * @param id      Story comment id
     * @param comment Comment text
     * @param role    Commenter role
     */
    void updateStoryComment(int id, String comment, Integer role);

    /**
     * Delete story comment
     *
     * @param id Story comment id
     */
    void deleteStoryComment(int id);

    /**
     * Returns all comments of a story in temporal order
     *
     * @param id Story id
     * @return Ordered list of comments
     */
    public List<StoryCommentEntity> getStoryComments(int id);
}

