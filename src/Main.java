import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); }
        catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> {
            try {
                BookingSystem.load();
            } catch (RuntimeException ex) {
                JOptionPane.showMessageDialog(null, "Could not load data files:\n" + ex.getMessage(),
                        "Startup Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            new LoginForm().setVisible(true);
        });
    }
}