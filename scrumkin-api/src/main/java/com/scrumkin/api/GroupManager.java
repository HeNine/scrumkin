package com.scrumkin.api;

import java.util.Collection;
import java.util.List;

import javax.ejb.Local;

import com.scrumkin.api.exceptions.PermissionInvalidException;
import com.scrumkin.jpa.GroupEntity;
import com.scrumkin.jpa.PermissionEntity;
import com.scrumkin.jpa.ProjectEntity;
import com.scrumkin.jpa.UserEntity;

/**
 * Used to manage groups.
 */
@Local
public interface GroupManager {
	
    /**
     * Add new user to project {@code project}.
     *
     * @param name        Group name
     * @param permissions Permissions for the group
     * @throws PermissionInvalidException if some permissions don't exist
     */
    public void addGroup(String name, Collection<PermissionEntity> permissions) throws PermissionInvalidException;

    /**
     * Create new group for project {@code project}
     *
     * @param name        Group name
     * @param project     Project group belongs to
     * @param permissions Permissions for the group
     * @throws PermissionInvalidException
     */
    public int addGroup(String name, ProjectEntity project, Collection<PermissionEntity> permissions) throws
            PermissionInvalidException;

    /**
     * Adds user to group
     *
     * @param user  User to add to group
     * @param group Group to add the user to
     */
    public void addUserToGroup(UserEntity user, GroupEntity group);

    /**
     * Delete user from group
     *
     * @param user  User to add to group
     * @param group Group from which to delete the user
     */
    public void deleteUserFromGroup(UserEntity user, GroupEntity group);

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
