package com.scrumkin.ejb;

import com.scrumkin.api.GroupManager;
import com.scrumkin.api.ProjectManager;
import com.scrumkin.api.UserManager;
import com.scrumkin.api.exceptions.*;
import com.scrumkin.jpa.*;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Project manager EJB.
 */
@Stateless(mappedName = "projectManager")
public class ProjectManagerEJB implements ProjectManager {

    @PersistenceContext(unitName = "scrumkin_PU")
    private EntityManager em;

    @Inject
    private GroupManager gm;
    @Inject
    private UserManager um;

    @Override
    public void addProject(@NotNull String name, UserEntity productOwner, UserEntity scrumMaster,
                           @NotNull Collection<UserEntity> developers) throws ProjectNameNotUniqueException {

        if (!projectNameIsUnique(name)) {
            throw new ProjectNameNotUniqueException("Project name [" + name + "] is not unique.");
        }

        ProjectEntity project = new ProjectEntity();
        project.setName(name);

        project.setGroups(new HashSet<GroupEntity>(3, 1));

        // create user groups and add users to groups
        Collection<PermissionEntity> permissions;
        // Product owner

        try {
            permissions = new HashSet<>(2, 1);
            permissions.add(em.find(PermissionEntity.class, 3));
            permissions.add(em.find(PermissionEntity.class, 15));

            int groupId = gm.addGroup(name + " Product Owner", project, permissions);

            gm.addUserToGroup(productOwner, gm.getGroup(groupId));
        } catch (PermissionInvalidException e) {
        }

        // Scrum master

        try {
            permissions = new HashSet<>(4, 1);
            permissions.add(em.find(PermissionEntity.class, 6));
            permissions.add(em.find(PermissionEntity.class, 5));
            permissions.add(em.find(PermissionEntity.class, 16));
            permissions.add(em.find(PermissionEntity.class, 8));

            int groupId = gm.addGroup(name + " Scrum Master", project, permissions);

            gm.addUserToGroup(scrumMaster, gm.getGroup(groupId));
        } catch (PermissionInvalidException e) {
        }

        // Developers

        try {
            permissions = new HashSet<>(4, 1);
            permissions.add(em.find(PermissionEntity.class, 13));
            permissions.add(em.find(PermissionEntity.class, 9));
            permissions.add(em.find(PermissionEntity.class, 11));
            permissions.add(em.find(PermissionEntity.class, 12));

            int groupId = gm.addGroup(name + " Team Member", project, permissions);

            for (UserEntity user : developers) {
                gm.addUserToGroup(user, gm.getGroup(groupId));
            }
        } catch (PermissionInvalidException e) {
        }

        em.persist(project);
    }

    @Override
    public void updateProject(int projectID, String name, int[] userIDs, int[][] userProjectGroupIDs) throws
            ProjectNameNotUniqueException, UserNotInProject {

        if (!name.equals("-")) {
            if (!projectNameIsUnique(name)) {
                throw new ProjectNameNotUniqueException("Project name [" + name + "] is not unique.");
            }
            ProjectEntity project = em.find(ProjectEntity.class, projectID);
            project.setName(name);

            em.persist(project);
        }

        int j = 0;
        for(int userID : userIDs) {
            UserEntity user = um.getUser(userID);
            Collection<GroupEntity> userGroups = user.getGroups();

            for (int userGroupID : userProjectGroupIDs[j]) {
                GroupEntity userGroup = gm.getGroup(userGroupID);
                if(!userGroups.contains(userGroup)) {
                    userGroups.add(userGroup);
                }
            }

            em.persist(user);
            j++;
        }
    }


    @Override
    public void deleteUserFromProject(int userId, int projectId) throws UserNotInProject {
        UserEntity user = um.getUser(userId);
        ProjectEntity userProject = getProject(projectId);

        boolean userInProject = false;
        Collection<GroupEntity> userGroups = user.getGroups();
        Iterator<GroupEntity> userGroupsIter = userGroups.iterator();

        while (userGroupsIter.hasNext()) {
            GroupEntity userGroup = userGroupsIter.next();
            ProjectEntity groupProject = userGroup.getProject();

            if (groupProject != null && groupProject.equals(userProject)) {
                userInProject = true;
                userGroupsIter.remove();
            }
        }

        if (!userInProject) {
            throw new UserNotInProject("User " + user.getName() + " is not assigned to project " +
                    userProject.getName());
        }

        em.persist(user);
    }

