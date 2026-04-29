package exceptions;

public class CreditLimitExceededException extends UniversityException {
    private static final int MAX_CREDITS = 21;
 
    public CreditLimitExceededException(int attempted) {
        super("Credit limit exceeded: attempted " + attempted + ", max allowed is " + MAX_CREDITS);
    }
}
