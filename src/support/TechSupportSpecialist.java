package support;

import data.DataStore;
import enums.RequestStatus;
import users.Employee;

import java.util.*;
import java.util.stream.Collectors;

public class TechSupportSpecialist extends Employee {

    private static final long serialVersionUID = 1L;

    private List<TechSupportRequest> assignedRequests;


    public TechSupportSpecialist(String id, String username, String password, String firstName, String lastName, String email) {
        super(username, password, firstName, lastName, email);
        this.assignedRequests = new ArrayList<>();
    }

    public void receiveRequest(TechSupportRequest request) {
        if (request != null && !assignedRequests.contains(request)) {
            assignedRequests.add(request);
            DataStore.getInstance().addTechRequest(request);
        }
    }

    public List<TechSupportRequest> viewNewRequests() {
        List<TechSupportRequest> newRequests = assignedRequests.stream()
                .filter(r -> r.getStatus() == RequestStatus.NEW)
                .collect(Collectors.toList());

        newRequests.forEach(TechSupportRequest::markViewed);
        DataStore.getInstance().logAction(getId(), "Viewed " + newRequests.size() + " new requests.");
        return newRequests;
    }


    public List<TechSupportRequest> getAllRequests() {
        return new ArrayList<>(assignedRequests);
    }

    public List<TechSupportRequest> getRequestsByStatus(RequestStatus status) {
        return assignedRequests.stream()
                .filter(r -> r.getStatus() == status)
                .collect(Collectors.toList());
    }


    public void acceptRequest(TechSupportRequest request) {
        requireOwnership(request);
        request.accept();
        DataStore.getInstance().logAction(getId(), "Accepted request: " + request.getId());
    }


    public void rejectRequest(TechSupportRequest request) {
        requireOwnership(request);
        request.reject();
        DataStore.getInstance().logAction(getId(), "Rejected request: " + request.getId());
    }

    public void startRequest(TechSupportRequest request) {
        requireOwnership(request);
        request.markInProcess();
        DataStore.getInstance().logAction(getId(), "Started request: " + request.getId());
    }
   
    public void completeRequest(TechSupportRequest request) {
        requireOwnership(request);
        request.complete();
        DataStore.getInstance().logAction(getId(), "Completed request: " + request.getId());
    }

    private void requireOwnership(TechSupportRequest request) {
        if (request == null) throw new IllegalArgumentException("Request must not be null");
        if (!assignedRequests.contains(request)) {
            throw new IllegalStateException("Request " + request.getId() + " is not assigned to this specialist.");
        }
    }

    @Override
    public String toString() {
        return String.format("TechSupportSpecialist{id='%s', name='%s', requests=%d}",
                getId(), getFullName(), assignedRequests.size());
    }
}