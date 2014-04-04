package com.scrumkin.api;

import com.scrumkin.jpa.TaskEntity;

public interface TaskManager {

    /**
     * Gets task by id.
     *
     * @param id task id
     */
    public TaskEntity getTask(int id);
}
