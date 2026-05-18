package users;

import academic.Course;
import academic.Mark;
import academic.Transcript;
import enums.Degree;
import enums.School;
import exceptions.CreditLimitExceededException;
import exceptions.TooManyFailedCoursesException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Student extends User {
    private List<Course> registeredCourses;
    private Transcript transcript;
    private Degree degree;
    private School school;
    private int failedCoursesCount;
    private final List<String> studentOrganizations = new ArrayList<>();
    private String headedOrganization;

    public Student() {
        super();
        this.registeredCourses = new ArrayList<>();
        this.transcript = new Transcript(this);
        this.degree = Degree.Bachelor;
    }

    public Student(String username, String password, String firstName, String lastName, String email) {
        super(username, password, firstName, lastName, email);
        this.registeredCourses = new ArrayList<>();
        this.transcript = new Transcript(this);
        this.degree = Degree.Bachelor;
    }

    public List<Course> getRegisteredCourses() {
        return Collections.unmodifiableList(registeredCourses);
    }

    public Transcript getTranscript() {
        return transcript;
    }

    public Degree getDegree() {
        return degree;
    }

    void setDegreeByAdmin(Degree degree) {
        this.degree = degree;
    }

    public School getSchool() {
        return school;
    }

    void setSchoolByAdmin(School school) {
        this.school = school;
    }

    public int getFailedCoursesCount() {
        return failedCoursesCount;
    }

    public int getCurrentCredits() {
        int total = 0;
        for (Course course : registeredCourses) {
            total += course.getCredits();
        }
        return total;
    }

    public void registerForCourse(Course course) throws CreditLimitExceededException, TooManyFailedCoursesException {
        if (course == null) {
            throw new IllegalArgumentException("Course must not be null");
        }
        if (registeredCourses.contains(course)) {
            System.out.println("Student is already registered for this course.");
            return;
        }

        if (failedCoursesCount >= 3) {
            throw new TooManyFailedCoursesException(getId(), failedCoursesCount);
        }

        int attemptedCredits = getCurrentCredits() + course.getCredits();
        if (attemptedCredits > 21) {
            throw new CreditLimitExceededException(attemptedCredits);
        }

        registeredCourses.add(course);
        course.enrollStudent(this);
    }

    public void dropCourse(Course course) {
        if (registeredCourses.remove(course)) {
            course.dropStudent(this);
        }
    }

    public void addMarkToTranscript(Course course, Mark mark) {
        Mark previousMark = transcript.getMarkByCourse(course);
        transcript.addRecord(course, mark);

        if (previousMark != null && !previousMark.isPassed() && mark.isPassed()) {
            failedCoursesCount--;
        } else if ((previousMark == null || previousMark.isPassed()) && !mark.isPassed()) {
            failedCoursesCount++;
        }
    }

    public String viewMarks() {
        StringBuilder builder = new StringBuilder();
        for (Course course : registeredCourses) {
            Mark mark = course.getMark(this);
            builder.append(course.getName())
                    .append(": ")
                    .append(mark)
                    .append('\n');
        }
        String output = builder.toString().trim();
        System.out.println(output);
        return output;
    }

    public String viewTranscript() {
        String summary = transcript.getTranscriptSummary();
        System.out.println(summary);
        return summary;
    }

    public String viewCourses() {
        StringBuilder builder = new StringBuilder();
        for (Course course : registeredCourses) {
            builder.append(course).append('\n');
        }
        String output = builder.toString().trim();
        System.out.println(output);
        return output;
    }

    public Teacher viewTeacherInfo(Course course, boolean lectureTeacher) {
        return lectureTeacher ? course.getLectureTeacher() : course.getPracticeTeacher();
    }

    public void rateTeacher(Teacher teacher, int rating) {
        teacher.addRating(rating);
    }

    public void joinOrganization(String organizationName) {
        if (!studentOrganizations.contains(organizationName)) {
            studentOrganizations.add(organizationName);
        }
    }

    public void leadOrganization(String organizationName) {
        joinOrganization(organizationName);
        this.headedOrganization = organizationName;
    }

    public List<String> getStudentOrganizations() {
        return Collections.unmodifiableList(studentOrganizations);
    }

    public String getHeadedOrganization() {
        return headedOrganization;
    }

    @Override
    public String toString() {
        return "Student{" +
                "id='" + getId() + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", degree=" + degree +
                ", school=" + school +
                ", currentCredits=" + getCurrentCredits() +
                '}';
    }
}
