package com.scrumkin.rs.json;

import com.scrumkin.jpa.StoryCommentEntity;
import com.scrumkin.jpa.UserStoryEntity;

import java.sql.Timestamp;

/**
 * Created by Matija on 29.4.2014.
 */
public class StoryCommentJSON {

    public int id;
    public String comment;
    public Timestamp date;

    public void init(StoryCommentEntity storyCommentEntity) {
        this.id = storyCommentEntity.getId();
        this.comment = storyCommentEntity.getComment();
        this.date = storyCommentEntity.getDate();
    }
}
