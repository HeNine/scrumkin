package com.scrumkin.jpa;

import java.io.Serializable;

import javax.persistence.*;

/**
 * Created by Matija on 25.3.2014.
 */
@Entity
@Table(name = "acceptence_tests", schema = "public", catalog = "scrumkin")
public class AcceptenceTestEntity implements Serializable {
	private static final long serialVersionUID = 1L;
    private int id;
    private String test;
    private Boolean accepted;
    private UserStoryEntity userStory;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "test")
    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
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

        AcceptenceTestEntity that = (AcceptenceTestEntity) o;

        if (id != that.id) return false;
        if (accepted != null ? !accepted.equals(that.accepted) : that.accepted != null) return false;
        if (test != null ? !test.equals(that.test) : that.test != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (test != null ? test.hashCode() : 0);
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
}
