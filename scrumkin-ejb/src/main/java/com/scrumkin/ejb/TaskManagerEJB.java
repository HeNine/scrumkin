package com.scrumkin.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.scrumkin.api.TaskManager;
import com.scrumkin.jpa.TaskEntity;

@Stateless
public class TaskManagerEJB implements TaskManager {

    @PersistenceContext(unitName = "scrumkin_PU")
    private EntityManager em;
    
    @Override
    public TaskEntity getTask(int id) {
    	TaskEntity task = em.find(TaskEntity.class, id);

        return task;
    }
}
