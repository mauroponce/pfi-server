package poncemoral.pfi.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "STUDENTS")
public class Student implements Serializable {

	private String firstName;
	private String lastName;
	private Integer LU;
	private String encodedImage;
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	@Lob
	public String getEncodedImage() {
		return encodedImage;
	}
	public void setEncodedImage(String encodedImage) {
		this.encodedImage = encodedImage;
	}
	@Id
	public Integer getLU() {
		return LU;
	}
	public void setLU(Integer lU) {
		LU = lU;
	}
	public Student() {}
}
