package com.scrumkin.ejb;

import java.util.List;

import javax.ejb.Remote;

import com.scrumkin.jpa.UserEntity;

@Remote
public interface AddUserBeanRemote {
	public void persistUser() throws InvalidGroupException;
}
