package com.scrumkin.rs;

import com.scrumkin.jpa.UserEntity;

import javax.ejb.Local;


/**
 * Created by Matija on 27.4.2014.
 */
public interface AuthenticatedRESTService {
    public UserEntity getAuthenticatedUser();

    public void setAuthenticatedUser(UserEntity user);
}
