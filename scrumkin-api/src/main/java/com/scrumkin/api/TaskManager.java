package com.scrumkin.api;

import com.scrumkin.api.exceptions.SprintNotActiveException;
import com.scrumkin.api.exceptions.TaskDoesNotExist;
import com.scrumkin.api.exceptions.TaskEstimatedTimeMustBePositive;
import com.scrumkin.api.exceptions.UserStoryRealizedException;
import com.scrumkin.jpa.TaskEntity;

import javax.ejb.Local;

@Local
public interface TaskManager {

    /**
     * Gets task by id.
     *
     * @param id task id
     */
    public TaskEntity getTask(int id);

    /**
     * Add a task to a story of an active sprint
     *
     * @param activeSprintID Active sprint ID
     * @param userStoryID    User story ID
     * @param description    Task description
     * @param estimated_time Estimated time for task
     * @param userID         User ID of user realizing the task (can be null)
     */
    public void addTaskToStory(int activeSprintID, int userStoryID, String description, double estimated_time,
                               Integer userID) throws SprintNotActiveException, UserStoryRealizedException,
            TaskEstimatedTimeMustBePositive;

    /**
     * Updates the task. Any argument may be null to indicate no change.
     *
     * @param id            Task ID
     * @param description   Task description
     * @param estimatedTime Estimated time for task
     * @param userId        User ID of user realizing the task (can be null)
     * @param accepted      Has user accepted the task
     */
    public void updateTask(int id, String description, Double estimatedTime, Integer userId,
                           Boolean accepted) throws UserStoryRealizedException,
            TaskEstimatedTimeMustBePositive, TaskDoesNotExist;
}
