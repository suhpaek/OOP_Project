package users;


import communication.Comment;
import enums.Gender;
import enums.Language;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public abstract class User implements Serializable {
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

    protected User() {
        this.id = UUID.randomUUID().toString();
        this.language = Language.EN;
        this.isActive = true;
    }

    protected User(String username, String password, String firstName, String lastName, String email) {
        this();
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public boolean login(String username, String password) {
        return isActive
                && Objects.equals(this.username, username)
                && Objects.equals(this.password, password);
    }

    public void logout() {
        this.isActive = false;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void selectLanguage(Language language) {
        this.language = language;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public Comment writeComment(String comment) {
        return new Comment(Math.abs(Objects.hash(id, comment)), comment, id);
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public Language getLanguage() {
        return language;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", fullName='" + getFullName() + '\'' +
                ", email='" + email + '\'' +
                ", language=" + language +
                ", isActive=" + isActive +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
