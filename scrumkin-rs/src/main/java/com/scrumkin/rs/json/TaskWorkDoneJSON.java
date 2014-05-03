package com.scrumkin.rs.json;

import com.scrumkin.jpa.TaskEntity;
import com.scrumkin.jpa.TasksWorkDoneEntity;
import com.scrumkin.jpa.UserEntity;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by Matija on 3.5.2014.
 */
public class TaskWorkDoneJSON {

    public int id;
    public int user;
    public int task;
    public double workDone;
    public double workRemaining;
    public Date date;

    public void init(TasksWorkDoneEntity tasksWorkDoneEntity) {
        this.id = tasksWorkDoneEntity.getId();
        this.user = tasksWorkDoneEntity.getUser().getId();
        this.task = tasksWorkDoneEntity.getTask().getId();
        this.workDone = tasksWorkDoneEntity.getWorkDone().doubleValue();
        this.workRemaining = tasksWorkDoneEntity.getWorkRemaining().doubleValue();
        this.date = tasksWorkDoneEntity.getDate();
    }

}
