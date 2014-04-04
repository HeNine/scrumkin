package com.scrumkin.rs.json;

import com.scrumkin.jpa.AcceptenceTestEntity;
import com.scrumkin.jpa.TaskEntity;
import com.scrumkin.jpa.UserStoryEntity;

import java.util.Iterator;

/**
 * Created by Matija on 4.4.2014.
 */
public class UserStoryJSON {
    public int id;
    public String title;
    public String story;
    public int bussinessValue;
    public int estimatedTime;
    public int[] acceptenceTests;
    public int[] tasks;
    public int priority;
    public int project;
    public int sprint;

    public void init(UserStoryEntity userStory) {
        id = userStory.getId();
        title = userStory.getTitle();
        story = userStory.getStory();
        bussinessValue = userStory.getBussinessValue();

        try {
            estimatedTime = userStory.getEstimatedTime().intValue();
        } catch (NullPointerException e) {
            estimatedTime = 0;
        }

        acceptenceTests = new int[userStory.getAcceptenceTests().size()];
        Iterator<AcceptenceTestEntity> atIt = userStory.getAcceptenceTests().iterator();
        for (int i = 0; i < acceptenceTests.length; i++) {
            acceptenceTests[i] = atIt.next().getId();
        }

        tasks = new int[userStory.getTasks().size()];
        Iterator<TaskEntity> tIt /* hihihi */ = userStory.getTasks().iterator();
        for (int i = 0; i < tasks.length; i++) {
            tasks[i] = tIt.next().getId();
        }

        priority = userStory.getPriority().getId();
        project = userStory.getProject().getId();
        try {
            sprint = userStory.getSprint().getId();
        } catch (NullPointerException e) {
            sprint = 0;
        }

    }
}
