package ui.users;

import java.util.List;
import java.util.Scanner;

import models.research.ResearchPaper;
import models.users.GraduateStudent;
import services.research.GraduateStudentService;
import ui.app.ConsoleScreen;
import ui.features.ResearchConsole;

public class GraduateStudentConsole implements ConsoleScreen {
    private final Scanner scanner;
    private final GraduateStudent student;
    private final StudentConsole studentConsole;
    private final ResearchConsole researchConsole;
    private final GraduateStudentService graduateStudentService = new GraduateStudentService();

    public GraduateStudentConsole(Scanner scanner, GraduateStudent student) {
        this.scanner = scanner;
        this.student = student;
        this.studentConsole = new StudentConsole(scanner, student);
        this.researchConsole = new ResearchConsole(scanner, student);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    studentConsole.start();
                    break;
                case "2":
                    researchConsole.start();
                    break;
                case "3":
                    viewDiplomaProjects();
                    break;
                case "4":
                    viewSupervisor();
                    break;
                case "5":
                    syncDiplomaProjects();
                    break;
                case "0":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("===== GRADUATE STUDENT MENU =====");
        System.out.println("Welcome, " + student.getFullName());
        System.out.println("Degree: " + student.getDegree());
        System.out.println("1. Student menu");
        System.out.println("2. Research menu");
        System.out.println("3. View diploma projects");
        System.out.println("4. View research supervisor");
        System.out.println("5. Add my published papers to diploma projects");
        System.out.println("0. Logout");
        System.out.print("Choice: ");
    }

    private void viewDiplomaProjects() {
        List<ResearchPaper> papers = graduateStudentService.getDiplomaProjects(student);
        if (papers.isEmpty()) {
            System.out.println("No diploma projects yet.");
            return;
        }
        for (int i = 0; i < papers.size(); i++) {
            ResearchPaper paper = papers.get(i);
            System.out.printf("%d. %s | %s | citations: %d%n",
                    i + 1, paper.getTitle(), paper.getJournalName(), paper.getCitations());
        }
    }

    private void viewSupervisor() {
        System.out.println(graduateStudentService.getSupervisorInfo(student));
    }

    private void syncDiplomaProjects() {
        int added = graduateStudentService.syncPublishedPapersAsDiplomaProjects(student);
        System.out.println("Added diploma projects: " + added);
    }
}
