package exceptions;

public class NonResearcherJoinProjectException extends UniversityException {
    public NonResearcherJoinProjectException(String userId) {
        super("User '" + userId + "' is not a Researcher and cannot join a ResearchProject.");
    }
}
