package poncemoral.pfi.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "ATTENDANCE")
public class Attendance implements Serializable {

	private Student student;
	private Course course;
	private Date date;//Trabajo con JodaTime pero persisto util.Date
	private Boolean attended;
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "student_id")
	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}
	@ManyToOne
	@JoinColumn(name = "course_id")
	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Boolean getAttended() {
		return attended;
	}

	public void setAttended(Boolean attended) {
		this.attended = attended;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	@SuppressWarnings("unused")
	private void setId(Integer id) {
		//orm
		this.id = id;
	}

	public Attendance(Student student, Course course, Boolean attended, Date date) {
		super();
		this.student = student;
		this.course = course;
		this.attended = attended;
		this.date = date;
	}
	public Attendance() {}
}
