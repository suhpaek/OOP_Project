package services;

import academic.RegistrationRequest;
import exceptions.CreditLimitExceededException;
import exceptions.TooManyFailedCoursesException;
import users.Manager;

public class ManagerService {
    private final CourseService courseService;
    public ManagerService() { this(new CourseService()); }
    public ManagerService(CourseService courseService) { this.courseService = courseService; }

    public void approveRegistration(Manager manager, RegistrationRequest request) throws CreditLimitExceededException, TooManyFailedCoursesException {
        if (manager == null || request == null || request.isProcessed()) return;
        courseService.enrollStudent(request.getCourse(), request.getStudent());
        request.markApproved(manager);
    }

    public void rejectRegistration(Manager manager, RegistrationRequest request) {
        if (manager == null || request == null || request.isProcessed()) return;
        request.markRejected(manager);
    }
}
