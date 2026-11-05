import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class Dashboard extends JFrame {
    public static final Color PRIMARY = new Color(27, 94, 32);
    public static final Color PRIMARY_LIGHT = new Color(232, 245, 233);
    public static final Color AVAILABLE = new Color(46, 160, 67);
    public static final Color BOOKED = new Color(205, 52, 52);
    public static final Color SELECTED = new Color(30, 110, 220);
    public static final Color DISABLED = new Color(190, 190, 190);
    public static final Color DANGER = new Color(183, 28, 28);
    public static final Color NEUTRAL = new Color(90, 100, 110);
    public static final Color BG = new Color(246, 248, 246);

    public static final Font FONT = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font BOLD = new Font("SansSerif", Font.BOLD, 14);
    public static final Font TITLE = new Font("SansSerif", Font.BOLD, 22);
    public static final Font SMALL = new Font("SansSerif", Font.PLAIN, 12);


    public static JButton button(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(BOLD);
        b.setOpaque(true);
        b.setContentAreaFilled(true);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(bg, 1));
        b.setMargin(new Insets(7, 10, 7, 10));
        b.setPreferredSize(new Dimension(Math.max(118, text.length() * 8 + 28), 38));
      
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT);
        return l;
    }

    public static JLabel heading(String text) {
        JLabel l = new JLabel(text);
        l.setFont(TITLE);
        l.setForeground(PRIMARY);
        return l;
    }

    public static void styleTable(JTable t) {
        t.setRowHeight(28);
        t.setFont(FONT);
        t.getTableHeader().setFont(BOLD);
        t.getTableHeader().setBackground(PRIMARY);
        t.getTableHeader().setForeground(Color.WHITE);
        t.setSelectionBackground(new Color(200, 230, 201));
        t.setSelectionForeground(Color.BLACK);
        t.setGridColor(new Color(220, 220, 220));
        t.setFillsViewportHeight(true);
    }

    public static void error(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    
    public static final String TERMS_AND_POLICY =
            "COURT BOOKING TERMS & CONDITIONS\n" +
            "\n" +
            "Welcome to Pickleball Court PH. By making a reservation, completing payment, or using any of our court facilities, you (\"User,\" \"Player,\" or \"Booking Party\") agree to comply with the following Terms and Conditions. Please read them carefully before confirming your booking.\n" +
            "\n" +
            "1. Payment and Booking Confirmation\n" +
            "\n" +
            "1.1 Full Payment Required. All court reservations must be paid in full through the authorized payment channels before a booking can be confirmed.\n" +
            "\n" +
            "1.2 Booking Confirmation. A reservation is considered official only after the system generates a booking ID or confirmation receipt and the required payment has been verified.\n" +
            "\n" +
            "1.3 Payment Verification. Users are responsible for providing accurate payment details and retaining their payment reference number or proof of payment for verification purposes.\n" +
            "\n" +
            "2. No-Refund, No-Cancellation, and No-Modification Policy\n" +
            "\n" +
            "2.1 No Refunds. All confirmed bookings are final and non-refundable. No cash, bank transfer, or e-wallet refunds will be issued for cancellations, missed sessions, shortened playing time, or failure to use a reserved court.\n" +
            "\n" +
            "2.2 No User-Initiated Modifications. Users are not permitted to independently change, move, exchange, or modify their confirmed booking date, time slot, or court assignment.\n" +
            "\n" +
            "2.3 Non-Transferable Bookings. All reservations are registered under the name or account provided during booking. Selling, transferring, or exchanging a reservation with another person without prior authorization from the administration is prohibited.\n" +
            "\n" +
            "2.4 Exceptions. Rescheduling may be considered only under the exceptional circumstances specified in Section 3 and is subject to administrative approval.\n" +
            "\n" +
            "3. Rescheduling Policy: Severe Weather and Emergencies\n" +
            "\n" +
            "3.1 Authorized Rescheduling Only. Only the administration is authorized to approve and process a booking reschedule. Users cannot directly reschedule, edit, or transfer their reservations through the booking system.\n" +
            "\n" +
            "3.2 Valid Grounds for Rescheduling. Rescheduling may be authorized when severe weather, heavy rain affecting court safety, flooding, natural disasters, facility emergencies, or other unforeseen emergencies make it unsafe or impossible to proceed with the scheduled session.\n" +
            "\n" +
            "3.3 No Refunds Under Any Circumstances. Refunds will not be provided for cancellations or disrupted bookings, including those affected by severe weather or emergencies. When a booking cannot proceed due to an eligible circumstance, the administration may offer rescheduling as the sole remedy under this policy.\n" +
            "\n" +
            "3.4 Administrative Assessment. The administration will assess the situation and determine whether rescheduling is warranted. Approval is not automatic and depends on the circumstances and availability of the courts.\n" +
            "\n" +
            "3.5 Rescheduling Procedure. Users must contact the administration as soon as possible when a qualifying situation arises. The administration will coordinate the replacement schedule directly with the affected user.\n" +
            "\n" +
            "3.6 Availability and Validity. Approved rescheduled bookings are subject to court availability and must be used within thirty (30) days of the original booking date, unless the administration specifies otherwise.\n" +
            "\n" +
            "3.7 Personal Conflicts. Personal schedule changes, late arrivals, changes of plans, or inability to attend are not valid grounds for rescheduling.\n" +
            "\n" +
            "4. Court Rules, Late Arrivals, and No-Shows\n" +
            "\n" +
            "4.1 Scheduled Playing Time. Players must arrive on time. Late arrival does not extend the reserved playing period, and the booking will end at its originally scheduled time.\n" +
            "\n" +
            "4.2 No-Show Policy. If the booking party fails to arrive within fifteen (15) minutes of the scheduled start time without notifying the administration, the reservation may be declared a no-show. The administration may release the court to other players, and the original payment will be forfeited.\n" +
            "\n" +
            "4.3 Proper Attire and Footwear. All players must wear appropriate athletic clothing and court-compatible, non-marking footwear. Entry may be denied to individuals who fail to comply with these requirements, without a refund or rescheduling entitlement.\n" +
            "\n" +
            "4.4 Compliance With Facility Rules. All players must follow posted rules, observe proper conduct, and comply with reasonable instructions from authorized facility personnel.\n" +
            "\n" +
            "5. Liability, Safety, and Property Damage\n" +
            "\n" +
            "5.1 Assumption of Risk. Players acknowledge that physical sports involve inherent risks, including slips, falls, collisions, and other injuries. Players are responsible for assessing their physical readiness and exercising appropriate care while using the facilities.\n" +
            "\n" +
            "5.2 Safety Responsibilities. Players must follow all safety guidelines and immediately report hazardous conditions, accidents, or damaged equipment to the administration.\n" +
            "\n" +
            "5.3 Property Damage. Users may be held financially responsible for damage to the courts, nets, equipment, or other facility property caused by negligence, misuse, intentional misconduct, or vandalism, subject to applicable law.\n" +
            "\n" +
            "5.4 Personal Belongings. Players are responsible for their personal belongings. The facility is not responsible for lost, stolen, or damaged items left unattended, except where liability cannot lawfully be excluded.\n" +
            "\n" +
            "5.5 Limitation of Liability. Nothing in these Terms and Conditions excludes or limits any liability that cannot legally be excluded or limited under applicable law.\n" +
            "\n" +
            "6. Right to Refuse Service\n" +
            "\n" +
            "The administration reserves the right, subject to applicable law, to refuse entry, suspend or terminate a booking, remove individuals from the premises, or restrict future bookings in cases involving disruptive behavior, harassment, property damage, violation of facility rules, or refusal to follow reasonable staff instructions.\n" +
            "\n" +
            "7. Contact Information and Customer Support\n" +
            "\n" +
            "For booking inquiries, payment verification, emergencies, or requests for administrative assistance, please contact us through the following channels:\n" +
            "\n" +
            "- Contact Number: +63 917 528 4063\n" +
            "- Email Address: pickleballcourtph@gmail.com\n" +
            "- Facebook Page: Facebook.com/PickleballCourtPH\n" +
            "\n" +
            "Please include your booking ID, registered name, scheduled date, and payment reference number when contacting the administration.\n" +
            "\n" +
            "Note: The contact details above are sample details for demonstration purposes. Replace them with your actual official contact information before publishing.\n" +
            "\n" +
            "8. Acceptance of Terms and Conditions\n" +
            "\n" +
            "By checking the agreement box and completing the payment process, you acknowledge that you have read, understood, and voluntarily agreed to these Terms and Conditions.\n" +
            "\n" +
            "The administration reserves the right to update these Terms and Conditions when necessary, subject to applicable law. Any applicable updates will be communicated through the booking system or official communication channels.\n" +
            "\n" +
            "Thank you for choosing our PickleballCourt PH. We look forward to providing you with a safe, enjoyable, and well-organized pickleball experience.\n";

    public Dashboard(Person user) {
        super("Pickleball Court Booking System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1150, 720));
        setLocationRelativeTo(null);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Dashboard.PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));
        JLabel title = new JLabel("PICKLEBALL COURT BOOKING");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        JLabel welcome = new JLabel(user.getWelcomeMessage() + "  (" + user.getRole() + ")   ");
        welcome.setFont(Dashboard.FONT);
        welcome.setForeground(Color.WHITE);
        JButton logout = Dashboard.button("Logout", Dashboard.DANGER);
        logout.setMnemonic(KeyEvent.VK_O);
        logout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Log out now?", "Logout",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dispose();
                new LoginForm().setVisible(true);
            }
        });
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        right.add(welcome);
        right.add(logout);
        header.add(title, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.LEFT);
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));
        tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabs.setBackground(Color.WHITE);
        tabs.setForeground(PRIMARY);
        tabs.setPreferredSize(new Dimension(200, 0));
        tabs.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        if (user.canManageSystem()) tabs.addTab("Dashboard", new AdminHomePanel());
        tabs.addTab("Book a Court", new BookingPanel(user));
        tabs.addTab(user.canManageSystem() ? "All Bookings" : "My Bookings", new BookingsPanel(user));
        if (user.canManageSystem()) {
            tabs.addTab("Reports", new ReportsPanel());
            tabs.addTab("Users", new UsersPanel(user));
        }
        tabs.addTab("My Account", new AccountPanel(user));
        for (int i = 0; i < tabs.getTabCount() && i < 9; i++) {
            tabs.setMnemonicAt(i, KeyEvent.VK_1 + i);
        }
        tabs.addChangeListener(e -> {
            Component c = tabs.getSelectedComponent();
            if (c instanceof Refreshable) ((Refreshable) c).refresh();
        });


        setLayout(new BorderLayout(0, 0));
        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    interface Refreshable { void refresh(); }

    static class BookingPanel extends JPanel implements Refreshable {
        private final Person user;
        private YearMonth month = YearMonth.now();
        private LocalDate selectedDate = LocalDate.now();
        private int selectedCourt = 1;
        private final TreeSet<Integer> selectedHours = new TreeSet<>();
    
        private final JPanel calendarGrid = new JPanel(new GridLayout(0, 7, 3, 3));
        private final JLabel monthLabel = new JLabel("", SwingConstants.CENTER);
        private final JButton prevBtn = Dashboard.button("<", Dashboard.NEUTRAL);
        private final JButton nextBtn = Dashboard.button(">",  Dashboard.NEUTRAL);
        private final JPanel slotGrid = new JPanel(new GridLayout(0, 2, 6, 6));
        private final JPanel courtSelector = new JPanel(new GridLayout(1, 5, 6, 0));
        private final JButton[] courtButtons = new JButton[BookingSystem.COURT_COUNT];
        private final JLabel dateTitle = new JLabel();
        private final JLabel summaryLabel = new JLabel();
        private final JTextField nameField = new JTextField(16);
        private final JTextField contactField = new JTextField(12);
        private final JButton payBtn = Dashboard.button("Proceed to Payment", Dashboard.PRIMARY);
        private final JButton clearBtn = Dashboard.button("Clear Selection",Dashboard.NEUTRAL);
    
        public BookingPanel(Person user) {
            this.user = user;
            setLayout(new BorderLayout(12, 12));
            setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            setBackground(Dashboard.BG);
            add(buildLeft(), BorderLayout.WEST);
            add(buildCenter(), BorderLayout.CENTER);
            add(buildBottom(), BorderLayout.SOUTH);
            nameField.setText(user.getFullName());
            contactField.setText(user.getContact());
            refresh();

            new javax.swing.Timer(15000, e -> { if (isShowing()) refresh(); }).start();
        }
    
        private JPanel buildLeft() {
            JPanel left = new JPanel(new BorderLayout(0, 10));
            left.setPreferredSize(new Dimension(350, 0));
            left.setOpaque(false);
    
            JPanel cal = new JPanel(new BorderLayout(0, 8));
            cal.setBackground(Color.WHITE);
            cal.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(210, 215, 210)),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)));
            monthLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
            monthLabel.setHorizontalAlignment(SwingConstants.CENTER);
            monthLabel.setForeground(Dashboard.PRIMARY);
            monthLabel.setMinimumSize(new Dimension(125, 36));
            monthLabel.setPreferredSize(new Dimension(140, 36));

            JPanel nav = new JPanel(new BorderLayout(6, 0));
            nav.setOpaque(false);
            Dimension navButtonSize = new Dimension(38, 36);
            prevBtn.setPreferredSize(navButtonSize);
            prevBtn.setMinimumSize(navButtonSize);
            nextBtn.setPreferredSize(navButtonSize);
            nextBtn.setMinimumSize(navButtonSize);
            prevBtn.setMargin(new Insets(4, 4, 4, 4));
            nextBtn.setMargin(new Insets(4, 4, 4, 4));
            nav.add(prevBtn, BorderLayout.WEST);
            nav.add(monthLabel, BorderLayout.CENTER);
            nav.add(nextBtn, BorderLayout.EAST);
            prevBtn.addActionListener(e -> { month = month.minusMonths(1); rebuildCalendar(); });
            nextBtn.addActionListener(e -> { month = month.plusMonths(1); rebuildCalendar(); });
            cal.add(nav, BorderLayout.NORTH);
            calendarGrid.setOpaque(false);
            cal.add(calendarGrid, BorderLayout.CENTER);
            left.add(cal, BorderLayout.CENTER);
    
            JPanel info = new JPanel();
            info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
            info.setBackground(Color.WHITE);
            info.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(210, 215, 210)),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));
            JLabel legendTitle = new JLabel("LEGEND");
            legendTitle.setFont(Dashboard.BOLD);
            info.add(legendTitle);
            info.add(Box.createVerticalStrut(6));
            info.add(legendItem(Dashboard.AVAILABLE, "Available"));
            info.add(legendItem(Dashboard.BOOKED, "Booked or on hold (held " + BookingSystem.HOLD_MINUTES + " min)"));
            info.add(legendItem(Dashboard.SELECTED, "Your selected slot / chosen date"));
            info.add(legendItem(Dashboard.DISABLED, "Past or not yet open"));
            info.add(Box.createVerticalStrut(10));
            JLabel rates = new JLabel("<html><b>RATES (per hour)</b><br>"
                    + BookingSystem.PESO + "400 : 8:00 AM - 5:00 PM<br>"
                    + BookingSystem.PESO + "500 : 5:00 PM - 12:00 AM<br>"
                    + "</html>");
            rates.setFont(Dashboard.SMALL);
            info.add(rates);
            left.add(info, BorderLayout.SOUTH);
            return left;
        }
    
        private JPanel legendItem(Color c, String text) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
            row.setOpaque(false);
            row.setAlignmentX(LEFT_ALIGNMENT);
            JLabel box = new JLabel();
            box.setOpaque(true);
            box.setBackground(c);
            box.setPreferredSize(new Dimension(18, 18));
            JLabel l = new JLabel(text);
            l.setFont(Dashboard.SMALL);
            row.add(box);
            row.add(l);
            return row;
        }
    
        private JPanel buildCenter() {
            JPanel center = new JPanel(new BorderLayout(0, 10));
            center.setOpaque(false);

            JPanel top = new JPanel(new BorderLayout(0, 10));
            top.setOpaque(false);
            dateTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
            dateTitle.setForeground(Dashboard.PRIMARY);
            top.add(dateTitle, BorderLayout.NORTH);

            courtSelector.setOpaque(false);
            for (int i = 0; i < BookingSystem.COURT_COUNT; i++) {
                final int courtId = i + 1;
                JButton courtButton = new JButton("Court " + courtId);
                courtButton.setFont(Dashboard.BOLD);
                courtButton.setFocusPainted(false);
                courtButton.setOpaque(true);
                courtButton.setContentAreaFilled(true);
                courtButton.setBorder(BorderFactory.createLineBorder(new Color(190, 205, 190), 1));
                courtButton.setPreferredSize(new Dimension(100, 42));
                courtButton.addActionListener(e -> {
                    selectedCourt = courtId;
                    selectedHours.clear();
                    updateCourtButtons();
                    rebuildSlots();
                });
                courtButtons[i] = courtButton;
                courtSelector.add(courtButton);
            }
            top.add(courtSelector, BorderLayout.CENTER);
            center.add(top, BorderLayout.NORTH);

            slotGrid.setBackground(Color.WHITE);
            slotGrid.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            JScrollPane sp = new JScrollPane(slotGrid);
            sp.getVerticalScrollBar().setUnitIncrement(16);
            center.add(sp, BorderLayout.CENTER);
            updateCourtButtons();
            return center;
        }

        private void updateCourtButtons() {
            for (int i = 0; i < courtButtons.length; i++) {
                JButton b = courtButtons[i];
                if (b == null) continue;
                boolean active = selectedCourt == i + 1;
                b.setBackground(active ? Dashboard.PRIMARY : Color.WHITE);
                b.setForeground(active ? Color.WHITE : Dashboard.PRIMARY);
                b.setBorder(BorderFactory.createLineBorder(active ? Dashboard.PRIMARY : new Color(190, 205, 190), 1));
            }
        }
    
        private JPanel buildBottom() {
            JPanel bottom = new JPanel(new BorderLayout(18, 0));
            bottom.setBackground(Color.WHITE);
            bottom.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(210, 215, 210)),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)));
    
            JPanel fields = new JPanel(new GridLayout(2, 2, 8, 6));
            fields.setOpaque(false);
            fields.add(Dashboard.label("Customer name:"));
            fields.add(nameField);
            fields.add(Dashboard.label("Contact no.:"));
            fields.add(contactField);
       
            nameField.setFont(Dashboard.FONT);
            contactField.setFont(Dashboard.FONT);
            fields.setPreferredSize(new Dimension(400, 66));
    
            summaryLabel.setFont(Dashboard.FONT);
    
            payBtn.setMnemonic('P');
            clearBtn.setMnemonic('C');
            clearBtn.addActionListener(e -> { clearSelection(); rebuildSlots(); });
            payBtn.addActionListener(e -> proceed());
            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            btns.setOpaque(false);
            btns.add(clearBtn);
            btns.add(payBtn);
    
            bottom.add(fields, BorderLayout.WEST);
            bottom.add(summaryLabel, BorderLayout.CENTER);
            bottom.add(btns, BorderLayout.EAST);
            return bottom;
        }
    
        @Override public void refresh() {
            BookingSystem.expireStaleHolds();
            rebuildCalendar();
            rebuildSlots();
        }
    
        private void rebuildCalendar() {
            monthLabel.setText(month.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
            prevBtn.setEnabled(month.isAfter(YearMonth.now()));
            nextBtn.setEnabled(month.isBefore(YearMonth.from(LocalDate.now().plusDays(BookingSystem.BOOKING_WINDOW_DAYS))));
            calendarGrid.removeAll();
            for (String d : new String[]{"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"}) {
                JLabel l = new JLabel(d, SwingConstants.CENTER);
                l.setFont(Dashboard.BOLD);
                calendarGrid.add(l);
            }
            int offset = month.atDay(1).getDayOfWeek().getValue() % 7;
            for (int i = 0; i < offset; i++) calendarGrid.add(new JLabel());
            for (int day = 1; day <= month.lengthOfMonth(); day++) {
                LocalDate d = month.atDay(day);
                JButton b = new JButton(String.valueOf(day));
                b.setFont(Dashboard.BOLD);
                b.setForeground(Color.WHITE);
                b.setOpaque(true);
                b.setContentAreaFilled(true);
                b.setFocusPainted(false);
                b.setPreferredSize(new Dimension(38, 36));
                boolean inWindow = BookingSystem.isWithinWindow(d);
                if (!inWindow) {
                    b.setBackground(Dashboard.DISABLED);
                   
                } else if (BookingSystem.availableSlots(d) == 0) {
                    b.setBackground(Dashboard.BOOKED);
                 
                } else {
                    b.setBackground(Dashboard.AVAILABLE);
                  
                }
                if (d.equals(selectedDate))
                    b.setBorder(new LineBorder(Dashboard.SELECTED, 4));
                else
                    b.setBorder(new LineBorder(Color.WHITE, 1));
                if (inWindow) {
                    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    b.addActionListener(e -> {
                        selectedDate = d;
                        clearSelection();
                        rebuildCalendar();
                        rebuildSlots();
                    });
                }
                calendarGrid.add(b);
            }
            calendarGrid.revalidate();
            calendarGrid.repaint();
        }
    
        private void rebuildSlots() {
            slotGrid.removeAll();
            dateTitle.setText("Select a time slot — " + selectedDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
            slotGrid.add(headerCell("TIME"));
            slotGrid.add(headerCell("COURT " + selectedCourt + " • HOURLY RATE"));

            for (int h = BookingSystem.FIRST_HOUR; h <= BookingSystem.LAST_HOUR; h++) {
                JLabel t = new JLabel(BookingSystem.label(h) + " - " + BookingSystem.label(h + 1), SwingConstants.CENTER);
                t.setFont(Dashboard.FONT);
                t.setOpaque(true);
                t.setBackground(Color.WHITE);
                t.setBorder(BorderFactory.createLineBorder(new Color(225, 230, 225), 1));
                slotGrid.add(t);
                slotGrid.add(slotButton(selectedCourt, h));
            }
            updateCourtButtons();
            slotGrid.revalidate();
            slotGrid.repaint();
            updateSummary();
        }
    
        private JLabel headerCell(String text) {
            JLabel l = new JLabel(text, SwingConstants.CENTER);
            l.setOpaque(true);
            l.setBackground(Dashboard.PRIMARY);
            l.setForeground(Color.WHITE);
            l.setFont(Dashboard.BOLD);
            l.setPreferredSize(new Dimension(100, 34));
            return l;
        }
    
        private JButton slotButton(int court, int h) {
            BookingSystem.SlotStatus st = BookingSystem.slotStatus(court, selectedDate, h);
            boolean sel = selectedCourt == court && selectedHours.contains(h);
            JButton b = new JButton();
            b.setFont(Dashboard.BOLD);
            b.setForeground(Color.WHITE);
            b.setOpaque(true);
            b.setContentAreaFilled(true);
            b.setFocusPainted(false);
            b.setBorder(new LineBorder(Color.WHITE, 1));
            b.setPreferredSize(new Dimension(100, 30));
            if (sel) {
                b.setBackground(Dashboard.SELECTED);
                b.setText("Selected");
               
            } else {
                switch (st) {
                    case AVAILABLE:
                        b.setBackground(Dashboard.AVAILABLE);
                        b.setText(BookingSystem.PESO + BookingSystem.rate(h));
                       
                        break;
                    case BOOKED:
                        b.setBackground(Dashboard.BOOKED);
                        b.setText("Booked");
                        
                        break;
                    case CLOSED:
                        b.setBackground(Dashboard.DISABLED);
                        b.setText("Closed");
                       
                        break;
                    default:
                        b.setBackground(Dashboard.DISABLED);
                        b.setText("");
                       
                }
            }
            if (sel || st == BookingSystem.SlotStatus.AVAILABLE) {
                b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                b.addActionListener(e -> toggle(court, h));
            }
            return b;
        }
    
        private void toggle(int courtId, int hour) {
            if (selectedCourt != courtId) {
                selectedHours.clear();
                selectedCourt = courtId;
            }
            if (!selectedHours.add(hour)) selectedHours.remove(hour);
            if (selectedHours.isEmpty()) { }
            rebuildSlots();
        }
    
        private void clearSelection() {
            selectedHours.clear();
        }
    
        private void updateSummary() {
            if (selectedHours.isEmpty()) {
                summaryLabel.setText("<html>&nbsp;&nbsp;Click green slots to select (one court per booking).</html>");
                payBtn.setEnabled(false);
                clearBtn.setEnabled(false);
            } else {
                int total = BookingSystem.computeTotal(selectedHours);
                summaryLabel.setText("<html>&nbsp;&nbsp;<b>Court " + selectedCourt + "</b> | "
                        + selectedDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) + "<br>&nbsp;&nbsp;"
                        + BookingSystem.formatRanges(new ArrayList<>(selectedHours)) + "<br>&nbsp;&nbsp;"
                        + "<b>Total: " + BookingSystem.PESO + String.format("%,d", total) + "</b> ("
                        + selectedHours.size() + " hr)</html>");
                payBtn.setEnabled(true);
                clearBtn.setEnabled(true);
            }
        }
    
        private void proceed() {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();
            try {
                BookingSystem.validateName(name);
                BookingSystem.validateContact(contact);
            } catch (Exception ex) {
                Dashboard.error(this, ex.getMessage());
                return;
            }
            if (selectedHours.isEmpty()) {
                Dashboard.error(this, "Please select at least one available time slot.");
                return;
            }
            Booking held;
            try {
                held = BookingSystem.holdBooking(user, name, contact, selectedCourt, selectedDate,
                        new ArrayList<>(selectedHours));
            } catch (Exception ex) {
                Dashboard.error(this, ex.getMessage());
                refresh();
                return;
            }
            clearSelection();
            refresh();

            PaymentDialog dlg = new PaymentDialog(SwingUtilities.getWindowAncestor(this), user, held);
            dlg.setVisible(true);
            Booking b = dlg.getResult();
            if (b != null) {
                Payment p = BookingSystem.paymentFor(b.getId());
                String msg = "Your court has been booked successfully!\n\n"
                        + "Booking ID:  " + b.getId() + "\n"
                        + "Customer:    " + b.getCustomerName() + "\n"
                        + "Court:       Court " + b.getCourtId() + " \n"
                        + "Date:        " + b.getDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")) + "\n"
                        + "Time:        " + b.getTimeSummary() + "\n"
                        + "Total:       " + BookingSystem.money(b.getTotal()) + "\n"
                        + "Payment ID:  " + p.getId() + "\n"
                        + "Reference:   " + p.getReference() + "  (QR)\n"
                        + "Status:      " + p.getStatus() + " (the admin will verify your reference number)\n\n"
                        + "No refund, no rebooking.\n"
                        + "Thank you and enjoy your game!";
                JOptionPane.showMessageDialog(this, msg, "Booking Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                BookingSystem.expireStaleHolds();
                if (Booking.HELD.equals(held.getStatus())) {
                    JOptionPane.showMessageDialog(this,
                            "Your slot is still held until " + held.getHoldExpiresAt().format(DateTimeFormatter.ofPattern("h:mm a"))
                            + ".\nOpen My Bookings to pay for it or cancel it.",
                            "Slot On Hold", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            refresh();
        }
    }

    static class PaymentDialog extends JDialog {
        private final Person user;
        private final Booking booking;
        private final JTextField refField = new JTextField();
        private final JLabel timerLabel = new JLabel("", SwingConstants.CENTER);
        private final javax.swing.Timer timer;
        private Booking result;

        public PaymentDialog(Window owner, Person user, Booking booking) {
            super(owner, "Payment - Scan QR", ModalityType.APPLICATION_MODAL);
            this.user = user;
            this.booking = booking;

            JPanel root = new JPanel(new BorderLayout(0, 10));
            root.setBorder(BorderFactory.createEmptyBorder(14, 20, 12, 20));

            timerLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            timerLabel.setOpaque(true);
            timerLabel.setBackground(new Color(255, 248, 225));
            timerLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

            JLabel summary = new JLabel("<html><b style='font-size:14px'>Booking Summary</b> (" + booking.getId() + ")<br>"
                    + "Customer: " + booking.getCustomerName() + " | Contact: " + booking.getContact() + "<br>"
                    + "Court " + booking.getCourtId() + " | "
                    + booking.getDate().format(DateTimeFormatter.ofPattern("EEE, MMM d, yyyy")) + "<br>"
                    + booking.getTimeSummary() + "<br>"
                    + "<b style='font-size:15px'>Amount to pay: " + BookingSystem.money(booking.getTotal()) + "</b></html>");
            summary.setFont(Dashboard.FONT);
            summary.setOpaque(true);
            summary.setBackground(Dashboard.PRIMARY_LIGHT);
            summary.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            JPanel north = new JPanel(new BorderLayout(0, 8));
            north.add(timerLabel, BorderLayout.NORTH);
            north.add(summary, BorderLayout.CENTER);
            root.add(north, BorderLayout.NORTH);

            JPanel center = new JPanel();
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
            JLabel how = new JLabel("<html><div style='text-align:center'><b>Scan to pay</b><br>"
                    + "Scan the QR code with GCash, Maya or any bank app and pay the exact amount.<br>"
                    + "Then type the reference number from your receipt below.<br> "
                    + "The admin will verify it after you confirm. </div></html>", SwingConstants.CENTER);
            how.setFont(Dashboard.SMALL);
            how.setAlignmentX(CENTER_ALIGNMENT);
            center.add(how);
            center.add(Box.createVerticalStrut(8));
            JComponent qr = qrComponent();
            qr.setAlignmentX(CENTER_ALIGNMENT);
            center.add(qr);
            center.add(Box.createVerticalStrut(10));
            JPanel refRow = new JPanel(new BorderLayout(8, 0));
            refRow.add(Dashboard.label("Reference no.:"), BorderLayout.WEST);
            refField.setFont(Dashboard.FONT);
            refRow.add(refField, BorderLayout.CENTER);
            refRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            center.add(refRow);
            center.add(Box.createVerticalStrut(10));

            JLabel note = new JLabel("<html><b>NOTE: NO REFUND, NO REBOOKING.</b> If you booked by mistake, "
                    + "click Cancel Booking and book again.</html>");
            note.setFont(Dashboard.SMALL);
            note.setForeground(Dashboard.DANGER);
            JPanel noteRow = new JPanel(new BorderLayout());
            noteRow.add(note, BorderLayout.CENTER);
            noteRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            center.add(noteRow);
            root.add(center, BorderLayout.CENTER);

            JButton pay = Dashboard.button("Submit for Approval", Dashboard.PRIMARY);
            JButton back = Dashboard.button("Back", Dashboard.NEUTRAL);
            JButton cancel = Dashboard.button("Cancel Booking", Dashboard.DANGER);
            pay.addActionListener(e -> pay());
            back.addActionListener(e -> dispose());
            cancel.addActionListener(e -> cancelHold());
            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            btns.add(cancel);
            btns.add(back);
            btns.add(pay);
            root.add(btns, BorderLayout.SOUTH);

            setContentPane(root);
            getRootPane().setDefaultButton(pay);
            pack();
            setSize(Math.max(getWidth(), 560), getHeight());
            setLocationRelativeTo(owner);

            timer = new javax.swing.Timer(1000, e -> tick());
            timer.start();
            tick();
        }

        private void tick() {
            long secs = java.time.Duration.between(LocalDateTime.now(), booking.getHoldExpiresAt()).getSeconds();
            if (secs <= 0) {
                timer.stop();
                BookingSystem.expireStaleHolds();
                JOptionPane.showMessageDialog(this,
                        "Your " + BookingSystem.HOLD_MINUTES + "-minute hold has expired.\n"
                        + "The slot was released for other players. You can book again if it is still available.",
                        "Hold Expired", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                return;
            }
            timerLabel.setText(String.format("Hold expires in %02d:%02d", secs / 60, secs % 60));
            timerLabel.setForeground(secs <= 60 ? Dashboard.DANGER : Dashboard.PRIMARY);
        }

        @Override public void dispose() {
            if (timer != null) timer.stop();
            super.dispose();
        }

        private void cancelHold() {
            if (JOptionPane.showConfirmDialog(this,
                    "Cancel this booking?\nThe slot will be released so other players (or you) can book it again.",
                    "Cancel Booking", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
            try {
                BookingSystem.cancelBooking(user, booking.getId());
                dispose();
            } catch (Exception ex) {
                Dashboard.error(this, ex.getMessage());
            }
        }

        
        private JComponent qrComponent() {
            JLabel l = new JLabel("", SwingConstants.CENTER);

            java.net.URL imageURL = Dashboard.class.getResource("/qrcode.jpg");

            if (imageURL != null) {
                ImageIcon ic = new ImageIcon(imageURL);

                if (ic.getIconWidth() > 0) {
                    double sc = Math.min(
                        200.0 / ic.getIconWidth(),
                        200.0 / ic.getIconHeight()
                    );

                    Image img = ic.getImage().getScaledInstance(
                        (int) (ic.getIconWidth() * sc),
                        (int) (ic.getIconHeight() * sc),
                        Image.SCALE_SMOOTH
                    );

                    l.setIcon(new ImageIcon(img));
                    return l;
                }
            }

    l.setPreferredSize(new Dimension(260, 260));
    l.setBorder(BorderFactory.createLineBorder(Color.GRAY));

    return l;
}


        private void showTerms() {
            JTextArea area = new JTextArea(TERMS_AND_POLICY);
            area.setEditable(false);
            area.setLineWrap(true);
            area.setWrapStyleWord(true);
            area.setFont(Dashboard.FONT);
            JScrollPane sp = new JScrollPane(area);
            sp.setPreferredSize(new Dimension(560, 450));
            area.setCaretPosition(0);
            JOptionPane.showMessageDialog(this, sp, "Terms and Policy", JOptionPane.PLAIN_MESSAGE);
        }

        private boolean confirmBooking(String ref) {
            JLabel details = new JLabel("<html><b>Please check your booking before confirming.</b><br><br>"
                    + "Reference no.: <b>" + ref + "</b><br>"
                    + "Court: Court " + booking.getCourtId() + "<br>"
                    + "Date: " + booking.getDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")) + "<br>"
                    + "Time: " + booking.getTimeSummary() + "<br>"
                    + "Amount paid: " + BookingSystem.money(booking.getTotal()) + "<br><br>"
                    + "Is the reference number exactly the same as your receipt?<br>"
                            + "If yes click OK and wait for verification. </html>");
            details.setFont(Dashboard.FONT);

            JCheckBox agreeBox = new JCheckBox("I have read and agree to the");
            agreeBox.setFont(Dashboard.SMALL);
            JButton termsLink = new JButton("Terms and Policy");
            termsLink.setFont(Dashboard.SMALL);
            termsLink.setForeground(Color.BLUE);
            termsLink.setBorderPainted(false);
            termsLink.setContentAreaFilled(false);
            termsLink.setMargin(new Insets(0, 4, 0, 0));
            termsLink.addActionListener(e -> showTerms());
            JPanel termsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            termsRow.add(agreeBox);
            termsRow.add(termsLink);

            JLabel note = new JLabel("<html><b>NOTE: NO REFUND, NO REBOOKING.</b><br>"
                    + "If you booked by mistake, click Cancel and book again.</html>");
            note.setFont(Dashboard.SMALL);
            note.setForeground(Dashboard.DANGER);

            JPanel panel = new JPanel(new BorderLayout(0, 10));
            panel.add(details, BorderLayout.NORTH);
            panel.add(termsRow, BorderLayout.CENTER);
            panel.add(note, BorderLayout.SOUTH);

            while (true) {
                int answer = JOptionPane.showConfirmDialog(this, panel, "Confirm Booking",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (answer != JOptionPane.OK_OPTION) return false;
                if (agreeBox.isSelected()) return true;
                Dashboard.error(this, "Please tick the box to agree to the Terms and Policy.");
            }
        }

        private void pay() {
            String ref = refField.getText().trim();
            try {
                BookingSystem.validateReference(ref);
            } catch (Exception ex) {
                Dashboard.error(this, ex.getMessage());
                return;
            }
            if (!confirmBooking(ref)) return;
            try {
                BookingSystem.submitPayment(user, booking.getId(), ref);
                result = booking;
                dispose();
            } catch (Exception ex) {
                Dashboard.error(this, ex.getMessage());
                if (Booking.EXPIRED.equals(booking.getStatus())) dispose();
            }
        }

        public Booking getResult() { return result; }
    }

    static class BookingsPanel extends JPanel implements Refreshable {
        private final Person user;
        private final DefaultTableModel model = new DefaultTableModel(new String[]{
                "Booking ID", "Customer", "Contact", "Court", "Date", "Time", "Payment ID", "Reference No.", "Total Amount", "Payment Status", "Booked On", "Booking Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        private final JTable table = new JTable(model);
        private final JTextField search = new JTextField(20);
        private final JLabel count = new JLabel();
        private final JCheckBox showHistory = new JCheckBox("Show expired / cancelled");
    
        public BookingsPanel(Person user) {
            this.user = user;
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            setBackground(Dashboard.BG);
    
            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.add(Dashboard.heading(user.canManageSystem() ? "All Bookings" : "My Bookings"), BorderLayout.WEST);
            JPanel s = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
            s.setOpaque(false);
            showHistory.setFont(Dashboard.SMALL);
            showHistory.setOpaque(false);
            showHistory.addActionListener(e -> refresh());
            s.add(showHistory);
            s.add(Dashboard.label("Search (ID, name, date, reference):"));
            search.setFont(Dashboard.FONT);
           
            s.add(search);
            top.add(s, BorderLayout.EAST);
            add(top, BorderLayout.NORTH);
    
            Dashboard.styleTable(table);
            table.getColumnModel().getColumn(9).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                    Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                    comp.setFont(Dashboard.BOLD);
                    if (!sel) comp.setForeground("Verified".equals(v) ? Dashboard.AVAILABLE : new Color(200, 120, 0));
                    return comp;
                }
            });
            table.getColumnModel().getColumn(11).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
                @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
                    Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                    comp.setFont(Dashboard.BOLD);
                    String st = String.valueOf(v);
                    if (!sel) comp.setForeground(st.startsWith("Booked") ? Dashboard.AVAILABLE
                            : st.startsWith("Held") ? new Color(200, 120, 0) : Dashboard.NEUTRAL);
                    return comp;
                }
            });
            table.moveColumn(11, 1);   
            add(new JScrollPane(table), BorderLayout.CENTER);
    
            JPanel bottom = new JPanel(new BorderLayout());
            bottom.setOpaque(false);
            bottom.add(count, BorderLayout.WEST);

            JButton reschedule = Dashboard.button("Reschedule Booking", Dashboard.PRIMARY);
            reschedule.addActionListener(e -> reschedule());
            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            btns.setOpaque(false);
            JButton payNow = Dashboard.button("Pay Now", Dashboard.AVAILABLE);
            payNow.addActionListener(e -> payNow());
            JButton cancelBtn = Dashboard.button("Cancel Booking", Dashboard.DANGER);
            cancelBtn.addActionListener(e -> cancelSelected());
            if (!user.canManageSystem()) btns.add(payNow);
            if (user.canManageSystem()) btns.add(reschedule);
            btns.add(cancelBtn);

            if (user.canManageSystem()) {
                JButton verify = Dashboard.button("Verify Payment", Dashboard.AVAILABLE);
                verify.addActionListener(e -> verifyPayment());
                btns.add(verify);

                JButton reject = Dashboard.button("Reject Payment", new Color(180, 65, 65));
                reject.addActionListener(e -> rejectPayment());
                btns.add(reject);

                JButton editCustomer = Dashboard.button("Edit Customer Details",Dashboard.PRIMARY);
                editCustomer.addActionListener(e -> editCustomerDetails());
                btns.add(editCustomer);
            }
            bottom.add(btns, BorderLayout.EAST);
            add(bottom, BorderLayout.SOUTH);
            
            new javax.swing.Timer(15000, e -> { if (isShowing()) refresh(); }).start();
            search.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { refresh(); }
                public void removeUpdate(DocumentEvent e) { refresh(); }
                public void changedUpdate(DocumentEvent e) { refresh(); }
            });
            refresh();
        }
    
           // when reloads, the selected booking is still on hold
        @Override public void refresh() {
            BookingSystem.expireStaleHolds();
            
            Object keep = null;   
            int sel = table.getSelectedRow();
            if (sel >= 0) keep = model.getValueAt(table.convertRowIndexToModel(sel), 0);
            model.setRowCount(0);
            String q = search.getText().trim().toLowerCase();
            List<Booking> list = new ArrayList<>(BookingSystem.getBookings());
            list.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
            for (Booking b : list) {
                if (!user.canManageSystem() && !b.getUsername().equals(user.getUsername())) continue;
                boolean inactive = Booking.EXPIRED.equals(b.getStatus()) || Booking.CANCELLED.equals(b.getStatus());
                if (inactive && !showHistory.isSelected()) continue;   // expired/cancelled attempts are hidden by default
                Payment p = BookingSystem.paymentFor(b.getId());
                String hay = (b.getId() + " " + (p == null ? "" : p.getId() + " " + p.getReference()) + " " + b.getCustomerName() + " " + b.getDate() + " Court " + b.getCourtId()).toLowerCase();
                if (!q.isEmpty() && !hay.contains(q)) continue;
                String status = b.getStatus();
                
                // expired payment
                String payStatus = p != null ? p.getStatus()
                        : (Booking.EXPIRED.equals(status) ? "Expired" : "-");
                if (Booking.HELD.equals(status)) {
                    long mins = java.time.Duration.between(LocalDateTime.now(), b.getHoldExpiresAt()).toMinutes();
                    status = "Held (" + Math.max(0, mins) + " min left)";
                }
                model.addRow(new Object[]{b.getId(), b.getCustomerName(), b.getContact(), "Court " + b.getCourtId(),
                        b.getDate(), b.getTimeSummary(),
                        p == null ? "-" : p.getId(), p == null ? "-" : p.getReference(),
                        BookingSystem.money(b.getTotal()), payStatus,
                        b.getCreatedAt().toString().replace('T', ' '), status});
            }
            if (keep != null)
                for (int i = 0; i < model.getRowCount(); i++)
                    if (keep.equals(model.getValueAt(i, 0))) { table.setRowSelectionInterval(i, i); break; }
            count.setText("Records: " + model.getRowCount());
        }

        // reminds user to select the data before clicking button
        private Booking selectedBooking() {
            int row = table.getSelectedRow();
            if (row < 0) { Dashboard.error(this, "Please select a booking first."); return null; }
            Booking b = BookingSystem.findBooking(String.valueOf(model.getValueAt(table.convertRowIndexToModel(row), 0)));
            if (b == null) Dashboard.error(this, "Booking not found.");
            return b;
        }

        //when rejected customer can pay in my bookings page
        private void payNow() {
            Booking b = selectedBooking();
            if (b == null) return;
            BookingSystem.expireStaleHolds();
            if (!b.getUsername().equals(user.getUsername())) { Dashboard.error(this, "You can pay only for your own booking."); return; }
            Payment p = BookingSystem.paymentFor(b.getId());
            if (Booking.HELD.equals(b.getStatus())) {
                PaymentDialog dlg = new PaymentDialog(SwingUtilities.getWindowAncestor(this), user, b);
                dlg.setVisible(true);
                if (dlg.getResult() != null)
                    JOptionPane.showMessageDialog(this, "Payment submitted! The admin will verify your reference number.",
                            "Booking Successful", JOptionPane.INFORMATION_MESSAGE);
            } else if (Booking.BOOKED.equals(b.getStatus()) && p != null && "Rejected".equalsIgnoreCase(p.getStatus())) {
                String ref = JOptionPane.showInputDialog(this,
                        "This payment was rejected: " + p.getRejectionReason()
                        + "\n\nEnter the correct reference number:", "Resubmit Payment", JOptionPane.QUESTION_MESSAGE);
                if (ref != null) {
                    try {
                        BookingSystem.resubmitPayment(user, b.getId(), ref);
                        JOptionPane.showMessageDialog(this, "Payment resubmitted. The admin will verify it again.",
                                "Payment Resubmitted", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        Dashboard.error(this, ex.getMessage());
                    }
                }
            } else if (Booking.EXPIRED.equals(b.getStatus())) {
                Dashboard.error(this, "This hold expired and the slot was released. Please book again from Book a Court.");
            } else {
                Dashboard.error(this, "Nothing to pay for this booking.");
            }
            refresh();
        }

        private void cancelSelected() {
            Booking b = selectedBooking();
            if (b == null) return;
            BookingSystem.expireStaleHolds();
            String msg;
            if (Booking.HELD.equals(b.getStatus())) {
                msg = "Cancel booking " + b.getId() + "?\nThe slot will be released so you can book a different date or time.";
            } else if (Booking.BOOKED.equals(b.getStatus()) && user.canManageSystem()) {
                msg = "Cancel PAID booking " + b.getId() + "?\nThe slot will be released. NO REFUND will be given.";
            } else if (Booking.BOOKED.equals(b.getStatus())) {
                Dashboard.error(this, "This booking is already paid.\nNo cancellation, no rebooking and no refund.");
                return;
            } else {
                Dashboard.error(this, "This booking is already " + b.getStatus().toLowerCase() + ".");
                refresh();
                return;
            }
            if (JOptionPane.showConfirmDialog(this, msg, "Cancel Booking", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;
            try {
                BookingSystem.cancelBooking(user, b.getId());
                refresh();
            } catch (Exception ex) {
                Dashboard.error(this, ex.getMessage());
            }
        }

        private String balanceNote(String paymentId) {
            Payment p = BookingSystem.findPayment(paymentId);
            if (p == null || p.getBalance() <= 0) return "";
            return "\n\nSTILL OWED: " + BookingSystem.money(p.getBalance())
                    + " (from a reschedule). Collect it before verifying.";
        }

        private void verifyPayment() {
            int row = table.getSelectedRow();
            if (row < 0) { Dashboard.error(this, "Please select a booking first."); return; }
            String paymentId = (String) model.getValueAt(row, 6);
            if ("-".equals(paymentId)) { Dashboard.error(this, "This booking has no payment record."); return; }
            if ("Verified".equals(model.getValueAt(row, 9))) {
                JOptionPane.showMessageDialog(this, "This payment is already verified.", "Verify Payment", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            String msg = "Verify this payment?\n\nPayment ID: " + paymentId
                    + "\nReference No.: " + model.getValueAt(row, 7)
                    + "\nAmount: " + model.getValueAt(row, 8)
                    + balanceNote(paymentId)
                    + "\n\nClick Yes only after you found this reference number and amount\nin the transaction history of your QR Ph account.";
            if (JOptionPane.showConfirmDialog(this, msg, "Verify Payment", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) return;
            try {
                BookingSystem.verifyPayment(user, paymentId);
                JOptionPane.showMessageDialog(this, "Payment " + paymentId + " is now Verified.", "Verified", JOptionPane.INFORMATION_MESSAGE);
                refresh();
            } catch (Exception ex) {
                Dashboard.error(this, ex.getMessage());
            }
        }


        private void rejectPayment() {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                Dashboard.error(this, "Please select a booking first.");
                return;
            }

            int row = table.convertRowIndexToModel(selectedRow);
            String paymentId = String.valueOf(model.getValueAt(row, 6));
            if ("-".equals(paymentId) || paymentId.isBlank()) {
                Dashboard.error(this, "This booking has no payment record.");
                return;
            }

            String status = String.valueOf(model.getValueAt(row, 9));
            if ("Verified".equalsIgnoreCase(status)) {
                Dashboard.error(this, "A verified payment cannot be rejected.");
                return;
            }
            if ("Rejected".equalsIgnoreCase(status)) {
                Dashboard.error(this, "This payment has already been rejected.");
                return;
            }

            String reason = JOptionPane.showInputDialog(
                    this,
                    "Enter why the payment is being rejected (for example, reference number not found or amount is incorrect):",
                    "Reject Payment",
                    JOptionPane.WARNING_MESSAGE
            );
            if (reason == null) return;
            reason = reason.trim();
            if (reason.isEmpty()) {
                Dashboard.error(this, "A rejection reason is required.");
                return;
            }

            String msg = "Reject this payment?\n\nPayment ID: " + paymentId
                    + "\nReference No.: " + model.getValueAt(row, 7)
                    + "\nAmount: " + model.getValueAt(row, 8)
                    + "\nReason: " + reason;
            if (JOptionPane.showConfirmDialog(this, msg, "Confirm Rejection",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION) return;

            try {
                BookingSystem.rejectPayment(user, paymentId, reason);
                JOptionPane.showMessageDialog(this,
                        "Payment " + paymentId + " has been rejected.",
                        "Payment Rejected", JOptionPane.INFORMATION_MESSAGE);
                refresh();
            } catch (Exception ex) {
                Dashboard.error(this, ex.getMessage());
            }
        }

        private void reschedule() {
            int selectedRow = table.getSelectedRow();
            if (selectedRow < 0) {
                Dashboard.error(this, "Please select a booking first.");
                return;
            }

            int modelRow = table.convertRowIndexToModel(selectedRow);
            String id = String.valueOf(model.getValueAt(modelRow, 0));
            Booking booking = BookingSystem.findBooking(id);
            if (booking == null) {
                Dashboard.error(this, "Booking not found.");
                return;
            }

            if (!Booking.BOOKED.equals(booking.getStatus())) {
                Dashboard.error(this, "Only a paid booking can be rescheduled.");
                return;
            }

            JComboBox<String> courtBox = new JComboBox<>();
            for (int c = 1; c <= BookingSystem.COURT_COUNT; c++) courtBox.addItem("Court " + c);
            courtBox.setSelectedItem("Court " + booking.getCourtId());
            JComboBox<LocalDate> dateBox = new JComboBox<>();
            JPanel slotsPanel = new JPanel(new GridLayout(0, 2, 6, 5));
            slotsPanel.setBorder(BorderFactory.createTitledBorder("Available time slots (select one or more)"));
            List<JCheckBox> slotChecks = new ArrayList<>();

            JPanel panel = new JPanel(new BorderLayout(8, 8));
            JPanel selectors = new JPanel(new GridLayout(0, 2, 8, 8));
            selectors.add(new JLabel("Court:"));
            selectors.add(courtBox);
            selectors.add(new JLabel("Available date:"));
            selectors.add(dateBox);
            panel.add(selectors, BorderLayout.NORTH);
            panel.add(new JScrollPane(slotsPanel), BorderLayout.CENTER);
            JLabel currentLabel = new JLabel("Current schedule: Court " + booking.getCourtId() + ", "
                    + booking.getDate() + ", " + booking.getTimeSummary());
            panel.add(currentLabel, BorderLayout.SOUTH);
            panel.setPreferredSize(new Dimension(480, 440));

            Runnable updateDates = () -> {
                int court = courtBox.getSelectedIndex() + 1;
                LocalDate previous = (LocalDate) dateBox.getSelectedItem();
                dateBox.removeAllItems();
                LocalDate today = LocalDate.now();
                for (int offset = 0; offset <= BookingSystem.BOOKING_WINDOW_DAYS; offset++) {
                    LocalDate d = today.plusDays(offset);
                    boolean hasAvailable = false;
                    for (int h = BookingSystem.FIRST_HOUR; h <= BookingSystem.LAST_HOUR; h++) {
                        if (isAvailableForReschedule(booking, court, d, h)) {
                            hasAvailable = true;
                            break;
                        }
                    }
                    if (hasAvailable) dateBox.addItem(d);
                }
                if (previous != null) dateBox.setSelectedItem(previous);
                if (dateBox.getSelectedIndex() < 0 && dateBox.getItemCount() > 0) {
                    if (booking.getCourtId() == court && dateBox.getItemCount() > 0
                            && containsDate(dateBox, booking.getDate())) {
                        dateBox.setSelectedItem(booking.getDate());
                    } else {
                        dateBox.setSelectedIndex(0);
                    }
                }
            };

            Runnable updateSlots = () -> {
                slotsPanel.removeAll();
                slotChecks.clear();
                Object dateValue = dateBox.getSelectedItem();
                if (!(dateValue instanceof LocalDate)) {
                    slotsPanel.add(new JLabel("No available dates for this court."));
                } else {
                    LocalDate d = (LocalDate) dateValue;
                    int court = courtBox.getSelectedIndex() + 1;
                    for (int h = BookingSystem.FIRST_HOUR; h <= BookingSystem.LAST_HOUR; h++) {
                        boolean available = isAvailableForReschedule(booking, court, d, h);
                        String text = BookingSystem.label(h) + " - " + BookingSystem.label(h + 1);
                        JCheckBox check = new JCheckBox(text + (available ? " (Available)" : " (Booked)"));
                        check.setEnabled(available);
                        check.setSelected(available && court == booking.getCourtId()
                                && d.equals(booking.getDate()) && booking.getHours().contains(h));
                        if (available) slotChecks.add(check);
                        slotsPanel.add(check);
                    }
                }
                slotsPanel.revalidate();
                slotsPanel.repaint();
            };

            courtBox.addActionListener(e -> {
                updateDates.run();
                updateSlots.run();
            });
            dateBox.addActionListener(e -> updateSlots.run());
            updateDates.run();
            updateSlots.run();

            if (dateBox.getItemCount() == 0) {
                Dashboard.error(this, "There are no available dates for rescheduling.");
                return;
            }

            while (true) {
                int answer = JOptionPane.showConfirmDialog(this, panel,
                        "Reschedule Booking " + id, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (answer != JOptionPane.OK_OPTION) return;
                try {
                    Object dateValue = dateBox.getSelectedItem();
                    if (!(dateValue instanceof LocalDate)) throw new Exception("Please select an available date.");
                    LocalDate newDate = (LocalDate) dateValue;
                    int newCourt = courtBox.getSelectedIndex() + 1;
                    List<Integer> newHours = new ArrayList<>();
                    for (int i = 0; i < slotsPanel.getComponentCount(); i++) {
                        Component component = slotsPanel.getComponent(i);
                        if (component instanceof JCheckBox) {
                            JCheckBox check = (JCheckBox) component;
                            if (check.isSelected()) {
                                for (int h = BookingSystem.FIRST_HOUR; h <= BookingSystem.LAST_HOUR; h++) {
                                    if (check.getText().startsWith(BookingSystem.label(h) + " - ")) {
                                        newHours.add(h);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    if (newHours.isEmpty()) throw new Exception("Select at least one time slot.");
                    BookingSystem.rescheduleBooking(user, id, newCourt, newDate, newHours);
                    Booking updated = BookingSystem.findBooking(id);
                    JOptionPane.showMessageDialog(this,
                            "Booking rescheduled successfully.\nNew schedule: Court "
                            + updated.getCourtId() + ", " + updated.getDate() + "\n"
                            + updated.getTimeSummary() + "\nNew total: "
                            + BookingSystem.money(updated.getTotal())
                            + "\n\nIf the new price is higher, collect the difference and verify the payment again.",
                            "Reschedule Successful", JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                    return;
                } catch (Exception ex) {
                    Dashboard.error(this, ex.getMessage());
                }
            }
        }

        private boolean containsDate(JComboBox<LocalDate> combo, LocalDate date) {
            for (int i = 0; i < combo.getItemCount(); i++) {
                if (date.equals(combo.getItemAt(i))) return true;
            }
            return false;
        }

        private boolean isAvailableForReschedule(Booking current, int court, LocalDate date, int hour) {
            if (!BookingSystem.isOpenHour(hour) || BookingSystem.isPast(date, hour)) return false;
            for (Booking other : BookingSystem.getBookings()) {
                if (other.getId().equals(current.getId()) || !other.blocksSlot()) continue;
                if (other.getCourtId() == court && other.getDate().equals(date)
                        && other.getHours().contains(hour)) return false;
            }
            return true;
        }


    private void editCustomerDetails() {
    int selectedRow = table.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(
                this,
                "Please select a booking first."
        );
        return;
    }

    int row = table.convertRowIndexToModel(selectedRow);
    String id = model.getValueAt(row, 0).toString();

    Booking booking = BookingSystem.findBooking(id);

    if (booking == null) {
        JOptionPane.showMessageDialog(this, "Booking not found.");
        return;
    }

    JTextField nameField =
            new JTextField(booking.getCustomerName());

    JTextField contactField =
            new JTextField(booking.getContact());

    JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
    panel.add(new JLabel("Customer Name:"));
    panel.add(nameField);
    panel.add(new JLabel("Contact Number:"));
    panel.add(contactField);

    int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Edit Customer Details",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
    );

    if (result != JOptionPane.OK_OPTION) {
        return;
    }

    try {
        BookingSystem.editCustomerDetails(
                user,
                id,
                nameField.getText(),
                contactField.getText()
        );

        JOptionPane.showMessageDialog(
                this,
                "Customer details updated successfully."
        );

        refresh();

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                ex.getMessage(),
                "Update Failed",
                JOptionPane.ERROR_MESSAGE
        );
    }
}

    }

    // admin dashboard
    static class AdminHomePanel extends JPanel implements Refreshable {
        private final JLabel todayTitle = Dashboard.heading("");
        private final JLabel[] courtLabels = new JLabel[BookingSystem.COURT_COUNT];
        private final JLabel weekLabel = Dashboard.label("");
        private final JLabel monthLabel = Dashboard.label("");

        public AdminHomePanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            setBackground(Dashboard.BG);

            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            top.add(todayTitle, BorderLayout.WEST);
            JButton refreshBtn = Dashboard.button("Refresh", Dashboard.PRIMARY);
            refreshBtn.addActionListener(e -> refresh());
            top.add(refreshBtn, BorderLayout.EAST);
            add(top, BorderLayout.NORTH);

            JPanel list = new JPanel(new GridLayout(0, 1, 0, 8));
            list.setOpaque(false);
            for (int i = 0; i < courtLabels.length; i++) {
                courtLabels[i] = Dashboard.label("");
                list.add(box(courtLabels[i]));
            }
            weekLabel.setFont(Dashboard.BOLD);
            monthLabel.setFont(Dashboard.BOLD);
            list.add(box(weekLabel));
            list.add(box(monthLabel));
            JPanel holder = new JPanel(new BorderLayout());
            holder.setOpaque(false);
            holder.add(list, BorderLayout.NORTH);
            add(holder, BorderLayout.CENTER);
            refresh();
        }

        private JPanel box(JLabel l) {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(Color.WHITE);
            p.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(210, 215, 210)),
                    BorderFactory.createEmptyBorder(12, 14, 12, 14)));
            p.add(l, BorderLayout.CENTER);
            return p;
        }

        @Override public void refresh() {
            BookingSystem.expireStaleHolds();
            LocalDate today = LocalDate.now();
            int slotsPerCourt = BookingSystem.LAST_HOUR - BookingSystem.FIRST_HOUR + 1;
            todayTitle.setText("Today - " + today.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));

            // Today: bookings and booked hours per court (held slots count because nobody else can take them)
            for (int court = 1; court <= BookingSystem.COURT_COUNT; court++) {
                int count = 0, hours = 0;
                for (Booking b : BookingSystem.getBookings()) {
                    if (b.blocksSlot() && b.getCourtId() == court && b.getDate().equals(today)) {
                        count++;
                        hours += b.getHours().size();
                    }
                }
                courtLabels[court - 1].setText("Court " + court + "     Bookings: " + count
                        + "     Slots booked: " + hours + " / " + slotsPerCourt
                        + "     Available: " + (slotsPerCourt - hours));
            }

            // This week (Monday to Sunday): paid bookings and their total
            LocalDate weekStart = today.with(DayOfWeek.MONDAY);
            LocalDate weekEnd = weekStart.plusDays(6);
            int weekCount = 0, weekTotal = 0;
            // This month: revenue from verified payments only
            YearMonth thisMonth = YearMonth.now();
            int monthRevenue = 0;
            for (Booking b : BookingSystem.getBookings()) {
                if (!Booking.BOOKED.equals(b.getStatus())) continue;
                if (!b.getDate().isBefore(weekStart) && !b.getDate().isAfter(weekEnd)) {
                    weekCount++;
                    weekTotal += b.getTotal();
                }
                Payment p = BookingSystem.paymentFor(b.getId());
                if (YearMonth.from(b.getDate()).equals(thisMonth) && p != null && "Verified".equals(p.getStatus()))
                    monthRevenue += b.getTotal();
            }
            weekLabel.setText("This week (" + weekStart.format(DateTimeFormatter.ofPattern("MMM d")) + " - "
                    + weekEnd.format(DateTimeFormatter.ofPattern("MMM d")) + "):     Bookings: " + weekCount
                    + "     Total: " + BookingSystem.money(weekTotal));
            monthLabel.setText("Total revenue for " + thisMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy"))
                    + " (verified payments):     " + BookingSystem.money(monthRevenue));
        }
    }

    // admin reports
    static class ReportsPanel extends JPanel implements Refreshable {
        private final JTextField dateField = new JTextField(LocalDate.now().toString(), 10);
        private final JTextArea out = new JTextArea();
    
        public ReportsPanel() {
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            setBackground(Dashboard.BG);
    
            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            top.setOpaque(false);
            top.add(Dashboard.heading("Reports"));
            top.add(Box.createHorizontalStrut(20));
            top.add(Dashboard.label("Date (yyyy-mm-dd):"));
            dateField.setFont(Dashboard.FONT);
          
            top.add(dateField);
            JButton gen = Dashboard.button("Generate Report",Dashboard.PRIMARY);
            gen.addActionListener(e -> refresh());
            top.add(gen);
            add(top, BorderLayout.NORTH);
    
            out.setEditable(false);
            out.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
            out.setMargin(new Insets(10, 10, 10, 10));
            add(new JScrollPane(out), BorderLayout.CENTER);
            refresh();
        }
    
        @Override public void refresh() {
            LocalDate d;
            try {
                d = LocalDate.parse(dateField.getText().trim());
            } catch (DateTimeParseException ex) {
                Dashboard.error(this, "Invalid date. Use the format yyyy-mm-dd (e.g., 2026-10-13).");
                return;
            }
            List<Booking> list = BookingSystem.bookingsOn(d);
            StringBuilder sb = new StringBuilder();
            sb.append("DAILY BOOKING REPORT - ").append(d.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))).append("\n");
            sb.append("=".repeat(86)).append("\n");
            sb.append(String.format("%-10s %-20s %-8s %-28s %12s%n", "Booking", "Customer", "Court", "Time", "Total"));
            sb.append("-".repeat(86)).append("\n");
            int income = 0, hrs = 0;
            int[] perCourt = new int[BookingSystem.COURT_COUNT + 1];
            for (Booking b : list) {
                income += b.getTotal();
                hrs += b.getHours().size();
                perCourt[b.getCourtId()] += b.getTotal();
                String nm = b.getCustomerName().length() > 20 ? b.getCustomerName().substring(0, 19) + "." : b.getCustomerName();
                sb.append(String.format("%-10s %-20s %-8s %-28s %12s%n", b.getId(), nm, "Court " + b.getCourtId(),
                        b.getTimeSummary(), BookingSystem.money(b.getTotal())));
            }
            if (list.isEmpty()) sb.append("No bookings for this date.\n");
            sb.append("-".repeat(86)).append("\n");
            sb.append(String.format("Total bookings : %d%nTotal hours    : %d%nTotal income   : %s%n%n",
                    list.size(), hrs, BookingSystem.money(income)));
            int pending = 0;
            for (Booking bk : list) {
                Payment pm = BookingSystem.paymentFor(bk.getId());
                if (pm != null && "Pending".equals(pm.getStatus())) pending++;
            }
            sb.append(String.format("Payments pending verification: %d of %d%n%n", pending, list.size()));
            sb.append("Income per court\n");
            for (int court = 1; court <= BookingSystem.COURT_COUNT; court++)
                sb.append(String.format("  %-8s %s%n", "Court " + court, BookingSystem.money(perCourt[court])));
            double all = 0;
            int allCount = 0;
            for (Booking b : BookingSystem.getBookings())
                if (Booking.BOOKED.equals(b.getStatus())) { all += b.getTotal(); allCount++; }
            sb.append(String.format("%nOVERALL (all dates, expired/cancelled excluded): %d bookings, %s%n", allCount, BookingSystem.money(all)));
            out.setText(sb.toString());
            out.setCaretPosition(0);
        }
    }

    // user management
    static class UsersPanel extends JPanel implements Refreshable {
        private final Person admin;
        private final DefaultTableModel model = new DefaultTableModel(new String[]{
                "User ID", "Username", "Full Name", "Contact", "Role (Access Level)", "Status", "Failed Attempts", "Date Created"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        private final JTable table = new JTable(model);
    
        public UsersPanel(Person admin) {
            this.admin = admin;
            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            setBackground(Dashboard.BG);
            add(Dashboard.heading("User Management"), BorderLayout.NORTH);
            Dashboard.styleTable(table);
            add(new JScrollPane(table), BorderLayout.CENTER);
    
            JButton add = Dashboard.button("Add User", Dashboard.PRIMARY);
            JButton block = Dashboard.button("Block User", Dashboard.NEUTRAL);
            JButton unblock = Dashboard.button("Unblock User", Dashboard.AVAILABLE);
            JButton del = Dashboard.button("Delete User", Dashboard.DANGER);
            add.addActionListener(e -> addUser());
            block.addActionListener(e -> block());
            unblock.addActionListener(e -> unblock());
            del.addActionListener(e -> delete());
            JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            btns.setOpaque(false);
            btns.add(add); btns.add(block); btns.add(unblock); btns.add(del);
            add(btns, BorderLayout.SOUTH);
            refresh();
        }
    
        @Override public void refresh() {
            model.setRowCount(0);
            // newest account first (highest user ID on top)
            List<Person> list = new ArrayList<>(BookingSystem.getUsers());
            list.sort((a, b) -> b.getUserId() - a.getUserId());
            for (Person p : list)
                model.addRow(new Object[]{p.getUserId(), p.getUsername(), p.getFullName(), p.getContact(), p.getRole(),
                        p.isBlocked() ? "BLOCKED" : "Active", p.getFailedAttempts(),
                        p.getCreatedAt() == null ? "-" : p.getCreatedAt().toString().replace('T', ' ')});
        }
    
        private String selectedUser() {
            int row = table.getSelectedRow();
            if (row < 0) { Dashboard.error(this, "Please select a user first."); return null; }
            return (String) model.getValueAt(row, 1);   // column 1 = Username
        }
    
        private void addUser() {
            JTextField name = new JTextField(), contact = new JTextField(), user = new JTextField();
            JPasswordField pw = new JPasswordField(), conf = new JPasswordField();
            JComboBox<String> role = new JComboBox<>(new String[]{"Customer", "Admin"});
            while (true) {
                JPanel p = new JPanel(new GridLayout(0, 2, 8, 8));
                p.add(new JLabel("Full name:")); p.add(name);
                p.add(new JLabel("Contact no.:")); p.add(contact);
                p.add(new JLabel("Username:")); p.add(user);
                p.add(new JLabel("Password:")); p.add(pw);
                p.add(new JLabel("Confirm password:")); p.add(conf);
                p.add(new JLabel("Access level:")); p.add(role);
                if (JOptionPane.showConfirmDialog(this, p, "Add User", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) return;
                try {
                    BookingSystem.addUser((String) role.getSelectedItem(), name.getText(), contact.getText(),
                            user.getText(), new String(pw.getPassword()), new String(conf.getPassword()));
                    JOptionPane.showMessageDialog(this, "User added.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    refresh();
                    return;
                } catch (Exception ex) {
                    Dashboard.error(this, ex.getMessage());
                }
            }
        }
    
        private void block() {
            String u = selectedUser();
            if (u == null) return;
            if (JOptionPane.showConfirmDialog(this, "Block " + u + "? They will not be able to log in.",
                    "Block User", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
            try {
                BookingSystem.block(admin, u);
                JOptionPane.showMessageDialog(this, u + " has been blocked.", "Done", JOptionPane.INFORMATION_MESSAGE);
                refresh();
            } catch (Exception ex) { Dashboard.error(this, ex.getMessage()); }
        }

        private void unblock() {
            String u = selectedUser();
            if (u == null) return;
            try {
                BookingSystem.unblock(u);
                JOptionPane.showMessageDialog(this, u + " has been unblocked.", "Done", JOptionPane.INFORMATION_MESSAGE);
                refresh();
            } catch (Exception ex) { Dashboard.error(this, ex.getMessage()); }
        }
    
        private void delete() {
            String u = selectedUser();
            if (u == null) return;
            JPasswordField pw = new JPasswordField();
            Object[] msg = {"Enter YOUR admin password to delete \"" + u + "\":", pw};
            if (JOptionPane.showConfirmDialog(this, msg, "Admin Verification", JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE) != JOptionPane.OK_OPTION) return;
            try {
                BookingSystem.deleteUser(admin, new String(pw.getPassword()), u);
                JOptionPane.showMessageDialog(this, "User deleted.", "Done", JOptionPane.INFORMATION_MESSAGE);
                refresh();
            } catch (Exception ex) { Dashboard.error(this, ex.getMessage()); }
        }
    }

    // user profile
    static class AccountPanel extends JPanel {
        public AccountPanel(Person user) {
            setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
            setBackground(Dashboard.BG);
    
            JPanel card = new JPanel(new GridLayout(0, 2, 10, 12));
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(210, 215, 210)),
                    BorderFactory.createEmptyBorder(20, 24, 20, 24)));
            card.setPreferredSize(new Dimension(520, 380));
    
            JLabel h = Dashboard.heading("My Account");
            card.add(h);
            card.add(new JLabel());
            row(card, "Name:", user.getFullName());
            row(card, "Username:", user.getUsername());
            row(card, "Contact:", user.getContact());
            row(card, "Access level:", user.getRole());
    
            JPasswordField oldPw = new JPasswordField(), newPw = new JPasswordField(), conf = new JPasswordField();
            card.add(Dashboard.label("Current password:")); card.add(oldPw);
            card.add(Dashboard.label("New password:")); card.add(newPw);
            card.add(Dashboard.label("Confirm new password:")); card.add(conf);
            JButton change = Dashboard.button("Change Password", Dashboard.PRIMARY);
            card.add(new JLabel());
            card.add(change);
            change.addActionListener(e -> {
                try {
                    BookingSystem.changePassword(user, new String(oldPw.getPassword()),
                            new String(newPw.getPassword()), new String(conf.getPassword()));
                    oldPw.setText(""); newPw.setText(""); conf.setText("");
                    JOptionPane.showMessageDialog(this, "Password changed successfully.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    Dashboard.error(this, ex.getMessage());
                }
            });
            add(card);
        }
    
        private void row(JPanel p, String k, String v) {
            p.add(Dashboard.label(k));
            JLabel l = Dashboard.label(v);
            l.setFont(Dashboard.BOLD);
            p.add(l);
        }
    }
}