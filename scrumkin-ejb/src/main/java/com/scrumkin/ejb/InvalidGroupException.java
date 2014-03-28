package com.scrumkin.ejb;

import javax.ejb.ApplicationException;

@ApplicationException(rollback=true)
public class InvalidGroupException extends Exception {

	private static final long serialVersionUID = -7341418054328480825L;
	
}
