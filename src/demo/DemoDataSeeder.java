package demo;

import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;

import data.UniversityDataStore;
import enums.CourseType;
import enums.Degree;
import enums.LessonType;
import enums.ManagerType;
import enums.NewsType;
import enums.RequestStatus;
import enums.School;
import enums.TeacherType;
import models.academic.AttendanceRecord;
import models.academic.Course;
import models.academic.Lesson;
import models.academic.Mark;
import models.academic.RegistrationRequest;
import models.communication.Comment;
import models.communication.Message;
import models.communication.News;
import models.organization.StudentOrganization;
import models.research.ResearchPaper;
import models.research.Researcher;
import models.support.TechSupportRequest;
import models.users.GraduateStudent;
import models.users.Manager;
import models.users.Student;
import models.users.Teacher;
import models.users.TechSupportSpecialist;
import models.users.User;
import services.research.JournalService;
import services.research.ResearchService;

public class DemoDataSeeder {
    private final UniversityDataStore dataStore;
    private final ResearchService researchService;
    private final JournalService journalService;

    public DemoDataSeeder() {
        this(UniversityDataStore.getInstance());
    }

    public DemoDataSeeder(UniversityDataStore dataStore) {
        this.dataStore = dataStore;
        this.researchService = new ResearchService(dataStore);
        this.journalService = new JournalService(dataStore);
    }

    public void seedDemoData() throws IOException {
        Student student = getOrCreateStudent("student", "Demo", "Student", School.SITE);
        Student altynai = getOrCreateStudent("altynai", "Altynai", "Member", School.SITE);
        Student daulet = getOrCreateStudent("daulet", "Daulet", "Member", School.BS);
        GraduateStudent graduate = getOrCreateGraduateStudent();
        Teacher oopTeacher = getOrCreateTeacher("teacher", "Demo", "Teacher", TeacherType.PROFESSOR, Degree.PhD);
        Teacher webTeacher = getOrCreateTeacher("webteacher", "Dana", "Webster", TeacherType.SENIOR_LECTOR, Degree.Master);
        Teacher mathTeacher = getOrCreateTeacher("mathteacher", "Askar", "Calculus", TeacherType.LECTOR, Degree.Master);
        getOrCreateManager();
        getOrCreateSupport();

        Course oop = getOrCreateCourse("CSCI2106", "Object-Oriented Programming and Design", 3, CourseType.MAJOR, 2, School.SITE);
        Course web = getOrCreateCourse("INF2205", "Web Development", 4, CourseType.MAJOR, 2, School.SITE);
        Course math = getOrCreateCourse("MATH120", "Calculus 2", 5, CourseType.MAJOR, 1, School.SITE);
        Course history = getOrCreateCourse("HIST110", "History of Kazakhstan", 3, CourseType.FREE_ELECTIVE, 1, null);
        Course research = getOrCreateCourse("RES700", "Research Methods", 3, CourseType.MAJOR, 1, School.SITE);

        setupCourse(oop, oopTeacher, oopTeacher, "Monday", "10:00", "302");
        setupCourse(web, webTeacher, webTeacher, "Wednesday", "14:00", "415");
        setupCourse(math, mathTeacher, mathTeacher, "Tuesday", "09:00", "210");
        setupCourse(history, oopTeacher, webTeacher, "Friday", "11:00", "118");
        setupCourse(research, oopTeacher, oopTeacher, "Thursday", "15:00", "501");

        enrollWithMark(student, oop, new Mark(27, 28, 36));
        enrollWithMark(student, web, new Mark(25, 26, 34));
        enrollWithMark(student, history, new Mark(24, 25, 32));
        enrollWithMark(altynai, oop, new Mark(29, 28, 38));
        enrollWithMark(altynai, math, new Mark(22, 24, 30));
        enrollWithMark(daulet, history, new Mark(21, 22, 31));
        enrollWithMark(graduate, research, new Mark(28, 29, 37));
        enrollWithMark(graduate, oop, new Mark(26, 27, 35));

        oopTeacher.addRating(5);
        oopTeacher.addRating(4);
        webTeacher.addRating(5);
        mathTeacher.addRating(4);

        seedAttendance(student, oop);
        seedAttendance(altynai, oop);
        seedAttendance(graduate, research);
        seedRegistrationRequests(student, altynai, daulet, math, web, oop);
        seedResearch(oopTeacher, graduate);
        seedOrganizations(student, altynai, daulet);
        seedNews(student, graduate);
        seedSupportRequests();
        seedMessages(oopTeacher, student, graduate);

        dataStore.setCourseRegistrationOpen(true);
        dataStore.save();
    }

