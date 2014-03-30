package com.scrumkin.jpa;

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * Created by Matija on 25.3.2014.
 */
@Entity
@Table(name = "groups", schema = "public", catalog = "scrumkin")
@NamedQueries(
		@NamedQuery(name = "invalidGroups", query = "SELECT g.id FROM GroupEntity g WHERE g.id IN :groupIds"))
public class GroupEntity {
	private int id;
	private String name;
	private ProjectEntity projects;
	private Collection<PermissionEntity> permissions;
	private Collection<UserEntity> users;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Basic
	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		GroupEntity that = (GroupEntity) o;

		if (id != that.id)
			return false;
		if (name != null ? !name.equals(that.name) : that.name != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = id;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}

	@ManyToOne
	@JoinColumn(name = "project_id", referencedColumnName = "id")
	public ProjectEntity getProjects() {
		return projects;
	}

	public void setProjects(ProjectEntity projects) {
		this.projects = projects;
	}

	@ManyToMany
	@JoinTable(name = "group_permissions", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
	public Collection<PermissionEntity> getPermissions() {
		return permissions;
	}

	public void setPermissions(Collection<PermissionEntity> permissions) {
		this.permissions = permissions;
	}

	@ManyToMany(mappedBy = "groups")
	public Collection<UserEntity> getUsers() {
		return users;
	}

	public void setUsers(Collection<UserEntity> users) {
		this.users = users;
	}
}