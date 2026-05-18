package exceptions;

public class TooManyFailedCoursesException extends UniversityException {
    public TooManyFailedCoursesException(String studentId, int failedCoursesCount) {
        super("Student '" + studentId + "' has failed " + failedCoursesCount + " courses and cannot register for new ones.");
    }
}
