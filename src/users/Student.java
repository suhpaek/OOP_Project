package users;

import academic.Course;
import academic.Mark;
import academic.Transcript;
import enums.Degree;
import enums.School;
import exceptions.CreditLimitExceededException;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private List<Course> registeredCourses;
    private Transcript transcript;
    private Degree degree;
    private School school;
    private int failedCoursesCount;

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
        return registeredCourses;
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

    public void registerForCourse(Course course) throws CreditLimitExceededException {
        if (registeredCourses.contains(course)) {
            System.out.println("Student is already registered for this course.");
            return;
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

    public void rateTeacher(Teacher teacher, int rating) {
        teacher.addRating(rating);
    }

    public void addMarkToTranscript(Course course, Mark mark) {
        transcript.addRecord(course, mark);
        if (!mark.isPassed()) {
            failedCoursesCount++;
        }
    }

    public void viewMarks() {
        for (Course course : registeredCourses) {
            Mark mark = course.getMark(this);
            System.out.println(course.getName() + ": " + mark);
        }
    }

    public void viewTranscript() {
        transcript.printTranscript();
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
