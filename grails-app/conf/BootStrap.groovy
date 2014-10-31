import poncemoral.pfi.domain.Course
import poncemoral.pfi.domain.Student
import poncemoral.pfi.domain.Teacher
import poncemoral.pfi.services.StudentService

class BootStrap {
	def courseService
    def init = { servletContext ->
		Student.withTransaction {
				
			
			Teacher smoral = new Teacher()
			smoral.setUsername("smoral")
			smoral.save()
			
			Teacher mponce = new Teacher()
			mponce.setUsername("mponce")
			mponce.save()
			
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
			
			Course course4 = new Course();
			course4.setCourseNumber(4)
			course4.setDayOfWeek(1) //monday
			course4.setName("Analisis II")
			course4.setHourFrom("07:45")
			course4.setHourTo("11:45")
			course4.setGenerateFacesdata(Boolean.FALSE);
			course4.getTeachers().add(smoral);
			
			Course course5 = new Course();
			course5.setCourseNumber(5)
			course5.setDayOfWeek(1) //monday
			course5.setName("Programacion I")
			course5.setHourFrom("07:55")
			course5.setHourTo("11:55")
			course5.setGenerateFacesdata(Boolean.FALSE);
			course5.getTeachers().add(mponce);
			
			//SE DEBERIA GUARDAR LA CARPETA FACES QUE ESTA DENTRO DEL REPO EN EL PATH QUE SE INDICA EN LA VARIABLE AppConstants.TRAINING_IMAGES_ROOT_FOLDER
			StudentService.createStudentsFromImageFolder(course1, course2, course4, course5)
			
			course1.save()
			courseService.createTrainingData(course1.courseNumber);			
			course2.save()
			courseService.createTrainingData(course2.courseNumber);			
			course4.save()
			courseService.createTrainingData(course4.courseNumber);			
			course5.save()
			courseService.createTrainingData(course5.courseNumber);
		}
    }
    def destroy = {
    }
}
