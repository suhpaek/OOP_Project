package services.academic;

import data.UniversityDataStore;
import enums.LessonType;
import models.academic.AttendanceRecord;
import models.academic.Course;
import models.users.Student;
import models.users.Teacher;
import models.users.User;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AttendanceService {
    private final UniversityDataStore dataStore;
    private final CourseService courseService;

    public AttendanceService() {
        this(UniversityDataStore.getInstance(), new CourseService());
    }

    public AttendanceService(UniversityDataStore dataStore, CourseService courseService) {
        this.dataStore = dataStore;
        this.courseService = courseService;
    }

    public void markAttendance(Teacher teacher, String studentUsername, String courseCode,
                               LessonType lessonType, LocalDate date, boolean present) throws Exception {
        Course course = courseService.findCourseByCode(courseCode);
        User user = dataStore.findUserByUsername(studentUsername);
        if (!(user instanceof Student) || course == null) {
            throw new IllegalArgumentException("Student or course not found.");
        }
        if (teacher == null || !teacher.getAssignedCourses().contains(course)) {
            throw new IllegalArgumentException("Teacher is not assigned to this course.");
        }
        Student student = (Student) user;
        if (!course.isStudentEnrolled(student)) {
            throw new IllegalArgumentException("Student is not enrolled in this course.");
        }
        dataStore.addAttendanceRecord(new AttendanceRecord(student.getId(), course.getCode(), lessonType, date, present));
        saveData();
    }

    public List<String> getAttendanceRows(Student student) {
        List<String> rows = new ArrayList<>();
        if (student == null) return rows;
        for (AttendanceRecord record : dataStore.getAttendanceRecords()) {
            if (student.getId().equals(record.getStudentId())) {
                rows.add(record.getDate() + " | " + record.getCourseCode() + " | "
                        + record.getLessonType() + " | " + (record.isPresent() ? "Present" : "Absent"));
            }
        }
        return rows;
    }

    private void saveData() {
        try {
            dataStore.save();
        } catch (IOException e) {
            throw new IllegalStateException("Could not save attendance.", e);
        }
    }
}
