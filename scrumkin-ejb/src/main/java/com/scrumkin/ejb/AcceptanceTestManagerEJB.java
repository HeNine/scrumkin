package com.scrumkin.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.mindrot.jbcrypt.BCrypt;

import com.scrumkin.api.AcceptanceTestManager;
import com.scrumkin.jpa.AcceptenceTestEntity;
import com.scrumkin.jpa.UserEntity;
import com.scrumkin.jpa.UserStoryEntity;

@Stateless
public class AcceptanceTestManagerEJB implements AcceptanceTestManager {

    @PersistenceContext(unitName = "scrumkin_PU")
    private EntityManager em;
    
    @Override
    public AcceptenceTestEntity getAcceptanceTest(int id) {
    	AcceptenceTestEntity acceptanceTest = em.find(AcceptenceTestEntity.class, id);

        return acceptanceTest;
    }
    
    @Override
    public AcceptenceTestEntity addAcceptanceTest(UserStoryEntity userStory, String test, boolean accepted) {
    	
    	AcceptenceTestEntity acceptanceTest = new AcceptenceTestEntity();
    	acceptanceTest.setUserStory(userStory);
    	acceptanceTest.setTest(test);
    	acceptanceTest.setAccepted(accepted);

        em.persist(acceptanceTest);
        
        return acceptanceTest;
    }
    
    @Override
    public List<AcceptenceTestEntity> addAcceptanceTests(UserStoryEntity userStory, String[] tests, boolean[] accepted) {
    	
    	List<AcceptenceTestEntity> acceptanceTests = new ArrayList<AcceptenceTestEntity>();
    	for(int i = 0; i < tests.length; i++) {
    		AcceptenceTestEntity acceptanceTest = addAcceptanceTest(userStory, tests[i], accepted[i]);
    		acceptanceTests.add(acceptanceTest);
    	}
    	
    	return acceptanceTests;
    }
}
