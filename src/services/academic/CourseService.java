package services.academic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import data.UniversityDataStore;
import enums.CourseType;
import enums.School;
import enums.LessonType;
import exceptions.CreditLimitExceededException;
import exceptions.TooManyFailedCoursesException;
import models.academic.Course;
import models.academic.Lesson;
import models.academic.RegistrationRequest;
import models.users.Student;
import models.users.Teacher;
import models.users.User;
import services.communication.OfficialMessageService;

public class CourseService {

    private final UniversityDataStore dataStore;
    private final ScheduleService scheduleService;

    public CourseService() {
        this(UniversityDataStore.getInstance(), new ScheduleService());
    }

    public CourseService(UniversityDataStore dataStore) {
        this(dataStore, new ScheduleService(dataStore, new OfficialMessageService(dataStore)));
    }

    public CourseService(UniversityDataStore dataStore, ScheduleService scheduleService) {
        this.dataStore = dataStore;
        this.scheduleService = scheduleService;
    }

    public void createCourse(Course course) {
        dataStore.addCourse(course);
    }

    public Course createCourse(String code, String name, int credits, CourseType type, int yearOfStudy, School intendedSchool)
            throws IOException {
        if (code == null || code.isBlank() || name == null || name.isBlank()) {
            throw new IllegalArgumentException("Course code and name are required.");
        }
        Course course = new Course(code.trim(), name.trim(), credits, type);
        course.setIntendedYearOfStudy(yearOfStudy);
        course.setIntendedSchool(intendedSchool);
        dataStore.addCourse(course);
        dataStore.save();
        return course;
    }

    public void enrollStudent(Course course, Student student) throws CreditLimitExceededException, TooManyFailedCoursesException {
        if (course != null && student != null) {
            student.registerForCourse(course);
        }
    }

    public List<Course> getAvailableCourses(Student student) {
        List<Course> result = new ArrayList<>();
        for (Course course : dataStore.getCourses()) {
            if (student == null || !student.getRegisteredCourses().contains(course)) {
                if (student != null) {
                    School courseSchool = course.getIntendedSchool();
                    School studentSchool = student.getSchool();
                    if (courseSchool != null && studentSchool != null && courseSchool != studentSchool) {
                        // Special rule: SITE students can take some MAJOR courses from SEOGI as free electives
                        if (studentSchool == School.SITE && course.getCourseType() == CourseType.MAJOR && courseSchool == School.SEOGI) {
                            result.add(course);
                        }
                        // otherwise skip course intended for another school
                        continue;
                    }
                }
                result.add(course);
            }
        }
        return result;
    }

