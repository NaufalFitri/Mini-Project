package GUIs;

import Data.Database;
import Entities.Doctor;
import Entities.Owner;
import Entities.Patient;
import Rooms.DiagnoseRoom;
import Rooms.TreatmentRoom;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GUI extends JFrame {

    private Database database;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JLabel clockLabel;
    public Timer refreshTimer;
    public Timer clockTimer;
    private String currentPanel = "dashboard";

    // Store table models for auto-refresh
    private DefaultTableModel diagnoseRoomModel;
    private DefaultTableModel treatmentRoomModel;
    private DefaultTableModel patientModel;
    private DefaultTableModel doctorModel;
    private DefaultTableModel ownerModel;

    // Color scheme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private final Color DANGER_COLOR = new Color(231, 76, 60);
    private final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private final Color CARD_COLOR = Color.WHITE;

    public GUI(Database database) {
        setTitle("AnimalsCare - Clinic Management System");
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Initialize database
        try {
            this.database = database;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Database connection failed: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        initializeUI();
        startTimers();
    }

    private void initializeUI() {
        // Main container
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(BACKGROUND_COLOR);

        // Top bar with clock
        JPanel topBar = createTopBar();
        container.add(topBar, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = createSidebar();
        container.add(sidebar, BorderLayout.WEST);

        // Main content area with CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Add panels
        mainPanel.add(createDashboardPanel(), "dashboard");
        mainPanel.add(createDiagnoseRoomPanel(), "diagnose");
        mainPanel.add(createTreatmentRoomPanel(), "treatment");
        mainPanel.add(createPatientsPanel(), "patients");
        mainPanel.add(createDoctorsPanel(), "doctors");
        mainPanel.add(createOwnersPanel(), "owners");

        container.add(mainPanel, BorderLayout.CENTER);
        add(container);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(44, 62, 80));
        topBar.setPreferredSize(new Dimension(0, 60));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Left side - System name
        JLabel systemLabel = new JLabel("AnimalsCare Management System");
        systemLabel.setFont(new Font("Arial", Font.BOLD, 18));
        systemLabel.setForeground(Color.WHITE);

        // Right side - Clock and auto-refresh indicator
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(new Color(44, 62, 80));

        // Auto-refresh indicator
        JLabel refreshLabel = new JLabel("● Auto-refresh: 5s");
        refreshLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        refreshLabel.setForeground(new Color(46, 204, 113));

        // Clock
        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Arial", Font.BOLD, 16));
        clockLabel.setForeground(Color.WHITE);
        updateClock();

        rightPanel.add(refreshLabel);
        rightPanel.add(clockLabel);

        topBar.add(systemLabel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);

        return topBar;
    }

    private void updateClock() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy  HH:mm:ss");
        clockLabel.setText(now.format(formatter));
    }

    private void startTimers() {
        // Clock timer - updates every second
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();

        // Data refresh timer - updates every 5 seconds
        refreshTimer = new Timer(5000, e -> refreshAllData());
        refreshTimer.start();
    }

    private void refreshAllData() {
        // Only refresh if models are initialized
        if (diagnoseRoomModel != null) {
            updateDiagnoseRoomTable(diagnoseRoomModel);
        }
        if (treatmentRoomModel != null) {
            updateTreatmentRoomTable(treatmentRoomModel);
        }
        if (patientModel != null) {
            updatePatientTable(patientModel);
        }
        if (doctorModel != null) {
            updateDoctorTable(doctorModel);
        }
        if (ownerModel != null) {
            updateOwnerTable(ownerModel);
        }
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setPreferredSize(new Dimension(250, 0));

        // Logo/Title
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(44, 62, 80));
        titlePanel.setMaximumSize(new Dimension(250, 80));
        JLabel titleLabel = new JLabel("AnimalsCare");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        sidebar.add(titlePanel);

        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        // Menu buttons
        addMenuButton(sidebar, "Dashboard", "dashboard");
        addMenuButton(sidebar, "Diagnose Rooms", "diagnose");
        addMenuButton(sidebar, "Treatment Rooms", "treatment");
        addMenuButton(sidebar, "Patients", "patients");
        addMenuButton(sidebar, "Doctors", "doctors");
        addMenuButton(sidebar, "Owners", "owners");

        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private void addMenuButton(JPanel sidebar, String text, String panelName) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(220, 45));
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(52, 73, 94));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            cardLayout.show(mainPanel, panelName);
            currentPanel = panelName;
        });

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(SECONDARY_COLOR);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(52, 73, 94));
            }
        });

        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel header = new JLabel("Dashboard");
        header.setFont(new Font("Arial", Font.BOLD, 28));
        header.setForeground(new Color(44, 62, 80));
        panel.add(header, BorderLayout.NORTH);

        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        statsPanel.setBackground(BACKGROUND_COLOR);

        statsPanel.add(createStatCard("Total Patients",
                String.valueOf(database.getPatientList().size()), PRIMARY_COLOR));
        statsPanel.add(createStatCard("Total Doctors",
                String.valueOf(database.getDoctorList().size()), SUCCESS_COLOR));
        statsPanel.add(createStatCard("Total Owners",
                String.valueOf(database.getOwnersList().size()), SECONDARY_COLOR));

        long occupiedDiagnose = database.getDiagnoseRooms().stream()
                .filter(DiagnoseRoom::isOccupied).count();
        statsPanel.add(createStatCard("Diagnose Rooms",
                occupiedDiagnose + " / " + database.getDiagnoseRooms().size(),
                new Color(155, 89, 182)));

        long occupiedTreatment = database.getTreatmentRooms().stream()
                .filter(TreatmentRoom::isOccupied).count();
        statsPanel.add(createStatCard("Treatment Rooms",
                occupiedTreatment + " / " + database.getTreatmentRooms().size(),
                new Color(230, 126, 34)));

        statsPanel.add(createStatCard("Available Rooms",
                String.valueOf(database.getDiagnoseRooms().size() +
                        database.getTreatmentRooms().size() -
                        occupiedDiagnose - occupiedTreatment),
                new Color(26, 188, 156)));

        panel.add(statsPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 3),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        titleLabel.setForeground(new Color(127, 140, 141));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(color);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createDiagnoseRoomPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel header = new JLabel("Diagnose Rooms");
        header.setFont(new Font("Arial", Font.BOLD, 28));
        panel.add(header, BorderLayout.NORTH);

        // Table
        String[] columns = {"Room ID", "Room Number", "Status", "Patient", "Doctor", "Actions"};
        diagnoseRoomModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only actions column
            }
        };

        JTable table = new JTable(diagnoseRoomModel);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.BLACK);

        // Populate table
        updateDiagnoseRoomTable(diagnoseRoomModel);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton startBtn = createStyledButton("Start Diagnosis", SUCCESS_COLOR);
        JButton recordBtn = createStyledButton("Record Notes", PRIMARY_COLOR);
        JButton finishBtn = createStyledButton("Finish Diagnosis", DANGER_COLOR);
        JButton refreshBtn = createStyledButton("Refresh", SECONDARY_COLOR);

        startBtn.addActionListener(e -> startDiagnosis(diagnoseRoomModel));
        recordBtn.addActionListener(e -> recordDiagnosis(table));
        finishBtn.addActionListener(e -> finishDiagnosis(table, diagnoseRoomModel));
        refreshBtn.addActionListener(e -> updateDiagnoseRoomTable(diagnoseRoomModel));

        buttonPanel.add(startBtn);
        buttonPanel.add(recordBtn);
        buttonPanel.add(finishBtn);
        buttonPanel.add(refreshBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createTreatmentRoomPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Treatment Rooms");
        header.setFont(new Font("Arial", Font.BOLD, 28));
        panel.add(header, BorderLayout.NORTH);

        String[] columns = {"Room ID", "Room Number", "Status", "Patient", "Doctor", "Start Time"};
        treatmentRoomModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(treatmentRoomModel);
        table.setRowHeight(40);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.BLACK);

        updateTreatmentRoomTable(treatmentRoomModel);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton startBtn = createStyledButton("Start Treatment", SUCCESS_COLOR);
        JButton finishBtn = createStyledButton("Finish Treatment", DANGER_COLOR);
        JButton refreshBtn = createStyledButton("Refresh", SECONDARY_COLOR);

        startBtn.addActionListener(e -> startTreatment(treatmentRoomModel));
        finishBtn.addActionListener(e -> finishTreatment(table, treatmentRoomModel));
        refreshBtn.addActionListener(e -> updateTreatmentRoomTable(treatmentRoomModel));

        buttonPanel.add(startBtn);
        buttonPanel.add(finishBtn);
        buttonPanel.add(refreshBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createPatientsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Patients");
        header.setFont(new Font("Arial", Font.BOLD, 28));
        panel.add(header, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Species", "Breed", "Age", "Gender", "Owner"};
        patientModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(patientModel);
        table.setRowHeight(35);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.BLACK);

        updatePatientTable(patientModel);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton addBtn = createStyledButton("Add Patient", SUCCESS_COLOR);
        JButton editBtn = createStyledButton("Edit Patient", PRIMARY_COLOR);
        JButton viewBtn = createStyledButton("View Details", SECONDARY_COLOR);
        JButton deleteBtn = createStyledButton("Delete Patient", DANGER_COLOR);
        JButton refreshBtn = createStyledButton("Refresh", new Color(149, 165, 166));

        addBtn.addActionListener(e -> addPatient(patientModel));
        editBtn.addActionListener(e -> editPatient(table, patientModel));
        viewBtn.addActionListener(e -> viewPatientDetails(table));
        deleteBtn.addActionListener(e -> deletePatient(table, patientModel));
        refreshBtn.addActionListener(e -> updatePatientTable(patientModel));

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(viewBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createDoctorsPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Doctors");
        header.setFont(new Font("Arial", Font.BOLD, 28));
        panel.add(header, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Specialization", "Phone"};
        doctorModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(doctorModel);
        table.setRowHeight(35);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.BLACK);

        updateDoctorTable(doctorModel);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton addBtn = createStyledButton("Add Doctor", SUCCESS_COLOR);
        JButton editBtn = createStyledButton("Edit Doctor", PRIMARY_COLOR);
        JButton deleteBtn = createStyledButton("Delete Doctor", DANGER_COLOR);
        JButton refreshBtn = createStyledButton("Refresh", SECONDARY_COLOR);

        addBtn.addActionListener(e -> addDoctor(doctorModel));
        editBtn.addActionListener(e -> editDoctor(table, doctorModel));
        deleteBtn.addActionListener(e -> deleteDoctor(table, doctorModel));
        refreshBtn.addActionListener(e -> updateDoctorTable(doctorModel));

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createOwnersPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Owners");
        header.setFont(new Font("Arial", Font.BOLD, 28));
        panel.add(header, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Phone", "Address"};
        ownerModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(ownerModel);
        table.setRowHeight(35);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(Color.BLACK);

        updateOwnerTable(ownerModel);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton addBtn = createStyledButton("Add Owner", SUCCESS_COLOR);
        JButton editBtn = createStyledButton("Edit Owner", PRIMARY_COLOR);
        JButton deleteBtn = createStyledButton("Delete Owner", DANGER_COLOR);
        JButton refreshBtn = createStyledButton("Refresh", SECONDARY_COLOR);

        addBtn.addActionListener(e -> addOwner(ownerModel));
        editBtn.addActionListener(e -> editOwner(table, ownerModel));
        deleteBtn.addActionListener(e -> deleteOwner(table, ownerModel));
        refreshBtn.addActionListener(e -> updateOwnerTable(ownerModel));

        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(refreshBtn);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Update methods
    private void updateDiagnoseRoomTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (DiagnoseRoom room : database.getDiagnoseRooms()) {
            model.addRow(new Object[]{
                    room.getRoomID(),
                    room.getRoomNumber(),
                    room.isOccupied() ? "Occupied" : "Available",
                    room.getCurrentPatient() != null ? room.getCurrentPatient().getName() : "-",
                    room.getAssignedDoctor() != null ? room.getAssignedDoctor().getName() : "-",
                    "View"
            });
        }
    }

    private void updateTreatmentRoomTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (TreatmentRoom room : database.getTreatmentRooms()) {
            String startTime = "-";
            if (room.getTreatmentStart() != null) {
                startTime = room.getTreatmentStart()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            }
            model.addRow(new Object[]{
                    room.getRoomID(),
                    room.getRoomNumber(),
                    room.isOccupied() ? "Occupied" : "Available",
                    room.getCurrentPatient() != null ? room.getCurrentPatient().getName() : "-",
                    room.getAssignedDoctor() != null ? room.getAssignedDoctor().getName() : "-",
                    startTime
            });
        }
    }

    private void updatePatientTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Patient patient : database.getPatientList()) {
            model.addRow(new Object[]{
                    patient.getId(),
                    patient.getName(),
                    patient.getSpecies(),
                    patient.getBreed(),
                    patient.getAge(),
                    patient.getGender().name(),
                    patient.getOwner() != null ? patient.getOwner().getName() : "N/A"
            });
        }
    }

    private void updateDoctorTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Doctor doctor : database.getDoctorList()) {
            model.addRow(new Object[]{
                    doctor.getId(),
                    doctor.getName(),
                    doctor.getField().name(),
                    doctor.getPhone()
            });
        }
    }

    private void updateOwnerTable(DefaultTableModel model) {
        model.setRowCount(0);
        for (Owner owner : database.getOwnersList()) {
            model.addRow(new Object[]{
                    owner.getId(),
                    owner.getName(),
                    owner.getPhone(),
                    owner.getAddress()
            });
        }
    }

    // Action methods
    private void startDiagnosis(DefaultTableModel model) {
        JComboBox<String> roomCombo = new JComboBox<>();
        for (DiagnoseRoom room : database.getDiagnoseRooms()) {
            if (!room.isOccupied()) {
                roomCombo.addItem(room.getRoomNumber());
            }
        }

        JComboBox<String> patientCombo = new JComboBox<>();
        for (Patient patient : database.getPatientList()) {
            patientCombo.addItem(patient.getName() + " (ID: " + patient.getId() + ")");
        }

        JComboBox<String> doctorCombo = new JComboBox<>();
        for (Doctor doctor : database.getDoctorList()) {
            doctorCombo.addItem(doctor.getName() + " - " + doctor.getField().name());
        }

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Room:"));
        panel.add(roomCombo);
        panel.add(new JLabel("Patient:"));
        panel.add(patientCombo);
        panel.add(new JLabel("Doctor:"));
        panel.add(doctorCombo);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Start Diagnosis", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION && roomCombo.getSelectedItem() != null) {
            String selectedRoom = (String) roomCombo.getSelectedItem();
            int patientIndex = patientCombo.getSelectedIndex();
            int doctorIndex = doctorCombo.getSelectedIndex();

            DiagnoseRoom room = database.getDiagnoseRooms().stream()
                    .filter(r -> r.getRoomNumber().equals(selectedRoom))
                    .findFirst().orElse(null);

            if (room != null) {
                Patient patient = database.getPatientList().get(patientIndex);
                Doctor doctor = database.getDoctorList().get(doctorIndex);
                room.startDiagnosis(patient, doctor);
                updateDiagnoseRoomTable(model);
                JOptionPane.showMessageDialog(this, "Diagnosis started successfully!");
            }
        }
    }

    private void recordDiagnosis(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room first!");
            return;
        }

        int roomId = (int) table.getValueAt(selectedRow, 0);
        DiagnoseRoom room = database.getDiagnoseRooms().stream()
                .filter(r -> r.getRoomID() == roomId)
                .findFirst().orElse(null);

        if (room != null && room.isOccupied()) {
            String notes = JOptionPane.showInputDialog(this,
                    "Enter diagnosis notes:",
                    "Record Diagnosis",
                    JOptionPane.PLAIN_MESSAGE);

            if (notes != null && !notes.trim().isEmpty()) {
                room.recordDiagnosis(notes);
                JOptionPane.showMessageDialog(this, "Notes recorded successfully!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Room is not occupied!");
        }
    }

    private void finishDiagnosis(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room first!");
            return;
        }

        int roomId = (int) table.getValueAt(selectedRow, 0);
        DiagnoseRoom room = database.getDiagnoseRooms().stream()
                .filter(r -> r.getRoomID() == roomId)
                .findFirst().orElse(null);

        if (room != null && room.isOccupied()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Finish diagnosis for " + room.getCurrentPatient().getName() + "?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                room.finishDiagnosis();
                updateDiagnoseRoomTable(model);
                JOptionPane.showMessageDialog(this, "Diagnosis finished successfully!");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Room is not occupied!");
        }
    }

    private void startTreatment(DefaultTableModel model) {
        JComboBox<String> roomCombo = new JComboBox<>();
        for (TreatmentRoom room : database.getTreatmentRooms()) {
            if (!room.isOccupied()) {
                roomCombo.addItem(room.getRoomNumber());
            }
        }

        JComboBox<String> patientCombo = new JComboBox<>();
        for (Patient patient : database.getPatientList()) {
            if (patient.getDiagnosis() != null) {
                patientCombo.addItem(patient.getName() + " (ID: " + patient.getId() + ")");
            }
        }

        JComboBox<String> doctorCombo = new JComboBox<>();
        for (Doctor doctor : database.getDoctorList()) {
            doctorCombo.addItem(doctor.getName() + " - " + doctor.getField().name());
        }

        JTextField medicationsField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.add(new JLabel("Room:"));
        panel.add(roomCombo);
        panel.add(new JLabel("Patient:"));
        panel.add(patientCombo);
        panel.add(new JLabel("Doctor:"));
        panel.add(doctorCombo);
        panel.add(new JLabel("Medications (comma-separated):"));
        panel.add(medicationsField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Start Treatment", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION && roomCombo.getSelectedItem() != null) {
            String selectedRoom = (String) roomCombo.getSelectedItem();
            int patientIndex = patientCombo.getSelectedIndex();
            int doctorIndex = doctorCombo.getSelectedIndex();

            TreatmentRoom room = database.getTreatmentRooms().stream()
                    .filter(r -> r.getRoomNumber().equals(selectedRoom))
                    .findFirst().orElse(null);

            if (room != null) {
                Patient patient = database.getPatientList().stream()
                        .filter(p -> p.getDiagnosis() != null)
                        .toArray(Patient[]::new)[patientIndex];
                Doctor doctor = database.getDoctorList().get(doctorIndex);

                List<String> medications = new ArrayList<>();
                String[] meds = medicationsField.getText().split(",");
                for (String med : meds) {
                    medications.add(med.trim());
                }

                room.startTreatment(patient, doctor, medications);
                updateTreatmentRoomTable(model);
                JOptionPane.showMessageDialog(this, "Treatment started successfully!");
            }
        }
    }

    private void finishTreatment(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a room first!");
            return;
        }

        int roomId = (int) table.getValueAt(selectedRow, 0);
        TreatmentRoom room = database.getTreatmentRooms().stream()
                .filter(r -> r.getRoomID() == roomId)
                .findFirst().orElse(null);

        if (room != null && room.isOccupied()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Finish treatment for " + room.getCurrentPatient().getName() + "?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                room.finishTreatment();

                try {
                    Statement stmt = database.getConnection().createStatement();
                    database.addTreatment(stmt, room);
                    JOptionPane.showMessageDialog(this, "Treatment finished and saved!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error saving treatment: " + ex.getMessage());
                }

                updateTreatmentRoomTable(model);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Room is not occupied!");
        }
    }

    private void addPatient(DefaultTableModel model) {
        JTextField nameField = new JTextField();
        JTextField speciesField = new JTextField();
        JTextField breedField = new JTextField();
        JTextField ageField = new JTextField();
        JComboBox<String> genderCombo = new JComboBox<>(
                new String[]{"Male", "Female"});

        JComboBox<String> ownerCombo = new JComboBox<>();
        for (Owner o : database.getOwnersList()) {
            if (o != null) {
                String ownerInfo = o.getName() + " (ID: " +
                        o.getId() + ")";
                if (ownerCombo.getItemCount() == 0 ||
                        !ownerCombo.getItemAt(ownerCombo.getItemCount()-1).equals(ownerInfo)) {
                    ownerCombo.addItem(ownerInfo);
                }
            }
        }

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Species:"));
        panel.add(speciesField);
        panel.add(new JLabel("Breed:"));
        panel.add(breedField);
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(new JLabel("Gender:"));
        panel.add(genderCombo);
        panel.add(new JLabel("Owner:"));
        panel.add(ownerCombo);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Add New Patient", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                String species = speciesField.getText();
                String breed = breedField.getText();
                int age = Integer.parseInt(ageField.getText());
                String gender = (String) genderCombo.getSelectedItem();

                String ownerStr = (String) ownerCombo.getSelectedItem();
                int ownerId = Integer.parseInt(ownerStr.substring(
                        ownerStr.indexOf("ID: ") + 4, ownerStr.length() -    1));

                Owner owner = database.getOwnersList().stream()
                        .filter(o -> o.getId() == ownerId)
                        .findFirst().orElse(null);

                if (owner != null) {
                    Statement stmt = database.getConnection().createStatement();
                    String msg = database.insertPatient(stmt, name, species,
                            breed, age, gender, owner);
                    updatePatientTable(model);
                    JOptionPane.showMessageDialog(this, msg);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error adding patient: " + ex.getMessage());
            }
        }
    }

    private void viewPatientDetails(JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient first!");
            return;
        }

        int patientId = (int) table.getValueAt(selectedRow, 0);
        Patient patient = database.getPatientList().stream()
                .filter(p -> p.getId() == patientId)
                .findFirst().orElse(null);

        if (patient != null) {
            StringBuilder details = new StringBuilder();
            details.append("Patient Details\n\n");
            details.append("ID: ").append(patient.getId()).append("\n");
            details.append("Name: ").append(patient.getName()).append("\n");
            details.append("Species: ").append(patient.getSpecies()).append("\n");
            details.append("Breed: ").append(patient.getBreed()).append("\n");
            details.append("Age: ").append(patient.getAge()).append("\n");
            details.append("Gender: ").append(patient.getGender().name()).append("\n");

            if (patient.getOwner() != null) {
                details.append("\nOwner Information:\n");
                details.append("Name: ").append(patient.getOwner().getName()).append("\n");
                details.append("Phone: ").append(patient.getOwner().getPhone()).append("\n");
                details.append("Address: ").append(patient.getOwner().getAddress()).append("\n");
            }

            if (patient.getDiagnosis() != null) {
                details.append("\nDiagnosis: ").append(patient.getDiagnosis()).append("\n");
            }

            JTextArea textArea = new JTextArea(details.toString());
            textArea.setEditable(false);
            textArea.setFont(new Font("Arial", Font.PLAIN, 14));
            JOptionPane.showMessageDialog(this, new JScrollPane(textArea),
                    "Patient Details", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void addDoctor(DefaultTableModel model) {
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JComboBox<Doctor.field> fieldCombo = new JComboBox<>(Doctor.field.values());

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Specialization:"));
        panel.add(fieldCombo);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Add New Doctor", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                String phone = phoneField.getText();
                Doctor.field specialization = (Doctor.field) fieldCombo.getSelectedItem();

                Statement stmt = database.getConnection().createStatement();
                String msg = database.insertDoctor(stmt, name, specialization, phone);
                updateDoctorTable(model);
                JOptionPane.showMessageDialog(this, msg);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error adding doctor: " + ex.getMessage());
            }
        }
    }

    private void editDoctor(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor first!");
            return;
        }

        int doctorId = (int) table.getValueAt(selectedRow, 0);
        Doctor doctor = database.getDoctorList().stream()
                .filter(d -> d.getId() == doctorId)
                .findFirst().orElse(null);

        if (doctor != null) {
            JTextField nameField = new JTextField(doctor.getName());
            JTextField phoneField = new JTextField(doctor.getPhone());
            JComboBox<Doctor.field> fieldCombo = new JComboBox<>(Doctor.field.values());
            fieldCombo.setSelectedItem(doctor.getField());

            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Phone:"));
            panel.add(phoneField);
            panel.add(new JLabel("Specialization:"));
            panel.add(fieldCombo);

            int result = JOptionPane.showConfirmDialog(this, panel,
                    "Edit Doctor", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    Doctor updatedDoctor = new Doctor(
                            doctor.getId(),
                            nameField.getText(),
                            ((Doctor.field) fieldCombo.getSelectedItem()).name(),
                            phoneField.getText()
                    );

                    Statement stmt = database.getConnection().createStatement();
                    String msg = database.updateDoctor(stmt, updatedDoctor);
                    updateDoctorTable(model);
                    JOptionPane.showMessageDialog(this, msg);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error updating doctor: " + ex.getMessage());
                }
            }
        }
    }

    private void deleteDoctor(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a doctor first!");
            return;
        }

        int doctorId = (int) table.getValueAt(selectedRow, 0);
        Doctor doctor = database.getDoctorList().stream()
                .filter(d -> d.getId() == doctorId)
                .findFirst().orElse(null);

        if (doctor != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete Dr. " + doctor.getName() + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Statement stmt = database.getConnection().createStatement();
                    String msg = database.deleteDoctor(stmt, doctor);
                    updateDoctorTable(model);
                    JOptionPane.showMessageDialog(this, msg);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error deleting doctor: " + ex.getMessage());
                }
            }
        }
    }

    private void editPatient(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient first!");
            return;
        }

        int patientId = (int) table.getValueAt(selectedRow, 0);
        Patient patient = database.getPatientList().stream()
                .filter(p -> p.getId() == patientId)
                .findFirst().orElse(null);

        if (patient != null) {
            JTextField nameField = new JTextField(patient.getName());
            nameField.setEditable(false); // Name is final
            JTextField speciesField = new JTextField(patient.getSpecies());
            speciesField.setEditable(false); // Species is final
            JTextField breedField = new JTextField(patient.getBreed());
            breedField.setEditable(false); // Breed is final
            JTextField ageField = new JTextField(String.valueOf(patient.getAge()));
            ageField.setEditable(false); // Age is final

            JComboBox<String> genderCombo = new JComboBox<>(new String[]{"Male", "Female"});
            genderCombo.setSelectedItem(patient.getGender().name());
            genderCombo.setEnabled(false); // Gender is final

            JComboBox<String> ownerCombo = new JComboBox<>();
            for (Patient p : database.getPatientList()) {
                if (p.getOwner() != null) {
                    String ownerInfo = p.getOwner().getName() + " (ID: " + p.getOwner().getId() + ")";
                    boolean exists = false;
                    for (int i = 0; i < ownerCombo.getItemCount(); i++) {
                        if (ownerCombo.getItemAt(i).equals(ownerInfo)) {
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) ownerCombo.addItem(ownerInfo);
                }
            }

            if (patient.getOwner() != null) {
                ownerCombo.setSelectedItem(patient.getOwner().getName() + " (ID: " + patient.getOwner().getId() + ")");
            }

            JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
            panel.add(new JLabel("Name (readonly):"));
            panel.add(nameField);
            panel.add(new JLabel("Species (readonly):"));
            panel.add(speciesField);
            panel.add(new JLabel("Breed (readonly):"));
            panel.add(breedField);
            panel.add(new JLabel("Age (readonly):"));
            panel.add(ageField);
            panel.add(new JLabel("Gender (readonly):"));
            panel.add(genderCombo);
            panel.add(new JLabel("Owner (editable):"));
            panel.add(ownerCombo);
            panel.add(new JLabel("Note: Only owner can be changed"));
            panel.add(new JLabel(""));

            int result = JOptionPane.showConfirmDialog(this, panel,
                    "Edit Patient", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    String ownerStr = (String) ownerCombo.getSelectedItem();
                    int ownerId = Integer.parseInt(ownerStr.substring(
                            ownerStr.indexOf("ID: ") + 4, ownerStr.length() - 1));

                    Owner newOwner = database.getPatientList().stream()
                            .map(Patient::getOwner)
                            .filter(o -> o != null && o.getId() == ownerId)
                            .findFirst().orElse(null);

                    if (newOwner != null) {
                        patient.setOwner(newOwner);
                        Statement stmt = database.getConnection().createStatement();
                        String msg = database.updatePatient(stmt, patient);
                        updatePatientTable(model);
                        JOptionPane.showMessageDialog(this, msg);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error updating patient: " + ex.getMessage());
                }
            }
        }
    }

    private void deletePatient(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient first!");
            return;
        }

        int patientId = (int) table.getValueAt(selectedRow, 0);
        Patient patient = database.getPatientList().stream()
                .filter(p -> p.getId() == patientId)
                .findFirst().orElse(null);

        if (patient != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete patient: " + patient.getName() + "?\n" +
                            "This action cannot be undone!",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Statement stmt = database.getConnection().createStatement();
                    String msg = database.deletePatient(stmt, patient);
                    updatePatientTable(model);
                    JOptionPane.showMessageDialog(this, msg);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error deleting patient: " + ex.getMessage());
                }
            }
        }
    }

    private void editOwner(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an owner first!");
            return;
        }

        int ownerId = (int) table.getValueAt(selectedRow, 0);
        Owner owner = database.getPatientList().stream()
                .map(Patient::getOwner)
                .filter(o -> o != null && o.getId() == ownerId)
                .findFirst().orElse(null);

        if (owner != null) {

            JTextField nameField = new JTextField(owner.getName());
            JTextField phoneField = new JTextField(owner.getPhone());
            JTextField addressField = new JTextField(owner.getAddress());

            JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
            panel.add(new JLabel("Name:"));
            panel.add(nameField);
            panel.add(new JLabel("Phone:"));
            panel.add(phoneField);
            panel.add(new JLabel("Address:"));
            panel.add(addressField);

            int result = JOptionPane.showConfirmDialog(this, panel,
                    "Edit Owner Information", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    owner.setName(nameField.getText());
                    owner.setPhone(phoneField.getText());
                    owner.setAddress(addressField.getText());

                    Statement stmt = database.getConnection().createStatement();
                    String msg = database.updateOwner(stmt, owner);
                    updateOwnerTable(model);
                    JOptionPane.showMessageDialog(this, msg);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error editing owner: " + ex.getMessage());
                }

                JOptionPane.showMessageDialog(this, "Owner updated successfully!");
            }
        }
    }

    private void deleteOwner(JTable table, DefaultTableModel model) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an owner first!");
            return;
        }

        int ownerId = (int) table.getValueAt(selectedRow, 0);
        Owner owner = database.getOwnersList().stream()
                .filter(o -> o.getId() == ownerId)
                .findFirst().orElse(null);

        if (owner != null) {
            // Check if owner has patients
            long patientCount = database.getPatientList().stream()
                    .filter(p -> p.getOwner() != null && p.getOwner().getId() == ownerId)
                    .count();

            if (patientCount > 0) {
                JOptionPane.showMessageDialog(this,
                        "Cannot delete owner: " + owner.getName() + "\n" +
                                "This owner has " + patientCount + " patient(s) assigned.\n" +
                                "Please delete or reassign all patients first.",
                        "Delete Failed",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete owner: " + owner.getName() + "?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    Statement stmt = database.getConnection().createStatement();
                    String msg = database.deleteOwner(stmt, owner);
                    updateOwnerTable(model);
                    JOptionPane.showMessageDialog(this, msg);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error deleting owner: " + ex.getMessage());
                }
            }
        }
    }

    private void addOwner(DefaultTableModel model) {
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField addressField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);

        int result = JOptionPane.showConfirmDialog(this, panel,
                "Add New Owner", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                String phone = phoneField.getText();
                String address = addressField.getText();

                Statement stmt = database.getConnection().createStatement();
                String msg = database.insertOwner(stmt, name, phone, address);
                updateOwnerTable(model);
                JOptionPane.showMessageDialog(this, msg);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error adding owner: " + ex.getMessage());
            }
        }
    }
}
