public class Admin extends Person {
    public Admin(String username, String password, String fullName, String contact,
                 int failedAttempts, boolean blocked) {
        super(username, password, fullName, contact, failedAttempts, blocked);
    }
    @Override public String getRole() { return "Admin"; }
    @Override public String getWelcomeMessage() { return "Administrator: " + getFullName(); }
    @Override public boolean canManageSystem() { return true; }
}