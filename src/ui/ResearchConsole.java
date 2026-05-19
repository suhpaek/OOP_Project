package ui;

import enums.CitationFormat;
import models.research.ResearchPaper;
import models.research.Researcher;
import models.users.User;
import pattern.strategy.SortByCitations;
import pattern.strategy.SortByDate;
import pattern.strategy.SortByPages;
import pattern.strategy.SortStrategy;
import services.ResearchService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class ResearchConsole {
    private final Scanner scanner;
    private final User user;
    private final ResearchService researchService = new ResearchService();

    public ResearchConsole(Scanner scanner, User user) {
        this.scanner = scanner;
        this.user = user;
    }

    public void start() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    viewMyPapers();
                    break;
                case "2":
                    publishPaper();
                    break;
                case "3":
                    viewHIndex();
                    break;
                case "4":
                    viewCitation();
                    break;
                case "5":
                    viewTopCitedResearcher();
                    break;
                case "6":
                    viewAllResearchers();
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
        System.out.println("===== RESEARCH MENU =====");
        System.out.println("1. View my papers");
        System.out.println("2. Publish paper");
        System.out.println("3. View my h-index");
        System.out.println("4. Get citation");
        System.out.println("5. View top cited researcher");
        System.out.println("6. View all researchers");
        System.out.println("0. Back");
        System.out.print("Choose: ");
    }

    private void viewMyPapers() {
        List<ResearchPaper> papers = getSortedPapers();
        if (papers.isEmpty()) {
            System.out.println("No papers found.");
            return;
        }
        printPapers(papers);
    }

    private List<ResearchPaper> getSortedPapers() {
        System.out.print("Sort by (1 citations, 2 date, 3 pages, other none): ");
        String choice = scanner.nextLine().trim();
        SortStrategy strategy = null;
        if ("1".equals(choice)) strategy = new SortByCitations();
        else if ("2".equals(choice)) strategy = new SortByDate();
        else if ("3".equals(choice)) strategy = new SortByPages();
        return strategy == null ? researchService.getPapers(user) : researchService.sortPapers(user, strategy);
    }

    private void publishPaper() {
        try {
            System.out.print("Title: ");
            String title = scanner.nextLine();
            System.out.print("Journal: ");
            String journal = scanner.nextLine();
            System.out.print("Pages: ");
            int pages = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Publish date (yyyy-mm-dd): ");
            LocalDate date = LocalDate.parse(scanner.nextLine().trim());
            System.out.print("DOI: ");
            String doi = scanner.nextLine();
            ResearchPaper paper = new ResearchPaper(title, journal, pages, date, doi);
            researchService.publishPaper(user, paper);
            System.out.println("Paper published.");
        } catch (Exception e) {
            System.out.println("Could not publish paper: " + e.getMessage());
        }
    }

    private void viewHIndex() {
        System.out.println("Your h-index: " + researchService.calculateHIndex(user));
    }

    private void viewCitation() {
        List<ResearchPaper> papers = researchService.getPapers(user);
        if (papers.isEmpty()) {
            System.out.println("No papers found.");
            return;
        }
        printPapers(papers);
        try {
            System.out.print("Paper number: ");
            int index = Integer.parseInt(scanner.nextLine().trim()) - 1;
            System.out.print("Format (PLAIN_TEXT/BIBTEX): ");
            CitationFormat format = CitationFormat.valueOf(scanner.nextLine().trim().toUpperCase());
            System.out.println(papers.get(index).getCitation(format));
        } catch (Exception e) {
            System.out.println("Could not create citation: " + e.getMessage());
        }
    }

    private void viewTopCitedResearcher() {
        Researcher researcher = researchService.getTopCitedResearcher();
        System.out.println(researcher == null ? "No researchers found." : researcher);
    }

    private void viewAllResearchers() {
        List<Researcher> researchers = researchService.getAllResearchers();
        if (researchers.isEmpty()) {
            System.out.println("No researchers found.");
            return;
        }
        System.out.printf("%-30s %-8s %-10s%n", "Name", "H-index", "Citations");
        for (Researcher researcher : researchers) {
            System.out.printf("%-30s %-8d %-10d%n",
                    researcher.getName(),
                    researcher.calculateHIndex(),
                    researcher.getTotalCitations());
        }
    }

    private void printPapers(List<ResearchPaper> papers) {
        System.out.printf("%-4s %-32s %-24s %-6s %-10s %-12s%n",
                "No", "Title", "Journal", "Pages", "Citations", "Date");
        for (int i = 0; i < papers.size(); i++) {
            ResearchPaper paper = papers.get(i);
            System.out.printf("%-4d %-32s %-24s %-6d %-10d %-12s%n",
                    i + 1,
                    paper.getTitle(),
                    paper.getJournalName(),
                    paper.getPages(),
                    paper.getCitations(),
                    paper.getPublishDate());
        }
    }
}
