public abstract class Person {
    private String username;
    private String password;
    private String fullName;
    private String contact;
    private int failedAttempts;
    private boolean blocked;
    private java.time.LocalDateTime createdAt; 
    private int userId;   
 
    // abstraction with encapsulation
    protected Person(String username, String password, String fullName, String contact,
                     int failedAttempts, boolean blocked) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.contact = contact;
        this.failedAttempts = failedAttempts;
        this.blocked = blocked;
    }
 
    public java.time.LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public int getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }
    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
 
    public boolean checkPassword(String plain) { return password.equals(plain); }
    public void setPassword(String plain) { this.password = plain; }
 
    // polymorphism
    public abstract String getRole();
    public abstract String getWelcomeMessage();
    public abstract boolean canManageSystem();
}