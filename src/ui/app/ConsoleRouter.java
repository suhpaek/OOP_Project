package ui.app;

import ui.users.*;
import models.users.Admin;
import models.users.GraduateStudent;
import models.users.Manager;
import models.users.Student;
import models.users.Teacher;
import models.users.TechSupportSpecialist;
import models.users.User;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.function.BiConsumer;

public class ConsoleRouter {
    private final Map<Class<?>, BiConsumer<Scanner, User>> routes = new LinkedHashMap<>();

    public ConsoleRouter() {
        register(Admin.class, (scanner, user) -> new AdminConsole(scanner, (Admin) user).start());
        register(Manager.class, (scanner, user) -> new ManagerConsole(scanner, (Manager) user).start());
        register(Teacher.class, (scanner, user) -> new TeacherConsole(scanner, (Teacher) user).start());
        register(TechSupportSpecialist.class, (scanner, user) -> new TechSupportConsole(scanner, (TechSupportSpecialist) user).start());
        register(GraduateStudent.class, (scanner, user) -> new GraduateStudentConsole(scanner, (GraduateStudent) user).start());
        register(Student.class, (scanner, user) -> new StudentConsole(scanner, (Student) user).start());
    }

    public <T extends User> void register(Class<T> type, BiConsumer<Scanner, User> consoleFactory) {
        routes.put(type, consoleFactory);
    }

    public void route(Scanner scanner, User user) {
        for (Map.Entry<Class<?>, BiConsumer<Scanner, User>> entry : routes.entrySet()) {
            if (entry.getKey().isInstance(user)) {
                entry.getValue().accept(scanner, user);
                return;
            }
        }
        System.out.println("Console menu for " + user.getClass().getSimpleName() + " is not implemented yet.");
    }
}
