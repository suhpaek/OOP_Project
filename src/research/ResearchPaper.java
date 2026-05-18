package research;

import enums.CitationFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ResearchPaper implements Serializable, Comparable<ResearchPaper> {

    private static final long serialVersionUID = 1L;

    private String       title;
    private List<Researcher> authors;
    private String       journalName;
    private int          pages;
    private int          citations;
    private LocalDate    publishDate;
    private String       doi;

    public static final Comparator<ResearchPaper> BY_CITATIONS =
            Comparator.comparingInt(ResearchPaper::getCitations).reversed();

    public static final Comparator<ResearchPaper> BY_DATE =
            Comparator.comparing(ResearchPaper::getPublishDate,
                    Comparator.nullsLast(Comparator.reverseOrder()));

    public static final Comparator<ResearchPaper> BY_LENGTH =
            Comparator.comparingInt(ResearchPaper::getPages).reversed();

    public ResearchPaper(String title, String journalName,
                         int pages, LocalDate publishDate, String doi) {
        this.title       = Objects.requireNonNull(title, "title must not be null");
        this.journalName = Objects.requireNonNull(journalName, "journalName must not be null");
        this.pages       = pages;
        this.publishDate = publishDate;
        this.doi         = doi;
        this.citations   = 0;
        this.authors     = new ArrayList<>();
    }

    public void addAuthor(Researcher researcher) {
        if (researcher != null && !authors.contains(researcher)) {
            authors.add(researcher);
        }
    }

    public void incrementCitations() {
        citations++;
    }

    public String getCitation(CitationFormat format) {
        String authorNames = authors.isEmpty()
                ? "Unknown Author"
                : authors.stream()
                         .map(Researcher::getName)
                         .collect(Collectors.joining(", "));

        int year = (publishDate != null) ? publishDate.getYear() : LocalDate.now().getYear();

        switch (format) {
            case BIBTEX:
                return buildBibtex(authorNames, year);
            case PLAIN_TEXT:
            default:
                return buildPlainText(authorNames);
        }
    }

    private String buildBibtex(String authorNames, int year) {
        String key = (doi != null && !doi.isBlank())
                ? doi.replaceAll("[^A-Za-z0-9]", "")
                : title.replaceAll("[^A-Za-z0-9]", "");

        return "@article{" + key + ",\n"
             + "  title   = {" + title + "},\n"
             + "  author  = {" + authorNames + "},\n"
             + "  journal = {" + journalName + "},\n"
             + "  year    = {" + year + "},\n"
             + "  pages   = {" + pages + "},\n"
             + "  doi     = {" + (doi != null ? doi : "N/A") + "}\n"
             + "}";
    }

    private String buildPlainText(String authorNames) {
        String dateStr = (publishDate != null)
                ? publishDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                : "n.d.";
        return authorNames + ". \"" + title + "\". "
             + journalName + ", " + dateStr + ". doi: " + (doi != null ? doi : "N/A");
    }

    @Override
    public int compareTo(ResearchPaper other) {
        if (other == null) return 1;
        if (publishDate == null && other.publishDate == null) return 0;
        if (publishDate == null) return -1;
        if (other.publishDate == null) return 1;
        return publishDate.compareTo(other.publishDate);
    }

    public String    getTitle()       { return title; }
    public void      setTitle(String t){ this.title = t; }

    public List<Researcher> getAuthors() { return Collections.unmodifiableList(authors); }

    public String    getJournalName()  { return journalName; }
    public void      setJournalName(String j) { this.journalName = j; }

    public int       getPages()        { return pages; }
    public void      setPages(int p)   { this.pages = p; }

    public int       getCitations()    { return citations; }
    public void      setCitations(int c){ this.citations = c; }

    public LocalDate getPublishDate()  { return publishDate; }
    public void      setPublishDate(LocalDate d) { this.publishDate = d; }

    public String    getDoi()          { return doi; }
    public void      setDoi(String d)  { this.doi = d; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchPaper)) return false;
        ResearchPaper that = (ResearchPaper) o;
        return Objects.equals(doi, that.doi) && Objects.equals(title, that.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doi, title);
    }

    @Override
    public String toString() {
        return String.format("ResearchPaper{title='%s', journal='%s', citations=%d, date=%s}",
                title, journalName, citations, publishDate);
    }
}