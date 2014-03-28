package com.scrumkin.api;

import java.util.Collection;

import javax.ejb.Local;

import com.scrumkin.api.exceptions.PermissionInvalidException;
import com.scrumkin.jpa.PermissionEntity;

@Local
public interface GroupManager {
	public void addGroup(Collection<PermissionEntity> permissions) throws PermissionInvalidException;
}
