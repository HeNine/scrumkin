package com.scrumkin.rs.json;

import com.scrumkin.jpa.AcceptenceTestEntity;
import com.scrumkin.jpa.UserStoryEntity;

/**
 * Created by Matija on 10.4.2014.
 */
public class AcceptanceTestJSON {

    public int id;
    public String test;
    public Boolean accepted;
    public int userStory;

    public void init(AcceptenceTestEntity test) {
        this.id = test.getId();
        this.test = test.getTest();
        this.accepted = test.getAccepted();
        this.userStory = test.getUserStory().getId();
    }
}
