package research;

import enums.CitationFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ResearchPaper implements Serializable, Comparable<ResearchPaper> {

    private String title;
    private List<Researcher> authors;
    private String journalName;
    private int pages;
    private int citations;
    private LocalDate publishDate;
    private String doi;

    public ResearchPaper(String title, String journalName,
                         int pages, LocalDate publishDate, String doi) {
        this.title = title;
        this.journalName = journalName;
        this.pages = pages;
        this.publishDate = publishDate;
        this.doi = doi;
        this.citations = 0;
        this.authors = new ArrayList<>();
    }

    public void addAuthor(Researcher r) {
        if (r != null && !authors.contains(r)) {
            authors.add(r);
        }
    }

    public String getCitation(CitationFormat format) {
        String authorNames = authors.isEmpty()
                ? "Unknown"
                : authors.stream().map(Researcher::getName).collect(Collectors.joining(", "));
        int year = publishDate == null ? LocalDate.now().getYear() : publishDate.getYear();

        if (format == CitationFormat.BIBTEX) {
            String key = doi == null || doi.isBlank()
                    ? title.replaceAll("[^A-Za-z0-9]", "")
                    : doi.replaceAll("[^A-Za-z0-9]", "");
            return "@article{" + key + ",\n"
                    + "  title={" + title + "},\n"
                    + "  author={" + authorNames + "},\n"
                    + "  journal={" + journalName + "},\n"
                    + "  year={" + year + "},\n"
                    + "  pages={" + pages + "},\n"
                    + "  doi={" + doi + "}\n"
                    + "}";
        }

        String date = publishDate == null ? "n.d." : publishDate.format(DateTimeFormatter.ISO_DATE);
        return authorNames + ". " + title + ". " + journalName + ", " + date + ". doi: " + doi;
    }

    public void incrementCitations() {
        citations++;
    }

   //getters and setter

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<Researcher> getAuthors() { return Collections.unmodifiableList(authors); }

    public String getJournalName() { return journalName; }
    public void setJournalName(String journalName) { this.journalName = journalName; }

    public int getPages() { return pages; }
    public void setPages(int pages) { this.pages = pages; }

    public int getCitations() { return citations; }
    public void setCitations(int citations) { this.citations = citations; }

    public LocalDate getPublishDate() { return publishDate; }
    public void setPublishDate(LocalDate publishDate) { this.publishDate = publishDate; }

    public String getDoi() { return doi; }
    public void setDoi(String doi) { this.doi = doi; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResearchPaper)) return false;
        ResearchPaper that = (ResearchPaper) o;
        return Objects.equals(doi, that.doi);
    }

    @Override
    public int hashCode() {
        return Objects.hash(doi);
    }

    @Override
    public int compareTo(ResearchPaper other) {
        if (other == null) return 1;
        if (publishDate == null && other.publishDate == null) return 0;
        if (publishDate == null) return -1;
        if (other.publishDate == null) return 1;
        return publishDate.compareTo(other.publishDate);
    }

}
