package services;

import academic.Course;
import academic.Mark;
import academic.Transcript;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TranscriptService {
    public double calculateGpa(Transcript transcript) { return transcript == null ? 0 : transcript.calculateGpa(); }

    public List<Course> getPassedCourses(Transcript transcript) { return transcript == null ? new ArrayList<>() : transcript.getPassedCourses(); }
    public List<Course> getFailedCourses(Transcript transcript) { return transcript == null ? new ArrayList<>() : transcript.getFailedCourses(); }

    public String buildSummary(Transcript transcript) { return transcript == null ? "GPA: 0.0" : transcript.getTranscriptSummary(); }

    public void addRecord(Transcript transcript, Course course, Mark mark) {
        if (transcript != null && course != null && mark != null) transcript.addRecord(course, mark);
    }
}
