/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author GABRIEL DY
 */
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.*;
import java.util.LinkedList;

public class GamingCafeSystem extends JFrame {

    private static final Color BACKGROUND = new Color(249, 247, 247);

    private static final Color PANEL = new Color(219, 226, 239);

    private static final Color ACCENT = new Color(63, 114, 175);

    private static final Color NAVY = new Color(17, 45, 78);

    private User currentUser;
    private JLabel lblUser;

    private JTable customerTable;
    private DefaultTableModel customerModel;

    private JTextField txtCustomerId;
    private JTextField txtCustomerName;
    private JTextField txtContactNumber;

    private JComboBox<String> cboMembership;
    private LinkedList<User> users;
    private LinkedList<Customer> customers;
    private LinkedList<PC> pcs;
    private LinkedList<Reservation> reservations;
    private LinkedList<Product> products;
    private LinkedList<Order> orders;
    private LinkedList<Session> sessions;

    private int loginAttempts = 0;

    private FileManager fileManager;

    public GamingCafeSystem() {

        fileManager = new FileManager();
        users = new LinkedList<>();
        reservations = fileManager.loadReservations();
        customers = fileManager.loadCustomers();
        pcs = fileManager.loadPCs();
        users = fileManager.loadUsers();
        products = fileManager.loadProducts();
        orders = fileManager.loadOrders();
        sessions = fileManager.loadSessions();

        if (products.isEmpty()) {

            loadDefaultProducts();

            fileManager.saveProducts(products);
        }

        if (users.isEmpty()) {

            users.add(new User(
                    "admin",
                    "admin123",
                    "Admin",
                    "What is your favorite game?",
                    "Valorant",
                    false,
                    0
            ));

            users.add(new User(
                    "staff",
                    "staff123",
                    "Staff",
                    "",
                    "",
                    false,
                    0
            ));

            fileManager.saveUsers(users);
        }

        while (currentUser == null) {

            if (login()) {
                break;
            }
        }

        initializeGUI();

        loadCustomers();
        loadPCs();
        loadReservations();
        loadProducts();

        refreshCustomerComboBox();
        refreshPCComboBox();

        clearCustomerFields();
        clearPCFields();
        clearReservationFields();

        addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {

                int confirm = JOptionPane.showConfirmDialog(null, "Exit System?", "Confirm Exit",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {

                    System.exit(0);
                }
            }
        });
    }

    private void initializeGUI() {

        setTitle("Gaming Cafe Management System");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        getContentPane().setBackground(BACKGROUND);

        lblUser = new JLabel(" Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");

        lblUser.setForeground(Color.WHITE);

        lblUser.setOpaque(true);

        lblUser.setBackground(ACCENT);

        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 16));

        JButton btnChangePassword = new JButton("Change Password");

        styleButton(btnChangePassword);

        btnChangePassword.addActionListener(e -> changePassword());

        JButton btnSignup = new JButton("Create User");

        styleButton(btnSignup);

        btnSignup.addActionListener(e -> createUser());

        JButton btnDeleteUser = new JButton("Delete User");

        styleButton(btnDeleteUser);

        btnDeleteUser.addActionListener(e -> deleteUser());

        JButton btnUnblockUser = new JButton("Unblock User");

        styleButton(btnUnblockUser);

        btnUnblockUser.addActionListener(e -> unblockUser());

        JButton btnLogout = new JButton("Logout");
        styleButton(btnLogout);
        btnLogout.addActionListener(e -> logout());

        JTabbedPane tabbedPane = new JTabbedPane();
        JPanel topPanel = new JPanel(new BorderLayout());

        topPanel.add(lblUser, BorderLayout.CENTER);

        JPanel accountPanel = new JPanel();

        accountPanel.setBackground(PANEL);

        if (currentUser.getRole().equals("Admin")) {

            accountPanel.add(btnSignup);
            accountPanel.add(btnDeleteUser);
            accountPanel.add(btnUnblockUser);
        }
        accountPanel.add(btnChangePassword);
        accountPanel.add(btnLogout);

        topPanel.add(accountPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        topPanel.setBackground(PANEL);

        tabbedPane.setBackground(PANEL);
        tabbedPane.setForeground(NAVY);

        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabbedPane.addTab("Customers", createCustomerPanel());

        tabbedPane.addTab("PCs", createPCPanel());

        tabbedPane.addTab("Reservations", createReservationPanel());
        tabbedPane.addTab("Food & Drinks", createFoodPanel());

        tabbedPane.addTab("Reports", createReportPanel());

        setVisible(true);
    }

    private boolean login() {

        JTextField txtUsername = new JTextField();

        JPasswordField txtPassword = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(2, 2));

        panel.add(new JLabel("Username"));
        panel.add(txtUsername);

        panel.add(new JLabel("Password"));
        panel.add(txtPassword);

        Object[] options = {
            "Login",
            "Forgot Password",
            "Cancel"
        };

        int result = JOptionPane.showOptionDialog(null, panel, "Login", JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (result == 1) {
            forgotPassword();
            return false;
        }

        if (result == 2 || result == JOptionPane.CLOSED_OPTION) {
            System.exit(0);
        }

        String username = txtUsername.getText();

        String password = new String(txtPassword.getPassword());

        for (User user : users) {

            if (user.getUsername().equalsIgnoreCase(username)) {

                if (user.isBlocked()
                        && !user.getRole().equalsIgnoreCase("Admin")) {

                    JOptionPane.showMessageDialog(this, "Account Blocked.\nContact Admin.");
                    return false;
                }

                if (user.login(username, password)) {

                    currentUser = user;

                    fileManager.saveUsers(users);

                    JOptionPane.showMessageDialog(this, "Login Successful");

                    return true;
                }

                fileManager.saveUsers(users);

                int remaining = 3 - user.getLoginAttempts();

                if (remaining > 0) {

                    JOptionPane.showMessageDialog(this, "Incorrect Password.\n" + remaining + " attempt(s) remaining.");

                } else {

                    JOptionPane.showMessageDialog(this, "Account has been BLOCKED.");
                }

                return false;
            }
        }

        JOptionPane.showMessageDialog(
                this,
                "Username not found."
        );

        return false;
    }

    private void logout() {

        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {

            dispose();

            SwingUtilities.invokeLater(() -> new GamingCafeSystem());
        }
    }

    private void changePassword() {

        JPasswordField txtCurrent = new JPasswordField();

        JPasswordField txtNew = new JPasswordField();

        JPasswordField txtConfirm = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(3, 2));

        panel.add(new JLabel("Current Password"));
        panel.add(txtCurrent);

        panel.add(new JLabel("New Password"));
        panel.add(txtNew);

        panel.add(new JLabel("Confirm Password"));
        panel.add(txtConfirm);

        int result = JOptionPane.showConfirmDialog(this, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) {

            return;
        }

        String currentPassword = new String(txtCurrent.getPassword());

        String newPassword = new String(txtNew.getPassword());

        String confirmPassword = new String(txtConfirm.getPassword());

        if (!currentUser.login(currentUser.getUsername(), currentPassword)) {

            JOptionPane.showMessageDialog(this, "Current password is incorrect.");

            return;
        }

        if (currentUser.getRole().equals("Staff")) {

            if (!verifyAdmin()) {

                JOptionPane.showMessageDialog(this, "Admin approval required.");

                return;
            }
        }

        if (!newPassword.equals(confirmPassword)) {

            JOptionPane.showMessageDialog(this, "Passwords do not match.");

            return;
        }

        if (newPassword.isEmpty()) {

            JOptionPane.showMessageDialog(this, "Password cannot be empty.");

            return;
        }

        currentUser.changePassword(newPassword);

        fileManager.saveUsers(users);

        JOptionPane.showMessageDialog(this, "Password updated successfully.");
    }

    private void createUser() {

        if (!currentUser.getRole().equals("Admin")) {

            JOptionPane.showMessageDialog(this, "Only Admin can create users.");

            return;
        }

        JTextField txtUsername = new JTextField();

        JPasswordField txtPassword = new JPasswordField();

        JComboBox<String> cboRole = new JComboBox<>();

        cboRole.addItem("Admin");
        cboRole.addItem("Staff");

        JPanel panel = new JPanel(new GridLayout(3, 2));

        panel.add(new JLabel("Username"));
        panel.add(txtUsername);

        panel.add(new JLabel("Password"));
        panel.add(txtPassword);

        panel.add(new JLabel("Role"));
        panel.add(cboRole);

        int result = JOptionPane.showConfirmDialog(this, panel, "Create User", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) {

            return;
        }

        String username = txtUsername.getText();

        String password = new String(txtPassword.getPassword());

        String role = cboRole.getSelectedItem().toString();

        for (User user : users) {

            if (user.getUsername().equalsIgnoreCase(username)) {

                JOptionPane.showMessageDialog(this, "Username already exists.");

                return;
            }
        }

        String securityQuestion = "";
        String securityAnswer = "";

        if (role.equalsIgnoreCase("Admin")) {

            securityQuestion = JOptionPane.showInputDialog(this, "Enter Security Question");

            if (securityQuestion == null || securityQuestion.trim().isEmpty()) {

                JOptionPane.showMessageDialog(this, "Security Question is required.");

                return;
            }

            securityAnswer = JOptionPane.showInputDialog(this, "Enter Security Answer");

            if (securityAnswer == null || securityAnswer.trim().isEmpty()) {

                JOptionPane.showMessageDialog(this, "Security Answer is required.");

                return;
            }
        }

        users.add(new User(
                username,
                password,
                role,
                securityQuestion,
                securityAnswer,
                false,
                0
        ));
        fileManager.saveUsers(users);

        JOptionPane.showMessageDialog(this, "User created successfully.");
    }

    private void forgotPassword() {

        String username = JOptionPane.showInputDialog(
                this,
                "Enter your username:"
        );

        User targetUser = null;

        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                targetUser = user;
                break;
            }
        }

        if (targetUser == null) {
            JOptionPane.showMessageDialog(this,
                    "User not found.");
            return;
        }
        if (targetUser.getRole().equalsIgnoreCase("Staff")) {

            JTextField txtAdminUser = new JTextField();
            JPasswordField txtAdminPass = new JPasswordField();

            JPanel panel = new JPanel(new GridLayout(2, 2));

            panel.add(new JLabel("Admin Username"));
            panel.add(txtAdminUser);

            panel.add(new JLabel("Admin Password"));
            panel.add(txtAdminPass);

            int result = JOptionPane.showConfirmDialog(
                    this,
                    panel,
                    "Admin Authorization",
                    JOptionPane.OK_CANCEL_OPTION
            );

            if (result != JOptionPane.OK_OPTION) {
                return;
            }

            boolean adminVerified = false;

            for (User user : users) {

                if (user.getRole().equalsIgnoreCase("Admin")
                        && user.getUsername().equals(txtAdminUser.getText())
                        && user.getPassword().equals(
                                new String(txtAdminPass.getPassword()))) {

                    adminVerified = true;
                    break;
                }
            }

            if (!adminVerified) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid Admin Credentials."
                );
                return;
            }

            String newPassword = JOptionPane.showInputDialog(
                    this,
                    "Enter New Password:"
            );

            if (newPassword == null || newPassword.trim().isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Password cannot be blank."
                );
                return;
            }

            targetUser.changePassword(newPassword);
            targetUser.setBlocked(false);
            targetUser.setLoginAttempts(0);

            fileManager.saveUsers(users);

            JOptionPane.showMessageDialog(
                    this,
                    "Password Reset Successful."
            );

            return;
        }

        if (targetUser.getRole().equalsIgnoreCase("Admin")) {

            String answer = JOptionPane.showInputDialog(
                    this,
                    targetUser.getSecurityQuestion()
            );

            if (answer == null) {
                return;
            }

            if (answer.equalsIgnoreCase(targetUser.getSecurityAnswer())) {

                String newPassword = JOptionPane.showInputDialog(
                        this,
                        "Enter New Password:"
                );

                if (newPassword != null && !newPassword.trim().isEmpty()) {

                    if (newPassword == null || newPassword.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(
                                this,
                                "Password cannot be blank."
                        );
                        return;
                    }

                    targetUser.changePassword(newPassword);
                    targetUser.setBlocked(false);
                    targetUser.setLoginAttempts(0);

                    fileManager.saveUsers(users);

                    JOptionPane.showMessageDialog(
                            this,
                            "Password Reset Successful."
                    );
                }

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Incorrect Security Answer."
                );
            }
        }

    }

    private void deleteUser() {

        if (!currentUser.getRole().equals("Admin")) {

            JOptionPane.showMessageDialog(this, "Only Admin can delete users.");

            return;
        }

        String username = JOptionPane.showInputDialog(this, "Enter Username to Delete");

        if (username == null || username.isEmpty()) {

            return;
        }

        User selectedUser = null;

        for (User user : users) {

            if (user.getUsername().equalsIgnoreCase(username)) {

                selectedUser = user;

                break;
            }
        }

        if (selectedUser == null) {

            JOptionPane.showMessageDialog(this, "User not found.");

            return;
        }

        if (selectedUser.getUsername().equals(currentUser.getUsername())) {

            JOptionPane.showMessageDialog(this, "You cannot delete your own account.");

            return;
        }

        int choice = JOptionPane.showConfirmDialog(this, "Delete user: " + username + "?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {

            if (selectedUser.getRole().equals("Admin")) {

                int adminCount = 0;

                for (User user : users) {

                    if (user.getRole().equals("Admin")) {

                        adminCount++;
                    }
                }

                if (adminCount == 1) {

                    JOptionPane.showMessageDialog(this, "Cannot delete the last Admin account.");

                    return;
                }
            }

            users.remove(selectedUser);

            fileManager.saveUsers(users);

            JOptionPane.showMessageDialog(this, "User deleted successfully.");
        }
    }

    private void unblockUser() {

        String username
                = JOptionPane.showInputDialog(
                        this,
                        "Username to unblock"
                );

        if (username == null
                || username.trim().isEmpty()) {
            return;
        }

        for (User user : users) {

            if (user.getUsername().equalsIgnoreCase(username)) {

                user.setBlocked(false);
                user.setLoginAttempts(0);

                fileManager.saveUsers(users);

                JOptionPane.showMessageDialog(
                        this,
                        "User unblocked successfully."
                );

                return;
            }
        }

        JOptionPane.showMessageDialog(
                this,
                "User not found."
        );
    }

    private void styleButton(JButton button) {

        button.setBackground(ACCENT);

        button.setForeground(Color.WHITE);

        button.setFocusPainted(false);

        button.setFont(
                new Font("Segoe UI", Font.BOLD, 12));
    }

    private boolean verifyAdmin() {

        JTextField txtAdmin = new JTextField();

        JPasswordField txtPassword = new JPasswordField();

        JPanel panel = new JPanel(new GridLayout(2, 2));

        panel.add(new JLabel("Admin Username"));

        panel.add(txtAdmin);

        panel.add(new JLabel("Admin Password"));

        panel.add(txtPassword);

        int result = JOptionPane.showConfirmDialog(this, panel, "Admin Verification", JOptionPane.OK_CANCEL_OPTION);

        if (result != JOptionPane.OK_OPTION) {

            return false;
        }

        String username = txtAdmin.getText();

        String password = new String(txtPassword.getPassword());

        for (User user : users) {

            if (user.getRole().equals("Admin") && user.login(username, password)) {

                return true;
            }
        }

        JOptionPane.showMessageDialog(this, "Invalid Admin Credentials.");

        return false;
    }

    private JLabel createLabel(String text) {

        JLabel label = new JLabel(text);

        label.setForeground(NAVY);

        label.setFont(new Font("Segoe UI", Font.BOLD, 14));

        return label;
    }

    private JPanel createCustomerPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        txtCustomerId = new JTextField();
        txtCustomerId.setEditable(false);
        txtCustomerName = new JTextField();
        txtContactNumber = new JTextField();
        panel.setBackground(PANEL);

        formPanel.setBackground(PANEL);
        cboMembership = new JComboBox<>();

        cboMembership.addItem("Regular");
        cboMembership.addItem("VIP");

        formPanel.add(createLabel("Customer ID"));

        formPanel.add(txtCustomerId);
        formPanel.add(createLabel("Customer Name"));

        formPanel.add(txtCustomerName);

        formPanel.add(createLabel("Contact Number"));

        formPanel.add(txtContactNumber);

        formPanel.add(createLabel("Membership"));

        formPanel.add(cboMembership);

        panel.add(formPanel, BorderLayout.NORTH);

        customerModel = new DefaultTableModel();
        customerModel.addColumn("ID");
        customerModel.addColumn("Name");
        customerModel.addColumn("Contact");
        customerModel.addColumn("Membership");
        customerTable = new JTable(customerModel);

        customerTable.setBackground(Color.WHITE);

        customerTable.setForeground(NAVY);

        customerTable.setSelectionBackground(ACCENT);

        customerTable.setRowHeight(25);
        customerTable.setShowGrid(true);

        customerTable.setGridColor(ACCENT);

        customerTable.getTableHeader().setBackground(ACCENT);

        customerTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(customerTable);

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnSearch = new JButton("Search");
        JButton btnSort = new JButton("Sort Membership");
        JButton btnRefresh = new JButton("Refresh");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnSort);
        buttonPanel.add(btnRefresh);

        styleButton(btnAdd);
        styleButton(btnUpdate);
        styleButton(btnDelete);
        styleButton(btnSearch);
        styleButton(btnSort);
        styleButton(btnRefresh);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addCustomer());
        btnUpdate.addActionListener(e -> updateCustomer());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnSearch.addActionListener(e -> searchCustomer());
        btnRefresh.addActionListener(e -> {
            customers = fileManager.loadCustomers();

            loadCustomers();

            clearCustomerFields();

            JOptionPane.showMessageDialog(this, "Customer list refreshed.");
        });
        btnSort.addActionListener(e -> {

            sortCustomersByMembership();
            loadCustomers();

            JOptionPane.showMessageDialog(this, "Customers sorted by Membership.");
        });

        customerTable.getSelectionModel().addListSelectionListener(e -> loadSelectedCustomer());

        return panel;
    }

    private void addCustomer() {

        String id = txtCustomerId.getText();
        String name = txtCustomerName.getText();
        String contact = txtContactNumber.getText();
        String membership = cboMembership.getSelectedItem().toString();

        if (id.isEmpty() || name.isEmpty()) {

            JOptionPane.showMessageDialog(this, "Please fill in all required fields.");

            return;
        }

        Customer customer = new Customer(id, name, contact, membership);

        customers.add(customer);

        loadCustomers();

        clearCustomerFields();
        refreshCustomerComboBox();
        fileManager.saveCustomers(customers);
    }

    private void updateCustomer() {

        if (currentUser.getRole().equals("Staff")) {

            if (!verifyAdmin()) {

                return;
            }
        }

        String id = txtCustomerId.getText();

        for (Customer customer : customers) {

            if (customer.getCustomerId().equals(id)) {

                customer.setName(txtCustomerName.getText());

                customer.setContactNumber(txtContactNumber.getText());

                customer.setMembershipType(cboMembership.getSelectedItem().toString());

                break;
            }
        }

        loadCustomers();
        clearCustomerFields();
        fileManager.saveCustomers(customers);
    }

    private void deleteCustomer() {

        if (currentUser.getRole().equals("Staff")) {

            if (!verifyAdmin()) {

                return;
            }
        }

        String id = txtCustomerId.getText();

        Customer selectedCustomer = null;

        for (Customer customer : customers) {

            if (customer.getCustomerId().equals(id)) {

                selectedCustomer = customer;
                break;
            }
        }

        if (selectedCustomer != null) {

            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this customer?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {

                customers.remove(selectedCustomer);

                fileManager.saveCustomers(customers);

                loadCustomers();

                clearCustomerFields();

                JOptionPane.showMessageDialog(this, "Customer deleted successfully."
                );
            }
        }
    }

    private void searchCustomer() {

        String id = JOptionPane.showInputDialog(this, "Enter Customer ID:");

        if (id == null) {
            return;
        }

        for (Customer customer : customers) {

            if (customer.getCustomerId().equals(id)) {

                txtCustomerId.setText(customer.getCustomerId());

                txtCustomerName.setText(customer.getName());

                txtContactNumber.setText(customer.getContactNumber());

                cboMembership.setSelectedItem(customer.getMembershipType());

                JOptionPane.showMessageDialog(this, "Customer Found!");

                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Customer Not Found!");
    }

    private void sortCustomersByMembership() {

        for (int i = 0; i < customers.size() - 1; i++) {

            for (int j = 0; j < customers.size() - i - 1; j++) {

                String current = customers.get(j).getMembershipType();

                String next = customers.get(j + 1).getMembershipType();

                if (current.equals("Regular") && next.equals("VIP")) {

                    Customer temp = customers.get(j);

                    customers.set(j, customers.get(j + 1));

                    customers.set(j + 1, temp);
                }
            }
        }
    }

    private void loadCustomers() {

        if (customerModel == null) {
            return;
        }
        customerModel.setRowCount(0);

        for (Customer customer : customers) {

            customerModel.addRow(
                    new Object[]{
                        customer.getCustomerId(),
                        customer.getName(),
                        customer.getContactNumber(),
                        customer.getMembershipType()
                    }
            );
        }
    }

    private void loadSelectedCustomer() {

        int row = customerTable.getSelectedRow();

        if (row < 0) {
            return;
        }

        txtCustomerId.setText(customerModel.getValueAt(row, 0).toString());

        txtCustomerName.setText(customerModel.getValueAt(row, 1).toString());

        txtContactNumber.setText(customerModel.getValueAt(row, 2).toString());

        cboMembership.setSelectedItem(customerModel.getValueAt(row, 3).toString());
    }

    private void clearCustomerFields() {

        txtCustomerId.setText(generateCustomerId());

        txtCustomerName.setText("");
        txtContactNumber.setText("");

        cboMembership.setSelectedIndex(0);
    }

    private JTable pcTable;
    private DefaultTableModel pcModel;

    private JTextField txtPcId;
    private JTextField txtPcName;
    private JTextField txtHourlyRate;

    private JComboBox<String> cboStatus;

    private JPanel createPCPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));

        txtPcId = new JTextField();
        txtPcId.setEditable(false);
        txtPcName = new JTextField();
        txtHourlyRate = new JTextField();

        cboStatus = new JComboBox<>();

        cboStatus.setBackground(Color.WHITE);
        panel.setBackground(PANEL);

        formPanel.setBackground(PANEL);

        cboStatus.addItem("AVAILABLE");
        cboStatus.addItem("OCCUPIED");
        cboStatus.addItem("MAINTENANCE");

        formPanel.add(createLabel("PC ID"));

        formPanel.add(txtPcId);

        formPanel.add(createLabel("PC Name"));

        formPanel.add(txtPcName);

        formPanel.add(createLabel("Hourly Rate"));

        formPanel.add(txtHourlyRate);

        formPanel.add(createLabel("Status"));

        formPanel.add(cboStatus);

        panel.add(formPanel, BorderLayout.NORTH);

        pcModel = new DefaultTableModel();

        pcModel.addColumn("PC ID");
        pcModel.addColumn("PC Name");
        pcModel.addColumn("Hourly Rate");
        pcModel.addColumn("Status");

        pcTable = new JTable(pcModel);

        pcTable.setBackground(Color.WHITE);

        pcTable.setForeground(NAVY);

        pcTable.setGridColor(Color.GRAY);

        pcTable.setSelectionBackground(ACCENT);

        pcTable.setSelectionForeground(Color.WHITE);

        pcTable.setRowHeight(25);
        pcTable.setShowGrid(true);

        pcTable.setGridColor(ACCENT);

        pcTable.getTableHeader().setBackground(ACCENT);

        pcTable.getTableHeader().setForeground(Color.WHITE);

        panel.add(new JScrollPane(pcTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton btnAdd = new JButton("Add");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Delete");
        JButton btnSearch = new JButton("Search");
        JButton btnStartSession = new JButton("Start Session");

        JButton btnEndSession = new JButton("End Session");
        JButton btnSortPC = new JButton("Sort PC Name");
        JButton btnRefresh = new JButton("Refresh");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnStartSession);
        buttonPanel.add(btnEndSession);
        buttonPanel.add(btnSortPC);
        buttonPanel.add(btnRefresh);

        styleButton(btnAdd);
        styleButton(btnUpdate);
        styleButton(btnDelete);
        styleButton(btnSearch);
        styleButton(btnStartSession);
        styleButton(btnEndSession);
        styleButton(btnSortPC);
        styleButton(btnRefresh);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addPC());
        btnUpdate.addActionListener(e -> updatePC());
        btnDelete.addActionListener(e -> deletePC());
        btnSearch.addActionListener(e -> searchPC());
        btnRefresh.addActionListener(e -> {

            pcs = fileManager.loadPCs();

            loadPCs();

            clearPCFields();

            JOptionPane.showMessageDialog(this, "PC list refreshed.");
        });

        btnStartSession.addActionListener(e -> startSession());

        btnEndSession.addActionListener(e -> endSession());

        pcTable.getSelectionModel().addListSelectionListener(e -> loadSelectedPC());

        btnSortPC.addActionListener(e -> {

            sortPCsByName();
            loadPCs();

            JOptionPane.showMessageDialog(this, "PCs sorted alphabetically.");
        });

        return panel;
    }

    private void addPC() {

        try {

            String id = txtPcId.getText();
            String name = txtPcName.getText();

            double rate = Double.parseDouble(txtHourlyRate.getText());

            String status = cboStatus.getSelectedItem().toString();

            PC pc = new PC(id, name, rate, status);

            pcs.add(pc);

            loadPCs();

            clearPCFields();
            refreshPCComboBox();
            fileManager.savePCs(pcs);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, "Invalid input.");

        }
    }

    private void updatePC() {

        if (currentUser.getRole().equals("Staff")) {

            if (!verifyAdmin()) {

                return;
            }
        }

        String id = txtPcId.getText();

        for (PC pc : pcs) {

            if (pc.getPcId().equals(id)) {

                pc.setPcName(txtPcName.getText());

                pc.setHourlyRate(Double.parseDouble(txtHourlyRate.getText()));

                pc.setStatus(cboStatus.getSelectedItem().toString());

                break;
            }
        }

        loadPCs();

        clearPCFields();
        fileManager.savePCs(pcs);
    }

    private void deletePC() {

        if (currentUser.getRole().equals("Staff")) {

            if (!verifyAdmin()) {

                return;
            }
        }

        String id = txtPcId.getText();

        PC selectedPC = null;

        for (PC pc : pcs) {

            if (pc.getPcId().equals(id)) {

                selectedPC = pc;
                break;
            }
        }

        if (selectedPC != null) {

            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this PC?", "Confirm Delete",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {

                pcs.remove(selectedPC);

                fileManager.savePCs(pcs);

                loadPCs();

                clearPCFields();

                JOptionPane.showMessageDialog(this, "PC deleted successfully.");
            }
        } else {

            JOptionPane.showMessageDialog(this, "PC not found.");
        }
    }

    private void searchPC() {

        String id = JOptionPane.showInputDialog(this, "Enter PC ID");

        if (id == null) {
            return;
        }

        for (PC pc : pcs) {

            if (pc.getPcId().equals(id)) {

                txtPcId.setText(pc.getPcId());

                txtPcName.setText(pc.getPcName());

                txtHourlyRate.setText(String.valueOf(pc.getHourlyRate()));

                cboStatus.setSelectedItem(pc.getStatus());

                JOptionPane.showMessageDialog(this, "PC Found!");

                return;
            }
        }

        JOptionPane.showMessageDialog(this, "PC Not Found!");
    }

    private void sortPCsByName() {

        for (int i = 0; i < pcs.size() - 1; i++) {

            for (int j = 0; j < pcs.size() - i - 1; j++) {

                if (pcs.get(j).getPcName().compareToIgnoreCase(pcs.get(j + 1).getPcName()) > 0) {

                    PC temp = pcs.get(j);

                    pcs.set(j, pcs.get(j + 1));

                    pcs.set(j + 1, temp);
                }
            }
        }
    }

    private void loadPCs() {

        if (pcModel == null) {
            return;
        }
        pcModel.setRowCount(0);

        for (PC pc : pcs) {

            pcModel.addRow(
                    new Object[]{
                        pc.getPcId(),
                        pc.getPcName(),
                        pc.getHourlyRate(),
                        pc.getStatus()
                    }
            );
        }
    }

    private void loadSelectedPC() {

        int row = pcTable.getSelectedRow();

        if (row < 0) {
            return;
        }

        txtPcId.setText(pcModel.getValueAt(row, 0).toString());

        txtPcName.setText(pcModel.getValueAt(row, 1).toString());

        txtHourlyRate.setText(pcModel.getValueAt(row, 2).toString());

        cboStatus.setSelectedItem(pcModel.getValueAt(row, 3).toString());
    }

    private void clearPCFields() {

        txtPcId.setText(generatePCId());

        txtPcName.setText("");
        txtHourlyRate.setText("");

        cboStatus.setSelectedIndex(0);
    }

    private double totalSessionRevenue = 0;
    private double totalReservationRevenue = 0;

    private void startSession() {

        String customerId = JOptionPane.showInputDialog(this, "Enter Customer ID");

        if (customerId == null || customerId.isEmpty()) {

            return;
        }

        Customer selectedCustomer = null;

        for (Customer customer : customers) {

            if (customer.getCustomerId().equals(customerId)) {

                selectedCustomer = customer;

                break;
            }
        }

        if (selectedCustomer == null) {

            JOptionPane.showMessageDialog(this, "Customer not found.");

            return;
        }

        String hoursInput = JOptionPane.showInputDialog(this, "Enter Hours");

        if (hoursInput == null || hoursInput.isEmpty()) {

            return;
        }

        double hours;

        try {

            hours = Double.parseDouble(hoursInput);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, "Invalid hours.");

            return;
        }

        String pcId = txtPcId.getText();

        PC selectedPC = null;

        for (PC pc : pcs) {

            if (pc.getPcId().equals(pcId)) {

                selectedPC = pc;

                break;
            }
        }

        if (selectedPC == null) {

            JOptionPane.showMessageDialog(this, "Select a PC first.");

            return;
        }

        if (selectedPC.getStatus().equals("OCCUPIED")) {

            JOptionPane.showMessageDialog(this, "PC is already occupied.");

            return;
        }

        double subtotal = hours * selectedPC.getHourlyRate();

        double discount = 0;

        if (selectedCustomer.getMembershipType().equalsIgnoreCase("VIP")) {

            discount = subtotal * 0.10;
        }

        double total = subtotal - discount;

        String sessionId
                = generateSessionId();

        Session session
                = new Session(
                        sessionId,
                        customerId,
                        selectedPC.getPcId(),
                        hours,
                        java.time.LocalDate.now()
                                .toString());

        session.calculatePayment(
                selectedPC.getHourlyRate(),
                selectedCustomer.getMembershipType());

        sessions.add(session);

        fileManager.saveSessions(
                sessions);

        totalSessionRevenue += total;

        selectedPC.setStatus("OCCUPIED");

        fileManager.savePCs(pcs);

        loadPCs();

        JOptionPane.showMessageDialog(this,
                "Session Started\n\n"
                + "Customer: " + customerId
                + "\nMembership: " + selectedCustomer.getMembershipType()
                + "\nHours: " + hours
                + "\nSubtotal: PHP " + subtotal
                + "\nDiscount: PHP " + discount
                + "\nTotal: PHP " + total);
    }

    private void endSession() {

        String pcId = txtPcId.getText();

        PC selectedPC = null;

        for (PC pc : pcs) {

            if (pc.getPcId().equals(pcId)) {

                selectedPC = pc;

                break;
            }
        }

        if (selectedPC == null) {

            JOptionPane.showMessageDialog(this, "Select a PC first.");

            return;
        }

        if (selectedPC.getStatus().equals("AVAILABLE")) {

            JOptionPane.showMessageDialog(this, "PC is already available.");

            return;
        }

        selectedPC.setStatus("AVAILABLE");

        fileManager.savePCs(pcs);

        loadPCs();

        JOptionPane.showMessageDialog(this, "Session Ended.");
    }

    private JTable reservationTable;
    private DefaultTableModel reservationModel;

    private JTextField txtReservationId;
    private JTextField txtDate;
    private JTextField txtStartTime;
    private JTextField txtEndTime;

    private JComboBox<String> cboCustomer;
    private JComboBox<String> cboPC;

    private JPanel createReservationPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 5, 5));

        txtReservationId = new JTextField();
        txtReservationId.setEditable(false);
        txtDate = new JTextField();
        txtStartTime = new JTextField();
        txtEndTime = new JTextField();

        cboCustomer = new JComboBox<>();
        cboPC = new JComboBox<>();

        formPanel.add(createLabel("Reservation ID"));

        formPanel.add(txtReservationId);

        formPanel.add(createLabel("Customer"));

        formPanel.add(cboCustomer);

        formPanel.add(createLabel("PC"));

        formPanel.add(cboPC);

        formPanel.add(createLabel("Date (e.g. 2026-10-30)"));

        formPanel.add(txtDate);

        formPanel.add(createLabel("Start Time (e.g. 08:00)"));

        formPanel.add(txtStartTime);

        formPanel.add(createLabel("End Time (e.g. 13:00)"));

        formPanel.add(txtEndTime);

        panel.add(formPanel, BorderLayout.NORTH);

        reservationModel = new DefaultTableModel();

        reservationModel.addColumn("Reservation ID");
        reservationModel.addColumn("Customer ID");
        reservationModel.addColumn("PC ID");
        reservationModel.addColumn("Date");
        reservationModel.addColumn("Status");
        reservationModel.addColumn("Total");

        panel.setBackground(PANEL);

        formPanel.setBackground(PANEL);
        reservationTable = new JTable(reservationModel);

        reservationTable.setBackground(Color.WHITE);

        reservationTable.setForeground(NAVY);

        reservationTable.setGridColor(Color.GRAY);

        reservationTable.setSelectionBackground(ACCENT);

        reservationTable.setSelectionForeground(Color.WHITE);

        reservationTable.setRowHeight(25);
        reservationTable.setShowGrid(true);

        reservationTable.setGridColor(ACCENT);

        reservationTable.getTableHeader().setBackground(ACCENT);

        reservationTable.getTableHeader().setForeground(Color.WHITE);

        panel.add(new JScrollPane(reservationTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton btnAdd = new JButton("Add");
        JButton btnDelete = new JButton("Delete");
        JButton btnSearch = new JButton("Search");
        JButton btnComplete = new JButton("Complete");
        JButton btnSortReservation = new JButton("Sort Date");
        JButton btnRefresh = new JButton("Refresh");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnSearch);
        buttonPanel.add(btnComplete);
        buttonPanel.add(btnSortReservation);
        buttonPanel.add(btnRefresh);

        styleButton(btnAdd);
        styleButton(btnDelete);
        styleButton(btnSearch);
        styleButton(btnComplete);
        styleButton(btnSortReservation);
        styleButton(btnRefresh);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> addReservation());

        btnDelete.addActionListener(e -> deleteReservation());

        btnSearch.addActionListener(e -> searchReservation());

        btnComplete.addActionListener(e -> completeReservation());

        btnSortReservation.addActionListener(e -> {

            sortReservationsByDate();
            loadReservations();

            JOptionPane.showMessageDialog(this, "Reservations sorted by Date.");
        });
        btnRefresh.addActionListener(e -> {

            reservations
                    = fileManager.loadReservations();

            loadReservations();

            clearReservationFields();

            JOptionPane.showMessageDialog(this, "Reservations refreshed.");
        });

        if (currentUser.getRole().equals("Staff")) {

            btnDelete.setEnabled(false);
        }

        reservationTable.getSelectionModel().addListSelectionListener(e -> loadSelectedReservation());

        return panel;
    }

    private void refreshCustomerComboBox() {

        cboCustomer.removeAllItems();

        for (Customer customer : customers) {

            cboCustomer.addItem(customer.getCustomerId());
        }
    }

    private void refreshPCComboBox() {

        cboPC.removeAllItems();

        for (PC pc : pcs) {

            cboPC.addItem(pc.getPcId());
        }
    }

    private void addReservation() {

        try {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

            LocalTime startTime = LocalTime.parse(txtStartTime.getText().trim(), formatter);

            LocalTime endTime = LocalTime.parse(txtEndTime.getText().trim(), formatter);

            LocalTime openingTime = LocalTime.of(8, 0);

            LocalTime closingTime = LocalTime.of(22, 0);

            if (startTime.isBefore(openingTime) || endTime.isAfter(closingTime)) {

                JOptionPane.showMessageDialog(this,
                        """
            Invalid Reservation Time!

            Gaming Cafe Operating Hours:
            08:00 AM - 10:00 PM
            """
                );

                return;
            }

            if (!endTime.isAfter(startTime)) {

                JOptionPane.showMessageDialog(this, "End Time must be greater than Start Time.");

                return;
            }

            long hours = Duration.between(startTime, endTime).toHours();

            String customerId = cboCustomer.getSelectedItem().toString();

            String pcId = cboPC.getSelectedItem().toString();

            for (Reservation reservation : reservations) {

                if (!reservation.getStatus().equals("ACTIVE")) {

                    continue;
                }

                if (reservation.getPcId().equals(pcId)
                        && reservation.getDate().equals(txtDate.getText())) {

                    LocalTime existingStart = LocalTime.parse(reservation.getStartTime());

                    LocalTime existingEnd = LocalTime.parse(reservation.getEndTime());

                    if (startTime.isBefore(existingEnd) && endTime.isAfter(existingStart)) {

                        JOptionPane.showMessageDialog(this,
                                """
                    Reservation Conflict!

                    This PC is already reserved
                    during the selected time.
                    """
                        );

                        return;
                    }
                }
            }

            String membership = "Regular";

            double rate = 0;

            for (Customer customer : customers) {

                if (customer.getCustomerId().equals(customerId)) {

                    membership = customer.getMembershipType();

                    break;
                }
            }

            for (PC pc : pcs) {

                if (pc.getPcId().equals(pcId)) {

                    rate = pc.getHourlyRate();

                    break;
                }
            }

            Reservation reservation = new Reservation(txtReservationId.getText(), customerId, pcId, txtDate.getText(),
                    txtStartTime.getText(), txtEndTime.getText());

            reservation.calculatePayment(hours, rate, membership);

            totalReservationRevenue += reservation.getTotal();

            reservations.add(reservation);
            fileManager.saveReservations(reservations);
            loadReservations();
            loadPCs();

            JOptionPane.showMessageDialog(this,
                    "Reservation Created Successfully\n\n"
                    + "Hours: " + reservation.getHours()
                    + "\nSubtotal: " + reservation.getSubtotal()
                    + "\nDiscount: " + reservation.getDiscount()
                    + "\nVAT: " + reservation.getVat()
                    + "\nTotal: " + reservation.getTotal()
            );

            clearReservationFields();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this,
                    """
                Invalid Input

                Example:

                Date: 2026-10-30

                Start Time: 08:00

                End Time: 12:00
                """
            );
        }
    }

    private void sortReservationsByDate() {

        for (int i = 0; i < reservations.size() - 1; i++) {

            for (int j = 0; j < reservations.size() - i - 1; j++) {

                String date1 = reservations.get(j).getDate();

                String date2 = reservations.get(j + 1).getDate();

                if (date1.compareTo(date2) > 0) {

                    Reservation temp = reservations.get(j);

                    reservations.set(j, reservations.get(j + 1));

                    reservations.set(j + 1, temp);
                }
            }
        }
    }

    private void loadReservations() {

        if (reservationModel == null) {
            return;
        }

        reservationModel.setRowCount(0);

        for (Reservation reservation : reservations) {

            if (reservation.getStatus().equals("ACTIVE")) {

                reservationModel.addRow(
                        new Object[]{
                            reservation.getReservationId(),
                            reservation.getCustomerId(),
                            reservation.getPcId(),
                            reservation.getDate(),
                            reservation.getStatus(),
                            reservation.getTotal()
                        }
                );
            }
        }
    }

    private void deleteReservation() {

        String id = txtReservationId.getText();

        Reservation selectedReservation = null;

        for (Reservation reservation : reservations) {

            if (reservation.getReservationId().equals(id)) {

                selectedReservation = reservation;
                break;
            }
        }

        if (selectedReservation != null) {

            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this reservation?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {

                for (PC pc : pcs) {

                    if (pc.getPcId().equals(selectedReservation.getPcId())) {

                        pc.setStatus("AVAILABLE");

                        fileManager.savePCs(pcs);

                        break;
                    }
                }
                reservations.remove(selectedReservation);
                fileManager.saveReservations(reservations);
                loadReservations();

                clearReservationFields();

                JOptionPane.showMessageDialog(this, "Reservation deleted successfully.");
            }

        } else {

            JOptionPane.showMessageDialog(this, "Reservation not found.");
        }
    }

    private void searchReservation() {

        String id = JOptionPane.showInputDialog(this, "Enter Reservation ID");

        if (id == null) {
            return;
        }

        for (Reservation reservation : reservations) {

            if (reservation.getReservationId().equals(id)) {

                JOptionPane.showMessageDialog(this, reservation.getDetails());

                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Reservation Not Found");
    }

    private void loadSelectedReservation() {

        int row = reservationTable.getSelectedRow();

        if (row < 0) {
            return;
        }

        txtReservationId.setText(reservationModel.getValueAt(row, 0).toString());
    }

    private void clearReservationFields() {

        txtReservationId.setText(generateReservationId());

        txtDate.setText("");
        txtStartTime.setText("");
        txtEndTime.setText("");
    }

    private void completeReservation() {

        String reservationId = txtReservationId.getText();

        Reservation selected = null;

        for (Reservation reservation : reservations) {

            if (reservation.getReservationId().equals(reservationId)) {

                selected = reservation;
                break;
            }
        }

        if (selected == null) {

            JOptionPane.showMessageDialog(this, "Reservation not found.");

            return;
        }

        if (selected.getStatus().equals("COMPLETED")) {

            JOptionPane.showMessageDialog(this, "Reservation already completed.");

            return;
        }

        selected.setStatus("COMPLETED");
        fileManager.saveReservations(reservations);
        for (PC pc : pcs) {

            if (pc.getPcId().equals(selected.getPcId())) {

                pc.setStatus("AVAILABLE");

                break;
            }
        }

        fileManager.savePCs(pcs);

        loadReservations();
        loadPCs();

        JOptionPane.showMessageDialog(this, "Reservation completed successfully."
        );
    }

    private JTable productTable;
    private DefaultTableModel productModel;

    private JPanel createFoodPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        panel.setBackground(PANEL);

        productModel = new DefaultTableModel();

        productModel.addColumn("Product ID");
        productModel.addColumn("Product Name");
        productModel.addColumn("Category");
        productModel.addColumn("Price");
        productModel.addColumn("Stock");

        productTable = new JTable(productModel);

        productTable.setBackground(Color.WHITE);
        productTable.setForeground(NAVY);
        productTable.setSelectionBackground(ACCENT);
        productTable.setSelectionForeground(Color.WHITE);
        productTable.setRowHeight(25);

        productTable.getTableHeader().setBackground(ACCENT);

        productTable.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(productTable);

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton btnSearch = new JButton("Search");

        JButton btnSort = new JButton("Sort Price");

        JButton btnRefresh = new JButton("Refresh");

        JButton btnOrder = new JButton("Order");
        JButton btnRestock = new JButton("Restock");

        styleButton(btnSearch);
        styleButton(btnSort);
        styleButton(btnRefresh);
        styleButton(btnOrder);
        styleButton(btnRestock);

        buttonPanel.add(btnSearch);
        buttonPanel.add(btnSort);
        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnOrder);
        buttonPanel.add(btnRestock);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        btnSearch.addActionListener(e -> searchProduct());

        btnSort.addActionListener(e -> {

            sortProductsByPrice();

            loadProducts();

            JOptionPane.showMessageDialog(this, "Products sorted by price.");
        });

        btnRefresh.addActionListener(e -> {

            products = fileManager.loadProducts();

            loadProducts();

            JOptionPane.showMessageDialog(this, "Products refreshed.");
        });

        btnOrder.addActionListener(e -> orderProduct());

        btnRestock.addActionListener(e -> restockProduct());

        return panel;
    }

    private void loadDefaultProducts() {

        if (!products.isEmpty()) {
            return;
        }

        products.add(new Product("P001", "Pancit Canton", "Food", 35, 50));

        products.add(new Product("P002", "Fries", "Food", 50, 50));

        products.add(new Product("P003", "Nachos", "Food", 70, 50));

        products.add(new Product("P004", "Burger", "Food", 80, 50));

        products.add(new Product("P005", "Footlong", "Food", 90, 50));

        products.add(new Product("P006", "Coffee", "Drink", 30, 50));

        products.add(new Product("P007", "Coke", "Drink", 35, 50));

        products.add(new Product("P008", "Sprite", "Drink", 35, 50));

        products.add(new Product("P009", "Royal", "Drink", 35, 50));

        products.add(new Product("P010", "Water", "Drink", 20, 50));
    }

    private void loadProducts() {

        if (productModel == null) {

            return;
        }

        productModel.setRowCount(0);

        for (Product product : products) {

            productModel.addRow(
                    new Object[]{
                        product.getProductId(),
                        product.getProductName(),
                        product.getCategory(),
                        product.getPrice(),
                        product.getStock()
                    });
        }
    }

    private void searchProduct() {

        String id = JOptionPane.showInputDialog(this, "Enter Product ID");

        if (id == null || id.isEmpty()) {

            return;
        }

        for (Product product : products) {

            if (product.getProductId().equalsIgnoreCase(id)) {

                JOptionPane.showMessageDialog(this,
                        "Product Found\n\n"
                        + "ID: " + product.getProductId()
                        + "\nName: " + product.getProductName()
                        + "\nCategory: " + product.getCategory()
                        + "\nPrice: PHP " + product.getPrice()
                        + "\nStock: " + product.getStock());

                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Product Not Found");
    }

    private void sortProductsByPrice() {

        for (int i = 0; i < products.size() - 1; i++) {

            for (int j = 0; j < products.size() - i - 1; j++) {

                if (products.get(j).getPrice() > products.get(j + 1).getPrice()) {

                    Product temp = products.get(j);

                    products.set(j, products.get(j + 1));

                    products.set(j + 1, temp);
                }
            }
        }
    }

    private void orderProduct() {

        int row = productTable.getSelectedRow();

        if (row < 0) {

            JOptionPane.showMessageDialog(this, "Select a product first.");

            return;
        }

        String customerId = JOptionPane.showInputDialog(this, "Enter Customer ID");

        if (customerId == null || customerId.isEmpty()) {

            return;
        }

        Customer selectedCustomer = null;

        for (Customer customer : customers) {

            if (customer.getCustomerId().equals(customerId)) {

                selectedCustomer = customer;

                break;
            }
        }

        if (selectedCustomer == null) {

            JOptionPane.showMessageDialog(this, "Customer not found.");

            return;
        }

        String quantityInput = JOptionPane.showInputDialog(this, "Enter Quantity");

        if (quantityInput == null) {

            return;
        }

        int quantity;

        try {

            quantity = Integer.parseInt(quantityInput);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, "Invalid quantity");

            return;
        }

        Product product = products.get(row);

        if (quantity <= 0) {

            JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.");

            return;
        }

        if (quantity > product.getStock()) {

            JOptionPane.showMessageDialog(this, "Insufficient stock.");

            return;
        }

        double subtotal = product.getPrice() * quantity;

        double discount = 0;

        if (selectedCustomer.getMembershipType().equalsIgnoreCase("VIP")) {

            discount = subtotal * 0.10;
        }

        double total = subtotal - discount;

        product.setStock(product.getStock() - quantity);

        String orderId = generateOrderId();

        Order order = new Order(orderId, customerId, product.getProductId(), quantity, java.time.LocalDate.now().toString());

        order.setSubtotal(subtotal);

        order.setDiscount(discount);

        order.setTotal(total);

        orders.add(order);
        fileManager.saveProducts(products);
        fileManager.saveOrders(orders);

        loadProducts();

        JOptionPane.showMessageDialog(this,
                "ORDER RECEIPT\n\n"
                + "Order ID: " + orderId
                + "\nCustomer: " + customerId
                + "\nProduct: " + product.getProductName()
                + "\nQuantity: " + quantity
                + "\nSubtotal: PHP " + subtotal
                + "\nDiscount: PHP " + discount
                + "\nTotal: PHP " + total);
    }

    private String generateOrderId() {

        int max = 0;

        for (Order order : orders) {

            String id = order.getOrderId();

            int number = Integer.parseInt(id.substring(1));

            if (number > max) {

                max = number;
            }
        }

        return String.format("O%03d", max + 1);
    }

    private void restockProduct() {

        if (currentUser.getRole().equals("Staff")) {

            if (!verifyAdmin()) {

                return;
            }
        }

        int row = productTable.getSelectedRow();

        if (row < 0) {

            JOptionPane.showMessageDialog(this, "Select a product first.");

            return;
        }

        String input = JOptionPane.showInputDialog(this, "Enter quantity to add");

        if (input == null) {

            return;
        }

        try {

            int quantity = Integer.parseInt(input);

            if (quantity <= 0) {

                JOptionPane.showMessageDialog(this, "Invalid quantity.");

                return;
            }

            Product product = products.get(row);

            product.setStock(product.getStock() + quantity);

            fileManager.saveProducts(products);

            loadProducts();

            JOptionPane.showMessageDialog(this, quantity + " stock added to " + product.getProductName());

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, "Invalid input.");
        }
    }

    private JTextArea txtReport;

    private JPanel createReportPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        txtReport = new JTextArea();
        txtReport.setBackground(Color.WHITE);

        txtReport.setForeground(NAVY);

        txtReport.setFont(new Font("Consolas", Font.PLAIN, 14));

        txtReport.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(txtReport);

        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();

        JButton btnGenerate = new JButton("Generate Report");

        JButton btnSalesReport = new JButton("Generate Sales Report");

        styleButton(btnGenerate);
        styleButton(btnSalesReport);

        buttonPanel.add(btnGenerate);
        buttonPanel.add(btnSalesReport);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        btnGenerate.addActionListener(e -> generateReport());
        btnSalesReport.addActionListener(e -> generateSalesReport());

        JButton btnSave = new JButton("Save Report");
        buttonPanel.add(btnSave);

        styleButton(btnSave);

        btnSave.addActionListener(e -> saveReport());

        return panel;
    }

    private void generateReport() {

        int availablePCs = 0;
        int occupiedPCs = 0;
        int maintenancePCs = 0;

        int activeReservations = 0;
        int completedReservations = 0;

        double reservationRevenue = 0;
        double sessionRevenue = 0;

        StringBuilder reservationDetails = new StringBuilder();

        StringBuilder sessionDetails = new StringBuilder();

        for (PC pc : pcs) {

            if (pc.getStatus().equalsIgnoreCase("AVAILABLE")) {

                availablePCs++;

            } else if (pc.getStatus().equalsIgnoreCase("OCCUPIED")) {

                occupiedPCs++;

            } else {

                maintenancePCs++;
            }
        }

        for (Reservation reservation : reservations) {

            reservationRevenue += reservation.getTotal();

            if (reservation.getStatus().equals("ACTIVE")) {

                activeReservations++;

            } else {

                completedReservations++;
            }

            reservationDetails.append(
                    "Reservation ID: ")
                    .append(reservation.getReservationId())
                    .append("\nCustomer: ")
                    .append(reservation.getCustomerId())
                    .append("\nPC: ")
                    .append(reservation.getPcId())
                    .append("\nDate: ")
                    .append(reservation.getDate())
                    .append("\nTotal: PHP ")
                    .append(reservation.getTotal())
                    .append("\n-------------------------\n");
        }

        for (Session session : sessions) {

            sessionRevenue += session.getTotal();

            sessionDetails.append(
                    "Session ID: ")
                    .append(session.getSessionId())
                    .append("\nCustomer: ")
                    .append(session.getCustomerId())
                    .append("\nPC: ")
                    .append(session.getPcId())
                    .append("\nHours: ")
                    .append(session.getHours())
                    .append("\nDate: ")
                    .append(session.getDate())
                    .append("\nTotal: PHP ")
                    .append(session.getTotal())
                    .append(
                            "\n-------------------------\n");
        }

        double totalRevenue
                = reservationRevenue
                + sessionRevenue;

        txtReport.setText(
                "===== GAMING CAFE REPORT =====\n\n"
                + "Total Customers: " + customers.size()
                + "\n\nTotal PCs: " + pcs.size()
                + "\n\nAvailable PCs: " + availablePCs
                + "\nOccupied PCs: " + occupiedPCs
                + "\nMaintenance PCs: " + maintenancePCs
                + "\n\nTotal Reservations: " + reservations.size()
                + "\nActive Reservations: " + activeReservations
                + "\nCompleted Reservations: " + completedReservations
                + "\n\nReservation Revenue: PHP " + reservationRevenue
                + "\nSession Revenue: PHP " + sessionRevenue
                + "\nTotal Revenue: PHP " + totalRevenue
                + "\n\n===== SESSION DETAILS =====\n\n" + sessionDetails.toString()
                + "\n\n===== RESERVATION DETAILS =====\n\n" + reservationDetails.toString()
                + "\nSystem User: " + currentUser.getUsername()
                + " (" + currentUser.getRole() + ")"
        );
    }

    private void generateSalesReport() {

        StringBuilder salesDetails = new StringBuilder();

        double foodRevenue = 0;

        for (Order order : orders) {

            String productName = "";

            for (Product product : products) {

                if (product.getProductId()
                        .equals(order.getProductId())) {

                    productName = product.getProductName();

                    break;
                }
            }

            foodRevenue += order.getTotal();

            salesDetails.append(
                    "Order ID: ")
                    .append(order.getOrderId())
                    .append("\nCustomer: ")
                    .append(order.getCustomerId())
                    .append("\nProduct: ")
                    .append(productName)
                    .append("\nQuantity: ")
                    .append(order.getQuantity())
                    .append("\nTotal: PHP ")
                    .append(order.getTotal())
                    .append("\n--------------------------------\n");
        }

        txtReport.setText(
                "===== SALES REPORT =====\n\n"
                + salesDetails.toString()
                + "\nTotal Orders: " + orders.size()
                + "\n\nFood Revenue: PHP " + foodRevenue
                + "\n\nTop Selling Product: " + getTopSellingProduct()
        );
    }

    private String getTopSellingProduct() {

        if (orders.isEmpty()) {

            return "No Orders Yet";
        }

        String topProduct = "";

        int highestQuantity = 0;

        for (Product product : products) {

            int totalQuantity = 0;

            for (Order order : orders) {

                if (order.getProductId().equals(product.getProductId())) {

                    totalQuantity += order.getQuantity();
                }
            }

            if (totalQuantity > highestQuantity) {

                highestQuantity = totalQuantity;

                topProduct = product.getProductName();
            }
        }

        return topProduct;
    }

    private void saveReport() {

        try {

            java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter("report.txt"));

            writer.write(txtReport.getText());

            writer.close();

            JOptionPane.showMessageDialog(this, "Report Saved");

        } catch (Exception e) {

            JOptionPane.showMessageDialog(this, "Error Saving Report");
        }
    }

    public void displayEntity(Entity entity) {

        System.out.println(entity.getDetails());
    }

    private String generateCustomerId() {

        int max = 0;

        for (Customer customer : customers) {

            String id = customer.getCustomerId();

            int number = Integer.parseInt(id.substring(1));

            if (number > max) {

                max = number;
            }
        }

        return String.format("C%03d", max + 1);
    }

    private String generatePCId() {

        int max = 0;

        for (PC pc : pcs) {

            String id = pc.getPcId();

            int number = Integer.parseInt(id.substring(2));

            if (number > max) {

                max = number;
            }
        }

        return String.format("PC%03d", max + 1);
    }

    private String generateReservationId() {

        int max = 0;

        for (Reservation reservation : reservations) {

            String id = reservation.getReservationId();

            int number = Integer.parseInt(id.substring(1));

            if (number > max) {

                max = number;
            }
        }

        return String.format("R%03d", max + 1);
    }

    private String generateSessionId() {

        int max = 0;

        for (Session session : sessions) {

            String id = session.getSessionId();

            int number = Integer.parseInt(id.substring(1));

            if (number > max) {

                max = number;
            }
        }

        return String.format("S%03d", max + 1);
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new GamingCafeSystem()
        );

    }
}
