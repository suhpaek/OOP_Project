package services;

import academic.Lesson;
import academic.Schedule;

public class ScheduleService {
    public void addLesson(Schedule schedule, Lesson lesson) { if (schedule != null) schedule.addLesson(lesson); }
    public void removeLesson(Schedule schedule, Lesson lesson) { if (schedule != null) schedule.removeLesson(lesson); }
}
