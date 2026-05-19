package models.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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

    public void addRequest(TechSupportRequest request) {
        requests.add(request);
    }

    public List<TechSupportRequest> getRequests() {
        return Collections.unmodifiableList(requests);
    }
}
