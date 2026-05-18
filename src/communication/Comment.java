package communication;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String text;
    private String authorId;
    private LocalDateTime createdAt;

    public Comment(int id, String text, String authorId) {
        this.id = id;
        this.text = Objects.requireNonNull(text,     "text must not be null");
        this.authorId = Objects.requireNonNull(authorId, "authorId must not be null");
        this.createdAt = LocalDateTime.now();
    }

    public int getId() { 
        return id; 
    }
    public String getText(){ 
        return text; 
    }
    public String getAuthorId(){ 
        return authorId; 
    }
    public LocalDateTime getCreatedAt(){ 
        return createdAt; 
    }

    public void setText(String text) {
        this.text = Objects.requireNonNull(text, "text must not be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment)) return false;
        Comment comment = (Comment) o;
        return id == comment.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Comment{id=%d, author='%s', at=%s, text='%s'}",
                id, authorId, createdAt, text);
    }
}