package system;

import enums.Language;
import i18n.I18n;
import models.users.User;
import services.AuthenticationService;

import javax.swing.*;
import java.awt.*;

// Login Frame for University Management System
// Allows users to login with username and password
public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn;
    private JButton backBtn;
    private JComboBox<String> languageCombo;

    public LoginFrame(UIMain parent) {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240));

        // Top panel for language selector
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setBackground(new Color(240, 240, 240));

        JLabel langLabel = new JLabel("Language:");
        languageCombo = new JComboBox<>(new String[]{"EN", "RU", "KZ"});
        languageCombo.setSelectedItem(I18n.getCurrentLanguage().name());
        languageCombo.addActionListener(e -> changeLanguage());

        topPanel.add(langLabel);
        topPanel.add(languageCombo);

        // Center panel with login fields
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("LOGIN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(titleLabel, gbc);

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        centerPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 1;
        centerPanel.add(usernameField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        centerPanel.add(passwordField, gbc);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(new Color(240, 240, 240));

        loginBtn = new JButton("Login");
        loginBtn.setPreferredSize(new Dimension(120, 40));
        loginBtn.addActionListener(e -> performLogin());

        backBtn = new JButton("Back");
        backBtn.setPreferredSize(new Dimension(120, 40));
        backBtn.addActionListener(e -> goBack());

        bottomPanel.add(loginBtn);
        bottomPanel.add(backBtn);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    private void changeLanguage() {
        String selected = (String) languageCombo.getSelectedItem();
        Language lang = Language.valueOf(selected);
        I18n.setLanguage(lang);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Try to authenticate using AuthenticationService
        AuthenticationService authService = new AuthenticationService();
        User user = null;
        try {
            user = authService.login(username, password);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Authentication error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (user != null) {
            JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            new DashboardFrame(user);
            this.dispose();
        }
    }

    private void goBack() {
        new UIMain();
        this.dispose();
    }
}
