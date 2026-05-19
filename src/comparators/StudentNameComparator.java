package comparators;

import models.users.Student;

import java.util.Comparator;

public class StudentNameComparator implements Comparator<Student> {
    @Override
    public int compare(Student first, Student second) {
        return first.getFullName().compareToIgnoreCase(second.getFullName());
    }
}