    private Student getOrCreateStudent(String username, String firstName, String lastName, School school) {
        User user = findUser(username);
        if (user instanceof Student && !(user instanceof GraduateStudent)) {
            return (Student) user;
        }
        Student student = new Student(username, username, firstName, lastName, username + "@university.kz");
        student.setSchool(school);
        student.setDegree(Degree.Bachelor);
        dataStore.addUser(student);
        return student;
    }

    private GraduateStudent getOrCreateGraduateStudent() {
        User user = findUser("graduate");
        if (user instanceof GraduateStudent) {
            return (GraduateStudent) user;
        }
        GraduateStudent student = new GraduateStudent("graduate", "graduate", "Demo", "Graduate",
                "graduate@university.kz", Degree.Master);
        student.setSchool(School.SITE);
        dataStore.addUser(student);
        return student;
    }

    private Teacher getOrCreateTeacher(String username, String firstName, String lastName, TeacherType type, Degree degree) {
        User user = findUser(username);
        if (user instanceof Teacher) {
            return (Teacher) user;
        }
        Teacher teacher = new Teacher(username, username, firstName, lastName, username + "@university.kz", type);
        teacher.setDegree(degree);
        teacher.setSalary(600000);
        dataStore.addUser(teacher);
        researchService.getOrCreateResearcher(teacher);
        return teacher;
    }

    private Manager getOrCreateManager() {
        User user = findUser("manager");
        if (user instanceof Manager) {
            return (Manager) user;
        }
        Manager manager = new Manager("manager", "manager", "Demo", "Manager",
                "manager@university.kz", ManagerType.OFFICEREGISTRATOR);
        manager.setSalary(550000);
        dataStore.addUser(manager);
        return manager;
    }

    private TechSupportSpecialist getOrCreateSupport() {
        User user = findUser("support");
        if (user instanceof TechSupportSpecialist) {
            return (TechSupportSpecialist) user;
        }
        TechSupportSpecialist support = new TechSupportSpecialist("support", "support",
                "Demo", "Support", "support@university.kz");
        support.setSalary(400000);
        dataStore.addUser(support);
        return support;
    }

    private Course getOrCreateCourse(String code, String name, int credits, CourseType type, int year, School school) {
        for (Course course : dataStore.getCourses()) {
            if (course.getCode().equalsIgnoreCase(code)) {
                return course;
            }
        }
        Course course = new Course(code, name, credits, type);
        course.setIntendedYearOfStudy(year);
        course.setIntendedSchool(school);
        dataStore.addCourse(course);
        return course;
    }

