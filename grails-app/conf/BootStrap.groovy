import mauroponce.pfi.domain.Course
import mauroponce.pfi.domain.Student
import mauroponce.pfi.domain.Teacher
import mauroponce.pfi.services.StudentService

class BootStrap {
	def courseService
    def init = { servletContext ->
		Student.withTransaction {
				
			
			Teacher miralles = new Teacher()
			miralles.setUsername("mmiralles")
			miralles.save()
			
			Teacher sadopazo = new Teacher()
			sadopazo.setUsername("sadopazo")
			sadopazo.save()
			
			Course course1 = new Course();
			course1.setCourseNumber(3)
			course1.setDayOfWeek(1) //monday
			course1.setName("Fisica General")
			course1.setHourFrom("07:45")
			course1.setHourTo("11:45")
			course1.setGenerateFacesdata(Boolean.FALSE);			
			course1.getTeachers().add(miralles);
						
			Course course2 = new Course();
			course2.setCourseNumber(1)
			course2.setDayOfWeek(1) //monday
			course2.setName("Estadistica Aplicada")
			course2.setHourFrom("07:45")
			course2.setHourTo("11:45")
			course2.setGenerateFacesdata(Boolean.FALSE);			
			course2.getTeachers().add(sadopazo);
			//SE DEBERIA GUARDAR LA CARPETA FACES QUE ESTA DENTRO DEL REPO EN EL PATH QUE SE INDICA EN LA VARIABLE AppConstants.TRAINING_IMAGES_ROOT_FOLDER
			StudentService.createStudentsFromImageFolder(course1, course2)
			
			course1.save()
			courseService.createTrainingData(course1.courseNumber);			
			course2.save()
			courseService.createTrainingData(course2.courseNumber);
		}
    }
    def destroy = {
    }
}
