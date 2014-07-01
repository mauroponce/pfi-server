import mauroponce.pfi.domain.Course
import mauroponce.pfi.domain.Student
import mauroponce.pfi.domain.Teacher
import mauroponce.pfi.services.StudentService

class BootStrap {

    def init = { servletContext ->
		Student.withTransaction {
				
			
			Teacher miralles = new Teacher()
			miralles.setUsername("mmiralles")
			miralles.save()
			
			Course course = new Course();
			course.setCourseNumber(3)
			course.setDayOfWeek(1) //monday
			course.setName("Fisica General")
			course.setHourFrom("07:45")
			course.setHourTo("11:45")
			course.setGenerateFacesdata(Boolean.FALSE);
			
//			course.getStudents().add(mauro);
//			course.getStudents().add(cho);
//			course.getStudents().add(pau);
//			course.getStudents().add(goma);
			
			course.getTeachers().add(miralles);
			//SE DEBERIA GUARDAR LA CARPETA FACES QUE ESTA DENTRO DEL REPO EN EL PATH QUE SE INDICA EN LA VARIABLE AppConstants.TRAINING_IMAGES_ROOT_FOLDER
			StudentService.createStudentsFromImageFolder(course)
//			def students = CourseUtil.getStudentsFromImageFolder(course);
//			students.each { student ->
//				student.save()
//				course.getStudents().add(student)				
//			}
			course.save()
		}
    }
    def destroy = {
    }
}
