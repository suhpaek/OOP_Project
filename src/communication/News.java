package communication;

import enums.NewsType;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

public class News implements Serializable, Comparable<News> {

    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String content;
    private NewsType topic;
    private boolean pinned;
    private LocalDateTime createdAt;
    private String authorId;
    private List<Comment> comments;

    public News(int id, String title, String content, NewsType topic) {
        this(id, title, content, topic, null);
    }

    public News(int id, String title, String content, NewsType topic, String authorId) {
        this.id  = id;
        this.title = Objects.requireNonNull(title,   "title must not be null");
        this.content = Objects.requireNonNull(content, "content must not be null");
        this.topic = Objects.requireNonNull(topic,   "topic must not be null");
        this.pinned = (topic == NewsType.RESEARCH);
        this.createdAt = LocalDateTime.now();
        this.authorId = authorId;
        this.comments = new ArrayList<>();
    }

    public void addComment(Comment comment) {
        if (comment != null) comments.add(comment);
    }

    public void removeComment(int commentId) {
        comments.removeIf(c -> c.getId() == commentId);
    }

    public void setPinned(boolean pinned) {
        if (topic == NewsType.RESEARCH) {
            this.pinned = true;
        } else {
            this.pinned = pinned;
        }
    }

    @Override
    public int compareTo(News other) {
        if (this.pinned != other.pinned) {
            return this.pinned ? -1 : 1;
        }
        return other.createdAt.compareTo(this.createdAt);
    }

    public int getId() { 
        return id; 
    }
    public String getTitle(){ 
        return title; 
    }
    public String getContent(){ 
        return content; 
    }
    public NewsType getTopic() { 
        return topic; 
    }
    public boolean isPinned(){ 
        return pinned; 
    }
    public LocalDateTime getCreatedAt() { 
        return createdAt; 
    }
    public String getAuthorId(){ 
        return authorId; 
    }
    public List<Comment> getComments(){ 
        return Collections.unmodifiableList(comments); 
    }

    public void setTitle(String title) { 
        this.title = title; 
    }
    public void setContent(String content) { 
        this.content = content; 
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof News)) return false;
        News news = (News) o;
        return id == news.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("News{id=%d, title='%s', topic=%s, pinned=%b, comments=%d}",
                id, title, topic, pinned, comments.size());
    }
}