package net.nature.client.database;

import org.droidpersistence.annotation.Column;
import org.droidpersistence.annotation.PrimaryKey;
import org.droidpersistence.annotation.Table;

@Table(name="USER")
public class User {

	@PrimaryKey(autoIncrement=true)
	@Column(name="ID")
	private long id = -1;

	@Column(name="NAME")
	private String name;

	@Column(name="AVATARNAME")
	private String avatarName;

	public String getName() {
		return name;
	}

	public void setName(String username) {
		this.name = username;
	}

	public String getAvatarName() {
		return avatarName;
	}

	public void setAvatarName(String avatarName) {
		this.avatarName = avatarName;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}	

	public String toString(){		
		return "{user: id= " + id 
				+ ", name= " + name
				+ ", avatarName= " + avatarName 
				+ "}";
	}
}