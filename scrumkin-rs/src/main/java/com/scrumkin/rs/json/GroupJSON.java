package com.scrumkin.rs.json;

import com.scrumkin.jpa.GroupEntity;

public class GroupJSON {
    public int id;
    public int projectID;
    public String name;

    public void init(GroupEntity group) {
        id = group.getId();
        name = group.getName();
        projectID = group.getProject().getId();
    }
}
