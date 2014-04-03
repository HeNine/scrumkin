package com.scrumkin.api;

import com.scrumkin.api.exceptions.ProjectHasNoProductOwnerException;
import com.scrumkin.api.exceptions.ProjectHasNoScrumMasterException;
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
     * but cannot be null. Scrum Master is also added as a developer.
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

    /**
     * Returns product owner for project.
     *
     * @param project Project
     * @return Product owner
     */
    public UserEntity getProductOwner(ProjectEntity project) throws ProjectHasNoProductOwnerException;

    /**
     * Returns scrum master for project.
     *
     * @param project Project
     * @return Scrum master
     */
    public UserEntity getScrumMaster(ProjectEntity project) throws ProjectHasNoScrumMasterException;

    /**
     * Returns developers on project
     *
     * @param project Project
     * @return List of developers
     */
    public Collection<UserEntity> getDevelopers(ProjectEntity project);
}
