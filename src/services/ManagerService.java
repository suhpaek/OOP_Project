package services;

import comparators.StudentGpaComparator;
import comparators.StudentNameComparator;
import data.DataStore;
import exceptions.CreditLimitExceededException;
import exceptions.TooManyFailedCoursesException;
import models.academic.RegistrationRequest;
import models.users.Manager;
import models.users.Student;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ManagerService {
    private final DataStore dataStore;
    private final CourseService courseService;

    public ManagerService() {
        this(DataStore.getInstance(), new CourseService());
    }

    public ManagerService(CourseService courseService) {
        this(DataStore.getInstance(), courseService);
    }

    public ManagerService(DataStore dataStore, CourseService courseService) {
        this.dataStore = dataStore;
        this.courseService = courseService;
    }

    public boolean isCourseRegistrationOpen() {
        return dataStore.isCourseRegistrationOpen();
    }

    public void setCourseRegistrationOpen(boolean open) throws IOException {
        dataStore.setCourseRegistrationOpen(open);
        dataStore.save();
    }

    public void approveRegistration(Manager manager, RegistrationRequest request) throws CreditLimitExceededException, TooManyFailedCoursesException {
        if (manager == null || request == null || request.isProcessed()) return;
        courseService.enrollStudent(request.getCourse(), request.getStudent());
        request.markApproved(manager);
        saveData();
    }

    public void rejectRegistration(Manager manager, RegistrationRequest request) {
        if (manager == null || request == null || request.isProcessed()) return;
        request.markRejected(manager);
        saveData();
    }

    public List<Student> viewStudentsByName(Manager manager) {
        List<Student> students = new ArrayList<>(dataStore.getStudents());
        students.sort(new StudentNameComparator());
        return students;
    }

    public List<Student> viewStudentsByGpa(Manager manager) {
        List<Student> students = new ArrayList<>(dataStore.getStudents());
        students.sort(new StudentGpaComparator());
        return students;
    }

    public String createAcademicReport(Manager manager) {
        StringBuilder builder = new StringBuilder("Academic performance report:\n");
        for (Student student : viewStudentsByGpa(manager)) {
            builder.append(student.getFullName())
                    .append(" - GPA: ")
                    .append(String.format("%.2f", student.getTranscript().calculateGpa()))
                    .append('\n');
        }
        return builder.toString();
    }

    private void saveData() {
        try {
            dataStore.save();
        } catch (IOException e) {
            throw new IllegalStateException("Could not save registration changes.", e);
        }
    }
}
