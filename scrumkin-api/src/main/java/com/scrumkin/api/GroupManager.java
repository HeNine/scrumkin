package com.scrumkin.api;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import com.scrumkin.api.exceptions.PermissionInvalidException;
import com.scrumkin.jpa.GroupEntity;
import com.scrumkin.jpa.PermissionEntity;

/**
 * Used to manage groups.
 */
@Local
public interface GroupManager {
	
    /**
     * Add new user to project {@code project}.
     *
     * @param permissions Permissions for the group
     * @throws PermissionInvalidException if some permissions don't exist
     */
	public void addGroup(Collection<PermissionEntity> permissions) throws PermissionInvalidException;
	
    /**
     * Gets group entity by id.
     *
     * @param id Group id
     */
    public GroupEntity getGroup(int id);
    
    /**
     * Gets valid permissions.
     */
    public List<PermissionEntity> getValidPermissions();
}
