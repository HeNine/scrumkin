package com.scrumkin.ejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.scrumkin.api.GroupManager;
import com.scrumkin.api.exceptions.PermissionInvalidException;
import com.scrumkin.jpa.GroupEntity;
import com.scrumkin.jpa.PermissionEntity;

/**
 * Session Bean implementation class GroupManagerEJB
 */
@Stateless(mappedName = "groupManager")
public class GroupManagerEJB implements GroupManager {

	@PersistenceContext(unitName = "scrumkin_PU")
	private EntityManager em;

	public static List<PermissionEntity> validPermissions;

	public GroupManagerEJB() {
		validPermissions = getValidPermissions();
	}

	public List<PermissionEntity> getValidPermissions() {
		TypedQuery<PermissionEntity> query = em.createNamedQuery(
				"PermissionEntity.findAll", PermissionEntity.class);
		List<PermissionEntity> permissions = query.getResultList();

		return permissions;
	}

	@Override
	public void addGroup(Collection<PermissionEntity> permissions)
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

		em.getTransaction().begin();
		for (PermissionEntity p : permissions) {
			em.persist(p);
		}
		em.getTransaction().commit();
	}
	
	public GroupEntity getGroup(int id) {
		GroupEntity group = em.find(GroupEntity.class, id);

        return group;
	}

}
