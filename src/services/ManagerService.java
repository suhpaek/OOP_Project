package services;

import data.DataStore;
import models.academic.RegistrationRequest;
import exceptions.CreditLimitExceededException;
import exceptions.TooManyFailedCoursesException;
import models.users.Manager;

import java.io.IOException;

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

    private void saveData() {
        try {
            dataStore.save();
        } catch (IOException e) {
            throw new IllegalStateException("Could not save registration changes.", e);
        }
    }
}
