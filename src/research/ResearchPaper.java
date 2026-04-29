package research;

import enums.CitationFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
    }

    public String getCitation(CitationFormat format) {
    }

    public void incrementCitations() {
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
        if (!(o instanceof ResearchPaper that)) return false;
        return Objects.equals(doi, that.doi);
    }

}