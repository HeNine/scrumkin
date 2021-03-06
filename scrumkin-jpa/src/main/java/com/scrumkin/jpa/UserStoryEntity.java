package com.scrumkin.jpa;

import java.math.BigDecimal;
import java.util.Collection;

import javax.persistence.*;

/**
 * Created by Matija on 25.3.2014.
 */
@Entity
@Table(name = "user_stories", schema = "public", catalog = "scrumkin")
@NamedQueries({
        @NamedQuery(name = "UserStoryEntity.isUniqueTitle", query = "SELECT CASE WHEN (count(us) = 0) THEN true ELSE " +
                "false END FROM "
                + "UserStoryEntity us WHERE us.title = :title"),
        @NamedQuery(name = "UserStoryEntity.findAll", query = "SELECT us FROM "
                + "UserStoryEntity us")
})
public class UserStoryEntity {
    private int id;
    private String title;
    private String story;
    private int bussinessValue;
    private BigDecimal estimatedTime;
    private Collection<AcceptenceTestEntity> acceptenceTests;
    private Collection<TaskEntity> tasks;
    private Collection<StoryCommentEntity> comments;
    private PriorityEntity priority;
    private ProjectEntity project;
    private SprintEntity sprint;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_stories_id_seq")
    @SequenceGenerator(name = "user_stories_id_seq",
            sequenceName = "user_stories_id_seq",
            allocationSize = 1)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "story")
    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    @Basic
    @Column(name = "bussiness_value")
    public int getBussinessValue() {
        return bussinessValue;
    }

    public void setBussinessValue(int bussinessValue) {
        this.bussinessValue = bussinessValue;
    }

    @Basic
    @Column(name = "estimated_time")
    public BigDecimal getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(BigDecimal estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserStoryEntity that = (UserStoryEntity) o;

        if (bussinessValue != that.bussinessValue) return false;
        if (id != that.id) return false;
        if (estimatedTime != null ? !estimatedTime.equals(that.estimatedTime) : that.estimatedTime != null)
            return false;
        if (story != null ? !story.equals(that.story) : that.story != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (story != null ? story.hashCode() : 0);
        result = 31 * result + bussinessValue;
        result = 31 * result + (estimatedTime != null ? estimatedTime.hashCode() : 0);
        return result;
    }

    @OneToMany(mappedBy = "userStory", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    public Collection<AcceptenceTestEntity> getAcceptenceTests() {
        return acceptenceTests;
    }

    public void setAcceptenceTests(Collection<AcceptenceTestEntity> acceptenceTests) {
        this.acceptenceTests = acceptenceTests;
    }

    @OneToMany(mappedBy = "userStory", cascade = {CascadeType.REMOVE, CascadeType.PERSIST, CascadeType.MERGE})
    public Collection<TaskEntity> getTasks() {
        return tasks;
    }

    public void setTasks(Collection<TaskEntity> tasks) {
        this.tasks = tasks;
    }

    @ManyToOne
    @JoinColumn(name = "priority_id", referencedColumnName = "id", nullable = false)
    public PriorityEntity getPriority() {
        return priority;
    }

    public void setPriority(PriorityEntity priority) {
        this.priority = priority;
    }

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    public ProjectEntity getProject() {
        return project;
    }

    public void setProject(ProjectEntity project) {
        this.project = project;
    }

    @ManyToOne
    @JoinColumn(name = "sprint_id", referencedColumnName = "id")
    public SprintEntity getSprint() {
        return sprint;
    }

    public void setSprint(SprintEntity sprint) {
        this.sprint = sprint;
    }

    @OneToMany(mappedBy = "story", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    public Collection<StoryCommentEntity> getComments() {
        return comments;
    }

    public void setComments(Collection<StoryCommentEntity> comments) {
        this.comments = comments;
    }
}
