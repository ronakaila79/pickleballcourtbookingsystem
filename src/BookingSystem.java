import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;


public class BookingSystem {
    private static final List<Person> users = new ArrayList<>();
    private static final List<Booking> bookings = new ArrayList<>();
    private static final List<Payment> payments = new ArrayList<>();

    public static void load() {
        users.clear(); bookings.clear(); payments.clear();
        users.addAll(BookingSystem.loadUsers());
        bookings.addAll(BookingSystem.loadBookings());
        payments.addAll(BookingSystem.loadPayments());
        expireStaleHolds();   
        
        boolean changed = false;
        for (Booking b : bookings) {
            Payment p = paymentFor(b.getId());
            if (Booking.BOOKED.equals(b.getStatus()) && p != null && "Rejected".equalsIgnoreCase(p.getStatus())) {
                b.setStatus(Booking.CANCELLED);
                changed = true;
            }
        }
        if (changed) saveBookings();
        if (users.isEmpty()) {   // first run: create default administrator
            Admin a = new Admin("admin", "admin123", "System Administrator", "09000000000", 0, false);
            a.setUserId(1);
            a.setCreatedAt(LocalDateTime.now().withNano(0));
            users.add(a);
            saveUsers();
        }
    }

    public static List<Person> getUsers() { return users; }
    public static List<Booking> getBookings() { return bookings; }
    public static List<Payment> getPayments() { return payments; }

    public static void saveUsers() { BookingSystem.saveUsers(users); }
    public static void saveBookings() { BookingSystem.saveBookings(bookings); }
    public static void savePayments() { BookingSystem.savePayments(payments); }

    private static final Path DIR = Paths.get("data");

    public static String clean(String s) {
        return s == null ? "" : s.replace("|", "/").replace("\n", " ").replace("\r", " ");
    }

