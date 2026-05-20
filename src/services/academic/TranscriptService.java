package services.academic;

import models.academic.Course;
import models.academic.Mark;
import models.academic.Transcript;
import models.users.Student;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TranscriptService {
    public double calculateGpa(Transcript transcript) { return transcript == null ? 0 : transcript.calculateGpa(); }

    public List<Course> getPassedCourses(Transcript transcript) { return transcript == null ? new ArrayList<>() : transcript.getPassedCourses(); }
    public List<Course> getFailedCourses(Transcript transcript) { return transcript == null ? new ArrayList<>() : transcript.getFailedCourses(); }

    public String buildSummary(Transcript transcript) { return transcript == null ? "GPA: 0.0" : transcript.getTranscriptSummary(); }

    public String buildStudentTranscript(Student student) {
        if (student == null) return "No student selected.";
        StringBuilder builder = new StringBuilder();
        builder.append("===== TRANSCRIPT =====\n");
        builder.append("Student: ").append(student.getFullName()).append('\n');
        builder.append("ID: ").append(student.getId()).append('\n');
        builder.append("Degree: ").append(student.getDegree()).append('\n');
        builder.append("Educational program: Information Systems\n\n");

        Map<Integer, List<Map.Entry<Course, Mark>>> semesters = groupBySemester(student);
        int totalCredits = 0;
        int totalEcts = 0;
        for (Map.Entry<Integer, List<Map.Entry<Course, Mark>>> semester : semesters.entrySet()) {
            builder.append(formatSemester(semester.getKey(), semester.getValue()));
            for (Map.Entry<Course, Mark> entry : semester.getValue()) {
                totalCredits += entry.getKey().getCredits();
                totalEcts += toEcts(entry.getKey());
            }
        }
        builder.append(String.format("Total credits: %d. Total ECTS: %d. GPA %.2f.%n",
                totalCredits, totalEcts, student.getTranscript().calculateGpa()));
        return builder.toString();
    }

    private Map<Integer, List<Map.Entry<Course, Mark>>> groupBySemester(Student student) {
        Map<Integer, List<Map.Entry<Course, Mark>>> semesters = new java.util.TreeMap<>();
        int fallback = 1;
        for (Map.Entry<Course, Mark> entry : student.getTranscript().getCourseMarks().entrySet()) {
            Course course = entry.getKey();
            int year = Math.max(1, course.getIntendedYearOfStudy());
            int semester = year * 2 - (fallback++ % 2 == 0 ? 0 : 1);
            semesters.computeIfAbsent(semester, ignored -> new ArrayList<>()).add(entry);
        }
        return semesters;
    }

    private String formatSemester(int semesterNumber, List<Map.Entry<Course, Mark>> records) {
        StringBuilder builder = new StringBuilder();
        String term = semesterNumber % 2 == 1 ? "Fall" : "Spring";
        int studyYear = (semesterNumber + 1) / 2;
        builder.append(String.format("Year %d, Semester %d (%s)%n", studyYear, semesterNumber, term));
        builder.append(String.format("%-12s %-35s %-8s %-8s %-12s %-6s %-12s%n",
                "Code", "Course", "Credit", "ECTS", "Digit grade", "GPA", "Letter"));
        int credits = 0;
        int ects = 0;
        double points = 0;
        for (Map.Entry<Course, Mark> entry : records) {
            Course course = entry.getKey();
            Mark mark = entry.getValue();
            builder.append(String.format("%-12s %-35s %-8d %-8d %-12.2f %-6.2f %-12s%n",
                    course.getCode(),
                    course.getName(),
                    course.getCredits(),
                    toEcts(course),
                    mark.getTotal(),
                    toGpa(mark),
                    mark.getLetterGrade()));
            credits += course.getCredits();
            ects += toEcts(course);
            points += toGpa(mark) * course.getCredits();
        }
        double gpa = credits == 0 ? 0 : points / credits;
        builder.append(String.format("Took %d credits. Took %d ECTS. GPA %.2f.%n%n", credits, ects, gpa));
        return builder.toString();
    }

    private int toEcts(Course course) {
        return course.getCredits() <= 2 ? 4 : course.getCredits() + 2;
    }

    private double toGpa(Mark mark) {
        double total = mark.getTotal();
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

    public String generateTranscriptFile(Student student) throws IOException {
        if (student == null) throw new IllegalArgumentException("No student selected.");
        String fileName = "transcript_" + student.getUsername() + ".txt";
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(buildStudentTranscript(student));
        }
        return fileName;
    }

    public void addRecord(Transcript transcript, Course course, Mark mark) {
        if (transcript != null && course != null && mark != null) transcript.addRecord(course, mark);
    }
}
