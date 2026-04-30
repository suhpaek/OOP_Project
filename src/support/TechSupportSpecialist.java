package support;

import enums.RequestStatus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TechSupportSpecialist implements Serializable {
    private String id;
    private String fullName;
    private List<TechSupportRequest> requests;

    public TechSupportSpecialist(String id, String fullName) {
        this.id = id;
        this.fullName = fullName;
        this.requests = new ArrayList<>();
    }

    public List<TechSupportRequest> viewNewRequests() {
        List<TechSupportRequest> newRequests = new ArrayList<>();
        for (TechSupportRequest request : requests) {
            if (request.getStatus() == RequestStatus.NEW) {
                request.markViewed();
                newRequests.add(request);
            }
        }
        return newRequests;
    }

    public void acceptRequest(TechSupportRequest request) {
        request.setStatus(RequestStatus.ACCEPTED);
    }

    public void rejectRequest(TechSupportRequest request) {
        request.setStatus(RequestStatus.REJECTED);
    }

    public void completeRequest(TechSupportRequest request) {
        request.setStatus(RequestStatus.DONE);
    }

    public void addRequest(TechSupportRequest request) {
        requests.add(request);
    }
}
