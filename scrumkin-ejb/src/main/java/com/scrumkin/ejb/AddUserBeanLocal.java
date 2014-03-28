package com.scrumkin.ejb;

import java.util.List;

import javax.ejb.Local;

import com.scrumkin.jpa.UserEntity;

@Local
public interface AddUserBeanLocal {
	public void persistUser() throws InvalidGroupException;
}
