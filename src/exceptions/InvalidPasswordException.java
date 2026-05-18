package exceptions;

public class InvalidPasswordException extends AuthenticationException {
    public InvalidPasswordException(String username) {
        super("Wrong password for user: " + username);
    }
}
