package communication;

import enums.NewsType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class News implements Serializable {
    private int id;
    private String title;
    private String content;
    private NewsType topic;
    private boolean pinned;
    private LocalDateTime createdAt;
    private List<Comment> comments;

    public News(int id, String title, String content, NewsType topic) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.topic = topic;
        this.pinned = topic == NewsType.RESEARCH;
        this.createdAt = LocalDateTime.now();
        this.comments = new ArrayList<>();
    }

    public void addComment(Comment comment) {
        comments.add(comment);
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public NewsType getTopic() {
        return topic;
    }

    public boolean isPinned() {
        return pinned;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<Comment> getComments() {
        return comments;
    }
}
