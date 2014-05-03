package com.scrumkin.api;

import com.scrumkin.api.exceptions.*;
import com.scrumkin.jpa.TaskEntity;

import javax.ejb.Local;
import java.sql.Timestamp;
import java.util.Collection;

@Local
public interface TaskManager {

    /**
     * Gets task by id.
     *
     * @param id task id
     */
    public TaskEntity getTask(int id);

    /**
     * Add a task to a story of an active sprint.
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
                           Boolean accepted) throws TaskEstimatedTimeMustBePositive, TaskDoesNotExist,
            TaskAlreadyFinished, TaskNotAccepted;

    /**
     * Get tasks user is assigned to.
     *
     * @param id User id
     * @return Collection of tasks
     */
    public Collection<TaskEntity> getUserTasks(int id);

    /**
     * Add work done to a task.
     *
     * @param id            Task ID
     * @param userId        User ID
     * @param workDone      Work done in hours
     * @param workRemaining Work remaining on task in hours
     * @param date          Date of work done
     */
    public void addTaskWorkDone(int id, int userId, double workDone, double workRemaining, Timestamp date)
            throws TaskWorkDoneMustBePositive, TaskEstimatedTimeMustBePositive;

    /**
     * Finishes specified task a user is assigned to.
     *
     * @param id task id
     */
    public void finishUserTask(int id) throws TaskAlreadyFinished, TaskNotAccepted;
}
