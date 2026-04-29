package exceptions;

public class AuthenticationException extends UniversityException {
    public AuthenticationException(String message) {
        super("Authentication failed: " + message);
    }
}

