import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class LoginForm extends JFrame {
    private final JTextField userField = new JTextField(20);
    private final JPasswordField passField = new JPasswordField(20);

    public LoginForm() {
        super("Pickleball Court Booking System | Sign In");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 800);                       
        setMinimumSize(new Dimension(1150, 720));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new GridBagLayout());  
        root.setBackground(Dashboard.PRIMARY);
        JPanel box = new JPanel(new BorderLayout());
        box.setOpaque(false);
        box.setPreferredSize(new Dimension(420, 540));

        JLabel brand = new JLabel("<html><div style='text-align:center'>PICKLEBALL<br>COURT BOOKING</div></html>",
                SwingConstants.CENTER);
        brand.setFont(new Font("SansSerif", Font.BOLD, 28));
        brand.setForeground(Color.WHITE);
        brand.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        box.add(brand, BorderLayout.NORTH);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 230, 220)),
                BorderFactory.createEmptyBorder(28, 26, 28, 26)));

        JLabel signIn = Dashboard.heading("Welcome back");
        signIn.setAlignmentX(LEFT_ALIGNMENT);
        card.add(signIn);
        card.add(Box.createVerticalStrut(18));

        card.add(fieldRow("Username", userField));
        card.add(Box.createVerticalStrut(10));
        card.add(fieldRow("Password", passField));

        JCheckBox show = new JCheckBox("Show password");
        show.setBackground(Color.WHITE);
        show.setFont(Dashboard.SMALL);
        show.setAlignmentX(LEFT_ALIGNMENT);
        char echo = passField.getEchoChar();
        show.addActionListener(e -> passField.setEchoChar(show.isSelected() ? (char) 0 : echo));
        card.add(show);
        card.add(Box.createVerticalStrut(16));

        JButton login = Dashboard.button("SIGN IN",Dashboard.PRIMARY);
        login.setMnemonic(KeyEvent.VK_L);
        login.setAlignmentX(LEFT_ALIGNMENT);
        login.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        login.addActionListener(e -> doLogin());
        card.add(login);
        card.add(Box.createVerticalStrut(8));

        JButton signUp = Dashboard.button("CREATE A NEW ACCOUNT",Dashboard.NEUTRAL);
        signUp.setMnemonic(KeyEvent.VK_S);
        signUp.setAlignmentX(LEFT_ALIGNMENT);
        signUp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        signUp.addActionListener(e -> new SignUpDialog(this).setVisible(true));
        card.add(signUp);

        box.add(card, BorderLayout.CENTER);
        root.add(box);
        setContentPane(root);
        getRootPane().setDefaultButton(login);
    }

    private JPanel fieldRow(String label, JComponent field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(Color.WHITE);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.add(Dashboard.label(label), BorderLayout.NORTH);
        field.setFont(Dashboard.FONT);
        field.setPreferredSize(new Dimension(300, 34));
        p.add(field, BorderLayout.CENTER);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        return p;
    }

    private void doLogin() {
        try {
            Person p = BookingSystem.login(userField.getText(), new String(passField.getPassword()));
            passField.setText("");
            dispose();
            new Dashboard(p).setVisible(true);
        } catch (Exception ex) {
            passField.setText("");
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Failed", JOptionPane.WARNING_MESSAGE);
        }
    }

    static class SignUpDialog extends JDialog {
        private final JTextField nameF = new JTextField();
        private final JTextField contactF = new JTextField();
        private final JTextField userF = new JTextField();
        private final JPasswordField passF = new JPasswordField();
        private final JPasswordField confF = new JPasswordField();
    
        public SignUpDialog(Window owner) {
            super(owner, "Create Account", ModalityType.APPLICATION_MODAL);
            JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
            form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
            add(form, "Full name", nameF);
            add(form, "Contact Number", contactF);
            add(form, "Username", userF);
            add(form, "Password", passF);
            add(form, "Confirm password", confF);
    
            JButton create = Dashboard.button("Create Account", Dashboard.PRIMARY);
            JButton cancel = Dashboard.button("Close", Dashboard.NEUTRAL);
            create.addActionListener(e -> submit());
            cancel.addActionListener(e -> dispose());
            JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            btns.add(cancel);
            btns.add(create);
    
            setLayout(new BorderLayout());
            add(form, BorderLayout.CENTER);
            add(btns, BorderLayout.SOUTH);
            getRootPane().setDefaultButton(create);
            setSize(460, 340);
            setLocationRelativeTo(owner);
        }
    
        private void add(JPanel form, String label, JComponent f) {
            form.add(Dashboard.label(label));
            f.setFont(Dashboard.FONT);
            form.add(f);
        }
    
        private void submit() {
            try {
                BookingSystem.register(nameF.getText(), contactF.getText(), userF.getText(),
                        new String(passF.getPassword()), new String(confF.getPassword()));
                JOptionPane.showMessageDialog(this, "Account created! You can now log in.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } catch (Exception ex) {
                Dashboard.error(this, ex.getMessage());
            }
        }
    }
}
