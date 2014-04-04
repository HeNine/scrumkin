package com.scrumkin.api;

import com.scrumkin.jpa.PriorityEntity;

public interface PriorityManager {

    /**
     * Gets priority entity by id.
     *
     * @param id Priority id
     */
    public PriorityEntity getPriority(int id);
}
