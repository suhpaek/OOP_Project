package models.users;

public class Employee extends User {
    private double salary;

    public Employee() {
        super();
    }

    public Employee(String username, String password, String firstName, String lastName, String email) {
        super(username, password, firstName, lastName, email);
    }

    public double getSalary() {
        return salary;
    }

    void setSalaryByAdmin(double salary) {
        this.salary = salary;
    }

    public void setSalary(double salary) {
        setSalaryByAdmin(salary);
    }
}
