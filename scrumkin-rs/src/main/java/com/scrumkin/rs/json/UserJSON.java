package com.scrumkin.rs.json;

import com.scrumkin.jpa.UserEntity;

/**
 * Created by Matija on 28.3.2014.
 */
public class UserJSON {

    public int id;
    public String username;
    public String password;
    public String name;
    public String email;

    public void init(UserEntity userEntity) {
        this.username = userEntity.getUsername();
        this.name = userEntity.getName();
    }
}