    private void setupCourse(Course course, Teacher lectureTeacher, Teacher practiceTeacher,
                             String day, String time, String room) {
        course.setLectureTeacher(lectureTeacher);
        course.setPracticeTeacher(practiceTeacher);
        lectureTeacher.assignCourse(course);
        practiceTeacher.assignCourse(course);
        if (course.getLessons().isEmpty()) {
            course.addLesson(new Lesson(LessonType.LECTURE, day, time, room, lectureTeacher));
            course.addLesson(new Lesson(LessonType.PRACTICE, day, "16:00", room + "A", practiceTeacher));
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

    private void seedAttendance(Student student, Course course) {
        addAttendance(student, course, LessonType.LECTURE, LocalDate.now().minusDays(7), true);
        addAttendance(student, course, LessonType.PRACTICE, LocalDate.now().minusDays(5), true);
        addAttendance(student, course, LessonType.LECTURE, LocalDate.now().minusDays(2), false);
    }

    private void addAttendance(Student student, Course course, LessonType type, LocalDate date, boolean present) {
        dataStore.addAttendanceRecord(new AttendanceRecord(student.getId(), course.getCode(), type, date, present));
    }

    private void seedRegistrationRequests(Student student, Student altynai, Student daulet,
                                          Course math, Course web, Course oop) {
        addRegistrationRequest(student, math, false);
        addRegistrationRequest(altynai, web, true);
        addRegistrationRequest(daulet, oop, false);
    }

    private void addRegistrationRequest(Student student, Course course, boolean approved) {
        if (hasRequest(student, course)) {
            return;
        }
        RegistrationRequest request = new RegistrationRequest(student, course);
        if (approved) {
            request.markApproved(getOrCreateManager());
        }
        dataStore.addRegistrationRequest(request);
    }

    private boolean hasRequest(Student student, Course course) {
        for (RegistrationRequest request : dataStore.getRegistrationRequests()) {
            if (request.getStudent().equals(student) && request.getCourse().equals(course)) {
                return true;
            }
        }
        return false;
    }

    private void seedResearch(Teacher teacher, GraduateStudent graduate) {
        Researcher supervisor = researchService.getOrCreateResearcher(teacher);
        ResearchPaper paper = new ResearchPaper("Adaptive Learning Analytics", "KBTU Research Journal",
                12, LocalDate.of(2025, 4, 12), "10.2025/demo-adaptive");
        paper.setCitations(8);
        publishIfMissing(teacher, paper);

        ResearchPaper diploma = new ResearchPaper("Course Registration Optimization",
                "Student Research Proceedings", 9, LocalDate.of(2026, 2, 20), "10.2026/demo-registration");
        diploma.setCitations(3);
        publishIfMissing(graduate, diploma);
        try {
            graduate.setResearchSupervisorByAdmin(supervisor);
        } catch (Exception ignored) {
            graduate.setResearchSupervisor(supervisor);
        }
        journalService.createJournal("KBTU Research Journal");
        journalService.createJournal("Student Research Proceedings");
    }

    private void publishIfMissing(User user, ResearchPaper paper) {
        for (ResearchPaper existing : researchService.getPapers(user)) {
            if (existing.equals(paper)) {
                return;
            }
        }
        researchService.publishPaper(user, paper);
    }

    private void seedOrganizations(Student student, Student altynai, Student daulet) {
        StudentOrganization programming = getOrCreateOrganization("Programming Club",
                "Competitive programming and project nights");
        programming.setHeadStudentId(student.getId());
        programming.addMember(altynai.getId());
        student.leadOrganization(programming.getName());
        altynai.joinOrganization(programming.getName());

        StudentOrganization debate = getOrCreateOrganization("Debate Club", "Public speaking and debate practice");
        debate.setHeadStudentId(daulet.getId());
        debate.addMember(student.getId());
        daulet.leadOrganization(debate.getName());
        student.joinOrganization(debate.getName());
    }

    private StudentOrganization getOrCreateOrganization(String name, String description) {
        for (StudentOrganization organization : dataStore.getStudentOrganizations()) {
            if (organization.getName().equalsIgnoreCase(name)) {
                return organization;
            }
        }
        StudentOrganization organization = new StudentOrganization(name, description);
        dataStore.addStudentOrganization(organization);
        return organization;
    }

    private void seedNews(Student student, GraduateStudent graduate) {
        addNews(1, "Registration is open", "Course registration is open for demo students.",
                NewsType.GENERAL, student.getId());
        addNews(2, "Exam rooms booked", "Rooms 302 and 415 are booked for midterm exams.",
                NewsType.GENERAL, student.getId());
        addNews(3, "Research paper published",
                "A graduate research paper was published in Student Research Proceedings.",
                NewsType.RESEARCH, graduate.getId());
    }

    private void addNews(int id, String title, String content, NewsType type, String commentAuthorId) {
        for (News item : dataStore.getNews()) {
            if (item.getTitle().equalsIgnoreCase(title)) {
                return;
            }
        }
        News news = new News(id, title, content, type);
        news.addComment(new Comment(id * 10, "Demo comment for discussion.", commentAuthorId));
        dataStore.addNews(news);
    }

    private void seedSupportRequests() {
        addSupportRequest("demo-support-1", "Cannot open transcript",
                "Student cannot open generated transcript file.", RequestStatus.NEW);
        addSupportRequest("demo-support-2", "Projector does not work",
                "Room 302 projector should be fixed before exam.", RequestStatus.ACCEPTED);
        addSupportRequest("demo-support-3", "Printer toner is empty",
                "Library printer needs toner replacement.", RequestStatus.DONE);
    }

    private void addSupportRequest(String id, String title, String description, RequestStatus status) {
        for (TechSupportRequest request : dataStore.getSupportRequests()) {
            if (request.getId().equals(id)) {
                return;
            }
        }
        TechSupportRequest request = new TechSupportRequest(id, title, description);
        request.setStatus(status);
        dataStore.addSupportRequest(request);
    }

    private void seedMessages(Teacher teacher, Student student, GraduateStudent graduate) {
        addMessage(teacher.getId(), student.getId(), "Please check the updated OOP practice schedule.");
        addMessage(graduate.getId(), teacher.getId(), "I uploaded my draft diploma research paper.");
    }

    private void addMessage(String senderId, String receiverId, String text) {
        for (Message message : dataStore.getMessages()) {
            if (message.getSenderId().equals(senderId)
                    && message.getReceiverId().equals(receiverId)
                    && message.getText().equals(text)) {
                return;
            }
        }
        dataStore.addMessage(new Message(UUID.randomUUID().toString(), senderId, receiverId, text));
    }

    private User findUser(String username) {
        try {
            return dataStore.findUserByUsername(username);
        } catch (Exception e) {
            return null;
        }
    }
}
