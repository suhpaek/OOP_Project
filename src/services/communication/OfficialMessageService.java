package services.communication;

import data.UniversityDataStore;
import models.communication.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OfficialMessageService {
    private static final String SYSTEM_ID = "system";
    private final UniversityDataStore dataStore;

    public OfficialMessageService() {
        this(UniversityDataStore.getInstance());
    }

    public OfficialMessageService(UniversityDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void publish(String text) {
        if (text == null || text.isBlank()) return;
        dataStore.addMessage(new Message(UUID.randomUUID().toString(), SYSTEM_ID, SYSTEM_ID, text));
        saveData();
    }

    public List<String> getOfficialMessages() {
        List<String> rows = new ArrayList<>();
        for (Message message : dataStore.getMessages()) {
            if (SYSTEM_ID.equals(message.getSenderId())) {
                rows.add(message.getSentAt() + " | " + message.getText());
            }
        }
        return rows;
    }

    private void saveData() {
        try {
            dataStore.save();
        } catch (IOException e) {
            throw new IllegalStateException("Could not save official message.", e);
        }
    }
}
