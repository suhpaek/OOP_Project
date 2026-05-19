package services;

import enums.RequestStatus;
import models.support.TechSupportRequest;
import models.support.TechSupportSpecialist;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SupportRequestService {
    public TechSupportRequest createRequest(String title, String description) {
        if (title == null || title.isBlank() || description == null || description.isBlank()) return null;
        return new TechSupportRequest(UUID.randomUUID().toString(), title, description);
    }

    public void assignRequest(TechSupportSpecialist specialist, TechSupportRequest request) {
        if (specialist != null && request != null) specialist.addRequest(request);
    }

    public List<TechSupportRequest> viewNewRequests(TechSupportSpecialist specialist) {
        List<TechSupportRequest> result = new ArrayList<>();
        if (specialist == null) return result;
        for (TechSupportRequest request : specialist.getRequests()) {
            if (request.getStatus() == RequestStatus.NEW) {
                request.markViewed();
                result.add(request);
            }
        }
        return result;
    }

    public void accept(TechSupportRequest request) { updateStatus(request, RequestStatus.ACCEPTED); }
    public void reject(TechSupportRequest request) { updateStatus(request, RequestStatus.REJECTED); }
    public void complete(TechSupportRequest request) { updateStatus(request, RequestStatus.DONE); }
    private void updateStatus(TechSupportRequest request, RequestStatus status) { if (request != null) request.setStatus(status); }
}
