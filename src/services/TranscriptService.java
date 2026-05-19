package services;

import models.academic.Course;
import models.academic.Mark;
import models.academic.Transcript;
import models.users.Student;

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
        builder.append(String.format("%-12s %-35s %-8s %-8s %-6s%n", "Code", "Course", "Credits", "Total", "Grade"));
        for (Map.Entry<Course, Mark> entry : student.getTranscript().getCourseMarks().entrySet()) {
            Course course = entry.getKey();
            Mark mark = entry.getValue();
            builder.append(String.format("%-12s %-35s %-8d %-8.2f %-6s%n",
                    course.getCode(),
                    course.getName(),
                    course.getCredits(),
                    mark.getTotal(),
                    mark.getLetterGrade()));
        }
        builder.append(String.format("GPA: %.2f", student.getTranscript().calculateGpa()));
        return builder.toString();
    }

    public void addRecord(Transcript transcript, Course course, Mark mark) {
        if (transcript != null && course != null && mark != null) transcript.addRecord(course, mark);
    }
}
