package com.scrumkin.ejb;

import com.scrumkin.api.ProjectManager;
import com.scrumkin.api.exceptions.ProjectNameNotUniqueException;
import com.scrumkin.jpa.ProjectEntity;
import com.scrumkin.jpa.UserEntity;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * Project manager EJB.
 */
@Stateless(mappedName = "projectManager")
public class ProjectManagerEJB implements ProjectManager {

    @PersistenceContext(unitName = "scrumkin_PU")
    private EntityManager em;

    @Override
    public void addProject(@NotNull String name, UserEntity productOwner, UserEntity scrumMaster,
                           @NotNull Collection<UserEntity> developers) throws ProjectNameNotUniqueException {

        TypedQuery<Boolean> isUniqueQuery = em.createNamedQuery("isUnique", Boolean.class);
        isUniqueQuery.setParameter("name", name);

        boolean isUnique = isUniqueQuery.getSingleResult();
        if (!isUnique) {
            throw new ProjectNameNotUniqueException("Project name [" + name + "] is not unique.");
        }

        ProjectEntity project = new ProjectEntity();
        project.setName(name);

        // create user groups and add users to groups

        em.persist(project);
    }

    @Override
    public ProjectEntity getProject(int id) {
        ProjectEntity project = em.find(ProjectEntity.class, id);

        return project;
    }

}
