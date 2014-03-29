package com.scrumkin.api;

import com.scrumkin.api.exceptions.ProjectNameNotUniqueException;
import com.scrumkin.jpa.ProjectEntity;
import com.scrumkin.jpa.UserEntity;

import javax.ejb.Local;
import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * Used to manage projects.
 */
@Local
public interface ProjectManager {

    /**
     * Create new project with name {@code name}. Product owner and Scrum master can be null. Developers can be empty
     * but cannot be null.
     *
     * @param name         Project name
     * @param productOwner Project owner (can be null)
     * @param scrumMaster  Scrum mater (can be null)
     * @param developers   Collection of developers assigned to project
     */
    public void addProject(@NotNull String name, UserEntity productOwner, UserEntity scrumMaster,
                           @NotNull Collection<UserEntity> developers) throws ProjectNameNotUniqueException;

    /**
     * Gets project entity by id.
     *
     * @param id Project id
     */
    public ProjectEntity getProject(int id);

}
