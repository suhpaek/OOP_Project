package ui;

import data.DataStore;
import i18n.I18n;
import models.users.Admin;
import models.users.Manager;
import models.users.Student;
import models.users.Teacher;
import models.users.TechSupportSpecialist;
import models.users.User;
import services.AuthenticationService;
import services.DemoDataService;

import java.util.Scanner;

public class ConsoleApplication {
    private final DataStore store = DataStore.getInstance();
    private final Scanner scanner = new Scanner(System.in);
    private final AuthenticationService authenticationService = new AuthenticationService(store);

    public void run() {
        initialize();

        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    login();
                    break;
                case "0":
                    running = false;
                    saveData();
                    System.out.println(I18n.t("app.exit"));
                    break;
                default:
                    System.out.println(I18n.t("app.invalid"));
            }
        }
    }

    private void initialize() {
        try {
            store.load();
            store.ensureDefaultAdmin();
            new DemoDataService(store).seedDemoData();
            store.save();
        } catch (Exception e) {
            System.out.println("Could not initialize storage: " + e.getMessage());
        }
    }

    private void printMainMenu() {
        System.out.println();
        System.out.println("===== " + I18n.t("app.title") + " =====");
        System.out.println("Demo users: admin/admin, student/student, teacher/teacher, manager/manager, support/support");
        System.out.println("1. " + I18n.t("login.option"));
        System.out.println("0. " + I18n.t("login.exit"));
        System.out.print(I18n.t("menu.choice") + ": ");
    }

    private void login() {
        AuthConsole authConsole = new AuthConsole(scanner, authenticationService);
        User user = authConsole.login();
        if (user == null) return;

        if (user instanceof Admin) {
            new AdminConsole(scanner, (Admin) user).start();
        } else if (user instanceof Manager) {
            new ManagerConsole(scanner, (Manager) user).start();
        } else if (user instanceof Teacher) {
            new TeacherConsole(scanner, (Teacher) user).start();
        } else if (user instanceof Student) {
            new StudentConsole(scanner, (Student) user).start();
        } else if (user instanceof TechSupportSpecialist) {
            new TechSupportConsole(scanner, (TechSupportSpecialist) user).start();
        } else {
            System.out.println("Console menu for " + user.getClass().getSimpleName() + " is not implemented yet.");
        }
        logout(user);
    }

    private void logout(User user) {
        try {
            authenticationService.logout(user);
        } catch (Exception e) {
            System.out.println("Logout failed: " + e.getMessage());
        }
    }

    private void saveData() {
        try {
            store.save();
        } catch (Exception e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }
}
