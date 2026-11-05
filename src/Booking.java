import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Booking {
    private final String id;
    private final String username;
    private String customerName;
    private String contact;
    private int courtId;
    private LocalDate date;
    private List<Integer> hours;
    private int total;
    private final LocalDateTime createdAt;
    
    private String status;

    public static final String HELD = "Held", BOOKED = "Booked", EXPIRED = "Expired", CANCELLED = "Cancelled";

    public Booking(String id, String username, String customerName, String contact, int courtId,
                   LocalDate date, List<Integer> hours, int total, LocalDateTime createdAt) {
        this(id, username, customerName, contact, courtId, date, hours, total, createdAt, BOOKED);
    }

    public Booking(String id, String username, String customerName, String contact, int courtId,
                   LocalDate date, List<Integer> hours, int total, LocalDateTime createdAt, String status) {
        this.status = status;
        this.id = id;
        this.username = username;
        this.customerName = customerName;
        this.contact = contact;
        this.courtId = courtId;
        this.date = date;
        this.hours = new ArrayList<>(hours);
        Collections.sort(this.hours);
        this.total = total;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String n) { this.customerName = n; }
    public String getContact() { return contact; }
    public void setContact(String c) { this.contact = c; }
    public int getCourtId() { return courtId; }
    public LocalDate getDate() { return date; }
    public List<Integer> getHours() { return hours; }
    public int getTotal() { return total; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getHoldExpiresAt() { return createdAt.plusMinutes(BookingSystem.HOLD_MINUTES); }
    public boolean isHoldExpired() { return HELD.equals(status) && LocalDateTime.now().isAfter(getHoldExpiresAt()); }

    public boolean blocksSlot() {
        return BOOKED.equals(status) || (HELD.equals(status) && !isHoldExpired());
    }

    public String getTimeSummary() { return BookingSystem.formatRanges(hours); }
    
    public void setCourtId(int courtId) {
    this.courtId = courtId;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setHours(List<Integer> hours) {
        this.hours = new ArrayList<>(hours);
        Collections.sort(this.hours);
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String toLine() {
        StringBuilder h = new StringBuilder();
        for (int i = 0; i < hours.size(); i++) { if (i > 0) h.append(','); h.append(hours.get(i)); }
        return String.join("|", id, BookingSystem.clean(username), BookingSystem.clean(customerName),
                BookingSystem.clean(contact), String.valueOf(courtId), date.toString(), h.toString(),
                String.valueOf(total), createdAt.toString(), status);
    }

    public static Booking fromLine(String line) {
        String[] f = line.split("\\|", -1);
        List<Integer> hs = new ArrayList<>();
        for (String s : f[6].split(",")) if (!s.isBlank()) hs.add(Integer.parseInt(s.trim()));
        return new Booking(f[0], f[1], f[2], f[3], Integer.parseInt(f[4]), LocalDate.parse(f[5]), hs,
                Integer.parseInt(f[7]), LocalDateTime.parse(f[8]), f.length > 9 && !f[9].isBlank() ? f[9] : BOOKED);
    }

  
}
