package services.academic;

import data.UniversityDataStore;
import models.academic.Course;
import models.academic.Lesson;
import models.academic.Schedule;
import models.users.Teacher;
import services.communication.OfficialMessageService;

public class ScheduleService {
    private final UniversityDataStore dataStore;
    private final OfficialMessageService officialMessageService;

    public ScheduleService() {
        this(UniversityDataStore.getInstance(), new OfficialMessageService());
    }

    public ScheduleService(UniversityDataStore dataStore, OfficialMessageService officialMessageService) {
        this.dataStore = dataStore;
        this.officialMessageService = officialMessageService;
    }

    public void addLesson(Schedule schedule, Lesson lesson) { if (schedule != null) schedule.addLesson(lesson); }
    public void removeLesson(Schedule schedule, Lesson lesson) { if (schedule != null) schedule.removeLesson(lesson); }

    public void ensureSlotIsFree(String courseCode, String day, String time, String room, Teacher teacher) {
        for (Course existingCourse : dataStore.getCourses()) {
            for (Lesson lesson : existingCourse.getLessons()) {
                boolean sameTime = equalsIgnoreCase(lesson.getDay(), day) && equalsIgnoreCase(lesson.getTime(), time);
                if (!sameTime) continue;
                if (equalsIgnoreCase(lesson.getRoom(), room)) {
                    throw new IllegalArgumentException("Room is already booked for "
                            + existingCourse.getCode() + " at this time.");
                }
                if (teacher != null && teacher.equals(lesson.getInstructor())) {
                    throw new IllegalArgumentException("Teacher is already assigned to "
                            + existingCourse.getCode() + " at this time.");
                }
            }
        }
    }

    public void publishRoomBookingMessage(String courseCode, String day, String time, String room) {
        officialMessageService.publish("Room booked: " + room + " for " + courseCode + " on " + day + " at " + time);
    }

    private boolean equalsIgnoreCase(String first, String second) {
        return first != null && second != null && first.equalsIgnoreCase(second);
    }
}
