package system;

import enums.Language;
import i18n.I18n;
import data.DataStore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// Main entry point for the University Management System UI
// Displays login/register buttons and language selector
public class UIMain extends JFrame {

    private JLabel titleLabel;
    private JLabel langLabel;
    private JButton loginBtn;
    private JButton registerBtn;
    private JComboBox<String> languageCombo;
    private JCheckBox darkModeCheck;

    public UIMain() {
        // Load data at startup
        try {
            DataStore.getInstance().load();
        } catch (Exception ex) {
            System.err.println("Error loading data: " + ex.getMessage());
        }

        setTitle(I18n.t("app.title"));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        // Save data when closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    DataStore.getInstance().save();
                } catch (Exception ex) {
                    System.err.println("Error saving data: " + ex.getMessage());
                }
            }
        });

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

        langLabel = new JLabel("Language:");
        languageCombo = new JComboBox<>(new String[]{"EN", "RU", "KZ"});
        languageCombo.setSelectedItem(I18n.getCurrentLanguage().name());
        languageCombo.addActionListener(e -> changeLanguage());
        darkModeCheck = new JCheckBox("Dark mode");
        darkModeCheck.setOpaque(false);
        darkModeCheck.addActionListener(e -> applyTheme(mainPanel));

        topPanel.add(langLabel);
        topPanel.add(languageCombo);
        topPanel.add(darkModeCheck);

        // Center panel with title and buttons
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title
        titleLabel = new JLabel(I18n.t("app.title"));
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(titleLabel, gbc);

        // Login button
        loginBtn = new JButton(I18n.t("login.option"));
        loginBtn.setPreferredSize(new Dimension(150, 50));
        loginBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        loginBtn.addActionListener(e -> openLoginFrame());
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        centerPanel.add(loginBtn, gbc);

        // Register button
        registerBtn = new JButton("Register");
        registerBtn.setPreferredSize(new Dimension(150, 50));
        registerBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        registerBtn.addActionListener(e -> openRegisterFrame());
        gbc.gridx = 1;
        gbc.gridy = 1;
        centerPanel.add(registerBtn, gbc);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        setContentPane(mainPanel);
        applyTheme(mainPanel);
    }

    private void changeLanguage() {
        String selected = (String) languageCombo.getSelectedItem();
        Language lang = Language.valueOf(selected);
        I18n.setLanguage(lang);

        // Update labels
        titleLabel.setText(I18n.t("app.title"));
        loginBtn.setText(I18n.t("login.option"));
        registerBtn.setText("Register");
        langLabel.setText("Language:");
        setTitle(I18n.t("app.title"));
    }

    private void openLoginFrame() {
        new LoginFrame(this);
        this.dispose();
    }

    private void openRegisterFrame() {
        new RegisterFrame(this);
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UIMain::new);
    }

    private void applyTheme(Container root) {
        boolean dark = darkModeCheck != null && darkModeCheck.isSelected();
        Color bg = dark ? new Color(34, 40, 49) : new Color(240, 240, 240);
        Color fg = dark ? new Color(238, 238, 238) : Color.BLACK;
        applyTheme(root, bg, fg);
    }

    private void applyTheme(Component component, Color bg, Color fg) {
        if (component instanceof JPanel || component instanceof JCheckBox) {
            component.setBackground(bg);
        }
        component.setForeground(fg);
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                applyTheme(child, bg, fg);
            }
        }
    }
}
