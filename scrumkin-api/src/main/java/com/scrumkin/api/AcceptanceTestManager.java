package com.scrumkin.api;

import com.scrumkin.jpa.AcceptenceTestEntity;

public interface AcceptanceTestManager {

    /**
     * Gets acceptance test entity by id.
     *
     * @param id acceptance test id
     */
    public AcceptenceTestEntity getAcceptanceTest(int id);
}