    @Override
    public Collection<ProjectEntity> getAllProjects() {

        return em.createNamedQuery("ProjectEntity.getAllProjects", ProjectEntity.class).getResultList();
    }

    @Override
    public ProjectEntity getProject(int id) {
        ProjectEntity project = em.find(ProjectEntity.class, id);

        return project;
    }

    public GroupEntity getProjectGroup(String name, ProjectEntity project) {
        GroupEntity group = null;
        for (GroupEntity g : project.getGroups()) {
            if (g.getName().endsWith(name)) {
                group = g;
                break;
            }
        }

        return group;
    }

    public GroupEntity getProductOwnerGroup(ProjectEntity project) {
        return getProjectGroup("Product Owner", project);
    }

    public GroupEntity getScrumMasterGroup(ProjectEntity project) {
        return getProjectGroup("Scrum Master", project);
    }

    public GroupEntity getDeveloperGroup(ProjectEntity project) {
        return getProjectGroup("Team Member", project);
    }

    @Override
    public UserEntity getProductOwner(ProjectEntity project) throws ProjectHasNoProductOwnerException {
        GroupEntity group = getProductOwnerGroup(project);

        try {
            return group.getUsers().iterator().next();
        } catch (NullPointerException | NoSuchElementException e) {
            throw new ProjectHasNoProductOwnerException();
        }

    }

    @Override
    public UserEntity getScrumMaster(ProjectEntity project) throws ProjectHasNoScrumMasterException {
        GroupEntity group = getScrumMasterGroup(project);

        try {
            return group.getUsers().iterator().next();
        } catch (NullPointerException | NoSuchElementException e) {
            throw new ProjectHasNoScrumMasterException();
        }
    }

    @Override
    public Collection<UserEntity> getDevelopers(ProjectEntity project) {
        GroupEntity group = getDeveloperGroup(project);

        return group.getUsers();
    }

    @Override
    public void setProductOwner(int userID, int projectID) throws ProductOwnerOrScrumMasterOnly {
        ProjectEntity project = getProject(projectID);
        GroupEntity productOwnerGroup = getProductOwnerGroup(project);
        UserEntity user = um.getUser(userID);

        try {
            UserEntity scrumMaster = getScrumMaster(project);
            if(user.equals(scrumMaster)) {
                throw new ProductOwnerOrScrumMasterOnly();
            }
        } catch (ProjectHasNoScrumMasterException e) {
        }

        try {
            UserEntity productOwner = getProductOwner(project);
            gm.deleteUserFromGroup(productOwner, productOwnerGroup);
        } catch (ProjectHasNoProductOwnerException e) {
        }

        gm.addUserToGroup(user, productOwnerGroup);
    }

    @Override
    public void setScrumMaster(int userID, int projectID) throws ProductOwnerOrScrumMasterOnly {
        ProjectEntity project = getProject(projectID);
        GroupEntity scrumMasterGroup = getScrumMasterGroup(project);
        UserEntity user = um.getUser(userID);

        try {
            UserEntity productOwner = getProductOwner(project);
            if(user.equals(productOwner)) {
                throw new ProductOwnerOrScrumMasterOnly();
            }
        } catch (ProjectHasNoProductOwnerException e) {
        }

        try {
            UserEntity scrumMaster = getScrumMaster(project);
            gm.deleteUserFromGroup(scrumMaster, scrumMasterGroup);
        } catch (ProjectHasNoScrumMasterException e) {
        }

        gm.addUserToGroup(user, scrumMasterGroup);
    }

    @Override
    public void setDeveloper(int userID, int projectID, boolean assign) {
        ProjectEntity project = getProject(projectID);
        GroupEntity developerGroup = getDeveloperGroup(project);

        UserEntity user = um.getUser(userID);
        if(assign) {
            gm.addUserToGroup(user, developerGroup);
        }
        else {
            gm.deleteUserFromGroup(user, developerGroup);
        }
    }

    @Override
    public Collection<UserStoryEntity> getProjectStories(int id) {
        return getProject(id).getUserStories();
    }

    @Override
    public Collection<SprintEntity> getProjectSprints(int id) {
        return getProject(id).getSprints();
    }

    private boolean projectNameIsUnique(String name) {
        TypedQuery<ProjectEntity> projectsQuery = em.createNamedQuery("ProjectEntity.getProjectByName",
                ProjectEntity.class);
        projectsQuery.setParameter("name", name);


        return projectsQuery.getResultList().size() == 0;
    }

}
