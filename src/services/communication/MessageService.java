package services.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import data.UniversityDataStore;
import models.communication.Message;
import models.users.Employee;
import models.users.User;

public class MessageService {

    private final UniversityDataStore dataStore;

    public MessageService() {
        this(UniversityDataStore.getInstance());
    }

    public MessageService(UniversityDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public Message sendMessage(Employee sender, Employee receiver, String text) {
        if (sender == null || receiver == null || text == null || text.isBlank()) {
            return null;
        }
        Message message = new Message(UUID.randomUUID().toString(), sender.getId(), receiver.getId(), text);
        dataStore.addMessage(message);
        try {
            dataStore.save();
        } catch (Exception ignored) {
        }
        return message;
    }

    public Message sendMessageToUsername(Employee sender, String receiverUsername, String text) throws Exception {
        User user = dataStore.findUserByUsername(receiverUsername);
        if (!(user instanceof Employee)) {
            throw new IllegalArgumentException("Receiver is not an employee.");
        }
        Message message = sendMessage(sender, (Employee) user, text);
        if (message == null) {
            throw new IllegalArgumentException("Message text is required.");
        }
        return message;
    }

    public List<Message> getReceivedMessages(Employee employee) {
        List<Message> result = new ArrayList<>();
        if (employee == null) {
            return result;
        }
        for (Message message : dataStore.getMessages()) {
            if (employee.getId().equals(message.getReceiverId())) {
                result.add(message);
            }
        }
        return result;
    }

    public List<String[]> getInboxRows(Employee employee) {
        List<String[]> rows = new ArrayList<>();
        for (Message message : getReceivedMessages(employee)) {
            rows.add(new String[] {
                    message.getId(),
                    getUsernameOrId(message.getSenderId()),
                    getUsernameOrId(message.getReceiverId()),
                    message.getText()
            });
        }
        return rows;
    }

    private String getUsernameOrId(String userId) {
        try {
            return dataStore.findUserById(userId).getUsername();
        } catch (Exception ignored) {
            return userId;
        }
    }
}