    private static List<String> read(String name) {
        try {
            Path p = DIR.resolve(name);
            if (!Files.exists(p)) return new ArrayList<>();
            return Files.readAllLines(p, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read " + name, e);
        }
    }

    private static void write(String name, List<String> lines) {
        try {
            Files.createDirectories(DIR);
            Files.write(DIR.resolve(name), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot save " + name, e);
        }
    }

    public static List<Person> loadUsers() {
        List<Person> list = new ArrayList<>();
        for (String line : read("users.txt")) {
            if (line.isBlank()) continue;
            try {
                String[] f = line.split("\\|", -1);
                int fails = Integer.parseInt(f[5]);
                boolean blocked = Boolean.parseBoolean(f[6]);
                Person p = f[0].equals("Admin") ? new Admin(f[1], f[2], f[3], f[4], fails, blocked)
                                                : new Customer(f[1], f[2], f[3], f[4], fails, blocked);
                p.setUserId(f.length > 7 ? Integer.parseInt(f[7]) : list.size() + 1);
                if (f.length > 8 && !f[8].isBlank()) p.setCreatedAt(LocalDateTime.parse(f[8]));
                list.add(p);
            } catch (RuntimeException ex) {  }
        }
        return list;
    }

    public static void saveUsers(List<Person> users) {
        List<String> lines = new ArrayList<>();
        for (Person p : users) {
            lines.add(String.join("|", p.getRole(), clean(p.getUsername()), p.getPassword(),
                    clean(p.getFullName()), clean(p.getContact()),
                    String.valueOf(p.getFailedAttempts()), String.valueOf(p.isBlocked()),
                    String.valueOf(p.getUserId()),
                    p.getCreatedAt() == null ? "" : p.getCreatedAt().toString()));
        }
        write("users.txt", lines);
    }

    public static List<Booking> loadBookings() {
        List<Booking> list = new ArrayList<>();
        for (String line : read("bookings.txt")) {
            if (line.isBlank()) continue;
            try { list.add(Booking.fromLine(line)); } catch (RuntimeException ex) { /* skip */ }
        }
        return list;
    }

    public static void saveBookings(List<Booking> bookings) {
        List<String> lines = new ArrayList<>();
        for (Booking b : bookings) lines.add(b.toLine());
        write("bookings.txt", lines);
    }

    public static List<Payment> loadPayments() {
        List<Payment> list = new ArrayList<>();
        for (String line : read("payments.txt")) {
            if (line.isBlank()) continue;
            try { list.add(Payment.fromLine(line)); } catch (RuntimeException ex) { /* skip */ }
        }
        return list;
    }

    public static void savePayments(List<Payment> payments) {
        List<String> lines = new ArrayList<>();
        for (Payment p : payments) lines.add(p.toLine());
        write("payments.txt", lines);
    }


    public static final int MAX_ATTEMPTS = 3;

    public static Person find(String username) {
        for (Person p : BookingSystem.getUsers())
            if (p.getUsername().equalsIgnoreCase(username)) return p;
        return null;
    }

    public static Person login(String username, String password) throws Exception {
        if (username == null || username.isBlank() || password == null || password.isEmpty())
            throw new Exception("Please enter your username and password.");
        Person p = find(username.trim());
        if (p == null) throw new Exception("Invalid username or password.");
        if (p.isBlocked())
            throw new Exception("This account is blocked after " + MAX_ATTEMPTS
                    + " failed attempts.\nOnly the administrator can unblock it.");
        if (!p.checkPassword(password)) {
            p.setFailedAttempts(p.getFailedAttempts() + 1);
            String msg;
            if (p.getFailedAttempts() >= MAX_ATTEMPTS) {
                p.setBlocked(true);
                msg = "Too many failed attempts. Your account is now blocked.\nOnly the administrator can unblock it.";
            } else {
                msg = "Invalid username or password.\nAttempts left: " + (MAX_ATTEMPTS - p.getFailedAttempts());
            }
            BookingSystem.saveUsers();
            throw new Exception(msg);
        }
        p.setFailedAttempts(0);
        BookingSystem.saveUsers();
        return p;
    }

    public static void validateName(String name) throws Exception {
        if (name == null || !name.trim().matches("[A-Za-z][A-Za-z .'-]{1,49}"))
            throw new Exception("Please enter a valid full name (letters only, at least 2 characters).");
    }

    public static void validateContact(String contact) throws Exception {
        if (contact == null || !contact.trim().matches("09\\d{9}"))
            throw new Exception("Contact number must be 11 digits and start with 09 (e.g., 09171234567).");
    }

    private static void validateAccount(String fullName, String contact, String username,
                                        String password, String confirm) throws Exception {
        validateName(fullName);
        validateContact(contact);
        if (username == null || !username.trim().matches("[A-Za-z0-9_]{4,15}"))
            throw new Exception("Username must be 4-15 characters (letters, numbers, underscore).");
        if (find(username.trim()) != null) throw new Exception("That username is already taken.");
        validatePassword(password);
        if (!password.equals(confirm)) throw new Exception("Passwords do not match.");
    }

    private static void validatePassword(String password) throws Exception {
        if (password == null || password.length() < 6)
            throw new Exception("Password must be at least 6 characters.");
        if (password.contains("|"))   // | separates the fields in users.txt
            throw new Exception("Password cannot contain the | character.");
    }

    public static void register(String fullName, String contact, String username,
                                String password, String confirm) throws Exception {
        addUser("Customer", fullName, contact, username, password, confirm);
    }

    public static void addUser(String role, String fullName, String contact, String username,
                               String password, String confirm) throws Exception {
        validateAccount(fullName, contact, username, password, confirm);
        Person p = role.equals("Admin")
                ? new Admin(username.trim(), password, fullName.trim(), contact.trim(), 0, false)
                : new Customer(username.trim(), password, fullName.trim(), contact.trim(), 0, false);
        p.setUserId(nextUserId());
        p.setCreatedAt(LocalDateTime.now().withNano(0));
        BookingSystem.getUsers().add(p);
        BookingSystem.saveUsers();
    }

    private static int nextUserId() {
        int max = 0;
        for (Person u : users) max = Math.max(max, u.getUserId());
        return max + 1;
    }

    public static void changePassword(Person p, String oldPw, String newPw, String confirm) throws Exception {
        if (!p.checkPassword(oldPw)) throw new Exception("Current password is incorrect.");
        validatePassword(newPw);
        if (!newPw.equals(confirm)) throw new Exception("New passwords do not match.");
        if (newPw.equals(oldPw)) throw new Exception("New password must be different from the current one.");
        p.setPassword(newPw);
        BookingSystem.saveUsers();
    }

    public static void unblock(String username) throws Exception {
        Person p = find(username);
        if (p == null) throw new Exception("User not found.");
        p.setBlocked(false);
        p.setFailedAttempts(0);
        BookingSystem.saveUsers();
    }

    public static void block(Person admin, String username) throws Exception {
        Person p = find(username);
        if (p == null) throw new Exception("User not found.");
        if (p == admin) throw new Exception("You cannot block the account you are logged in with.");
        if (p.isBlocked()) throw new Exception("This user is already blocked.");
        p.setBlocked(true);
        BookingSystem.saveUsers();
    }

    public static void deleteUser(Person admin, String adminPassword, String username) throws Exception {
        if (!admin.checkPassword(adminPassword)) throw new Exception("Incorrect admin password. Nothing was deleted.");
        Person target = find(username);
        if (target == null) throw new Exception("User not found.");
        if (target == admin) throw new Exception("You cannot delete the account you are logged in with.");
        if (target.getRole().equals("Admin")) {
            int admins = 0;
            for (Person p : BookingSystem.getUsers()) if (p.getRole().equals("Admin")) admins++;
            if (admins <= 1) throw new Exception("You cannot delete the last administrator.");
        }
        for (Booking b : BookingSystem.getBookings())
            if (b.getUsername().equals(target.getUsername()) && b.blocksSlot())
                throw new Exception("This user still has an active or held booking. Cancel it first so it is not inherited by a future account with the same username.");
        BookingSystem.getUsers().remove(target);
        BookingSystem.saveUsers();
    }

    static void resubmitPayment(Person user, String id, String ref) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public enum SlotStatus { AVAILABLE, BOOKED, CLOSED, PAST }

    public static final String PESO = "\u20B1";   // peso sign
    public static final int COURT_COUNT = 5;   // 5 indoor courts
    public static final int FIRST_HOUR = 8;     // 8:00 AM
    public static final int LAST_HOUR = 23;     // last slot 11:00 PM - 12:00 AM
    public static final int BOOKING_WINDOW_DAYS = 60;

    public static boolean isOpenHour(int h) { return h >= 8 && h <= 23; }

    public static int rate(int h) { return h <= 16 ? 400 : 500; }

    public static int computeTotal(Collection<Integer> hours) {
        int t = 0;
        for (int h : hours) t += rate(h);
        return t;
    }

    public static boolean isWithinWindow(LocalDate d) {
        LocalDate today = LocalDate.now();
        return !d.isBefore(today) && !d.isAfter(today.plusDays(BOOKING_WINDOW_DAYS));
    }

    public static boolean isBooked(int courtId, LocalDate d, int hour) {
        for (Booking b : BookingSystem.getBookings())
            if (b.blocksSlot() && b.getCourtId() == courtId && b.getDate().equals(d) && b.getHours().contains(hour)) return true;
        return false;
    }

    public static boolean isPast(LocalDate d, int hour) {
        LocalDateTime now = LocalDateTime.now();
        if (d.isBefore(now.toLocalDate())) return true;
        return d.equals(now.toLocalDate()) && hour <= now.getHour();
    }

    public static SlotStatus slotStatus(int courtId, LocalDate d, int hour) {
        if (!isOpenHour(hour)) return SlotStatus.CLOSED;
        if (isBooked(courtId, d, hour)) return SlotStatus.BOOKED;
        if (isPast(d, hour)) return SlotStatus.PAST;
        return SlotStatus.AVAILABLE;
    }

    public static int availableSlots(LocalDate d) {
        int n = 0;
        for (int court = 1; court <= COURT_COUNT; court++)
            for (int h = FIRST_HOUR; h <= LAST_HOUR; h++)
                if (slotStatus(court, d, h) == SlotStatus.AVAILABLE) n++;
        return n;
    }

    public static String formatRanges(List<Integer> hours) {
        if (hours.isEmpty()) return "";
        List<Integer> hs = new ArrayList<>(hours);
        Collections.sort(hs);
        StringBuilder sb = new StringBuilder();
        int start = hs.get(0), prev = start;
        for (int i = 1; i < hs.size(); i++) {
            int h = hs.get(i);
            if (h != prev + 1) {
                sb.append(BookingSystem.label(start)).append(" - ").append(BookingSystem.label(prev + 1)).append(", ");
                start = h;
            }
            prev = h;
        }
        sb.append(BookingSystem.label(start)).append(" - ").append(BookingSystem.label(prev + 1));
        return sb.toString();
    }

    private static String nextId(String prefix, List<String> existing) {
        int max = 0;
        for (String id : existing) {
            try { max = Math.max(max, Integer.parseInt(id.substring(prefix.length() + 1))); }
            catch (RuntimeException ex) { /* ignore */ }
        }
        return String.format("%s-%04d", prefix, max + 1);
    }

    private static String nextBookingId() {
        List<String> ids = new ArrayList<>();
        for (Booking b : BookingSystem.getBookings()) ids.add(b.getId());
        return nextId("BK", ids);
    }

    private static String nextPaymentId() {
        List<String> ids = new ArrayList<>();
        for (Payment p : BookingSystem.getPayments()) ids.add(p.getId());
        return nextId("PY", ids);
    }

    public static Payment paymentFor(String bookingId) {
        for (Payment p : BookingSystem.getPayments()) if (p.getBookingId().equals(bookingId)) return p;
        return null;
    }

    public static Payment findPayment(String id) {
        for (Payment p : getPayments()) if (p.getId().equals(id)) return p;
        return null;
    }

    public static synchronized void verifyPayment(Person admin, String paymentId) throws Exception {
        if (admin == null || !admin.canManageSystem())
            throw new Exception("Only an administrator can verify payments.");

        Payment p = findPayment(paymentId);
        if (p == null) throw new Exception("Payment not found.");
        if ("Verified".equalsIgnoreCase(p.getStatus()))
            throw new Exception("This payment is already verified.");
        if ("Rejected".equalsIgnoreCase(p.getStatus()))
            throw new Exception("This payment was rejected. The customer must submit corrected payment details before it can be verified.");
        if (p.getReference() == null || p.getReference().isBlank() || "-".equals(p.getReference()))
            throw new Exception("This payment has no reference number.");

        Booking bk = findBooking(p.getBookingId());
        if (bk == null || !Booking.BOOKED.equals(bk.getStatus()))
            throw new Exception("This booking is " + (bk == null ? "missing" : bk.getStatus().toLowerCase()) + ", so its payment cannot be verified.");

        String oldStatus = p.getStatus();
        double oldPaid = p.getAmountPaid();
        p.setStatus("Verified");
        p.setAmountPaid(p.getTotal());   // the admin confirmed the full current price (any top-up/refund is now settled)
        try {
            savePayments();
        } catch (RuntimeException ex) {
            p.setStatus(oldStatus);
            p.setAmountPaid(oldPaid);
            throw new Exception("Could not save payment verification: " + ex.getMessage());
        }
    }

    public static synchronized void rejectPayment(Person admin, String paymentId, String reason) throws Exception {
        if (admin == null || !admin.canManageSystem())
            throw new Exception("Only an administrator can reject payments.");

        Payment p = findPayment(paymentId);
        if (p == null) throw new Exception("Payment not found.");
        if ("Verified".equalsIgnoreCase(p.getStatus()))
            throw new Exception("A verified payment cannot be rejected.");
        if (reason == null || reason.trim().isEmpty())
            throw new Exception("Please provide a reason for rejecting this payment.");

        // Rejecting cancels the booking so its slot is released for other players
        Booking b = findBooking(p.getBookingId());
        String oldStatus = p.getStatus();
        String oldReason = p.getRejectionReason();
        String oldBookingStatus = b == null ? null : b.getStatus();
        p.setStatus("Rejected");
        p.setRejectionReason(reason.trim());
        if (b != null) b.setStatus(Booking.CANCELLED);

        try {
            savePayments();
            saveBookings();
        } catch (RuntimeException ex) {
            p.setStatus(oldStatus);
            p.setRejectionReason(oldReason);
            if (b != null) b.setStatus(oldBookingStatus);
            throw new Exception("Could not save payment rejection: " + ex.getMessage());
        }
    }
    
    

    public static Booking findBooking(String id) {
        for (Booking b : BookingSystem.getBookings()) if (b.getId().equals(id)) return b;
        return null;
    }
    
    public static synchronized void editCustomerDetails(
            Person admin,
            String id,
            String name,
            String contact) throws Exception {

    if (admin == null || !admin.canManageSystem()) {
        throw new Exception(
                "Only an administrator can edit customer details.");
    }

    Booking booking = findBooking(id);

    if (booking == null) {
        throw new Exception("Booking not found.");
    }

    validateName(name);
    validateContact(contact);

    String oldName = booking.getCustomerName();
    String oldContact = booking.getContact();

    try {
        booking.setCustomerName(name.trim());
        booking.setContact(contact.trim());

        saveBookings();
    } catch (RuntimeException ex) {
        booking.setCustomerName(oldName);
        booking.setContact(oldContact);

        try {
            saveBookings();
        } catch (RuntimeException rollbackError) {
            ex.addSuppressed(rollbackError);
        }

        throw new Exception(
                "Could not save customer details: " + ex.getMessage());
    }
    
    
}
    public static final int HOLD_MINUTES = 15;
    public static final int MAX_HELD_PER_USER = 1;  
    public static synchronized int expireStaleHolds() {
        int n = 0;
        for (Booking b : bookings)
            if (b.isHoldExpired()) { b.setStatus(Booking.EXPIRED); n++; }
        if (n > 0) {
            try { saveBookings(); } catch (RuntimeException ex) { }
        }
        return n;
    }

    private static boolean referenceUsed(String ref) {
        for (Payment old : payments)
            for (String part : old.getReference().split("/"))
                if (part.equalsIgnoreCase(ref)) return true;
        return false;
    }

    public static void validateReference(String ref) throws Exception {
        if (ref == null || ref.isEmpty())
            throw new Exception("Please enter the reference number from your payment receipt.");
        if (!ref.matches("[A-Za-z0-9]+"))
            throw new Exception("The reference number can only contain letters and numbers (no spaces or symbols).");
        if (ref.length() < 6 || ref.length() > 20)
            throw new Exception("The reference number must be 6 to 20 characters long. Please check your receipt.");
        if (referenceUsed(ref))
            throw new Exception("This reference number was already used for another payment.");
    }

    public static synchronized Booking holdBooking(Person user, String name, String contact, int courtId,
            LocalDate date, List<Integer> hours) throws Exception {
        if (user == null) throw new Exception("Please log in first.");
        expireStaleHolds();
        if (hours == null || hours.isEmpty()) throw new Exception("Please select at least one time slot.");
        validateName(name);
        validateContact(contact);
        if (courtId < 1 || courtId > COURT_COUNT) throw new Exception("Please select a valid court.");
        if (date == null || !isWithinWindow(date)) throw new Exception("That date cannot be booked.");
        int held = 0;
        for (Booking b : bookings)
            if (b.getUsername().equals(user.getUsername()) && Booking.HELD.equals(b.getStatus())) held++;
        if (held >= MAX_HELD_PER_USER)
            throw new Exception("You already have a booking waiting for payment.\nPay for it or cancel it in My Bookings before holding another slot.");
        for (int h : hours) {
            SlotStatus s = slotStatus(courtId, date, h);
            if (s == SlotStatus.BOOKED)
                throw new Exception("Sorry, " + label(h) + " on Court " + courtId
                        + " was just booked or is on hold by another player. Please choose another slot.");
            if (s != SlotStatus.AVAILABLE)
                throw new Exception(label(h) + " is no longer available for booking.");
        }
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Booking b = new Booking(nextBookingId(), user.getUsername(), name.trim(), contact.trim(),
                courtId, date, hours, computeTotal(hours), now, Booking.HELD);
        bookings.add(b);
        try {
            saveBookings();
        } catch (RuntimeException ex) {
            bookings.remove(b);
            throw new Exception("Could not hold the slot: " + ex.getMessage());
        }
        return b;
    }

    public static synchronized Payment submitPayment(Person user, String bookingId, String reference) throws Exception {
        Booking b = findBooking(bookingId);
        if (b == null) throw new Exception("Booking not found.");
        if (user == null || !b.getUsername().equals(user.getUsername()))
            throw new Exception("You can pay only for your own booking.");
        expireStaleHolds();
        if (Booking.EXPIRED.equals(b.getStatus()))
            throw new Exception("Your " + HOLD_MINUTES + "-minute hold has expired and the slot was released.\nPlease book again.");
        if (!Booking.HELD.equals(b.getStatus()))
            throw new Exception("This booking is not waiting for payment.");
        String ref = reference == null ? "" : reference.trim();
        validateReference(ref);
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Payment p = new Payment(nextPaymentId(), b.getId(), "QR Ph", ref, b.getTotal(), b.getTotal(), 0, now);
        b.setStatus(Booking.BOOKED);
        payments.add(p);
        try {
            saveBookings();
            savePayments();
        } catch (RuntimeException ex) {
            b.setStatus(Booking.HELD);
            payments.remove(p);
            throw new Exception("Could not save the payment: " + ex.getMessage());
        }
        return p;
    }

    public static synchronized void cancelBooking(Person user, String id) throws Exception {
        if (user == null) throw new Exception("Please log in first.");
        Booking b = findBooking(id);
        if (b == null) throw new Exception("Booking not found.");
        boolean admin = user.canManageSystem();
        if (!admin && !b.getUsername().equals(user.getUsername()))
            throw new Exception("You can cancel only your own booking.");
        expireStaleHolds();
        String old = b.getStatus();
        if (Booking.BOOKED.equals(old)) {
            if (!admin) throw new Exception("This booking is already paid.\nNo cancellation, no rebooking and no refund.");
        } else if (!Booking.HELD.equals(old)) {
            throw new Exception("This booking is already " + old.toLowerCase() + ".");
        }
        b.setStatus(Booking.CANCELLED);
        try {
            saveBookings();
        } catch (RuntimeException ex) {
            b.setStatus(old);
            throw new Exception("Could not cancel the booking: " + ex.getMessage());
        }
    }

   
    public static synchronized void rescheduleBooking(Person admin, String id,
            int newCourtId, LocalDate newDate, List<Integer> newHours) throws Exception {
        if (admin == null || !admin.canManageSystem())
            throw new Exception("Only the administrator can reschedule a booking.");
        expireStaleHolds();

        Booking b = findBooking(id);
        if (b == null) throw new Exception("Booking not found.");
        if (!Booking.BOOKED.equals(b.getStatus()))
            throw new Exception("Only a paid booking can be rescheduled.");
        if (newCourtId < 1 || newCourtId > COURT_COUNT) {
            throw new Exception("Please select a valid court (1-" + COURT_COUNT + ").");
        }
        if (newDate == null || !isWithinWindow(newDate)) {
            throw new Exception("Choose a date from today through the next " + BOOKING_WINDOW_DAYS + " days.");
        }
        if (newHours == null || newHours.isEmpty()) {
            throw new Exception("Select at least one time slot.");
        }

        List<Integer> hours = new ArrayList<>(newHours);
        Collections.sort(hours);
        if (new HashSet<>(hours).size() != hours.size()) {
            throw new Exception("Duplicate time slots are not allowed.");
        }

        for (int hour : hours) {
            if (!isOpenHour(hour)) throw new Exception("Selected hour " + hour + " is outside operating hours.");
            if (isPast(newDate, hour)) throw new Exception(label(hour) + " is in the past.");
            for (Booking other : bookings) {
                if (other.getId().equals(b.getId()) || !other.blocksSlot()) continue;
                if (other.getCourtId() == newCourtId && other.getDate().equals(newDate)
                        && other.getHours().contains(hour)) {
                    throw new Exception(label(hour) + " on Court " + newCourtId
                            + " is already booked. Choose another available slot.");
                }
            }
        }

        int oldCourt = b.getCourtId();
        LocalDate oldDate = b.getDate();
        List<Integer> oldHours = new ArrayList<>(b.getHours());
        int oldTotal = b.getTotal();
        Payment payment = paymentFor(id);
        int oldPaymentTotal = payment == null ? 0 : payment.getTotal();
        String oldPaymentStatus = payment == null ? null : payment.getStatus();
        int newTotal = computeTotal(hours);

        b.setCourtId(newCourtId);
        b.setDate(newDate);
        b.setHours(hours);
        b.setTotal(newTotal);
        if (payment != null) {
            payment.setTotal(newTotal);
            if (payment.getBalance() > 0 && !"Rejected".equalsIgnoreCase(oldPaymentStatus)) payment.setStatus("Pending");   // customer must pay the difference
        }

        try {
            saveBookings();
            if (payment != null) savePayments();
        } catch (RuntimeException ex) {
            b.setCourtId(oldCourt);
            b.setDate(oldDate);
            b.setHours(oldHours);
            b.setTotal(oldTotal);
            if (payment != null) {
                payment.setTotal(oldPaymentTotal);
                payment.setStatus(oldPaymentStatus);
            }
            try {
                saveBookings();
                if (payment != null) savePayments();
            } catch (RuntimeException rollbackError) {
                ex.addSuppressed(rollbackError);
            }
            throw new Exception("Could not save the rescheduled booking: " + ex.getMessage());
        }
    }

    public static List<Booking> bookingsOn(LocalDate d) {
        List<Booking> list = new ArrayList<>();
        for (Booking b : BookingSystem.getBookings())
            if (b.getDate().equals(d) && Booking.BOOKED.equals(b.getStatus())) list.add(b);
        list.sort((a, c) -> a.getHours().get(0) - c.getHours().get(0));
        return list;
    }

    public static String money(double v) { return PESO + String.format("%,.2f", v); }

    public static String label(int hour) {
        int h = hour % 24;
        String ampm = h < 12 ? "AM" : "PM";
        int h12 = (h % 12 == 0) ? 12 : h % 12;
        return h12 + ":00 " + ampm;
    }
        
    
}