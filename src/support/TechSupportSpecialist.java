package support;

import enums.RequestStatus;

import java.util.ArrayList;
import java.util.List;

public class TechSupportSpecialist {
    private String id;
    private String fullName;
    private List<TechSupportRequest> requests;

    public TechSupportSpecialist(String id, String fullName) {
        this.id = id;
        this.fullName = fullName;
        this.requests = new ArrayList<>();
    }

    public List<TechSupportRequest> viewNewRequests() {
        return requests;
    }

    public void acceptRequest(TechSupportRequest request) {
        request.setStatus(RequestStatus.ACCEPTED);
    }

    public void rejectRequest(TechSupportRequest request) {
        request.setStatus(RequestStatus.DECLINED);
    }

    public void completeRequest(TechSupportRequest request) {
        request.setStatus(RequestStatus.ACCEPTED);
    }

    public void addRequest(TechSupportRequest request) {
        requests.add(request);
    }
}

