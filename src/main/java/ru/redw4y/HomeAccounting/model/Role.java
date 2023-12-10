package ru.redw4y.HomeAccounting.model;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the roles database table.
 * 
 */
@Entity
@Table(name="roles")
public class Role implements Serializable {
	@Override
	public String toString() {
		return "Role [name=" + name + "]";
	}

	private static final long serialVersionUID = 1L;

	@Id
	private Integer id;
	private String name;

	@OneToMany(mappedBy="role")
	private List<User> users;

	public Role() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(Roles role) {
		this.name = role.toString();
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public User addUser(User user) {
		getUsers().add(user);
		user.setRole(this);

		return user;
	}

	public User removeUser(User user) {
		getUsers().remove(user);
		user.setRole(null);
		return user;
	}

}
enum Roles {
	ADMIN, USER
}