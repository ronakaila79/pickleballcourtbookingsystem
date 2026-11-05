public class Customer extends Person {
    public Customer(String username, String password, String fullName, String contact,
                    int failedAttempts, boolean blocked) {
        super(username, password, fullName, contact, failedAttempts, blocked);
    }
    @Override public String getRole() { return "Customer"; }
    @Override public String getWelcomeMessage() { return "Welcome, " + getFullName() + "!"; }
    @Override public boolean canManageSystem() { return false; }
}