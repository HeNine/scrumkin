package com.scrumkin.ejb;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.scrumkin.api.AcceptanceTestManager;
import com.scrumkin.jpa.AcceptenceTestEntity;

@Stateless
public class AcceptanceTestManagerEJB implements AcceptanceTestManager {

    @PersistenceContext(unitName = "scrumkin_PU")
    private EntityManager em;
    
    @Override
    public AcceptenceTestEntity getAcceptanceTest(int id) {
    	AcceptenceTestEntity acceptanceTest = em.find(AcceptenceTestEntity.class, id);

        return acceptanceTest;
    }
}
