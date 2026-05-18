package users;

import communication.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Employee extends User {
    private double salary;
    private final List<Message> sentMessages = new ArrayList<>();

    public Employee() {
        super();
    }

    public Employee(String username, String password, String firstName, String lastName, String email) {
        super(username, password, firstName, lastName, email);
    }

    public double getSalary() {
        return salary;
    }

    void setSalaryByAdmin(double salary) {
        this.salary = salary;
    }

    public Message sendMessage(Employee receiver, String msg1) {
        Message message = new Message(UUID.randomUUID().toString(), getId(), receiver.getId(), msg1);
        sentMessages.add(message);
        return message;
    }

    public List<Message> getSentMessages() {
        return sentMessages;
    }
}
