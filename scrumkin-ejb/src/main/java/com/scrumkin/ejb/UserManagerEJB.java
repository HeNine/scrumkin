package com.scrumkin.ejb;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.scrumkin.api.UserManager;
import com.scrumkin.api.exceptions.UserInvalidGroupsException;
import com.scrumkin.api.exceptions.UserNotUniqueException;
import com.scrumkin.api.exceptions.UserUsernameNotUniqueException;
import com.scrumkin.jpa.GroupEntity;
import com.scrumkin.jpa.UserEntity;

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
			UserNotUniqueException {

		TypedQuery<Boolean> isUniqueUsernameQuery = em.createNamedQuery(
				"isUniqueUsername", Boolean.class);
		isUniqueUsernameQuery.setParameter("username", username);

		boolean isUniqueUsername = isUniqueUsernameQuery.getSingleResult();
		if (!isUniqueUsername) {
			throw new UserUsernameNotUniqueException("Username [" + username
					+ "] is not unique.");
		}

		TypedQuery<Boolean> isUniqueUserQuery = em.createNamedQuery(
				"isUniqueUser", Boolean.class);
		isUniqueUserQuery.setParameter("name", name);
		isUniqueUserQuery.setParameter("email", email);

		boolean isUniqueUser = isUniqueUserQuery.getSingleResult();
		if (!isUniqueUser) {
			throw new UserNotUniqueException("User with name [" + name
					+ "] and email [" + email + "] is not unique.");
		}
	
		List<Integer> userGroupIDs = new ArrayList<Integer>(systemGroups.size());
		for (GroupEntity userGroup : systemGroups) {
			userGroupIDs.add(userGroup.getId());
		}

		TypedQuery<Integer> invalidGroupsQuery = em.createNamedQuery(
				"invalidGroups", Integer.class);
		invalidGroupsQuery.setParameter("groupIds", userGroupIDs);

		List<Integer> invalidGroups = invalidGroupsQuery.getResultList();
		if (invalidGroups != null) {
			throw new UserInvalidGroupsException("Groups "
					+ invalidGroups.toString() + " are not valid.");
		}

		UserEntity user = new UserEntity();
		user.setUsername(username);
		user.setPassword(hash(password));
		user.setName(name);
		user.setEmail(email);
		user.setGroups(systemGroups);

		em.persist(user);
	}

	public String hash(String string) {

		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(string.getBytes("UTF-8"));

			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < hash.length; i++) {
				sb.append(Integer.toString((hash[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

}
