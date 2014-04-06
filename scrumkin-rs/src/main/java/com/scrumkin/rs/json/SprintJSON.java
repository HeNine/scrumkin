package com.scrumkin.rs.json;

import com.scrumkin.jpa.SprintEntity;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Created by Matija on 29.3.2014.
 */
public class SprintJSON {

    public int id;
    public Date startDate;
    public Date endDate;
    public BigDecimal velocity;
    public int projectId;

    public void init(SprintEntity sprint) {
        this.id = sprint.getId();
        this.startDate = sprint.getStartDate();
        this.endDate = sprint.getEndDate();
        this.velocity = sprint.getVelocity();
        this.projectId = sprint.getProject().getId();
    }
}
