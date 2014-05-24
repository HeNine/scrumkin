package com.scrumkin.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.scrumkin.api.GroupManager;
import com.scrumkin.api.exceptions.PermissionInvalidException;
import com.scrumkin.jpa.GroupEntity;
import com.scrumkin.jpa.PermissionEntity;
import com.scrumkin.jpa.ProjectEntity;
import com.scrumkin.jpa.UserEntity;

/**
 * Session Bean implementation class GroupManagerEJB
 */
@Stateless(mappedName = "groupManager")
public class GroupManagerEJB implements GroupManager {

    @PersistenceContext(unitName = "scrumkin_PU")
    private EntityManager em;

    public List<PermissionEntity> validPermissions;

    public GroupManagerEJB() {

    }

    @PostConstruct
    public void initPermissions() {
        validPermissions = getValidPermissions();
    }

    @Override
    public List<PermissionEntity> getValidPermissions() {
        TypedQuery<PermissionEntity> query = em.createNamedQuery(
                "PermissionEntity.findAll", PermissionEntity.class);
        List<PermissionEntity> permissions = query.getResultList();

        return permissions;
    }

    @Override
    public void addGroup(String name, Collection<PermissionEntity> permissions)
            throws PermissionInvalidException {
        List<PermissionEntity> invalidPermissions = new ArrayList<PermissionEntity>(
                permissions.size());
        Collections.copy(invalidPermissions,
                (List<PermissionEntity>) permissions);

        invalidPermissions.removeAll(validPermissions);
        if (invalidPermissions.size() != 0) {
            throw new PermissionInvalidException("Permissions "
                    + invalidPermissions.toString() + " are not valid.");
        }

        GroupEntity group = new GroupEntity();
        group.setName(name);
        group.setPermissions(permissions);

        em.persist(group);

//		for (PermissionEntity p : permissions) {
//			em.persist(p);
//		}
    }

    @Override
    public int addGroup(String name, ProjectEntity project, Collection<PermissionEntity> permissions) throws
            PermissionInvalidException {
        List<PermissionEntity> invalidPermissions = new ArrayList<PermissionEntity>(
                permissions);
//        Collections.copy(invalidPermissions,
//                (List<PermissionEntity>) permissions);

        invalidPermissions.removeAll(validPermissions);
        if (invalidPermissions.size() != 0) {
            throw new PermissionInvalidException("Permissions "
                    + invalidPermissions.toString() + " are not valid.");
        }

        GroupEntity group = new GroupEntity();
        group.setName(name);
        group.setProject(project);
        group.setPermissions(permissions);

        project.getGroups().add(group);

        em.persist(group);
        em.flush();

        return group.getId();
    }

    @Override
    public void addUserToGroup(UserEntity user, GroupEntity group) {
        if (user.getGroups().contains(group)) {
            return;
        }

        user.getGroups().add(group);

        em.persist(user);
    }

    @Override
    public void deleteUserFromGroup(UserEntity user, GroupEntity group) {
        if (!user.getGroups().contains(group)) {
            return;
        }

        user.getGroups().remove(group);

        em.persist(user);
    }

    @Override
    public GroupEntity getGroup(int id) {
        GroupEntity group = em.find(GroupEntity.class, id);

        return group;
    }

}
