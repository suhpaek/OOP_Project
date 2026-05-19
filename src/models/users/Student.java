package models.users;

import models.academic.Course;
import models.academic.Mark;
import models.academic.Transcript;
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

    public void setDegree(Degree degree) {
        setDegreeByAdmin(degree);
    }

    public School getSchool() {
        return school;
    }

    void setSchoolByAdmin(School school) {
        this.school = school;
    }

    public void setSchool(School school) {
        setSchoolByAdmin(school);
    }

    public int getFailedCoursesCount() {
        return failedCoursesCount;
    }

    public void setFailedCoursesCount(int failedCoursesCount) {
        this.failedCoursesCount = Math.max(0, failedCoursesCount);
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

    public void joinOrganization(String organizationName) {
        if (!studentOrganizations.contains(organizationName)) {
            studentOrganizations.add(organizationName);
        }
    }

    public void leaveOrganization(String organizationName) {
        studentOrganizations.remove(organizationName);
        if (organizationName != null && organizationName.equals(headedOrganization)) {
            headedOrganization = null;
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
