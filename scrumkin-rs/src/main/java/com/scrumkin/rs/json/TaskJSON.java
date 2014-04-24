package com.scrumkin.rs.json;

import com.scrumkin.jpa.TaskEntity;

public class TaskJSON {
    public int id;
    public String description;
    public double estimatedTime;
    public double workDone;
    public boolean accepted;
    public int userStoryID;
    public int assigneeID;

    public void init(TaskEntity task) {
        id = task.getId();
        description = task.getDescription();
        try {
            estimatedTime = task.getEstimatedTime().doubleValue();
        } catch (NullPointerException e) {
            estimatedTime = 0;
        }

        workDone = task.getWorkDone().doubleValue();
        try {
            accepted = task.getAccepted();
        } catch (NullPointerException e) {
            accepted = false;
        }

        userStoryID = task.getUserStory().getId();
        try {
            assigneeID = task.getAssignee().getId();
        } catch (NullPointerException e) {
            assigneeID = 0;
        }
    }
}
