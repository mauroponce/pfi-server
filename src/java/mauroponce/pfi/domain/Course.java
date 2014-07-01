package mauroponce.pfi.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "COURSES")
public class Course implements Serializable {

	private Integer courseNumber;
	private String name;
	private List<Teacher> teachers = new ArrayList<Teacher>();
	private List<Student> students = new ArrayList<Student>();
	private Integer dayOfWeek; // Monday = 1... Sunday = 7
	private String hourFrom;// 7:45, 18:30, etc
	private String hourTo;
	private Boolean generateFacesdata;
		
	@Id
	public Integer getCourseNumber() {
		return courseNumber;
	}
	
	public void setCourseNumber(Integer courseNumber) {
		this.courseNumber = courseNumber;
	}
	@ManyToMany
	public void setStudents(List<Student> students) {
		this.students = students;
	}	
	@ManyToMany
	@JoinTable(name = "COURSES_TEACHERS", 
		joinColumns = 
			@JoinColumn(name = "COURSE_NUMBER"),
		inverseJoinColumns = 
			@JoinColumn(name = "TEACHER_USERNAME")
	)
	public List<Teacher> getTeachers() {
		return teachers;
	}
	public void setTeachers(List<Teacher> teachers) {
		this.teachers = teachers;
	}
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "COURSES_STUDENTS", 
		joinColumns = 
			@JoinColumn(name = "COURSE_NUMBER"),
		inverseJoinColumns = 
			@JoinColumn(name = "STUDENT_LU")
	)
	public List<Student> getStudents() {
		return students;
	}
	public Integer getDayOfWeek() {
		return dayOfWeek;
	}
	public void setDayOfWeek(Integer dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	public String getHourFrom() {
		return hourFrom;
	}
	public void setHourFrom(String hourFrom) {
		this.hourFrom = hourFrom;
	}
	public String getHourTo() {
		return hourTo;
	}
	public void setHourTo(String hourTo) {
		this.hourTo = hourTo;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getGenerateFacesdata() {
		return generateFacesdata;
	}
	public void setGenerateFacesdata(Boolean generateFacesdata) {
		this.generateFacesdata = generateFacesdata;
	}
}
