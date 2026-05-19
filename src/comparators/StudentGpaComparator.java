package comparators;

import models.users.Student;

import java.util.Comparator;

public class StudentGpaComparator implements Comparator<Student> {
    @Override
    public int compare(Student first, Student second) {
        return Double.compare(
                second.getTranscript().calculateGpa(),
                first.getTranscript().calculateGpa()
        );
    }
}
