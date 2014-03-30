package com.scrumkin.jpa;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Created by Matija on 25.3.2014.
 */
@Entity
@Table(name = "sprints", schema = "public", catalog = "scrumkin")
@NamedQueries(
        @NamedQuery(name = "SprintEntity.isTimeSlotAvailable", query = "SELECT s " +
                "FROM SprintEntity s " +
                "WHERE s.project = :project " +
                "AND (:startDate >= s.startDate AND :startDate <= s.endDate " +
                "OR :endDate >= s.startDate AND :endDate <= s.endDate " +
                "OR :endDate >= s.endDate AND :startDate <= s.startDate)")
)
public class SprintEntity {
    private int id;
    private Date startDate;
    private Date endDate;
    private BigDecimal velocity;
    private ProjectEntity project;
    private Collection<UserStoryEntity> userStories;

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
    @Column(name = "start_date")
    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    @Basic
    @Column(name = "end_date")
    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Basic
    @Column(name = "velocity")
    public BigDecimal getVelocity() {
        return velocity;
    }

    public void setVelocity(BigDecimal velocity) {
        this.velocity = velocity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SprintEntity that = (SprintEntity) o;

        if (id != that.id) return false;
        if (endDate != null ? !endDate.equals(that.endDate) : that.endDate != null) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        if (velocity != null ? !velocity.equals(that.velocity) : that.velocity != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (velocity != null ? velocity.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    @OneToMany(mappedBy = "sprint")
    public Collection<UserStoryEntity> getUserStories() {
        return userStories;
    }

    public void setUserStories(Collection<UserStoryEntity> userStories) {
        this.userStories = userStories;
    }
}
