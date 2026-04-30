package users;


import communication.Comment;
import enums.Gender;
import enums.Language;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public abstract class User implements Serializable {
    private static final Set<String> USED_IDS = ConcurrentHashMap.newKeySet();
    private String id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private Gender gender;
    private Date dateOfBirth;
    private String email;
    private Language language;
    private boolean isActive;
    private transient boolean loggedIn;

    protected User() {
        this.id = generateUniqueId();
        this.language = Language.EN;
        this.isActive = true;
    }

    private static String generateUniqueId() {
        String newId;
        do {
            newId = UUID.randomUUID().toString();
        } while (!USED_IDS.add(newId));
        return newId;
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
        loggedIn = isActive
                && Objects.equals(this.username, username)
                && Objects.equals(this.password, password);
        return loggedIn;
    }

    public void logout() {
        loggedIn = false;
    }

    public final void updateEmail(String email) {
        this.email = email;
    }

    public final void selectLanguage(Language language) {
        this.language = language;
    }

    public final void changePassword(String password) {
        this.password = password;
    }

    void updateProfileByAdmin(String username, String firstName, String lastName, Gender gender, Date dateOfBirth) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        setDateOfBirthByAdmin(dateOfBirth);
    }

    void setActiveByAdmin(boolean active) {
        isActive = active;
    }

    private void setDateOfBirthByAdmin(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth == null ? null : new Date(dateOfBirth.getTime());
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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Gender getGender() {
        return gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth == null ? null : new Date(dateOfBirth.getTime());
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

    public boolean isLoggedIn() {
        return loggedIn;
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
