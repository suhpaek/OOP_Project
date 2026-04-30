package system;

import academic.Course;
import academic.Mark;
import data.DataStore;
import enums.CourseType;
import enums.Degree;
import enums.TeacherType;
import exceptions.InvalidSupervisorException;
import java.time.LocalDate;
import research.ResearchPaper;
import research.Researcher;
import users.Admin;
import users.GraduateStudent;
import users.Student;
import users.Teacher;

public class Main {
    public static void main(String[] args) throws Exception {
        DataStore store = DataStore.getInstance();

        Admin admin = new Admin("admin", "123", "Main", "Admin", "admin@uni.kz");
        Student student = new Student("student", "123", "Aruzhan", "Student", "student@uni.kz");
        Teacher professor = new Teacher("prof", "123", "Alan", "Turing", "prof@uni.kz", TeacherType.PROFESSOR);

        store.addUser(admin);
        admin.addUser(student);
        admin.addUser(professor);

        UserLoginTest(store);

        Course oop = new Course("CS101", "Object-Oriented Programming", 5, CourseType.MAJOR);
        professor.assignCourse(oop);
        student.registerForCourse(oop);

        professor.putMark(student, oop, new Mark(30, 30, 35));
        student.viewTranscript();

        student.rateTeacher(professor, 5);
        System.out.println("Teacher rating: " + professor.getAverageRating());

        Researcher researcher = new Researcher(professor);
        ResearchPaper paper = new ResearchPaper(
                "OOP in University Systems",
                "KBTU Research Journal",
                12,
                LocalDate.now(),
                "10.1234/oop"
        );

        researcher.publishPaper(paper);
        paper.incrementCitations();
        paper.incrementCitations();
        paper.incrementCitations();

        System.out.println("H-index: " + researcher.calculateHIndex());
        System.out.println(paper.getCitation(enums.CitationFormat.PLAIN_TEXT));

        GraduateStudent grad = new GraduateStudent("grad", "123", "Dana", "Master", "grad@uni.kz", Degree.Master);

        try {
            admin.updateGraduateStudentInfo(grad, Degree.Master, researcher);
            System.out.println("Supervisor assigned: " + grad.getResearchSupervisorName());
        } catch (InvalidSupervisorException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("News count: " + store.getNews().size());

        store.save();
        System.out.println("Saved successfully.");
    }

    private static void UserLoginTest(DataStore store) throws Exception {
        System.out.println("Login test: " + store.authenticate("student", "123").getFullName());
    }
}
