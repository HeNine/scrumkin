package com.scrumkin.jpa;

import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by Matija on 25.3.2014.
 */
@Entity
@Table(name = "tasks", schema = "public", catalog = "scrumkin")
public class TaskEntity {
    private int id;
    private String description;
    private BigDecimal estimatedTime;
    private BigDecimal workDone;
    private Boolean accepted;
    private UserStoryEntity userStory;
    private UserEntity assignee;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Basic
    @Column(name = "estimated_time")
    public BigDecimal getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(BigDecimal estimatedTime) {
        this.estimatedTime = estimatedTime;
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
    @Column(name = "accepted")
    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskEntity that = (TaskEntity) o;

        if (id != that.id) return false;
        if (accepted != null ? !accepted.equals(that.accepted) : that.accepted != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (estimatedTime != null ? !estimatedTime.equals(that.estimatedTime) : that.estimatedTime != null)
            return false;
        if (workDone != null ? !workDone.equals(that.workDone) : that.workDone != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (estimatedTime != null ? estimatedTime.hashCode() : 0);
        result = 31 * result + (workDone != null ? workDone.hashCode() : 0);
        result = 31 * result + (accepted != null ? accepted.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "user_story_id", referencedColumnName = "id", nullable = false)
    public UserStoryEntity getUserStory() {
        return userStory;
    }

    public void setUserStory(UserStoryEntity userStory) {
        this.userStory = userStory;
    }

    @ManyToOne
    @JoinColumn(name = "assignee_id", referencedColumnName = "id")
    public UserEntity getAssignee() {
        return assignee;
    }

    public void setAssignee(UserEntity assignee) {
        this.assignee = assignee;
    }
}
