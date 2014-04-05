package com.scrumkin.rs.json;

import com.scrumkin.jpa.GroupEntity;
import com.scrumkin.jpa.UserEntity;

import java.util.Iterator;

/**
 * Created by Matija on 28.3.2014.
 */
public class UserJSON {

    public int id;
    public String username;
    public String password;
    public String name;
    public String email;
    public int[] groups;

    public void init(UserEntity ue) {
        this.id = ue.getId();
        this.username = ue.getUsername();
//        this.password = ue.getPassword(); Rajš ne, da ne gre password po nesreči ven po netu
        this.name = ue.getName();
        this.email = ue.getEmail();

        Iterator<GroupEntity> gIt /* hehe */ = ue.getGroups().iterator();
        this.groups = new int[ue.getGroups().size()];
        for (int i = 0; i < groups.length; i++) {
            groups[i] = gIt.next().getId();
        }
    }

}
