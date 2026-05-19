package services;

import data.DataStore;
import models.academic.Course;
import models.academic.Mark;
import models.users.Student;
import models.users.Teacher;
import models.users.User;

import java.io.IOException;

public class GradeService {
    private final DataStore dataStore;
    private final CourseService courseService;

    public GradeService() {
        this(DataStore.getInstance(), new CourseService());
    }

    public GradeService(DataStore dataStore, CourseService courseService) {
        this.dataStore = dataStore;
        this.courseService = courseService;
    }

    public void putMark(Teacher teacher, Course course, Student student, Mark mark) {
        if (teacher == null || !teacher.getAssignedCourses().contains(course)) return;
        putMark(course, student, mark);
    }

    public void putMark(Course course, Student student, Mark mark) {
        if (course == null || student == null || mark == null || !course.isStudentEnrolled(student)) return;
        course.putMark(student, mark);
        student.addMarkToTranscript(course, mark);
    }

    public void putMark(Teacher teacher, String studentUsername, String courseCode,
                        double first, double second, double exam) throws Exception {
        User user = dataStore.findUserByUsername(studentUsername);
        Course course = courseService.findCourseByCode(courseCode);
        if (!(user instanceof Student) || course == null) {
            throw new IllegalArgumentException("Student or course not found.");
        }
        if (!teacher.getAssignedCourses().contains(course)) {
            throw new IllegalArgumentException("Teacher is not assigned to this course.");
        }
        if (!course.isStudentEnrolled((Student) user)) {
            throw new IllegalArgumentException("Student is not registered for this course.");
        }
        putMark(teacher, course, (Student) user, new Mark(first, second, exam));
        saveData();
    }

    private void saveData() throws IOException {
        dataStore.save();
    }

    public double total(Mark mark) { return mark == null ? 0 : mark.getTotal(); }
    public String letterGrade(Mark mark) { return mark == null ? null : mark.getLetterGrade(); }
}
