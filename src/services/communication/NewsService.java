package services.communication;

import models.communication.Comment;
import models.communication.News;
import data.UniversityDataStore;
import enums.NewsType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NewsService {
    private final UniversityDataStore dataStore;
    public NewsService() { this(UniversityDataStore.getInstance()); }
    public NewsService(UniversityDataStore dataStore) { this.dataStore = dataStore; }

    public News publish(String title, String content, NewsType type) {
        if (title == null || title.isBlank() || content == null || content.isBlank() || type == null) return null;
        News news = new News(Math.abs((title + System.nanoTime()).hashCode()), title, content, type);
        dataStore.addNews(news);
        saveData();
        return news;
    }

    public Comment addComment(News news, String text, String authorId) {
        if (news == null || text == null || text.isBlank() || authorId == null || authorId.isBlank()) return null;
        Comment comment = new Comment(Math.abs((text + authorId + System.nanoTime()).hashCode()), text, authorId);
        news.addComment(comment);
        saveData();
        return comment;
    }

    public News findNewsById(int id) {
        for (News news : dataStore.getNews()) {
            if (news.getId() == id) return news;
        }
        throw new IllegalArgumentException("News not found.");
    }

    public List<News> getNews() { return dataStore.getNews(); }

    public List<String> getFormattedNews() {
        List<News> news = dataStore.getNews();
        news.sort(Comparator.comparing(News::isPinned).reversed()
                .thenComparing(News::getCreatedAt).reversed());
        List<String> rows = new ArrayList<>();
        for (News item : news) {
            rows.add((item.isPinned() ? "[PINNED] " : "")
                    + item.getTopic() + " | " + item.getTitle()
                    + "\n" + item.getContent());
        }
        return rows;
    }

    private void saveData() {
        try {
            dataStore.save();
        } catch (IOException e) {
            throw new IllegalStateException("Could not save news.", e);
        }
    }
}
