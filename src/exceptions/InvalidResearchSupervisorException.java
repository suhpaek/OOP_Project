package exceptions;

public class InvalidResearchSupervisorException extends UniversityException {
    public InvalidResearchSupervisorException(String researcherName, int hIndex) {
        super("Research supervisor '" + researcherName + "' has h-index " + hIndex + ", but at least 3 is required.");
    }
}
