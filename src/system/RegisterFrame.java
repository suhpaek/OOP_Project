package system;

import data.DataStore;
import enums.Language;
import i18n.I18n;
import models.users.User;
import pattern.factory.ConcreteUserFactory;

import javax.swing.*;
import java.awt.*;
import java.util.regex.Pattern;

// Register Frame for University Management System
// Allows new users to register with role selection
public class RegisterFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JTextField emailField;
    private JComboBox<String> roleCombo;
    private JButton registerBtn;
    private JButton backBtn;
    private JComboBox<String> languageCombo;

    public RegisterFrame(UIMain parent) {
        setTitle("Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 500);
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

        // Center panel with registration fields
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("REGISTER");
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
        JLabel passwordLabel = new JLabel("Password (min 6):");
        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        gbc.gridy = 2;
        centerPanel.add(passwordField, gbc);

        // Name
        JLabel nameLabel = new JLabel("Full Name:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        centerPanel.add(nameLabel, gbc);

        nameField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 3;
        centerPanel.add(nameField, gbc);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        centerPanel.add(emailLabel, gbc);

        emailField = new JTextField(15);
        gbc.gridx = 1;
        gbc.gridy = 4;
        centerPanel.add(emailField, gbc);

        // Role
        JLabel roleLabel = new JLabel("Role:");
        gbc.gridx = 0;
        gbc.gridy = 5;
        centerPanel.add(roleLabel, gbc);

        roleCombo = new JComboBox<>(new String[]{
            "Student", "Teacher", "Manager", "Admin", "TechSupportSpecialist"
        });
        gbc.gridx = 1;
        gbc.gridy = 5;
        centerPanel.add(roleCombo, gbc);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel with buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(new Color(240, 240, 240));

        registerBtn = new JButton("Register");
        registerBtn.setPreferredSize(new Dimension(120, 40));
        registerBtn.addActionListener(e -> performRegister());

        backBtn = new JButton("Back");
        backBtn.setPreferredSize(new Dimension(120, 40));
        backBtn.addActionListener(e -> goBack());

        bottomPanel.add(registerBtn);
        bottomPanel.add(backBtn);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);
    }

    private void changeLanguage() {
        String selected = (String) languageCombo.getSelectedItem();
        Language lang = Language.valueOf(selected);
        I18n.setLanguage(lang);
    }

    private void performRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String role = (String) roleCombo.getSelectedItem();

        // Validation
        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if username already exists
        try {
            DataStore.getInstance().findUserByUsername(username);
            // If we get here, user exists
            JOptionPane.showMessageDialog(this, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (exceptions.UserNotFoundException ex) {
            // User doesn't exist, which is good - proceed with registration
        }

        // Create user using Factory Pattern
        ConcreteUserFactory factory = new ConcreteUserFactory();
        try {
            User newUser = factory.createUser(role);
            String[] names = splitName(name);
            newUser.updateProfile(username, names[0], names[1], null, null);
            newUser.changePassword(password);
            newUser.updateEmail(email);
            newUser.selectLanguage(I18n.getCurrentLanguage());

            // Add to DataStore
            DataStore.getInstance().addUser(newUser);
            try {
                DataStore.getInstance().save();
            } catch (Exception ex) {
                System.err.println("Error saving: " + ex.getMessage());
            }

            JOptionPane.showMessageDialog(this, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            goBack();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid role selected", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(emailRegex, email);
    }

    private String[] splitName(String name) {
        String[] parts = name.trim().split("\\s+", 2);
        if (parts.length == 1) return new String[]{parts[0], ""};
        return parts;
    }

    private void goBack() {
        new UIMain();
        this.dispose();
    }
}
