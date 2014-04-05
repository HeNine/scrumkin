package com.scrumkin.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.mindrot.jbcrypt.BCrypt;

import com.scrumkin.api.UserManager;
import com.scrumkin.api.exceptions.UserEmailMismatchException;
import com.scrumkin.api.exceptions.UserInvalidGroupsException;
import com.scrumkin.api.exceptions.UserNotUniqueException;
import com.scrumkin.api.exceptions.UserPasswordMismatchException;
import com.scrumkin.api.exceptions.UserUsernameNotUniqueException;
import com.scrumkin.jpa.GroupEntity;
import com.scrumkin.jpa.PriorityEntity;
import com.scrumkin.jpa.UserEntity;
import com.scrumkin.jpa.UserStoryEntity;

/**
 * Session Bean implementation class AddUsersBean.
 */
@Stateless(mappedName = "userManager")
public class UserManagerEJB implements UserManager {

    @PersistenceContext(unitName = "scrumkin_PU")
    private EntityManager em;

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
