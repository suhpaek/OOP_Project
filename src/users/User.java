package users;


import communication.Comment;
import communication.News;
import enums.Gender;
import enums.Language;
import java.util.Date;
import java.util.List;

public abstract class User {
    private String id;
    private String username;
    private String password;
    protected String firstName;
    protected String lastName;
    private Gender gender;
    private Date dateOfBirth;
    private String email;
    private Language language;
    private boolean isActive;

    public boolean login(String username, String Password){}

    public void logout(){}

    public void updateEmail(String email){}

    public void selectLanguage(Language language){}

    public void changePassword(String password){}

    public Comment writeComment(String comment){}
}
