package com.scrumkin.rs.json;

import com.scrumkin.jpa.GroupEntity;
import com.scrumkin.jpa.PermissionEntity;

import java.util.Iterator;

public class GroupJSON {
    public int id;
    public int projectID;
    public String name;
    public String[] permissions;

    public void init(GroupEntity group) {
        id = group.getId();
        name = group.getName();
        projectID = group.getProject().getId();

        permissions = new String[group.getPermissions().size()];
        Iterator<PermissionEntity> gIt = group.getPermissions().iterator();
        for (int i = 0; i < permissions.length; i++) {
            permissions[i] = gIt.next().getName();
        }
    }
}
