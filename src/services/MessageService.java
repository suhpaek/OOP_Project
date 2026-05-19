package services;

import models.communication.Message;
import data.DataStore;
import models.users.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageService {
    private final DataStore dataStore;
    public MessageService() { this(DataStore.getInstance()); }
    public MessageService(DataStore dataStore) { this.dataStore = dataStore; }

    public Message sendMessage(Employee sender, Employee receiver, String text) {
        if (sender == null || receiver == null || text == null || text.isBlank()) return null;
        Message message = new Message(UUID.randomUUID().toString(), sender.getId(), receiver.getId(), text);
        dataStore.addMessage(message);
        return message;
    }

    public List<Message> getReceivedMessages(Employee employee) {
        List<Message> result = new ArrayList<>();
        if (employee == null) return result;
        for (Message message : dataStore.getMessages()) {
            if (employee.getId().equals(message.getReceiverId())) result.add(message);
        }
        return result;
    }
}
