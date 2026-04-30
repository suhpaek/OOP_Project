package exceptions;

public class InvalidSupervisorException extends UniversityException {
    public InvalidSupervisorException(String researcherName, int hIndex) {
        super("Researcher '" + researcherName + "' cannot be a supervisor because h-index is " + hIndex + ", minimum is 3.");
    }
}
