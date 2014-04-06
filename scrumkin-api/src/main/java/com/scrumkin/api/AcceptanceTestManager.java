package com.scrumkin.api;

import java.util.List;

import com.scrumkin.jpa.AcceptenceTestEntity;
import com.scrumkin.jpa.UserStoryEntity;

import javax.ejb.Local;

@Local
public interface AcceptanceTestManager {

    /**
     * Gets acceptance test entity by id.
     *
     * @param id acceptance test id
     */
    public AcceptenceTestEntity getAcceptanceTest(int id);
    
    /**
     * Adds acceptance test entity to user story {@code userStory}.
     *
     * @param userStory User story to which acceptance test belongs to
     * @param test Test to be performed on user story
     * @param accepted Specifies whether acceptance test has been accepted
     */
    public AcceptenceTestEntity addAcceptanceTest(UserStoryEntity userStory, String test, boolean accepted);
    
    /**
     * Adds acceptance test entities to user story {@code userStory}.
     *
     * @param userStory User story to which acceptance test belongs to
     * @param tests Tests to be performed on user story
     * @param accepted Specifies whether specific acceptance test has been accepted
     */
    public List<AcceptenceTestEntity> addAcceptanceTests(UserStoryEntity userStory, String[] tests, boolean[] accepted);
}
