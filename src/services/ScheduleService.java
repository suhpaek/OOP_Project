package services;

import models.academic.Lesson;
import models.academic.Schedule;

public class ScheduleService {
    public void addLesson(Schedule schedule, Lesson lesson) { if (schedule != null) schedule.addLesson(lesson); }
    public void removeLesson(Schedule schedule, Lesson lesson) { if (schedule != null) schedule.removeLesson(lesson); }
}