    public List<Course> getRegisteredCourses(Student student) {
        if (student == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(student.getRegisteredCourses());
    }

    public List<Course> getAssignedCourses(Teacher teacher) {
        if (teacher == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(teacher.getAssignedCourses());
    }

    public RegistrationRequest createRegistrationRequest(Student student, String courseCode) throws IOException {
        if (!dataStore.isCourseRegistrationOpen()) {
            throw new IllegalStateException("Course registration is closed.");
        }
        Course course = findCourseByCode(courseCode);
        if (course == null) {
            throw new IllegalArgumentException("Course not found.");
        }
        RegistrationRequest request = new RegistrationRequest(student, course);
        dataStore.addRegistrationRequest(request);
        dataStore.save();
        return request;
    }

    public Course findCourseByCode(String code) {
        if (code == null) {
            return null;
        }
        for (Course course : dataStore.getCourses()) {
            if (course.getCode().equalsIgnoreCase(code.trim())) {
                return course;
            }
        }
        return null;
    }

    public String getTeacherInfo(Student student, String courseCode) {
        Course course = findCourseByCode(courseCode);
        if (course == null) {
            throw new IllegalArgumentException("Course not found.");
        }
        if (student != null && !student.getRegisteredCourses().contains(course)) {
            throw new IllegalArgumentException("Student is not registered for this course.");
        }

        return "Course: " + course + "\n"
                + "Lecture teacher: " + formatTeacher(course.getLectureTeacher()) + "\n"
                + "Practice teacher: " + formatTeacher(course.getPracticeTeacher());
    }

    private String formatTeacher(Teacher teacher) {
        if (teacher == null) {
            return "not assigned";
        }
        String degree = teacher.getDegree() == null ? "N/A" : teacher.getDegree().name();
        return teacher.getFullName()
                + " | " + teacher.getEmail()
                + " | degree: " + degree
                + " | rating " + String.format("%.2f", teacher.getAverageRating());
    }

    public void dropStudent(Course course, Student student) {
        if (student != null) {
            student.dropCourse(course);

        }
    }

    public void setLectureTeacher(Course course, Teacher teacher) {
        if (course != null) {
            course.setLectureTeacher(teacher);

        }
        if (teacher != null) {
            teacher.assignCourse(course);

        }
    }

    public void setPracticeTeacher(Course course, Teacher teacher) {
        if (course != null) {
            course.setPracticeTeacher(teacher);

        }
        if (teacher != null) {
            teacher.assignCourse(course);

        }
    }

    public void assignTeacher(String courseCode, String teacherUsername, boolean lecture) throws Exception {
        Course course = findCourseByCode(courseCode);
        User user = dataStore.findUserByUsername(teacherUsername);
        if (course == null || !(user instanceof Teacher)) {
            throw new IllegalArgumentException("Course or teacher not found.");
        }
        if (lecture) {
            setLectureTeacher(course, (Teacher) user);
        } else {
            setPracticeTeacher(course, (Teacher) user);
        }
        dataStore.save();
    }

    public void addLesson(Course course, Lesson lesson) {
        if (course != null) {
            course.addLesson(lesson);

        }
    }

    public Lesson addLesson(String courseCode, LessonType type, String day, String time,
            String room, String teacherUsername) throws Exception {
        Course course = findCourseByCode(courseCode);
        User user = dataStore.findUserByUsername(teacherUsername);
        if (course == null || !(user instanceof Teacher)) {
            throw new IllegalArgumentException("Course or teacher not found.");
        }
        scheduleService.ensureSlotIsFree(courseCode, day, time, room, (Teacher) user);
        Lesson lesson = new Lesson(type, day, time, room, (Teacher) user);
        addLesson(course, lesson);
        assignTeacher(courseCode, teacherUsername, type == LessonType.LECTURE);
        scheduleService.publishRoomBookingMessage(courseCode, day, time, room);
        dataStore.save();
        return lesson;
    }

    public List<String> getSchedule(Student student) {
        List<String> rows = new ArrayList<>();
        if (student == null) {
            return rows;
        }

        class Entry {

            String day, time, room, code, type, teacher;

            Entry(String day, String time, String room, String code, String type, String teacher) {
                this.day = day;
                this.time = time;
                this.room = room;
                this.code = code;
                this.type = type;
                this.teacher = teacher;
            }
        }

        List<Entry> entries = new ArrayList<>();
        List<Course> courses = getRegisteredCourses(student);
        for (Course course : courses) {
            for (Lesson lesson : course.getLessons()) {
                String teacherName = lesson.getInstructor() == null ? "TBA" : lesson.getInstructor().getFullName();
                entries.add(new Entry(lesson.getDay(), lesson.getTime(), lesson.getRoom(), course.getCode(), String.valueOf(lesson.getType()), teacherName));
            }
        }

        final List<String> dayOrder = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");

        entries.sort(new Comparator<Entry>() {
            @Override
            public int compare(Entry a, Entry b) {
                int da = dayOrder.indexOf(a.day);
                int db = dayOrder.indexOf(b.day);
                da = da == -1 ? Integer.MAX_VALUE : da;
                db = db == -1 ? Integer.MAX_VALUE : db;
                if (da != db) {
                    return Integer.compare(da, db);
                }
                int ta = parseTime(a.time);
                int tb = parseTime(b.time);
                if (ta != tb) {
                    return Integer.compare(ta, tb);
                }
                return a.code.compareToIgnoreCase(b.code);
            }

            private int parseTime(String time) {
                if (time == null) {
                    return 0;
                }
                try {
                    String[] parts = time.split(":");
                    int h = Integer.parseInt(parts[0].trim());
                    int m = parts.length > 1 ? Integer.parseInt(parts[1].trim()) : 0;
                    return h * 60 + m;
                } catch (Exception e) {
                    return 0;
                }
            }
        });

        for (Entry e : entries) {
            rows.add(String.format("%s | %s | %s | %s | %s | %s", e.day, e.time, e.room, e.code, e.type, e.teacher));
        }
        return rows;
    }

    public List<Student> getStudentsForTeacherCourse(Teacher teacher, String courseCode) {
        Course course = findCourseByCode(courseCode);
        if (course == null) {
            throw new IllegalArgumentException("Course not found.");
        }
        if (teacher == null) {
            return new ArrayList<>();
        }
        if (!teacher.getAssignedCourses().contains(course)) {
            throw new IllegalArgumentException("Teacher is not assigned to this course.");
        }
        return new ArrayList<>(course.getEnrolledStudents());
    }
}
