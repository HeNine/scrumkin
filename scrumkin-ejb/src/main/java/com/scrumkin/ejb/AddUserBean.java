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

import com.scrumkin.jpa.GroupEntity;
import com.scrumkin.jpa.PermissionEntity;
import com.scrumkin.jpa.UserEntity;

/**
 * Session Bean implementation class AddUsersBean.
 */
@Stateless(mappedName = "addUsers")
public class AddUserBean implements AddUserBeanLocal, AddUserBeanRemote {

	String username;
	String password;
	String confirmPassword;
	String name;
	String email;
	String confirmEmail;
	List<String> groups;

	@PersistenceContext(unitName = "scrumkin_PU")
	EntityManager em;

	public AddUserBean() {

	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getConfirmEmail() {
		return confirmEmail;
	}

	public void setConfirmEmail(String confirmEmail) {
		this.confirmEmail = confirmEmail;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public String[] getAvailablePermissions() {
		TypedQuery<PermissionEntity> query = em.createNamedQuery(
				"PermissionEntity.findAll", PermissionEntity.class);
		List<PermissionEntity> permissions = (List<PermissionEntity>) query
				.getResultList();

		String[] permissionNames = new String[permissions.size()];
		for (int i = 0; i < permissions.size(); i++) {
			permissionNames[i] = permissions.get(i).getName();
		}

		return permissionNames;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void persistUser() throws InvalidGroupException {
		Collection<GroupEntity> userGroups = new ArrayList<>();
		for (String userGroup : groups) {
			GroupEntity group = (GroupEntity) em
					.createQuery("SELECT * FROM groups g WHERE g.name = :name")
					.setParameter("name", userGroup).getSingleResult();
			if (group == null) {
				throw new InvalidGroupException();
			} else {
				userGroups.add(group);
			}
		}

		UserEntity user = new UserEntity();
		user.setUsername(this.username);
		user.setPassword(hash(this.password));
		user.setName(this.name);
		user.setEmail(this.email);
		user.setGroups(userGroups);

		em.persist(user);
		em.flush();

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
