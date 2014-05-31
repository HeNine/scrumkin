package com.scrumkin.rs.json;

import com.scrumkin.jpa.BurndownEntity;
import com.scrumkin.jpa.ProjectEntity;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Created by Matija on 31.5.2014.
 */
public class BurndownJSON {
    public int id;
    public double workRemaining;
    public Date date;
    public int projectId;

    public void init(BurndownEntity burndownEntity) {
        this.id = burndownEntity.getId();
        this.workRemaining = burndownEntity.getWorkRemaining().doubleValue();
        this.date = burndownEntity.getDate();
        this.projectId = burndownEntity.getProject().getId();
    }
}
