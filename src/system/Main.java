package system;

import communication.*;
import data.DataStore;
import enums.*;
import exceptions.*;
import research.*;
import support.*;
import users.*;

import java.time.LocalDate;
import java.util.List;

public class Main {

    static class DemoUser extends User {
        public DemoUser(String id, String username, String password,
                        String firstName, String lastName, String email) {
            super(username, password, firstName, lastName, email);
        }
    }

    static class DemoEmployee extends Employee {
        public DemoEmployee(String id, String username, String password,
                            String firstName, String lastName, String email) {
            super(username, password, firstName, lastName, email);
        }
    }

    public static void main(String[] args) throws AuthenticationException {

        separator("UNIVERSITY SYSTEM — RESEARCH / COMMUNICATION / SUPPORT DEMO");

        DataStore ds = DataStore.getInstance();

        //Create users
        separator("1. USER CREATION & AUTHENTICATION");

        DemoUser  aliceUser  = new DemoUser("U001", "alice",  "pass1", "Alice",  "Smith",  "alice@uni.kz");
        DemoUser  bobUser    = new DemoUser("U002", "bob",    "pass2", "Bob",    "Jones",  "bob@uni.kz");
        DemoUser  carolUser  = new DemoUser("U003", "carol",  "pass3", "Carol",  "White",  "carol@uni.kz");
        DemoUser  daveUser   = new DemoUser("U004", "dave",   "pass4", "Dave",   "Brown",  "dave@uni.kz");

        ds.addUser(aliceUser);
        ds.addUser(bobUser);
        ds.addUser(carolUser);
        ds.addUser(daveUser);

        aliceUser.login("alice", "pass1");
            System.out.println("✔ Alice logged in successfully");

            aliceUser.login("alice", "wrong_pass");

        aliceUser.selectLanguage(Language.KZ);
        System.out.println("Language set to: " + aliceUser.getLanguage());

        //Researcher setup
        separator("2. RESEARCHER SETUP & H-INDEX");

        Researcher aliceR = new Researcher(aliceUser);
        Researcher bobR   = new Researcher(bobUser);
        Researcher carolR = new Researcher(carolUser);

        ds.addResearcher(aliceR);
        ds.addResearcher(bobR);
        ds.addResearcher(carolR);

        //Research papers
        separator("3. PUBLISHING RESEARCH PAPERS");

        ResearchPaper paper1 = new ResearchPaper(
                "LMS Logs and Student Performance",
                "Journal of Educational Technology",
                12, LocalDate.of(2023, 5, 10), "10.1000/abc123");

        ResearchPaper paper2 = new ResearchPaper(
                "Influence of Retaking a Course",
                "Journal of Higher Education",
                8, LocalDate.of(2022, 11, 20), "10.1000/xyz456");

        ResearchPaper paper3 = new ResearchPaper(
                "AI in University Systems",
                "IEEE Transactions on Education",
                15, LocalDate.of(2024, 1, 5), "10.1000/ieee789");

        paper1.setCitations(10);
        paper2.setCitations(5);
        paper3.setCitations(3);

        aliceR.publishPaper(paper1);
        aliceR.publishPaper(paper2);
        aliceR.publishPaper(paper3);
        bobR.publishPaper(paper1);

        System.out.println("Alice's papers published: " + aliceR.getPapers().size());
        System.out.println("Alice h-index: " + aliceR.calculateHIndex());
        System.out.println("Alice total citations: " + aliceR.getTotalCitations());

        //Citation formats
        separator("4. CITATION FORMATS");

        System.out.println("--- PLAIN TEXT ---");
        System.out.println(paper1.getCitation(CitationFormat.PLAIN_TEXT));

        System.out.println("\n--- BIBTEX ---");
        System.out.println(paper1.getCitation(CitationFormat.BIBTEX));

        //Sorted paper printing
        separator("5. SORTED PAPER PRINTING");

        System.out.println("By citations (desc):");
        aliceR.printPapers(Researcher.BY_CITATIONS)
              .forEach(p -> System.out.printf("  %-45s  citations=%d%n", p.getTitle(), p.getCitations()));

        System.out.println("\nBy date (newest first):");
        aliceR.printPapers(Researcher.BY_DATE)
              .forEach(p -> System.out.printf("  %-45s  date=%s%n", p.getTitle(), p.getPublishDate()));

        System.out.println("\nBy length (pages, desc):");
        aliceR.printPapers(Researcher.BY_LENGTH)
              .forEach(p -> System.out.printf("  %-45s  pages=%d%n", p.getTitle(), p.getPages()));

        //Top cited researcher
        separator("6. TOP CITED RESEARCHER");

        List<Researcher> allR = ds.getAllResearchers();
        Researcher.getTopCitedResearcher(allR)
                  .ifPresent(top -> System.out.println("Top cited: " + top));

        Researcher.announceTopCitedResearcher(allR);
        System.out.println("Announcement news posted to DataStore.");

        //Supervisor validation
        separator("7. SUPERVISOR H-INDEX VALIDATION");

        System.out.println("Carol h-index (no papers): " + carolR.calculateHIndex());

        try {
            carolR.validateAsSupervisor();
        } catch (InvalidSupervisorException e) {
            System.out.println("✔ Caught expected: " + e.getMessage());
        }

        try {
            aliceR.validateAsSupervisor();
            System.out.println("✔ Alice can supervise (h=" + aliceR.calculateHIndex() + ")");
        } catch (InvalidSupervisorException e) {
            System.out.println("Unexpected: " + e.getMessage());
        }

        //Research project
        separator("8. RESEARCH PROJECTS");

        ResearchProject project = new ResearchProject(
                "AI-Enhanced Learning Analytics",
                "Investigating AI tools for real-time student performance feedback.");

        try {
            aliceR.joinProject(project);
            bobR.joinProject(project);
            System.out.println("Participants: " + project.getParticipants().size());
            project.addParticipant(carolUser);
        } catch (NonResearcherJoinProjectException e) {
            System.out.println("✔ Caught expected: " + e.getMessage());
        }

        project.addPaper(paper3);
        System.out.println("Project papers: " + project.getPublishedPapers().size());

        //Journal
        separator("9. JOURNAL SUBSCRIPTION (Observer Pattern)");

        Journal journal = new Journal("NU Research Journal");
        ds.addJournal(journal);

        journal.subscribe(aliceUser);
        journal.subscribe(carolUser);
        journal.subscribe(daveUser);
        System.out.println("Subscribers: " + journal.getSubscribers().size());

        // Carol unsubscribes
        journal.unsubscribe(carolUser);
        System.out.println("After unsubscribe: " + journal.getSubscribers().size());

        // Publish a paper 
        journal.publishPaper(paper3);
        System.out.println("Notification log:");
        journal.getNotificationLog().forEach(n -> System.out.println("  " + n));

        //News (pinning + comments)
        separator("10. NEWS — PINNING & COMMENTS");

        List<News> newsList = ds.getNewsSorted();
        System.out.println("News (pinned first):");
        newsList.forEach(n -> System.out.printf("  [%s] %s  pinned=%b%n",
                n.getTopic(), n.getTitle(), n.isPinned()));

        // Add a general news item
        News generalNews = new News(9999, "Campus Renovation Update",
                "The main building renovation is scheduled for summer break.", NewsType.GENERAL);
        ds.addNews(generalNews);

        // Comments
        Comment c1 = new Comment(1, "Great news, looking forward to it!", aliceUser.getId());
        Comment c2 = new Comment(2, "Will the library be affected?",        bobUser.getId());
        generalNews.addComment(c1);
        generalNews.addComment(c2);

        System.out.println("\nGeneral news comments:");
        generalNews.getComments().forEach(c ->
                System.out.printf("  [%s] %s%n", c.getAuthorId(), c.getText()));

        //Messaging
        separator("11. MESSAGING");

        DemoEmployee emp1 = new DemoEmployee("E001", "emp1", "pass", "John", "Doe", "john@uni.kz");
        DemoEmployee emp2 = new DemoEmployee("E002", "emp2", "pass", "Jane", "Roe", "jane@uni.kz");
        ds.addUser(emp1);
        ds.addUser(emp2);

       Message sentMsg1 = emp1.sendMessage(emp2, "Hi Jane, please review the research proposal.");
       Message sentMsg2 = emp2.sendMessage(emp1, "Done! Looks great.");

        emp1.sendMessage(emp2, sentMsg1.getText());
        emp2.sendMessage(emp1, sentMsg2.getText());

        System.out.println("Jane's inbox: " + emp2.getSentMessages().size() + " message(s)");
        System.out.println("Conversation:");
        ds.getConversation(emp1.getId(), emp2.getId())
          .forEach(m -> System.out.printf("  [%s → %s] %s%n",
                  m.getSenderId(), m.getReceiverId(), m.getText()));

        //Complaints
        separator("12. COMPLAINTS (TEACHER → DEAN)");

        Complaint complaint = new Complaint(1,
                "Student was repeatedly absent without excuse.",
                UrgencyLevel.HIGH, "teacher-t01", "U002");

        System.out.println("Complaint filed: " + complaint);
        complaint.resolve();
        System.out.println("Resolved: " + complaint.isResolved());

        //Tech Support 
        separator("13. TECH SUPPORT REQUESTS");

        TechSupportSpecialist specialist = new TechSupportSpecialist(
                "TS001", "techsup", "pass", "Tim", "Support", "tim@uni.kz");
        ds.addUser(specialist);

        TechSupportRequest req1 = new TechSupportRequest(
                "REQ001", "Projector broken in Room 204",
                "The projector in room 204 does not turn on.", aliceUser.getId());
        TechSupportRequest req2 = new TechSupportRequest(
                "REQ002", "Printer out of paper",
                "2nd floor printer needs paper refill.", bobUser.getId());

        specialist.receiveRequest(req1);
        specialist.receiveRequest(req2);

        System.out.println("New requests before view: " +
                specialist.getRequestsByStatus(RequestStatus.NEW).size());

        List<TechSupportRequest> viewed = specialist.viewNewRequests();
        System.out.println("Viewed " + viewed.size() + " request(s) → status now VIEWED");

        specialist.acceptRequest(req1);
        System.out.println("REQ001 status: " + req1.getStatus());

        specialist.rejectRequest(req2);
        System.out.println("REQ002 status: " + req2.getStatus());

        specialist.startRequest(req1);
        System.out.println("REQ001 status: " + req1.getStatus());

        specialist.completeRequest(req1);
        System.out.println("REQ001 status: " + req1.getStatus());

        //Action Logs 
        separator("14. ACTION LOG (Admin audit trail)");

        System.out.println("All logged actions:");
        ds.getActionLogs().forEach(log ->
                System.out.printf("  [%s] %s — %s%n",
                        log.getCreatedAt().toLocalTime(), log.getActorId(), log.getAction()));

        //Persistence 
        separator("15. SERIALIZATION / PERSISTENCE");

        ds.saveToFile();
        System.out.println("DataStore saved. On next launch it will reload automatically.");

        separator("DEMO COMPLETE");
    }


    private static void separator(String label) {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("  " + label);
        System.out.println("═".repeat(60));
    }
}