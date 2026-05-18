package academic;

import users.Student;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Transcript implements Serializable {
    private Student student;
    private Map<Course, Mark> courseMarks;

    public Transcript() {
        this.courseMarks = new HashMap<>();
    }

    public Transcript(Student student) {
        this();
        this.student = student;
    }

    public Student getStudent() {
        return student;
    }

    public Map<Course, Mark> getCourseMarks() {
        return Collections.unmodifiableMap(courseMarks);
    }

    public void addRecord(Course course, Mark mark) {
        courseMarks.put(course, mark);
    }

    public Mark getMarkByCourse(Course course) {
        return courseMarks.get(course);
    }

    public double calculateGpa() {
        if (courseMarks.isEmpty()) return 0.0;

        double totalPoints = 0.0;
        int totalCredits = 0;

        for (Map.Entry<Course, Mark> entry : courseMarks.entrySet()) {
            Course course = entry.getKey();
            Mark mark = entry.getValue();

            totalPoints += convertToGpa(mark.getTotal()) * course.getCredits();
            totalCredits += course.getCredits();
        }

        return totalCredits == 0 ? 0.0 : totalPoints / totalCredits;
    }

    private double convertToGpa(double total) {
        if (total >= 95) return 4.0;
        if (total >= 90) return 3.67;
        if (total >= 85) return 3.33;
        if (total >= 80) return 3.0;
        if (total >= 75) return 2.67;
        if (total >= 70) return 2.33;
        if (total >= 65) return 2.0;
        if (total >= 60) return 1.67;
        if (total >= 55) return 1.33;
        if (total >= 50) return 1.0;
        return 0.0;
    }

    public void printTranscript() {
        System.out.println(getTranscriptSummary());
    }

    public String getTranscriptSummary() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<Course, Mark> entry : courseMarks.entrySet()) {
            builder.append(entry.getKey())
                    .append(" -> ")
                    .append(entry.getValue())
                    .append('\n');
        }
        builder.append("GPA: ").append(calculateGpa());
        return builder.toString();
    }

    public List<Course> getPassedCourses() {
        return courseMarks.entrySet().stream()
                .filter(entry -> entry.getValue().isPassed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public List<Course> getFailedCourses() {
        return courseMarks.entrySet().stream()
                .filter(entry -> !entry.getValue().isPassed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "Transcript{" +
                "student=" + student +
                ", gpa=" + calculateGpa() +
                '}';
    }
}
