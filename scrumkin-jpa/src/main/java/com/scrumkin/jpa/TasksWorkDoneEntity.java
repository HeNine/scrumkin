package com.scrumkin.jpa;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by Matija on 3.5.2014.
 */
@Entity
@Table(name = "tasks_work_done", schema = "public", catalog = "scrumkin")
@NamedQueries({
        @NamedQuery(name = "TasksWorkDoneEntity.getLogEntry", query = "SELECT wd FROM TasksWorkDoneEntity wd " +
                "WHERE wd.task.id = :task_id AND wd.date = :date"),
        @NamedQuery(name = "TasksWorkDoneEntity.deleteLogEntry", query = "DELETE FROM TasksWorkDoneEntity wd " +
                "WHERE wd.task.id = :task_id AND wd.date = :date")
})
public class TasksWorkDoneEntity {
    private int id;
    private UserEntity user;
    private TaskEntity task;
    private BigDecimal workDone;
    private BigDecimal workRemaining;
    private Date date;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tasks_work_done_id_seq")
    @SequenceGenerator(name = "tasks_work_done_id_seq",
            sequenceName = "tasks_work_done_id_seq",
            allocationSize = 1)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "work_done")
    public BigDecimal getWorkDone() {
        return workDone;
    }

    public void setWorkDone(BigDecimal workDone) {
        this.workDone = workDone;
    }

    @Basic
    @Column(name = "work_remaining")
    public BigDecimal getWorkRemaining() {
        return workRemaining;
    }

    public void setWorkRemaining(BigDecimal workRemaining) {
        this.workRemaining = workRemaining;
    }

    @Basic
    @Column(name = "date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TasksWorkDoneEntity that = (TasksWorkDoneEntity) o;

        if (id != that.id) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (workDone != null ? !workDone.equals(that.workDone) : that.workDone != null) return false;
        if (workRemaining != null ? !workRemaining.equals(that.workRemaining) : that.workRemaining != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (workDone != null ? workDone.hashCode() : 0);
        result = 31 * result + (workRemaining != null ? workRemaining.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "task_id")
    public TaskEntity getTask() {
        return task;
    }

    public void setTask(TaskEntity task) {
        this.task = task;
    }
}
