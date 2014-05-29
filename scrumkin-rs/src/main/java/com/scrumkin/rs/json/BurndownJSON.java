package com.scrumkin.rs.json;

import com.scrumkin.jpa.BurndownEntity;

import java.sql.Date;

/**
 * Created by Matija on 29.5.2014.
 */
public class BurndownJSON {
    public int projectId;
    public Date date;
    public double workRemaining;

    public void init(BurndownEntity burndownEntity) {
        this.projectId = burndownEntity.getProject().getId();
        this.date = burndownEntity.getDate();
        this.workRemaining = burndownEntity.getWorkRemaining().doubleValue();
    }
}
