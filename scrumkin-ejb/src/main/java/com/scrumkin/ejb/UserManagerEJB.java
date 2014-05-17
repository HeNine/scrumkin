package com.scrumkin.ejb;

import java.util.*;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.scrumkin.api.GroupManager;
import com.scrumkin.api.ProjectManager;
import com.scrumkin.api.exceptions.*;
import com.scrumkin.jpa.ProjectEntity;
import org.mindrot.jbcrypt.BCrypt;

import com.scrumkin.api.UserManager;
import com.scrumkin.jpa.GroupEntity;
import com.scrumkin.jpa.UserEntity;

/**
 * Session Bean implementation class AddUsersBean.
 */
@Stateless(mappedName = "userManager")
public class UserManagerEJB implements UserManager {

    @PersistenceContext(unitName = "scrumkin_PU")
    private EntityManager em;

    @Inject
    private GroupManager gm;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void addUser(String username, String password,
                        String confirmPassword, String name, String email,
                        String confirmEmail, Collection<GroupEntity> systemGroups)
            throws UserInvalidGroupsException, UserUsernameNotUniqueException,
            UserNotUniqueException, UserPasswordMismatchException, UserEmailMismatchException {

        if (!password.equals(confirmPassword)) {
            throw new UserPasswordMismatchException("Passwords don't match.");
        }

        if (!email.equals(confirmEmail)) {
            throw new UserEmailMismatchException("Emails don't match.");
        }
//
//        TypedQuery<Boolean> isUniqueUsernameQuery = em.createNamedQuery(
//                "UserEntity.isUniqueUsername", Boolean.class);
//        isUniqueUsernameQuery.setParameter("username", username);

//        boolean isUniqueUsername = isUniqueUsernameQuery.getSingleResult();
        if (!isUniqueUsername(username)) {
            throw new UserUsernameNotUniqueException("Username [" + username
                    + "] is not unique.");
        }

//        TypedQuery<Boolean> isUniqueUserQuery = em.createNamedQuery(
//                "UserEntity.isUniqueUser", Boolean.class);
//        isUniqueUserQuery.setParameter("name", name);
//        isUniqueUserQuery.setParameter("email", email);

//        boolean isUniqueUser = isUniqueUserQuery.getSingleResult();
        if (!isUniqueEmail(email)) {
            throw new UserNotUniqueException("User with name [" + name
                    + "] and email [" + email + "] is not unique.");
        }

//        List<Integer> userGroupIDs = new ArrayList<Integer>(systemGroups.size());
//        for (GroupEntity userGroup : systemGroups) {
//            userGroupIDs.add(userGroup.getId());
//        }

//        TypedQuery<String> invalidGroupsQuery = em.createNamedQuery(
//                "GroupEntity.invalidGroups", String.class);
//        invalidGroupsQuery.setParameter("groupIds", userGroupIDs);
//
//        List<String> invalidGroups = invalidGroupsQuery.getResultList();
//        if (invalidGroups != null) {
//            throw new UserInvalidGroupsException("Groups "
//                    + invalidGroups.toString() + " are not valid.");
//        }

        UserEntity user = new UserEntity();
        user.setUsername(username);
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        user.setPassword(hashedPassword);
        user.setName(name);
        user.setEmail(email);
        user.setGroups(systemGroups);

        em.persist(user);
    }

    @Override
    public UserEntity getUser(int id) {

        UserEntity user = em.find(UserEntity.class, id);

        return user;
    }

    @Override
    public List<UserEntity> getUsers() {

        TypedQuery<UserEntity> query = em.createNamedQuery(
                "UserEntity.findAll", UserEntity.class);
        List<UserEntity> users = query.getResultList();

        return users;
    }

    @Override
    public Collection<ProjectEntity> getUserProject(int id) {
        Set<ProjectEntity> projects = new HashSet<>();

        for(GroupEntity g : getUser(id).getGroups()){
            if(g.getProject()!= null){
                projects.add(g.getProject());
            }
        }

        return projects;
    }

    @Override
    public Collection<GroupEntity> getUsersProjectGroups(int userID, int projectID) {
        TypedQuery<GroupEntity> query = em.createNamedQuery(
                "GroupEntity.getUserProjectGroups", GroupEntity.class);
        query.setParameter("projectId", projectID);
        query.setParameter("userId", userID);

        List<GroupEntity> userProjectGroups = query.getResultList();

        return userProjectGroups;
    }

    @Override
    public void deleteUserGroups(int userId, int[] groupIds) throws UserNotInGroup {
        UserEntity user = getUser(userId);
        Map<String, ArrayList<String>> userNotInProjectGroups = new LinkedHashMap<String, ArrayList<String>>();

        for (int groupId : groupIds) {
            GroupEntity group = gm.getGroup(groupId);

            if(!group.getUsers().contains(user)) {
                String groupProjectName = group.getProject().getName();
                String GroupName = group.getName();

                addExceptionMapElement(userNotInProjectGroups, groupProjectName, GroupName);
            }
            else if (userNotInProjectGroups.size() == 0){
                gm.deleteUserFromGroup(user, group);
            }
        }

        if(userNotInProjectGroups.size() > 0) {
            throw new UserNotInGroup("User " + user.getName() + " doesn't belong to project/s-group/s: " +
                    userNotInProjectGroups.toString());
        }
    }

    @Override
    public void addUserGroups(int userId, int[] groupIds) throws UserInGroup {
        UserEntity user = getUser(userId);
        Map<String, ArrayList<String>> userInProjectGroups = new LinkedHashMap<String, ArrayList<String>>();

        for (int groupId : groupIds) {
            GroupEntity group = gm.getGroup(groupId);

            if(group.getUsers().contains(user)) {
                String groupProjectName = group.getProject().getName();
                String GroupName = group.getName();

                addExceptionMapElement(userInProjectGroups, groupProjectName, GroupName);
            }
            else if (userInProjectGroups.size() == 0) {
                gm.addUserToGroup(user, group);
            }
        }

        if(userInProjectGroups.size() > 0) {
            throw new UserInGroup("User " + user.getName() + " already belongs to project/s-group/s: " +
                    userInProjectGroups.toString());
        }
    }

    private void addExceptionMapElement(Map<String, ArrayList<String>> exceptionMap, String key, String value) {
        if (exceptionMap.containsKey(key)){
            exceptionMap.get(key).add(value);
        } else {
            ArrayList<String> userNotInGroups = new ArrayList<String>();
            userNotInGroups.add(value);
            exceptionMap.put(key, userNotInGroups);
        }
    }

    private boolean isUniqueUsername(String username) {
        TypedQuery<UserEntity> isUniqueQuery = em.createNamedQuery("UserEntity.getUserByUsername", UserEntity.class);
        isUniqueQuery.setParameter("username", username);

        return isUniqueQuery.getResultList().size() == 0;
    }

    private boolean isUniqueEmail(String email) {
        TypedQuery<UserEntity> isUniqueQuery = em.createNamedQuery("UserEntity.getUserByEmail", UserEntity.class);
        isUniqueQuery.setParameter("email", email);

        return isUniqueQuery.getResultList().size() == 0;
    }
}
