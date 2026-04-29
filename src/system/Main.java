package system;

import academic.Course;
import academic.Mark;
import academic.RegistrationRequest;
import data.DataStore;
import enums.CourseType;
import enums.ManagerType;
import enums.TeacherType;
import users.Admin;
import users.Manager;
import users.Student;
import users.Teacher;

public class Main {
    public static void main(String[] args) {
        try {
            DataStore dataStore = DataStore.getInstance();

            Admin admin = new Admin("admin", "admin123", "System", "Admin", "admin@kbtu.kz");
            Manager manager = new Manager("manager", "manager123", "Aruzhan", "Manager", "manager@kbtu.kz", ManagerType.OFFICEREGISTRATOR);
            Teacher teacher = new Teacher("teacher", "teacher123", "Ali", "Teacher", "teacher@kbtu.kz", TeacherType.LECTOR);
            Student student = new Student("student", "student123", "Dana", "Student", "student@kbtu.kz");

            admin.addUser(admin);
            admin.addUser(manager);
            admin.addUser(teacher);
            admin.addUser(student);

            Course oop = new Course("CS101", "Object-Oriented Programming", 6, CourseType.MAJOR);
            manager.addCourseForRegistration(oop);
            manager.assignCourseToTeacher(oop, teacher, true);

            RegistrationRequest request = new RegistrationRequest(student, oop);
            manager.approveRegistration(request);

            Mark mark = new Mark(28, 27, 35);
            teacher.putMark(student, oop, mark);

            System.out.println("Authenticated user: " + dataStore.authenticate("student", "student123").getFullName());
            System.out.println();
            teacher.viewCourses();
            System.out.println();
            student.viewMarks();
            System.out.println();
            student.viewTranscript();
            System.out.println();
            System.out.println(manager.createAcademicReport());
            dataStore.save();
        } catch (Exception e) {
            System.out.println("Application error: " + e.getMessage());
        }
    }
}
