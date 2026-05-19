package system;

import data.DataStore;
import enums.CourseType;
import enums.Language;
import enums.TeacherType;
import i18n.I18n;
import models.academic.Course;
import models.academic.Mark;
import models.research.ResearchPaper;
import models.users.Admin;
import models.users.Manager;
import models.users.Student;
import models.users.Teacher;
import models.users.User;
import pattern.factory.ConcreteUserFactory;
import pattern.observer.Journal;
import pattern.strategy.SortByCitations;
import pattern.strategy.SortByDate;
import pattern.strategy.SortByPages;
import pattern.strategy.SortStrategy;
import services.AuthenticationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DashboardFrame extends JFrame {
    private final DataStore store = DataStore.getInstance();
    private final User currentUser;
    private final JTextArea output = new JTextArea();
    private final JTabbedPane tabs = new JTabbedPane();
    private JCheckBox darkModeCheck;

    public DashboardFrame(User currentUser) {
        this.currentUser = currentUser;
        setTitle(currentUser.getClass().getSimpleName() + " - " + currentUser.getFullName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 640);
        setLocationRelativeTo(null);
        initUI();
        refreshSummary();
        setVisible(true);
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        JLabel welcome = new JLabel("Welcome, " + currentUser.getFullName() + " (" + currentUser.getClass().getSimpleName() + ")");
        welcome.setFont(new Font("Arial", Font.BOLD, 18));
        top.add(welcome, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JComboBox<Language> languageBox = new JComboBox<>(Language.values());
        languageBox.setSelectedItem(I18n.getCurrentLanguage());
        languageBox.addActionListener(e -> {
            Language language = (Language) languageBox.getSelectedItem();
            currentUser.selectLanguage(language);
            I18n.setLanguage(language);
            saveQuietly();
        });
        darkModeCheck = new JCheckBox("Dark mode");
        darkModeCheck.addActionListener(e -> applyTheme(root));
        JButton logout = new JButton(I18n.t("menu.logout"));
        logout.addActionListener(e -> logout());
        actions.add(languageBox);
        actions.add(darkModeCheck);
        actions.add(logout);
        top.add(actions, BorderLayout.EAST);

        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        tabs.addTab("Home", new JScrollPane(output));
        tabs.addTab("Users", usersPanel());
        tabs.addTab("Courses", coursesPanel());
        tabs.addTab("Research", researchPanel());
        if (currentUser instanceof Admin) tabs.addTab("Admin", adminPanel());
        if (currentUser instanceof Manager) tabs.addTab("Manager", managerPanel());
        if (currentUser instanceof Teacher) tabs.addTab("Teacher", teacherPanel());
        if (currentUser instanceof Student) tabs.addTab("Student", studentPanel());

        root.add(top, BorderLayout.NORTH);
        root.add(tabs, BorderLayout.CENTER);
        setContentPane(root);
        applyTheme(root);
    }

    private JPanel usersPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        DefaultTableModel model = new DefaultTableModel(new String[]{"Role", "Username", "Name", "Email", "Active"}, 0);
        JTable table = new JTable(model);
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> fillUsers(model));
        JButton export = new JButton("Export students CSV");
        export.addActionListener(e -> exportStudentsCsv());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(refresh);
        buttons.add(export);
        panel.add(buttons, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        fillUsers(model);
        return panel;
    }

    private JPanel coursesPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        DefaultListModel<Course> model = new DefaultListModel<>();
        JList<Course> list = new JList<>(model);
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> fillCourses(model));
        JButton register = new JButton("Register selected");
        register.addActionListener(e -> registerCourse(list.getSelectedValue()));
        JButton drop = new JButton("Drop selected");
        drop.addActionListener(e -> dropCourse(list.getSelectedValue()));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(refresh);
        buttons.add(register);
        buttons.add(drop);
        panel.add(buttons, BorderLayout.NORTH);
        panel.add(new JScrollPane(list), BorderLayout.CENTER);
        fillCourses(model);
        return panel;
    }

    private JPanel researchPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JTextArea area = new JTextArea();
        area.setEditable(false);
        JButton observer = new JButton("Observer demo");
        observer.addActionListener(e -> {
            Journal journal = new Journal("KBTU Research Journal");
            journal.subscribe(message -> area.append(message + "\n"));
            journal.publishResearchPaper("Patterns in University Systems");
        });
        JButton decorator = new JButton("Decorator demo");
        decorator.addActionListener(e -> {
            pattern.decorator.Researcher base = new pattern.decorator.BaseResearcher(currentUser);
            pattern.decorator.Researcher decorated = new pattern.decorator.ResearcherDecorator(base) {
                @Override
                public void conductResearch() {
                    super.conductResearch();
                    area.append(currentUser.getUsername() + " received UI research decorator bonus.\n");
                }
            };
            decorated.conductResearch();
        });
        JComboBox<String> sort = new JComboBox<>(new String[]{"Date", "Citations", "Pages"});
        JButton sortBtn = new JButton("Strategy sort demo");
        sortBtn.addActionListener(e -> area.setText(strategyDemo((String) sort.getSelectedItem())));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttons.add(observer);
        buttons.add(decorator);
        buttons.add(sort);
        buttons.add(sortBtn);
        panel.add(buttons, BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }

    private JPanel adminPanel() {
        JPanel panel = formPanel();
        JComboBox<String> role = new JComboBox<>(new String[]{"Student", "Teacher", "Manager", "Admin"});
        JTextField username = new JTextField(12);
        JPasswordField password = new JPasswordField(12);
        JTextField name = new JTextField(16);
        JTextField email = new JTextField(16);
        JButton create = new JButton("Create user");
        create.addActionListener(e -> createUser((String) role.getSelectedItem(), username.getText(), new String(password.getPassword()), name.getText(), email.getText()));
        addRow(panel, "Role", role);
        addRow(panel, "Username", username);
        addRow(panel, "Password", password);
        addRow(panel, "Full name", name);
        addRow(panel, "Email", email);
        addRow(panel, "", create);
        return panel;
    }

    private JPanel managerPanel() {
        JPanel panel = formPanel();
        JTextField code = new JTextField(10);
        JTextField name = new JTextField(16);
        JSpinner credits = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1));
        JComboBox<CourseType> type = new JComboBox<>(CourseType.values());
        JButton add = new JButton("Add course");
        add.addActionListener(e -> addCourse(code.getText(), name.getText(), (Integer) credits.getValue(), (CourseType) type.getSelectedItem()));
        addRow(panel, "Code", code);
        addRow(panel, "Name", name);
        addRow(panel, "Credits", credits);
        addRow(panel, "Type", type);
        addRow(panel, "", add);
        return panel;
    }

    private JPanel teacherPanel() {
        JPanel panel = formPanel();
        JTextField studentUsername = new JTextField(12);
        JTextField courseCode = new JTextField(10);
        JSpinner a1 = new JSpinner(new SpinnerNumberModel(25.0, 0.0, 30.0, 1.0));
        JSpinner a2 = new JSpinner(new SpinnerNumberModel(25.0, 0.0, 30.0, 1.0));
        JSpinner exam = new JSpinner(new SpinnerNumberModel(35.0, 0.0, 40.0, 1.0));
        JButton save = new JButton("Put mark");
        save.addActionListener(e -> putMark(studentUsername.getText(), courseCode.getText(), (Double) a1.getValue(), (Double) a2.getValue(), (Double) exam.getValue()));
        addRow(panel, "Student username", studentUsername);
        addRow(panel, "Course code", courseCode);
        addRow(panel, "First attestation", a1);
        addRow(panel, "Second attestation", a2);
        addRow(panel, "Final exam", exam);
        addRow(panel, "", save);
        return panel;
    }

    private JPanel studentPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JTextArea transcript = new JTextArea();
        transcript.setEditable(false);
        JButton refresh = new JButton("View transcript");
        refresh.addActionListener(e -> {
            if (currentUser instanceof Student) transcript.setText(((Student) currentUser).viewTranscript());
        });
        panel.add(refresh, BorderLayout.NORTH);
        panel.add(new JScrollPane(transcript), BorderLayout.CENTER);
        return panel;
    }

    private JPanel formPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.putClientProperty("row", 0);
        return panel;
    }

    private void addRow(JPanel panel, String label, Component field) {
        int row = (Integer) panel.getClientProperty("row");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        panel.add(field, gbc);
        panel.putClientProperty("row", row + 1);
    }

    private void fillUsers(DefaultTableModel model) {
        model.setRowCount(0);
        for (User user : store.getAllUsers()) {
            model.addRow(new Object[]{user.getClass().getSimpleName(), user.getUsername(), user.getFullName(), user.getEmail(), user.isActive()});
        }
    }

    private void fillCourses(DefaultListModel<Course> model) {
        model.clear();
        for (Course course : store.getCourses()) model.addElement(course);
    }

    private void createUser(String role, String username, String password, String name, String email) {
        if (username.isBlank() || password.isBlank() || name.isBlank() || email.isBlank()) {
            showError("Fill all fields.");
            return;
        }
        try {
            store.findUserByUsername(username);
            showError("Username already exists.");
            return;
        } catch (Exception ignored) {
        }
        User user = new ConcreteUserFactory().createUser(role);
        String[] parts = name.trim().split("\\s+", 2);
        user.updateProfile(username, parts[0], parts.length > 1 ? parts[1] : "", null, null);
        user.changePassword(password);
        user.updateEmail(email);
        if (user instanceof Teacher) ((Teacher) user).setTeacherType(TeacherType.LECTOR);
        store.addUser(user);
        saveQuietly();
        refreshSummary();
    }

    private void addCourse(String code, String name, int credits, CourseType type) {
        if (code.isBlank() || name.isBlank()) {
            showError("Fill course code and name.");
            return;
        }
        store.addCourse(new Course(code.trim(), name.trim(), credits, type));
        saveQuietly();
        refreshSummary();
    }

    private void registerCourse(Course course) {
        if (!(currentUser instanceof Student) || course == null) return;
        try {
            ((Student) currentUser).registerForCourse(course);
            saveQuietly();
            refreshSummary();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void dropCourse(Course course) {
        if (!(currentUser instanceof Student) || course == null) return;
        ((Student) currentUser).dropCourse(course);
        saveQuietly();
        refreshSummary();
    }

    private void putMark(String studentUsername, String courseCode, double first, double second, double exam) {
        if (!(currentUser instanceof Teacher)) return;
        try {
            User user = store.findUserByUsername(studentUsername.trim());
            Course course = findCourse(courseCode.trim());
            if (!(user instanceof Student) || course == null) {
                showError("Student or course not found.");
                return;
            }
            Teacher teacher = (Teacher) currentUser;
            if (!teacher.getAssignedCourses().contains(course)) teacher.assignCourse(course);
            if (!course.isStudentEnrolled((Student) user)) course.enrollStudent((Student) user);
            teacher.putMark((Student) user, course, new Mark(first, second, exam));
            saveQuietly();
            refreshSummary();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private Course findCourse(String code) {
        for (Course course : store.getCourses()) {
            if (course.getCode().equalsIgnoreCase(code)) return course;
        }
        return null;
    }

    private String strategyDemo(String selected) {
        List<ResearchPaper> papers = new ArrayList<>();
        ResearchPaper one = new ResearchPaper("OOP UI", "KBTU Journal", 12, LocalDate.now().minusDays(3), "10.1/ui");
        one.setCitations(4);
        ResearchPaper two = new ResearchPaper("Patterns", "KBTU Journal", 25, LocalDate.now().minusDays(1), "10.1/patterns");
        two.setCitations(11);
        papers.add(one);
        papers.add(two);
        SortStrategy strategy = "Citations".equals(selected) ? new SortByCitations() : "Pages".equals(selected) ? new SortByPages() : new SortByDate();
        strategy.sort(papers);
        StringBuilder builder = new StringBuilder("Sorted by " + selected + ":\n");
        for (ResearchPaper paper : papers) {
            builder.append(paper.getTitle()).append(" | pages=").append(paper.getPages()).append(" | citations=").append(paper.getCitations()).append(" | date=").append(paper.getPublishDate()).append('\n');
        }
        return builder.toString();
    }

    private void exportStudentsCsv() {
        try (FileWriter writer = new FileWriter("students.csv")) {
            writer.write("username,fullName,email,degree,credits\n");
            for (Student student : store.getStudents()) {
                writer.write(String.format("%s,%s,%s,%s,%d%n", student.getUsername(), student.getFullName(), student.getEmail(), student.getDegree(), student.getCurrentCredits()));
            }
            JOptionPane.showMessageDialog(this, "Exported to students.csv");
        } catch (IOException e) {
            showError(e.getMessage());
        }
    }

    private void refreshSummary() {
        output.setText("Users: " + store.getAllUsers().size() + "\nCourses: " + store.getCourses().size() + "\nNews: " + store.getNews().size() + "\n\n");
        output.append("Current user:\n" + currentUser + "\n");
    }

    private void logout() {
        try {
            new AuthenticationService().logout(currentUser);
        } catch (Exception ignored) {
        }
        new UIMain();
        dispose();
    }

    private void saveQuietly() {
        try {
            store.save();
        } catch (IOException e) {
            showError(e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void applyTheme(Container root) {
        boolean dark = darkModeCheck != null && darkModeCheck.isSelected();
        Color bg = dark ? new Color(34, 40, 49) : new Color(245, 247, 250);
        Color fg = dark ? new Color(238, 238, 238) : Color.BLACK;
        applyTheme(root, bg, fg);
    }

    private void applyTheme(Component component, Color bg, Color fg) {
        if (component instanceof JPanel || component instanceof JScrollPane || component instanceof JTabbedPane || component instanceof JCheckBox) {
            component.setBackground(bg);
        }
        component.setForeground(fg);
        if (component instanceof JTextArea || component instanceof JTable || component instanceof JList) {
            component.setBackground(bg.brighter());
            component.setForeground(fg);
        }
        if (component instanceof Container) {
            for (Component child : ((Container) component).getComponents()) {
                applyTheme(child, bg, fg);
            }
        }
    }
}
