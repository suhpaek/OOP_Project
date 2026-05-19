package pattern.factory;

import models.users.*;

public class ConcreteUserFactory extends UserFactory {

    @Override
    public User createUser(String userType) {
        switch (userType) {
            case "Student":
                return new Student();
            case "Teacher":
                return new Teacher();
            case "Manager":
                return new Manager();
            case "Admin":
                return new Admin();
            case "TechSupportSpecialist":
                return new TechSupportSpecialist();
            default:
                throw new IllegalArgumentException("Unknown user type: " + userType);
        }
    }
}
