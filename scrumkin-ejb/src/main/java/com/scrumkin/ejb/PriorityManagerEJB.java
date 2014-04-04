package com.scrumkin.ejb;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.scrumkin.api.PriorityManager;
import com.scrumkin.jpa.PriorityEntity;

public class PriorityManagerEJB implements PriorityManager {

    @PersistenceContext(unitName = "scrumkin_PU")
    private EntityManager em;
    
    public PriorityEntity getPriority(int id) {
    	PriorityEntity priority = em.find(PriorityEntity.class, id);

        return priority;
    }
}
