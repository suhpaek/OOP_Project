package models.academic;

import enums.LessonType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

public class AttendanceRecord implements Serializable {
    private String studentId;
    private String courseCode;
    private LessonType lessonType;
    private LocalDate date;
    private boolean present;

    public AttendanceRecord(String studentId, String courseCode, LessonType lessonType, LocalDate date, boolean present) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.lessonType = lessonType;
        this.date = date;
        this.present = present;
    }

    public String getStudentId() { return studentId; }
    public String getCourseCode() { return courseCode; }
    public LessonType getLessonType() { return lessonType; }
    public LocalDate getDate() { return date; }
    public boolean isPresent() { return present; }
    public void setPresent(boolean present) { this.present = present; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AttendanceRecord)) return false;
        AttendanceRecord that = (AttendanceRecord) o;
        return Objects.equals(studentId, that.studentId)
                && Objects.equals(courseCode, that.courseCode)
                && lessonType == that.lessonType
                && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, courseCode, lessonType, date);
    }
}
