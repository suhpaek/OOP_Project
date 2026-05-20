package services.search;

import data.UniversityDataStore;
import models.academic.Course;
import models.communication.News;
import models.users.User;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SearchService {
    private final UniversityDataStore dataStore;

    public SearchService() {
        this(UniversityDataStore.getInstance());
    }

    public SearchService(UniversityDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public List<User> searchUsers(String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        List<User> result = new ArrayList<>();
        for (User user : dataStore.getAllUsers()) {
            String row = user.getUsername() + " " + user.getFullName() + " " + user.getEmail() + " "
                    + user.getClass().getSimpleName();
            if (pattern.matcher(row).find()) result.add(user);
        }
        return result;
    }

    public List<Course> searchCourses(String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        List<Course> result = new ArrayList<>();
        for (Course course : dataStore.getCourses()) {
            String row = course.getCode() + " " + course.getName() + " " + course.getCourseType();
            if (pattern.matcher(row).find()) result.add(course);
        }
        return result;
    }

    public List<News> searchNews(String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        List<News> result = new ArrayList<>();
        for (News news : dataStore.getNews()) {
            String row = news.getTitle() + " " + news.getContent() + " " + news.getTopic();
            if (pattern.matcher(row).find()) result.add(news);
        }
        return result;
    }
}
