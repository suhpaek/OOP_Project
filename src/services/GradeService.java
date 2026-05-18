package services;

import academic.Course;
import academic.Mark;
import users.Student;
import users.Teacher;

public class GradeService {
    public void putMark(Teacher teacher, Course course, Student student, Mark mark) {
        if (teacher == null || !teacher.getAssignedCourses().contains(course)) return;
        putMark(course, student, mark);
    }

    public void putMark(Course course, Student student, Mark mark) {
        if (course == null || student == null || mark == null || !course.isStudentEnrolled(student)) return;
        course.putMark(student, mark);
        student.addMarkToTranscript(course, mark);
    }

    public double total(Mark mark) { return mark == null ? 0 : mark.getTotal(); }
    public String letterGrade(Mark mark) { return mark == null ? null : mark.getLetterGrade(); }
}
