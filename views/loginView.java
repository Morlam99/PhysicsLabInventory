package views;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * Sign-in window for the Physics Laboratory inventory desktop application.
 *
 * The accompanying loginView.form is the editable IntelliJ IDEA Swing UI Designer
 * layout. IntelliJ initializes its bound components during compilation. For
 * environments that compile with plain javac (without IDEA's form instrumenter),
 * the constructor builds an equivalent standard-Swing layout as a fallback.
 *
 * This view deliberately does not contain hard-coded usernames, passwords, or
 * database access. A controller can supply a LoginHandler when authentication
 * is ready to be connected.
 */
public class loginView {
    private static final Color NAVY = new Color(15, 43, 77);
    private static final Color NAVY_HOVER = new Color(24, 66, 114);
    private static final Color GOLD = new Color(246, 188, 46);
    private static final Color WHITE = Color.WHITE;
    private static final Color INK = new Color(32, 47, 68);
    private static final Color MUTED = new Color(111, 122, 139);
    private static final Color SOFT_BLUE = new Color(184, 207, 232);
    private static final Color INPUT_BORDER = new Color(210, 219, 231);
    private static final Color ERROR = new Color(181, 52, 65);
    private static final Color SUCCESS = new Color(33, 127, 85);

    private JPanel rootPanel;
    private JPanel brandPanel;
    private JPanel formPanel;
    private JLabel logoLabel;
    private JLabel brandOverlineLabel;
    private JLabel brandTitleLabel;
    private JLabel brandSubtitleLabel;
    private JLabel brandFooterLabel;
    private JLabel overlineLabel;
    private JLabel headlineLabel;
    private JLabel hintLabel;
    private JLabel departmentLabel;
    private JComboBox<String> departmentComboBox;
    private JLabel usernameLabel;
    private JTextField usernameField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel feedbackLabel;
    private JLabel footerLabel;

    private LoginHandler loginHandler;
    private Runnable onLoginSuccess;

    /** Called synchronously with a temporary password array; do not retain it. */
    @FunctionalInterface
    public interface LoginHandler {
        boolean authenticate(String department, String username, char[] password);
    }

    public loginView() {
        // IDEA initializes rootPanel from the .form; plain javac does not.
        if (rootPanel == null) {
            buildStandaloneSwingLayout();
        }
        applyTheme();
        wireActions();
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    /** Attach an actual authentication implementation from the controller layer. */
    public void setLoginHandler(LoginHandler loginHandler) {
        this.loginHandler = loginHandler;
    }

    /** Called only after the controller-provided authentication succeeds. */
    public void setOnLoginSuccess(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    public JFrame createFrame() {
        JFrame frame = new JFrame("Physics Lab Inventory | Sign in");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setContentPane(rootPanel);
        frame.getRootPane().setDefaultButton(loginButton);
        frame.setMinimumSize(new Dimension(800, 550));
        frame.setSize(940, 600);
        frame.setLocationRelativeTo(null);
        ImageIcon icon = findLogo();
        if (icon != null) {
            frame.setIconImage(icon.getImage());
        }
        return frame;
    }

    private void applyTheme() {
        rootPanel.setBackground(WHITE);
        rootPanel.setPreferredSize(new Dimension(940, 600));
        brandPanel.setBackground(NAVY);
        formPanel.setBackground(WHITE);

        logoLabel.setText("");
        ImageIcon logo = findLogo();
        if (logo != null) {
            logoLabel.setIcon(logo);
        } else {
            logoLabel.setText("POLYTECHNIC UNIVERSITY");
            logoLabel.setForeground(WHITE);
        }
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        logoLabel.getAccessibleContext().setAccessibleName("Polytechnic University logo");

        fontAndColor(brandOverlineLabel, 11, Font.BOLD, SOFT_BLUE);
        fontAndColor(brandTitleLabel, 20, Font.BOLD, WHITE);
        fontAndColor(brandSubtitleLabel, 13, Font.PLAIN, SOFT_BLUE);
        fontAndColor(brandFooterLabel, 10, Font.BOLD, SOFT_BLUE);
        brandOverlineLabel.setHorizontalAlignment(SwingConstants.CENTER);
        brandTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        brandSubtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        brandFooterLabel.setHorizontalAlignment(SwingConstants.CENTER);

        fontAndColor(overlineLabel, 11, Font.BOLD, new Color(34, 95, 163));
        fontAndColor(headlineLabel, 34, Font.BOLD, NAVY);
        fontAndColor(hintLabel, 13, Font.PLAIN, MUTED);
        fontAndColor(departmentLabel, 13, Font.BOLD, INK);
        fontAndColor(usernameLabel, 13, Font.BOLD, INK);
        fontAndColor(passwordLabel, 13, Font.BOLD, INK);
        fontAndColor(feedbackLabel, 12, Font.PLAIN, ERROR);
        fontAndColor(footerLabel, 12, Font.PLAIN, MUTED);

        departmentComboBox.setModel(new DefaultComboBoxModel<>(new String[]{"Physics Laboratory"}));
        departmentComboBox.setSelectedIndex(0);
        departmentComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        departmentComboBox.setForeground(INK);
        departmentComboBox.setBackground(WHITE);
        departmentComboBox.setFocusable(true);
        departmentComboBox.setPreferredSize(new Dimension(365, 46));
        departmentComboBox.setBorder(new LineBorder(INPUT_BORDER, 1, true));
        departmentComboBox.getAccessibleContext().setAccessibleName("Department");

        styleInput(usernameField);
        styleInput(passwordField);
        usernameField.setToolTipText("Enter your username");
        passwordField.setToolTipText("Enter your password");
        departmentLabel.setLabelFor(departmentComboBox);
        usernameLabel.setLabelFor(usernameField);
        passwordLabel.setLabelFor(passwordField);

        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setForeground(WHITE);
        loginButton.setBackground(NAVY);
        loginButton.setOpaque(true);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(365, 48));
        loginButton.setText("Sign in");
        loginButton.getAccessibleContext().setAccessibleDescription("Sign in to the Physics Laboratory inventory application");

        feedbackLabel.setText(" "); // reserve space for validation/status messages
    }

    private void wireActions() {
        loginButton.addActionListener(event -> attemptLogin());
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                loginButton.setBackground(NAVY_HOVER);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                loginButton.setBackground(NAVY);
            }
        });
        // Also works when the panel is embedded without createFrame().
        passwordField.addActionListener(event -> attemptLogin());
        usernameField.addActionListener(event -> passwordField.requestFocusInWindow());
    }

    private void attemptLogin() {
        String department = (String) departmentComboBox.getSelectedItem();
        String username = usernameField.getText().trim();
        char[] password = passwordField.getPassword();
        try {
            if (department == null || department.isBlank()) {
                showFeedback("Select a department.", false);
                departmentComboBox.requestFocusInWindow();
            } else if (username.isBlank()) {
                showFeedback("Enter your username.", false);
                usernameField.requestFocusInWindow();
            } else if (password.length == 0) {
                showFeedback("Enter your password.", false);
                passwordField.requestFocusInWindow();
            } else if (loginHandler == null) {
                showFeedback("Authentication has not been connected yet.", false);
            } else if (loginHandler.authenticate(department, username, password)) {
                showFeedback("Sign-in successful.", true);
                if (onLoginSuccess != null) {
                    onLoginSuccess.run();
                }
            } else {
                passwordField.setText("");
                showFeedback("Invalid username or password.", false);
                passwordField.requestFocusInWindow();
            }
        } catch (RuntimeException ex) {
            showFeedback("Unable to sign in. Please try again.", false);
        } finally {
            Arrays.fill(password, '\0');
        }
    }

    private void showFeedback(String message, boolean successful) {
        feedbackLabel.setForeground(successful ? SUCCESS : ERROR);
        feedbackLabel.setText(message);
    }

    private static void fontAndColor(JLabel label, int size, int style, Color color) {
        label.setFont(new Font("SansSerif", style, size));
        label.setForeground(color);
    }

    private static void styleInput(JTextField field) {
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setForeground(INK);
        field.setCaretColor(NAVY);
        field.setBackground(WHITE);
        field.setPreferredSize(new Dimension(365, 46));
        field.setBorder(new CompoundBorder(new LineBorder(INPUT_BORDER, 1, true),
                new EmptyBorder(0, 13, 0, 13)));
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent event) {
                field.setBorder(new CompoundBorder(new LineBorder(NAVY_HOVER, 1, true),
                        new EmptyBorder(0, 13, 0, 13)));
            }

            @Override
            public void focusLost(FocusEvent event) {
                field.setBorder(new CompoundBorder(new LineBorder(INPUT_BORDER, 1, true),
                        new EmptyBorder(0, 13, 0, 13)));
            }
        });
    }

    /** Load from the existing resource folder, whether run by IDEA or plain javac. */
    private ImageIcon findLogo() {
        String file = "polytechnic_login_logo.png";
        URL url = getClass().getResource("/resource/" + file);
        if (url == null) {
            url = getClass().getResource("/" + file);
        }
        if (url != null) {
            return new ImageIcon(url);
        }
        // Local-file fallback is useful when working from an unbuilt IDEA project.
        for (Path path : new Path[]{Path.of("resource", file),
                Path.of("PhysicsLabInventory", "resource", file)}) {
            if (Files.isRegularFile(path)) {
                try {
                    BufferedImage image = ImageIO.read(path.toFile());
                    if (image != null) return new ImageIcon(image);
                } catch (IOException ignored) {
                    // Ignore an unreadable image; the rest of the UI remains usable.
                }
            }
        }
        return null;
    }

    /**
     * Fallback for plain javac. When compiled inside IntelliJ with .form support,
     * the designer layout is used instead, and this method is not called.
     */
    private void buildStandaloneSwingLayout() {
        rootPanel = new JPanel(new BorderLayout());
        brandPanel = new JPanel(new BorderLayout());
        brandPanel.setPreferredSize(new Dimension(354, 600));
        formPanel = new JPanel(new GridBagLayout());

        logoLabel = new JLabel();
        brandOverlineLabel = new JLabel("POLYTECHNIC UNIVERSITY");
        brandTitleLabel = new JLabel("PHYSICS LABORATORY");
        brandSubtitleLabel = new JLabel("Inventory & Order Management");
        brandFooterLabel = new JLabel("PUERTO RICO  |  CAMPUS SYSTEM");

        JPanel brandCenter = new JPanel();
        brandCenter.setOpaque(false);
        brandCenter.setBorder(new EmptyBorder(30, 18, 10, 18));
        brandCenter.setLayout(new BoxLayout(brandCenter, BoxLayout.Y_AXIS));
        brandCenter.add(Box.createVerticalGlue());
        centered(brandCenter, logoLabel);
        brandCenter.add(Box.createVerticalStrut(31));
        centered(brandCenter, brandOverlineLabel);
        brandCenter.add(Box.createVerticalStrut(11));
        centered(brandCenter, brandTitleLabel);
        brandCenter.add(Box.createVerticalStrut(10));
        centered(brandCenter, brandSubtitleLabel);
        brandCenter.add(Box.createVerticalGlue());
        brandPanel.add(brandCenter, BorderLayout.CENTER);
        brandFooterLabel.setBorder(new EmptyBorder(0, 10, 28, 10));
        brandPanel.add(brandFooterLabel, BorderLayout.SOUTH);

        overlineLabel = new JLabel("STAFF ACCESS");
        headlineLabel = new JLabel("Sign in");
        hintLabel = new JLabel("Enter your account details to continue.");
        departmentLabel = new JLabel("Department");
        departmentComboBox = new JComboBox<>();
        usernameLabel = new JLabel("Username");
        usernameField = new JTextField();
        passwordLabel = new JLabel("Password");
        passwordField = new JPasswordField();
        loginButton = new JButton("Sign in");
        feedbackLabel = new JLabel(" ");
        footerLabel = new JLabel("Accounts are managed by your administrator.");

        JPanel fields = new JPanel();
        fields.setOpaque(false);
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));
        addFullWidth(fields, overlineLabel);
        gap(fields, 7);
        addFullWidth(fields, headlineLabel);
        gap(fields, 7);
        addFullWidth(fields, hintLabel);
        gap(fields, 22);
        addFullWidth(fields, departmentLabel);
        gap(fields, 7);
        addFullWidth(fields, departmentComboBox);
        gap(fields, 12);
        addFullWidth(fields, usernameLabel);
        gap(fields, 7);
        addFullWidth(fields, usernameField);
        gap(fields, 12);
        addFullWidth(fields, passwordLabel);
        gap(fields, 7);
        addFullWidth(fields, passwordField);
        gap(fields, 18);
        addFullWidth(fields, loginButton);
        gap(fields, 9);
        addFullWidth(fields, feedbackLabel);
        gap(fields, 18);
        addFullWidth(fields, footerLabel);
        fields.setPreferredSize(new Dimension(400, 500));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.CENTER;
        gc.insets = new Insets(30, 32, 30, 32);
        formPanel.add(fields, gc);

        rootPanel.add(brandPanel, BorderLayout.WEST);
        rootPanel.add(formPanel, BorderLayout.CENTER);
    }

    private static void centered(JPanel parent, JComponent component) {
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        parent.add(component);
    }

    private static void addFullWidth(JPanel parent, JComponent component) {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        int height;
        if (component instanceof JTextField || component instanceof JComboBox || component instanceof JButton) {
            height = 48;
        } else if (component instanceof JLabel && "Sign in".equals(((JLabel) component).getText())) {
            height = 46;
        } else {
            height = 20;
        }
        component.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        parent.add(component);
    }

    private static void gap(JPanel parent, int px) {
        parent.add(Box.createVerticalStrut(px));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (UnsupportedLookAndFeelException | ClassNotFoundException |
                     InstantiationException | IllegalAccessException ignored) {
                // The default Swing look-and-feel is also supported.
            }
            new loginView().createFrame().setVisible(true);
        });
    }
}
