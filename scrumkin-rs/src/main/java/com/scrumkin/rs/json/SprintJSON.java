package com.scrumkin.rs.json;

import com.scrumkin.jpa.SprintEntity;
import com.scrumkin.jpa.UserStoryEntity;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Iterator;

/**
 * Created by Matija on 29.3.2014.
 */
public class SprintJSON {

    public int id;
    public Date startDate;
    public Date endDate;
    public BigDecimal velocity;
    public int projectId;
    public int[] stories;

    public void init(SprintEntity sprint) {
        this.id = sprint.getId();
        this.startDate = sprint.getStartDate();
        this.endDate = sprint.getEndDate();
        this.velocity = sprint.getVelocity();
        this.projectId = sprint.getProject().getId();

        stories = new int[sprint.getUserStories().size()];
        Iterator<UserStoryEntity> sIt = sprint.getUserStories().iterator();
        for (int i = 0; i < stories.length; i++) {
            stories[i] = sIt.next().getId();
        }
    }
}
