package services;

import data.DataStore;
import enums.CourseType;
import enums.LessonType;
import enums.NewsType;
import models.academic.Course;
import models.academic.Mark;
import models.organization.StudentOrganization;
import models.support.TechSupportRequest;
import models.users.Manager;
import models.users.Student;
import models.users.Teacher;
import models.users.TechSupportSpecialist;
import models.users.User;

import java.io.IOException;

public class DemoDataService {
    private final DataStore dataStore;

    public DemoDataService() {
        this(DataStore.getInstance());
    }

    public DemoDataService(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void seedDemoData() throws IOException {
        Student student = getOrCreateStudent();
        Teacher teacher = getOrCreateTeacher();
        getOrCreateManager();
        getOrCreateSupport();

        Course oop = getOrCreateCourse("CSCI2106", "Object-Oriented Programming and Design", 3, CourseType.MAJOR, 2);
        Course web = getOrCreateCourse("INF2205", "Web Development", 4, CourseType.MAJOR, 2);
        setupCourse(oop, teacher, "Monday", "10:00", "302");
        setupCourse(web, teacher, "Wednesday", "14:00", "415");
        enrollWithMark(student, oop, new Mark(27, 28, 36));
        enrollWithMark(student, web, new Mark(25, 26, 34));

        dataStore.setCourseRegistrationOpen(true);
        seedOrganization(student);
        seedNewsAndSupport();
        dataStore.save();
    }

    private Student getOrCreateStudent() {
        User user = findUser("student");
        if (user instanceof Student) return (Student) user;
        Student student = new Student("student", "student", "Demo", "Student", "student@university.kz");
        dataStore.addUser(student);
        return student;
    }

    private Teacher getOrCreateTeacher() {
        User user = findUser("teacher");
        if (user instanceof Teacher) return (Teacher) user;
        Teacher teacher = new Teacher();
        teacher.updateProfile("teacher", "Demo", "Teacher", null, null);
        teacher.changePassword("teacher");
        teacher.updateEmail("teacher@university.kz");
        dataStore.addUser(teacher);
        return teacher;
    }

    private Manager getOrCreateManager() {
        User user = findUser("manager");
        if (user instanceof Manager) return (Manager) user;
        Manager manager = new Manager();
        manager.updateProfile("manager", "Demo", "Manager", null, null);
        manager.changePassword("manager");
        manager.updateEmail("manager@university.kz");
        dataStore.addUser(manager);
        return manager;
    }

    private TechSupportSpecialist getOrCreateSupport() {
        User user = findUser("support");
        if (user instanceof TechSupportSpecialist) return (TechSupportSpecialist) user;
        TechSupportSpecialist support = new TechSupportSpecialist("support", "support", "Demo", "Support", "support@university.kz");
        dataStore.addUser(support);
        return support;
    }

    private Course getOrCreateCourse(String code, String name, int credits, CourseType type, int year) {
        for (Course course : dataStore.getCourses()) {
            if (course.getCode().equalsIgnoreCase(code)) return course;
        }
        Course course = new Course(code, name, credits, type);
        course.setIntendedYearOfStudy(year);
        dataStore.addCourse(course);
        return course;
    }

    private void setupCourse(Course course, Teacher teacher, String day, String time, String room) {
        course.setLectureTeacher(teacher);
        teacher.assignCourse(course);
        if (course.getLessons().isEmpty()) {
            course.addLesson(new models.academic.Lesson(LessonType.LECTURE, day, time, room, teacher));
            course.addLesson(new models.academic.Lesson(LessonType.PRACTICE, day, "16:00", room + "A", teacher));
        }
    }

    private void enrollWithMark(Student student, Course course, Mark mark) {
        if (!student.getRegisteredCourses().contains(course)) {
            try {
                student.registerForCourse(course);
            } catch (Exception ignored) {
            }
        }
        course.putMark(student, mark);
        student.addMarkToTranscript(course, mark);
    }

    private void seedOrganization(Student student) {
        for (StudentOrganization organization : dataStore.getStudentOrganizations()) {
            if ("Programming Club".equalsIgnoreCase(organization.getName())) return;
        }
        StudentOrganization organization = new StudentOrganization("Programming Club", "Competitive programming and project nights");
        organization.setHeadStudentId(student.getId());
        student.leadOrganization(organization.getName());
        dataStore.addStudentOrganization(organization);
    }

    private void seedNewsAndSupport() {
        if (dataStore.getNews().isEmpty()) {
            dataStore.addNews(new models.communication.News(1, "Registration is open", "Course registration is open for demo students.", NewsType.GENERAL));
        }
        if (dataStore.getSupportRequests().isEmpty()) {
            dataStore.addSupportRequest(new TechSupportRequest("demo-support-1", "Cannot open transcript", "Demo request for tech support flow."));
        }
    }

    private User findUser(String username) {
        try {
            return dataStore.findUserByUsername(username);
        } catch (Exception e) {
            return null;
        }
    }
}
