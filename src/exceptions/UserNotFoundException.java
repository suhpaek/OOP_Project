package exceptions;

public class UserNotFoundException extends UniversityException {
    public UserNotFoundException(String userId) {
        super("User not found: " + userId);
    }
}
