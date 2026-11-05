import java.time.LocalDateTime;

public class Payment {
    private final String id;
    private final String bookingId;
    private final String method;
    private String reference;
    private int total;
    private double amountPaid;
    private final LocalDateTime createdAt;
    private String status;  
    private String rejectionReason = "";

    public Payment(String id, String bookingId, String method, String reference, int total, int amountPaid, int par, LocalDateTime createdAt) {
        this(id, bookingId, method, reference, total, amountPaid, createdAt, "Pending");
    }

    public Payment(String id, String bookingId, String method, String reference, int total,
                   double amountPaid, LocalDateTime createdAt, String status) {
        this.id = id; this.bookingId = bookingId; this.method = method; this.reference = reference;
        this.total = total; this.amountPaid = amountPaid; this.createdAt = createdAt; this.status = status;
    }

    public String getId() { 
        return id; }
    public String getBookingId() { 
        return bookingId; }
    public String getMethod() { 
        return method; }
    public String getReference() { 
        return reference; }
    public int getTotal() { 
        return total; }
    
    public void setTotal(int total) {
        this.total = total;
}
    public double getAmountPaid() { 
        return amountPaid; }

    public void setAmountPaid(double amountPaid) { 
        this.amountPaid = amountPaid; }

    public void setReference(String reference) { 
        this.reference = reference; }

    public int getBalance() { 
        return total - (int) Math.round(amountPaid); }
    public LocalDateTime getCreatedAt() { 
        return createdAt; }
    public String getStatus() { 
        return status; }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason == null ? "" : rejectionReason;
    }

    public String toLine() {
        return String.join("|", id, bookingId, method, BookingSystem.clean(reference), String.valueOf(total),
            String.valueOf(amountPaid), createdAt.toString(), status,
            BookingSystem.clean(rejectionReason));
}

    public static Payment fromLine(String line) {
    String[] f = line.split("\\|", -1);
        int i = f[6].contains("T") ? 6 : 7; 
    Payment payment = new Payment(f[0], f[1], f[2], f[3], Integer.parseInt(f[4]),
            Double.parseDouble(f[5]), LocalDateTime.parse(f[i]),
            f.length > i + 1 ? f[i + 1] : "Pending");
    if (f.length > i + 2) payment.setRejectionReason(f[i + 2]);
     return payment;
}

    
    
}
