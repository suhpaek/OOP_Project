package services.support;

import enums.RequestStatus;
import data.UniversityDataStore;
import models.support.TechSupportRequest;
import models.users.TechSupportSpecialist;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SupportRequestService {
    private final UniversityDataStore dataStore;

    public SupportRequestService() {
        this(UniversityDataStore.getInstance());
    }

    public SupportRequestService(UniversityDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public TechSupportRequest createRequest(String title, String description) {
        if (title == null || title.isBlank() || description == null || description.isBlank()) {
            throw new IllegalArgumentException("Title and description are required.");
        }
        TechSupportRequest request = new TechSupportRequest(UUID.randomUUID().toString(), title, description);
        dataStore.addSupportRequest(request);
        saveData();
        return request;
    }

    public void assignRequest(TechSupportSpecialist specialist, TechSupportRequest request) {
        if (specialist == null || request == null) {
            throw new IllegalArgumentException("Specialist and request are required.");
        }
    }

    public List<TechSupportRequest> viewNewRequests(TechSupportSpecialist specialist) {
        List<TechSupportRequest> result = new ArrayList<>();
        if (specialist == null) {
            return result;
        }
        for (TechSupportRequest request : dataStore.getSupportRequests()) {
            if (request.getStatus() == RequestStatus.NEW) {
                request.markViewed();
                result.add(request);
            }
        }
        saveData();
        return result;
    }

    public List<TechSupportRequest> getAllRequests() {
        return dataStore.getSupportRequests();
    }

    public void updateStatus(String requestId, RequestStatus status) {
        for (TechSupportRequest request : dataStore.getSupportRequests()) {
            if (request.getId().equals(requestId)) {
                request.setStatus(status);
                saveData();
                return;
            }
        }
        throw new IllegalArgumentException("Support request not found.");
    }

    public void accept(TechSupportRequest request) { updateStatus(request, RequestStatus.ACCEPTED); }
    public void reject(TechSupportRequest request) { updateStatus(request, RequestStatus.REJECTED); }
    public void complete(TechSupportRequest request) { updateStatus(request, RequestStatus.DONE); }

    private void updateStatus(TechSupportRequest request, RequestStatus status) {
        if (request != null) {
            request.setStatus(status);
            saveData();
        }
    }

    private void saveData() {
        try {
            dataStore.save();
        } catch (Exception e) {
            throw new IllegalStateException("Could not save support request changes.", e);
        }
    }
}
