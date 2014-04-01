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

    public void init(UserEntity ue) {
        this.id = ue.getId();
        this.username = ue.getUsername();
        this.password = ue.getPassword();
        this.name = ue.getName();
        this.email = ue.getEmail();
    }
    
}
