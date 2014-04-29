package com.scrumkin.jpa;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by Matija on 29.4.2014.
 */
@Entity
@Table(name = "story_comment", schema = "public", catalog = "scrumkin")
public class StoryCommentEntity {
    private int id;
    private String comment;
    private Timestamp date;
    private UserStoryEntity story;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "comment")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Basic
    @Column(name = "date")
    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StoryCommentEntity that = (StoryCommentEntity) o;

        if (id != that.id) return false;
        if (comment != null ? !comment.equals(that.comment) : that.comment != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @ManyToOne(cascade = {CascadeType.PERSIST})
    @JoinColumn(name = "story_id", referencedColumnName = "id", nullable = false)
    public UserStoryEntity getStory() {
        return story;
    }

    public void setStory(UserStoryEntity story) {
        this.story = story;
    }
}
