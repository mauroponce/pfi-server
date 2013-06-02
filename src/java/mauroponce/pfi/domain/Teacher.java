package mauroponce.pfi.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "TEACHERS")
public class Teacher implements Serializable{
	
	private String username;
	//private String password;
	
	@Id
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
/*	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}*/
	public Teacher() {}
}
