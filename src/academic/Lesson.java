package academic;

import enums.LessonType;
import users.Teacher;

import java.io.Serializable;

public class Lesson implements Serializable {
    private LessonType type;
    private String day;
    private String time;
    private String room;
    private Teacher instructor;

    public Lesson() {
    }

    public Lesson(LessonType type, String day, String time, String room, Teacher instructor) {
        this.type = type;
        this.day = day;
        this.time = time;
        this.room = room;
        this.instructor = instructor;
    }

    public LessonType getType() {
        return type;
    }

    public void setType(LessonType type) {
        this.type = type;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Teacher getInstructor() {
        return instructor;
    }

    public void setInstructor(Teacher instructor) {
        this.instructor = instructor;
    }

    @Override
    public String toString() {
        return "Lesson{" +
                "type=" + type +
                ", day='" + day + '\'' +
                ", time='" + time + '\'' +
                ", room='" + room + '\'' +
                ", instructor=" + instructor +
                '}';
    }
}
