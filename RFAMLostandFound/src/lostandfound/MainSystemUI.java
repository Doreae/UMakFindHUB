package lostandfound;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.io.InputStream;

public class MainSystemUI extends JFrame {

    // --- DATABASE CREDENTIALS ---
    private static final String DB_URL = "jdbc:mysql://localhost:3306/umak_lostfound_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = ""; 

    // --- CUSTOM BRANDING FONTS ---
    public static Font marcellusHeader;
    public static Font metropolisBody;
    public static Font metropolisBold;

    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    
    // Form Inputs
    private JTextField txtClaimItemID = new JTextField(20);
    private JTextField txtClaimItemName = new JTextField(20);
    private JTextField txtClaimSenderName = new JTextField(20); // NEW: Sender Name for Claim processing
    private JTextField txtClaimSenderID = new JTextField(20);   // NEW: Sender ID for Claim processing
    private JComboBox<String> cbCategoryFilter;
    private JComboBox<String> cbStatus;

    // Date Filter Inputs
    private JTextField txtDateFrom = new JTextField(10);
    private JTextField txtDateTo = new JTextField(10);
    private JTextField txtSearch;
    
    // Dynamic UI Elements
    private String currentUserRole; 
    private DefaultTableModel tableModel;
    private JLabel lblTotalFoundVal;
    private JLabel lblTotalClaimedVal;
    private JLabel lblTotalUnclaimedVal;
    private DefaultTableModel activityTableModel; 

    public MainSystemUI(String role) {
        this.currentUserRole = role;       
        
        setTitle("UMak Lost & Found Inventory System - Logged in as " + role);
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());	

        // ==========================================
        // GLOBAL HEADER (Centered & Visible on all screens)
        // ==========================================
        JPanel globalHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        globalHeader.setBackground(new Color(176, 205, 235)); 

        try {
            ImageIcon logoIcon = new ImageIcon(new ImageIcon("src/lostandfound/images/umak_logo.png").getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH));
            globalHeader.add(new JLabel(logoIcon));
        } catch (Exception e) {
            System.out.println("Logo not found at src/lostandfound/images/logo.png");
        }

        JLabel lblHeaderText = new JLabel("<html><div style='text-align: center; color: #1a1a1a;'>"
                + "<span style='font-size: 26px; font-family: Marcellus;'>University Of Makati</span><br>"
                + "<span style='font-size: 14px; font-family: Metropolis;'>Lost & Found Inventory System</span>"
                + "</div></html>");
        globalHeader.add(lblHeaderText);
        add(globalHeader, BorderLayout.NORTH);
        
        // --- 1. SIDEBAR NAVIGATION ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(245, 245, 245)); 
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        sidebar.setPreferredSize(new Dimension(220, 0)); 

        try {
            ImageIcon logoIcon = new ImageIcon(new ImageIcon("src/lostandfound/images/logo.png").getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
            JLabel lblSidebarLogo = new JLabel(logoIcon);
            lblSidebarLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(lblSidebarLogo);
        } catch (Exception e) {}

        sidebar.add(Box.createRigidArea(new Dimension(0, 10))); 

        JLabel lblMenu = new JLabel("MENU");
        lblMenu.setFont(metropolisBold.deriveFont(20f)); 
        lblMenu.setForeground(Color.DARK_GRAY);
        lblMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblMenu);
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 30))); 

        JButton btnDashboard = createSidebarButton("Dashboard", "📊", true); 
        JButton btnInventory = createSidebarButton("Inventory", "📦", false);
        JButton btnRegister = createSidebarButton("Add Found Item", "📝", false);
        JButton btnLogout = createSidebarButton("LOG OUT", "🚪", false);

        sidebar.add(btnDashboard);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15))); 
        sidebar.add(btnInventory);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(btnRegister);
        
        sidebar.add(Box.createVerticalGlue()); 
        sidebar.add(btnLogout);

        add(sidebar, BorderLayout.WEST);

        // --- 2. MAIN CONTENT AREA ---
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        mainContentPanel.add(createDashboardPanel(), "DASHBOARD");
        mainContentPanel.add(createInventoryPanel(), "INVENTORY");
        mainContentPanel.add(createRegisterPanel(), "REGISTER");
        mainContentPanel.add(createProcessClaimPanel(), "PROCESS_CLAIM");

        add(mainContentPanel, BorderLayout.CENTER);

        // --- 3. BUTTON NAVIGATION LOGIC ---
        btnDashboard.addActionListener(e -> {
            updateButtonColors(btnDashboard, btnInventory, btnRegister);
            refreshData(); 
            cardLayout.show(mainContentPanel, "DASHBOARD");
        });
        btnInventory.addActionListener(e -> {
            updateButtonColors(btnInventory, btnDashboard, btnRegister);
            refreshData(); 
            cardLayout.show(mainContentPanel, "INVENTORY");
        });
        btnRegister.addActionListener(e -> {
            updateButtonColors(btnRegister, btnDashboard, btnInventory);
            cardLayout.show(mainContentPanel, "REGISTER");
        });
        
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose(); 
                showLogin(); 
            }
        });

        refreshData();
    }

    // --- CENTRAL METHOD TO FETCH LATEST DB DATA ---
    private void refreshData() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            
            try (Statement stmt = conn.createStatement()) {
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM items");
                if (rs.next()) lblTotalFoundVal.setText(rs.getString(1));
                
                rs = stmt.executeQuery("SELECT COUNT(*) FROM items WHERE status = 'CLAIMED'");
                if (rs.next()) lblTotalClaimedVal.setText(rs.getString(1));
                
                rs = stmt.executeQuery("SELECT COUNT(*) FROM items WHERE status = 'UNCLAIMED'");
                if (rs.next()) lblTotalUnclaimedVal.setText(rs.getString(1));
            }
            
            if (activityTableModel != null) {
                activityTableModel.setRowCount(0); 
                try (Statement stmtAct = conn.createStatement();
                     ResultSet rsAct = stmtAct.executeQuery("SELECT date_found, item_name, category, status, location_found FROM items ORDER BY date_found DESC, item_id DESC LIMIT 10")) {
                    
                    while (rsAct.next()) {
                        String desc = rsAct.getString("category") + " - " + rsAct.getString("item_name");
                        activityTableModel.addRow(new Object[]{
                            rsAct.getDate("date_found"),
                            desc,
                            rsAct.getString("status"),
                            rsAct.getString("location_found")
                        });
                    }
                }
            }

            if (tableModel != null) {
                tableModel.setRowCount(0); 
                
                String keyword = (txtSearch != null) ? txtSearch.getText().trim() : "";
                String categoryFilter = (cbCategoryFilter != null) ? cbCategoryFilter.getSelectedItem().toString() : "All";
                String statusFilter = (cbStatus != null) ? cbStatus.getSelectedItem().toString() : "All";
                String dateFrom = (txtDateFrom != null) ? txtDateFrom.getText().trim() : "";
                String dateTo = (txtDateTo != null) ? txtDateTo.getText().trim() : "";

                StringBuilder sql = new StringBuilder("SELECT item_id, item_name, category, location_found, date_found, status FROM items WHERE 1=1");
                
                if (!keyword.isEmpty() && !keyword.equals("Search")) {
                    sql.append(" AND (item_name LIKE ? OR location_found LIKE ?)");
                }
                if (!categoryFilter.equals("All")) {
                    sql.append(" AND category = ?");
                }
                if (!statusFilter.equals("All")) {
                    sql.append(" AND status = ?");
                }
                if (!dateFrom.isEmpty()) {
                    sql.append(" AND date_found >= ?");
                }
                if (!dateTo.isEmpty()) {
                    sql.append(" AND date_found <= ?");
                }

                try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                    
                    int paramIndex = 1;
                    
                    if (!keyword.isEmpty() && !keyword.equals("Search")) {
                        String searchPattern = "%" + keyword + "%";
                        pstmt.setString(paramIndex++, searchPattern);
                        pstmt.setString(paramIndex++, searchPattern);
                    }
                    if (!categoryFilter.equals("All")) {
                        pstmt.setString(paramIndex++, categoryFilter);
                    }
                    if (!statusFilter.equals("All")) {
                        pstmt.setString(paramIndex++, statusFilter);
                    }
                    
                    try {
                        if (!dateFrom.isEmpty()) {
                            pstmt.setDate(paramIndex++, java.sql.Date.valueOf(dateFrom));
                        }
                        if (!dateTo.isEmpty()) {
                            pstmt.setDate(paramIndex++, java.sql.Date.valueOf(dateTo));
                        }
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid Date Format! Please use YYYY-MM-DD.", "Filter Error", JOptionPane.WARNING_MESSAGE);
                        return; 
                    }

                    try (ResultSet rs = pstmt.executeQuery()) {
                        while (rs.next()) {
                            String status = rs.getString("status");
                            String actionText = status.equals("UNCLAIMED") ? "[PROCESS CLAIM]" : "VIEW DETAILS";
                            
                            tableModel.addRow(new Object[]{
                                rs.getInt("item_id"),
                                rs.getString("item_name"),
                                rs.getString("category"),
                                rs.getString("location_found"),
                                rs.getDate("date_found"),
                                status,
                                actionText
                            });
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error during refresh: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- DASHBOARD PANEL ---
    private JPanel createDashboardPanel() {
        Image mainBgImage = new ImageIcon("src/lostandfound/images/bg.png").getImage();

        JPanel panel = new JPanel(new BorderLayout(0, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); 
                if (mainBgImage != null) {
                    g.drawImage(mainBgImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        panel.setBackground(new Color(133, 179, 235)); 
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel statsContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        statsContainer.setOpaque(false); 

        lblTotalFoundVal = new JLabel("0", SwingConstants.CENTER);
        lblTotalClaimedVal = new JLabel("0", SwingConstants.CENTER);
        lblTotalUnclaimedVal = new JLabel("0", SwingConstants.CENTER);

        statsContainer.add(createStatCard("Total Found Items", lblTotalFoundVal));
        statsContainer.add(createStatCard("Successfully Claimed", lblTotalClaimedVal));
        statsContainer.add(createStatCard("Unclaimed Items", lblTotalUnclaimedVal));

        panel.add(statsContainer, BorderLayout.NORTH);

        JPanel activityPanel = new JPanel(new BorderLayout(0, 10));
        activityPanel.setOpaque(false); 
        activityPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.WHITE)); 

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false); 

        JLabel lblActivityTitle = new JLabel("Recent Activity:");
        lblActivityTitle.setFont(marcellusHeader.deriveFont(24f)); 
        lblActivityTitle.setForeground(Color.WHITE); 
        lblActivityTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        titlePanel.add(lblActivityTitle, BorderLayout.WEST);

        JButton btnExport = new JButton("📥 Export to Excel");
        btnExport.setBackground(new Color(46, 204, 113)); 
        btnExport.setForeground(Color.BLACK);
        btnExport.setFont(metropolisBold);
        btnExport.setFocusPainted(false);
        btnExport.addActionListener(e -> exportToExcel()); 
        
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnWrapper.setOpaque(false);
        btnWrapper.add(btnExport);
        titlePanel.add(btnWrapper, BorderLayout.EAST);

        activityPanel.add(titlePanel, BorderLayout.NORTH);

     // ==========================================
     // 3. THE RECENT ACTIVITY TABLE (STYLED)
     // ==========================================
	     String[] columns = {"Date Logged", "Item Description", "Status", "Location"};
	     activityTableModel = new DefaultTableModel(null, columns) {
	         @Override
	         public boolean isCellEditable(int row, int column) { return false; }
	     };
	
	     JTable activityTable = new JTable(activityTableModel);
	     activityTable.setRowHeight(40);
	     activityTable.setFont(metropolisBody);
	     activityTable.getTableHeader().setFont(metropolisBold);
	
	     // --- THE FIXES ---
	     activityTable.setShowGrid(true);
	     activityTable.setGridColor(new Color(100, 150, 220)); // Blue grid lines
	     activityTable.setBackground(new Color(224, 247, 250)); // Light icy row color
	
	     // Match the header style to Inventory
	     activityTable.getTableHeader().setBackground(new Color(100, 150, 240));
	     activityTable.getTableHeader().setForeground(Color.BLACK);
	
	     JScrollPane scrollPane = new JScrollPane(activityTable);
	     // Set the background of the "box" area
	     scrollPane.getViewport().setBackground(new Color(133, 179, 235));
	     activityPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(activityPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, JLabel numberLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(200, 130)); 
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(metropolisBody.deriveFont(12f));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 5, 5, 5));

        numberLabel.setFont(metropolisBold.deriveFont(42f));
        numberLabel.setForeground(new Color(41, 128, 185)); 

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(numberLabel, BorderLayout.CENTER);
        
        return card;
    }

    // --- REGISTER FOUND ITEM PANEL (UPDATED UI TO MATCH MOCKUP) ---
    private JPanel createRegisterPanel() {
        // 1. Create the main background panel (Stretches the campus photo)
        Image mainBgImage = new ImageIcon("src/lostandfound/images/bg.png").getImage();
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); 
                if (mainBgImage != null) {
                    g.drawImage(mainBgImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        // Fallback color in case the image is missing
        mainPanel.setBackground(new Color(21, 35, 75)); 

        // 2. Create the light blue semi-transparent form box
        JPanel formBox = new JPanel(new GridBagLayout());
        formBox.setBackground(new Color(133, 179, 235, 230)); // 230 adds a slight transparency!
        formBox.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60)); // Thick padding inside the box
        formBox.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- TITLE ---
        JLabel lblTitle = new JLabel("Register New Found Item", SwingConstants.CENTER);
        lblTitle.setFont(marcellusHeader.deriveFont(42f)); // Made much larger to match image!
        lblTitle.setForeground(Color.BLACK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; 
        gbc.insets = new Insets(10, 10, 40, 10); // Extra space below title
        formBox.add(lblTitle, gbc);

        // Reset constraints for the form inputs
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);

        // --- FORM FIELDS ---
        JLabel lblItem = new JLabel("Item Name:");
        lblItem.setFont(metropolisBody);
        gbc.gridx = 0; gbc.gridy = 1; formBox.add(lblItem, gbc);
        JTextField txtItemName = new JTextField(20);
        txtItemName.setFont(metropolisBody);
        gbc.gridx = 1; gbc.gridy = 1; formBox.add(txtItemName, gbc);

        JLabel lblCat = new JLabel("Category:");
        lblCat.setFont(metropolisBody);
        gbc.gridx = 0; gbc.gridy = 2; formBox.add(lblCat, gbc);
        String[] categories = {"Electronics", "IDs/Documents", "Bags", "Clothing", "Valuables"};
        JComboBox<String> cbCategory = new JComboBox<>(categories);
        cbCategory.setFont(metropolisBody);
        cbCategory.setBackground(Color.WHITE); // Make dropdown white
        gbc.gridx = 1; gbc.gridy = 2; formBox.add(cbCategory, gbc);

        JLabel lblLoc = new JLabel("Location Found:");
        lblLoc.setFont(metropolisBody);
        gbc.gridx = 0; gbc.gridy = 3; formBox.add(lblLoc, gbc);
        JTextField txtLocation = new JTextField(20);
        txtLocation.setFont(metropolisBody);
        gbc.gridx = 1; gbc.gridy = 3; formBox.add(txtLocation, gbc);

        JLabel lblDate = new JLabel("Date Found (YYYY-MM-DD):");
        lblDate.setFont(metropolisBody);
        gbc.gridx = 0; gbc.gridy = 4; formBox.add(lblDate, gbc);
        JTextField txtDate = new JTextField(20);
        txtDate.setFont(metropolisBody);
        gbc.gridx = 1; gbc.gridy = 4; formBox.add(txtDate, gbc);

        JLabel lblSenderName = new JLabel("Found By (Sender Name):");
        lblSenderName.setFont(metropolisBody);
        gbc.gridx = 0; gbc.gridy = 5; formBox.add(lblSenderName, gbc);
        JTextField txtSenderName = new JTextField(20);
        txtSenderName.setFont(metropolisBody);
        gbc.gridx = 1; gbc.gridy = 5; formBox.add(txtSenderName, gbc);

        JLabel lblSenderID = new JLabel("Sender's ID:");
        lblSenderID.setFont(metropolisBody);
        gbc.gridx = 0; gbc.gridy = 6; formBox.add(lblSenderID, gbc);
        JTextField txtSenderID = new JTextField(20);
        txtSenderID.setFont(metropolisBody);
        gbc.gridx = 1; gbc.gridy = 6; formBox.add(txtSenderID, gbc);

        // --- BUTTON ---
        JButton btnSave = new JButton("SAVE");
        btnSave.setBackground(new Color(255, 240, 50)); // Bright Yellow
        btnSave.setForeground(Color.BLACK);
        btnSave.setFont(marcellusHeader.deriveFont(28f)); // Match the serif font in the image
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setMargin(new Insets(10, 40, 10, 40)); // Adds internal padding to make it wide
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; // Stop it from stretching all the way across
        gbc.anchor = GridBagConstraints.CENTER; // Perfectly center it
        gbc.insets = new Insets(30, 10, 10, 10); // Extra space above the button
        formBox.add(btnSave, gbc);

        // Add the blue form box to the center of the main photo panel
        mainPanel.add(formBox);

        // --- DATABASE LOGIC (Unchanged) ---
        btnSave.addActionListener(e -> {
            String name = txtItemName.getText();
            String category = cbCategory.getSelectedItem().toString();
            String location = txtLocation.getText();
            String dateStr = txtDate.getText();
            String senderName = txtSenderName.getText();
            String senderId = txtSenderID.getText();

            if(name.isEmpty() || location.isEmpty() || dateStr.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Please fill in all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO items (item_name, category, location_found, date_found, status, sender_name, sender_id) VALUES (?, ?, ?, ?, 'UNCLAIMED', ?, ?)")) {
                
                stmt.setString(1, name);
                stmt.setString(2, category);
                stmt.setString(3, location);
                stmt.setDate(4, java.sql.Date.valueOf(dateStr)); 
                stmt.setString(5, senderName); 
                stmt.setString(6, senderId);   
                
                stmt.executeUpdate();
                
                JOptionPane.showMessageDialog(mainPanel, "Item '" + name + "' added successfully!");
                txtItemName.setText("");
                txtLocation.setText("");
                txtDate.setText("");
                txtSenderName.setText("");
                txtSenderID.setText("");
                
                refreshData(); 

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(mainPanel, "Invalid Date Format. Please use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainPanel, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return mainPanel;
    }

    // --- INVENTORY TABLE PANEL ---
    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(21, 35, 75)); 
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel innerContainer = new JPanel(new BorderLayout(0, 15));
        innerContainer.setBackground(new Color(133, 179, 235)); 
        innerContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.setBackground(new Color(133, 179, 235)); 

        txtSearch = new JTextField(15);
        txtSearch.setFont(metropolisBody);
        txtSearch.setText("Search"); 
        txtSearch.setForeground(Color.GRAY);
        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().equals("Search")) { txtSearch.setText(""); txtSearch.setForeground(Color.BLACK); }
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().isEmpty()) { txtSearch.setForeground(Color.GRAY); txtSearch.setText("Search"); }
            }
        });
        topPanel.add(txtSearch);

        JLabel lblC = new JLabel("Category:");
        lblC.setFont(metropolisBody);
        topPanel.add(lblC);
        cbCategoryFilter = new JComboBox<>(new String[]{"All", "Electronics", "IDs", "Bags", "Clothing", "Valuables"});
        cbCategoryFilter.setFont(metropolisBody);
        topPanel.add(cbCategoryFilter);

        JLabel lblS = new JLabel("Status:");
        lblS.setFont(metropolisBody);
        topPanel.add(lblS);
        cbStatus = new JComboBox<>(new String[]{"All", "UNCLAIMED", "CLAIMED"});
        cbStatus.setFont(metropolisBody);
        topPanel.add(cbStatus);

        JButton btnDateFilter = new JButton("📅 Date");
        btnDateFilter.setFont(metropolisBold);
        btnDateFilter.setBackground(new Color(176, 205, 235));
        btnDateFilter.setFocusPainted(false);
        btnDateFilter.addActionListener(e -> {
            JPanel datePanel = new JPanel(new GridLayout(2, 2, 5, 10));
            datePanel.add(new JLabel("From (YYYY-MM-DD): "));
            datePanel.add(txtDateFrom);
            datePanel.add(new JLabel("To (YYYY-MM-DD): "));
            datePanel.add(txtDateTo);

            int result = JOptionPane.showConfirmDialog(panel, datePanel, "Filter by Date Range", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                refreshData(); 
            }
        });
        topPanel.add(btnDateFilter);

        JButton btnFilter = new JButton("🔍 Filter"); 
        btnFilter.setFont(metropolisBold);
        btnFilter.setBackground(new Color(255, 235, 59)); 
        btnFilter.setFocusPainted(false);
        btnFilter.addActionListener(e -> refreshData()); 
        topPanel.add(btnFilter);

        JButton btnReset = new JButton("✖ Reset");
        btnReset.setFont(metropolisBold);
        btnReset.setBackground(new Color(255, 100, 100)); 
        btnReset.setForeground(Color.BLACK);
        btnReset.setFocusPainted(false);
        btnReset.addActionListener(e -> {
            txtSearch.setText("Search");
            txtSearch.setForeground(Color.GRAY);
            cbCategoryFilter.setSelectedIndex(0);
            cbStatus.setSelectedIndex(0);
            txtDateFrom.setText("");
            txtDateTo.setText("");
            refreshData(); 
        });
        topPanel.add(btnReset);

        // --- EDIT RECEIVED ITEM BUTTON (ADMIN ONLY) ---
        if ("Admin".equalsIgnoreCase(currentUserRole)) {
            JButton btnEditItem = new JButton("✏ Edit Item");
            btnEditItem.setFont(metropolisBold);
            btnEditItem.setBackground(new Color(255, 165, 0)); 
            btnEditItem.setForeground(Color.BLACK);
            btnEditItem.setFocusPainted(false);
            btnEditItem.addActionListener(e -> {
                
                JScrollPane scrollPane = (JScrollPane) innerContainer.getComponent(1);
                JTable tbl = (JTable) scrollPane.getViewport().getView();
                int selectedRow = tbl.getSelectedRow(); 
                
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(panel, "Please click on a row in the table first to select an item to edit.", "No Item Selected", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                String itemID = tbl.getValueAt(selectedRow, 0).toString();
                String itemName = tbl.getValueAt(selectedRow, 1).toString();
                String itemCategory = tbl.getValueAt(selectedRow, 2).toString();
                String itemLocation = tbl.getValueAt(selectedRow, 3).toString();
                String itemDate = tbl.getValueAt(selectedRow, 4).toString();
                
                // Fetch Sender data directly from DB to populate Admin Edit Panel!
                String senderName = "";
                String senderID = "";
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                     PreparedStatement stmt = conn.prepareStatement("SELECT sender_name, sender_id FROM items WHERE item_id = ?")) {
                    stmt.setInt(1, Integer.parseInt(itemID));
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        senderName = rs.getString("sender_name");
                        senderID = rs.getString("sender_id");
                    }
                } catch (SQLException ex) { ex.printStackTrace(); }

                showEditItemPopup(itemID, itemName, itemCategory, itemLocation, itemDate, senderName, senderID);
            });
            topPanel.add(btnEditItem);
        }
        
        innerContainer.add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Item ID", "Item Name", "Category", "Location Found", "Date Found", "Status", "Actions"};
        
        tableModel = new DefaultTableModel(null, columns) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(40); 
        table.setFont(metropolisBody);
        table.setShowGrid(true);
        table.setGridColor(new Color(100, 150, 220)); 

        javax.swing.table.DefaultTableCellRenderer customRenderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER); 

                if (!isSelected) {
                    c.setBackground(new Color(153, 195, 245)); 
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(new Color(100, 150, 220)); 
                }

                if (column == 5 && value != null) {
                    if (value.toString().equals("CLAIMED")) {
                        c.setForeground(new Color(34, 139, 34)); 
                    } else if (value.toString().equals("UNCLAIMED")) {
                        c.setForeground(Color.RED); 
                    }
                }

                if (column == 6 && value != null) {
                    c.setBackground(new Color(255, 235, 59)); 
                    c.setForeground(Color.BLACK);
                    ((JComponent)c).setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(5, 10, 5, 10), 
                        BorderFactory.createLineBorder(Color.GRAY, 1)
                    ));
                } else {
                    ((JComponent)c).setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0)); 
                }

                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }

        table.getTableHeader().setFont(metropolisBold);
        table.getTableHeader().setBackground(new Color(100, 150, 240)); 
        table.getTableHeader().setForeground(Color.BLACK);

        table.getColumnModel().getColumn(0).setPreferredWidth(60); 
        table.getColumnModel().getColumn(1).setPreferredWidth(140); 
        table.getColumnModel().getColumn(2).setPreferredWidth(110); 
        table.getColumnModel().getColumn(3).setPreferredWidth(180); 
        table.getColumnModel().getColumn(4).setPreferredWidth(100); 
        table.getColumnModel().getColumn(5).setPreferredWidth(100); 
        table.getColumnModel().getColumn(6).setPreferredWidth(140); 

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(133, 179, 235)); 
        innerContainer.add(scrollPane, BorderLayout.CENTER);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (col == 6) { 
                    String status = table.getValueAt(row, 5).toString();
                    String itemID = table.getValueAt(row, 0).toString();
                    String itemName = table.getValueAt(row, 1).toString();

                    if (status.equals("UNCLAIMED")) {
                        txtClaimItemID.setText(itemID);
                        txtClaimItemName.setText(itemName);

                        // FETCH SENDER DATA BEFORE SHOWING PROCESS CLAIM SCREEN
                        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                             PreparedStatement stmt = conn.prepareStatement("SELECT sender_name, sender_id FROM items WHERE item_id = ?")) {
                            stmt.setInt(1, Integer.parseInt(itemID));
                            ResultSet rs = stmt.executeQuery();
                            if (rs.next()) {
                                txtClaimSenderName.setText(rs.getString("sender_name"));
                                txtClaimSenderID.setText(rs.getString("sender_id"));
                            }
                        } catch (SQLException ex) { ex.printStackTrace(); }

                        cardLayout.show(mainContentPanel, "PROCESS_CLAIM");
                        
                    } else if (status.equals("CLAIMED")) {
                        if (currentUserRole.equals("Admin")) {
                            showAdminEditPanel(itemID, itemName);
                        } else {
                            showStaffDetailsPopup(itemID, itemName, panel);
                        }
                    }
                }
            }
        });
        
        panel.add(innerContainer, BorderLayout.CENTER);
        return panel;
    }

    // --- ADMIN EDIT ITEM DETAILS PANEL ---
    private void showEditItemPopup(String itemID, String currentName, String currentCategory, String currentLocation, String currentDate, String currentSenderName, String currentSenderID) {
        // Updated Grid from 4 to 6 rows to accommodate Sender info
        JPanel editPanel = new JPanel(new GridLayout(6, 2, 5, 10));
        
        editPanel.add(new JLabel("Item Name:"));
        JTextField txtName = new JTextField(currentName);
        editPanel.add(txtName);
        
        editPanel.add(new JLabel("Category:"));
        JComboBox<String> cbCat = new JComboBox<>(new String[]{"Electronics", "IDs/Documents", "Bags", "Clothing", "Valuables"});
        for (int i = 0; i < cbCat.getItemCount(); i++) {
            if (cbCat.getItemAt(i).equalsIgnoreCase(currentCategory)) {
                cbCat.setSelectedIndex(i);
                break;
            }
        }
        editPanel.add(cbCat);
        
        editPanel.add(new JLabel("Location Found:"));
        JTextField txtLoc = new JTextField(currentLocation);
        editPanel.add(txtLoc);
        
        editPanel.add(new JLabel("Date Found:"));
        JTextField txtDate = new JTextField(currentDate);
        editPanel.add(txtDate);

        // --- NEW EDITABLE SENDER INFO ---
        editPanel.add(new JLabel("Sender Name:"));
        JTextField txtSenderName = new JTextField(currentSenderName);
        editPanel.add(txtSenderName);

        editPanel.add(new JLabel("Sender ID:"));
        JTextField txtSenderID = new JTextField(currentSenderID);
        editPanel.add(txtSenderID);
        
        int choice = JOptionPane.showConfirmDialog(this, editPanel, "Edit Found Item #" + itemID, JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (choice == JOptionPane.OK_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                 PreparedStatement stmt = conn.prepareStatement("UPDATE items SET item_name=?, category=?, location_found=?, date_found=?, sender_name=?, sender_id=? WHERE item_id=?")) {
                 
                 stmt.setString(1, txtName.getText());
                 stmt.setString(2, cbCat.getSelectedItem().toString());
                 stmt.setString(3, txtLoc.getText());
                 stmt.setDate(4, java.sql.Date.valueOf(txtDate.getText()));
                 stmt.setString(5, txtSenderName.getText()); // Save edited Sender Name
                 stmt.setString(6, txtSenderID.getText());   // Save edited Sender ID
                 stmt.setInt(7, Integer.parseInt(itemID));
                 
                 stmt.executeUpdate(); 
                 
                 JOptionPane.showMessageDialog(this, "Item details updated successfully!");
                 refreshData(); 
                 
            } catch (IllegalArgumentException ex) {
                 JOptionPane.showMessageDialog(this, "Invalid Date Format! Please use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                 ex.printStackTrace();
                 JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- STAFF READ-ONLY DETAILS ---
    private void showStaffDetailsPopup(String itemID, String itemName, JPanel parentPanel) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT claimant_name, student_id, date_claimed FROM claims WHERE item_id = ?")) {
            
            stmt.setInt(1, Integer.parseInt(itemID));
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String msg = "Item Details:\nID: " + itemID + "\nName: " + itemName + "\n\n"
                           + "Claimed By:\nName: " + rs.getString("claimant_name") + "\n"
                           + "Student ID: " + rs.getString("student_id") + "\n"
                           + "Date: " + rs.getDate("date_claimed");
                JOptionPane.showMessageDialog(parentPanel, msg, "View Details", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    // --- ADMIN EDIT / UNDO CLAIM PANEL ---
    private void showAdminEditPanel(String itemID, String itemName) {
        String dbClaimantName = ""; 
        String dbStudentID = "";
        String dbDateClaimed = "";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement stmt = conn.prepareStatement("SELECT claimant_name, student_id, date_claimed FROM claims WHERE item_id = ?")) {
            stmt.setInt(1, Integer.parseInt(itemID));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                dbClaimantName = rs.getString("claimant_name");
                dbStudentID = rs.getString("student_id");
                dbDateClaimed = rs.getDate("date_claimed").toString();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        JPanel editPanel = new JPanel(new GridLayout(4, 2, 5, 10));
        editPanel.add(new JLabel("Item Name:"));
        editPanel.add(new JLabel("<html><b>" + itemName + "</b></html>"));
        editPanel.add(new JLabel("Date Claimed:"));
        editPanel.add(new JLabel("<html><b>" + dbDateClaimed + "</b></html>"));
        editPanel.add(new JLabel("Claimant Name:"));
        JTextField txtEditName = new JTextField(dbClaimantName);
        editPanel.add(txtEditName);
        editPanel.add(new JLabel("Student ID:"));
        JTextField txtEditID = new JTextField(dbStudentID);
        editPanel.add(txtEditID);

        Object[] options = {"Save Changes", "Undo Claim (Return to Inventory)", "Cancel"};

        int choice = JOptionPane.showOptionDialog(this, editPanel, "Admin Options - Item #" + itemID, 
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[2]);

        if (choice == 0) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                 PreparedStatement stmt = conn.prepareStatement("UPDATE claims SET claimant_name = ?, student_id = ? WHERE item_id = ?")) {
                stmt.setString(1, txtEditName.getText());
                stmt.setString(2, txtEditID.getText());
                stmt.setInt(3, Integer.parseInt(itemID));
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Claim details updated successfully!");
                refreshData();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            
        } else if (choice == 1) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this claim? This returns the item to UNCLAIMED status.", "Confirm Undo", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                    conn.setAutoCommit(false); 
                    try (PreparedStatement delStmt = conn.prepareStatement("DELETE FROM claims WHERE item_id = ?");
                         PreparedStatement updStmt = conn.prepareStatement("UPDATE items SET status = 'UNCLAIMED' WHERE item_id = ?")) {
                        
                        delStmt.setInt(1, Integer.parseInt(itemID));
                        delStmt.executeUpdate();
                        
                        updStmt.setInt(1, Integer.parseInt(itemID));
                        updStmt.executeUpdate();
                        
                        conn.commit();
                        JOptionPane.showMessageDialog(this, "Claim undone. Item is back in inventory.");
                        refreshData();
                    } catch (SQLException ex) {
                        conn.rollback();
                        throw ex;
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    // --- PROCESS CLAIM PANEL (UPDATED UI TO MATCH MOCKUP) ---
    private JPanel createProcessClaimPanel() {
        // 1. Create the main background panel (Stretches the campus photo)
        Image mainBgImage = new ImageIcon("src/lostandfound/images/bg.png").getImage();
        JPanel mainPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); 
                if (mainBgImage != null) {
                    g.drawImage(mainBgImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        // Fallback color in case the image is missing
        mainPanel.setBackground(new Color(21, 35, 75)); 

        // 2. Create the light blue semi-transparent form box
        JPanel formBox = new JPanel(new GridBagLayout());
        formBox.setBackground(new Color(133, 179, 235, 230)); // 230 adds a slight transparency!
        formBox.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60)); // Thick padding inside the box
        formBox.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- TITLE ---
        JLabel lblTitle = new JLabel("Process Item Claim", SwingConstants.CENTER);
        lblTitle.setFont(marcellusHeader.deriveFont(42f)); // Made much larger
        lblTitle.setForeground(Color.BLACK);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; 
        gbc.insets = new Insets(10, 10, 40, 10); // Extra space below title
        formBox.add(lblTitle, gbc);

        // Reset constraints for the form inputs
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);

        // --- FORM FIELDS ---
        JLabel lblID = new JLabel("Item ID:");
        lblID.setFont(metropolisBody);
        gbc.gridx = 0; gbc.gridy = 1; formBox.add(lblID, gbc);
        txtClaimItemID.setEditable(false); 
        txtClaimItemID.setFont(metropolisBody);
        gbc.gridx = 1; gbc.gridy = 1; formBox.add(txtClaimItemID, gbc);

        JLabel lblN = new JLabel("Item Name:");
        lblN.setFont(metropolisBody);
        gbc.gridx = 0; gbc.gridy = 2; formBox.add(lblN, gbc);
        txtClaimItemName.setEditable(false); 
        txtClaimItemName.setFont(metropolisBody);
        gbc.gridx = 1; gbc.gridy = 2; formBox.add(txtClaimItemName, gbc);

        JLabel lblSenderN = new JLabel("Found By (Sender):");
        lblSenderN.setFont(metropolisBody);
        gbc.gridx = 0; gbc.gridy = 3; formBox.add(lblSenderN, gbc);
        txtClaimSenderName.setEditable(false); 
        txtClaimSenderName.setFont(metropolisBody);
        gbc.gridx = 1; gbc.gridy = 3; formBox.add(txtClaimSenderName, gbc);

        JLabel lblSenderID = new JLabel("Sender ID:");
        lblSenderID.setFont(metropolisBody);
        gbc.gridx = 0; gbc.gridy = 4; formBox.add(lblSenderID, gbc);
        txtClaimSenderID.setEditable(false); 
        txtClaimSenderID.setFont(metropolisBody);
        gbc.gridx = 1; gbc.gridy = 4; formBox.add(txtClaimSenderID, gbc);

        JLabel lblSID = new JLabel("Claimant Student ID:");
        lblSID.setFont(metropolisBody);
        gbc.gridx = 0; gbc.gridy = 5; formBox.add(lblSID, gbc);
        JTextField txtStudentID = new JTextField(20);
        txtStudentID.setFont(metropolisBody);
        gbc.gridx = 1; gbc.gridy = 5; formBox.add(txtStudentID, gbc);

        JLabel lblCN = new JLabel("Claimant Name:");
        lblCN.setFont(metropolisBody);
        gbc.gridx = 0; gbc.gridy = 6; formBox.add(lblCN, gbc);
        JTextField txtClaimantName = new JTextField(20);
        txtClaimantName.setFont(metropolisBody);
        gbc.gridx = 1; gbc.gridy = 6; formBox.add(txtClaimantName, gbc);

        // --- BUTTON ---
        JButton btnConfirm = new JButton("CONFIRM & RELEASE");
        btnConfirm.setBackground(new Color(255, 240, 50)); // Bright Yellow to match the SAVE button
        btnConfirm.setForeground(Color.BLACK);
        btnConfirm.setFont(marcellusHeader.deriveFont(26f)); // Styled to match the serif look
        btnConfirm.setFocusPainted(false);
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirm.setMargin(new Insets(10, 40, 10, 40)); 
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE; // Center it instead of stretching
        gbc.anchor = GridBagConstraints.CENTER; 
        gbc.insets = new Insets(30, 10, 10, 10); 
        formBox.add(btnConfirm, gbc);

        // Add the blue form box to the center of the main photo panel
        mainPanel.add(formBox);

        // --- DATABASE LOGIC ---
        btnConfirm.addActionListener(e -> {
            if(txtStudentID.getText().isEmpty() || txtClaimantName.getText().isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Student ID and Name are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
                conn.setAutoCommit(false); 
                
                try (PreparedStatement insertClaim = conn.prepareStatement("INSERT INTO claims (item_id, claimant_name, student_id, date_claimed) VALUES (?, ?, ?, CURDATE())");
                     PreparedStatement updateItem = conn.prepareStatement("UPDATE items SET status = 'CLAIMED' WHERE item_id = ?")) {
                    
                    int itemID = Integer.parseInt(txtClaimItemID.getText());
                    
                    insertClaim.setInt(1, itemID);
                    insertClaim.setString(2, txtClaimantName.getText());
                    insertClaim.setString(3, txtStudentID.getText());
                    insertClaim.executeUpdate();
                    
                    updateItem.setInt(1, itemID);
                    updateItem.executeUpdate();
                    
                    conn.commit(); 
                    JOptionPane.showMessageDialog(mainPanel, "Item successfully processed and returned!");
                    
                    txtStudentID.setText("");
                    txtClaimantName.setText("");
                    
                    refreshData(); 
                    cardLayout.show(mainContentPanel, "INVENTORY"); 
                    
                } catch (SQLException ex) {
                    conn.rollback(); 
                    throw ex;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(mainPanel, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        return mainPanel;
    }

    // --- FONT LOADER (Must be called before UI loads) ---
    public static void initFonts() {
        try {
            InputStream marcellusStream = MainSystemUI.class.getResourceAsStream("/lostandfound/fonts/Marcellus-Regular.ttf");
            InputStream metropolisStream = MainSystemUI.class.getResourceAsStream("/lostandfound/fonts/Metropolis-Regular.ttf");
            InputStream metropolisBoldStream = MainSystemUI.class.getResourceAsStream("/lostandfound/fonts/Metropolis-Bold.ttf");

            if (marcellusStream != null) {
                marcellusHeader = Font.createFont(Font.TRUETYPE_FONT, marcellusStream).deriveFont(26f);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(marcellusHeader);
            } else { marcellusHeader = new Font("Serif", Font.PLAIN, 26); }

            if (metropolisStream != null) {
                metropolisBody = Font.createFont(Font.TRUETYPE_FONT, metropolisStream).deriveFont(14f);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(metropolisBody);
            } else { metropolisBody = new Font("SansSerif", Font.PLAIN, 14); }

            if (metropolisBoldStream != null) {
                metropolisBold = Font.createFont(Font.TRUETYPE_FONT, metropolisBoldStream).deriveFont(Font.BOLD, 14f);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(metropolisBold);
            } else { metropolisBold = new Font("SansSerif", Font.BOLD, 14); }

        } catch (Exception e) {
            System.err.println("Fonts not found! Defaulting to system fonts.");
            marcellusHeader = new Font("Serif", Font.PLAIN, 26);
            metropolisBody = new Font("SansSerif", Font.PLAIN, 14);
            metropolisBold = new Font("SansSerif", Font.BOLD, 14);
        }
    }

    // --- LOGIN SCREEN LOGIC ---
    public static void showLogin() {
    	Image mainBgImage = new ImageIcon("src/lostandfound/images/bg.png").getImage();
    	
        JFrame loginFrame = new JFrame("UMak Security Login");
        loginFrame.setSize(800, 500); 
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);
        loginFrame.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
        headerPanel.setBackground(new Color(133, 179, 235)); 
        
        JPanel bgPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); 
                
                if (mainBgImage != null) {
                    g.drawImage(mainBgImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        
        bgPanel.setBackground(new Color(21, 35, 75)); 

        try {
            ImageIcon logoIcon = new ImageIcon(new ImageIcon("src/lostandfound/images/umak_logo.png").getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH));
            JLabel lblLogo = new JLabel(logoIcon);
            headerPanel.add(lblLogo);
        } catch (Exception e) { }

        JLabel lblHeaderText = new JLabel("<html><div style='text-align: center; color: #1a1a1a;'>"
                + "<span style='font-size: 26px; font-family: Marcellus;'>University Of Makati</span><br>"
                + "<span style='font-size: 14px; font-family: Metropolis;'>Lost & Found Inventory System</span>"
                + "</div></html>");
        headerPanel.add(lblHeaderText);
        loginFrame.add(headerPanel, BorderLayout.NORTH);

        JPanel loginBox = new JPanel(new GridBagLayout());
        loginBox.setBackground(new Color(133, 179, 235, 180)); 
        loginBox.setBorder(BorderFactory.createEmptyBorder(30, 50, 40, 50)); 
        loginBox.setOpaque(true);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblLoginTitle = new JLabel("LOGIN", SwingConstants.CENTER);
        lblLoginTitle.setFont(marcellusHeader.deriveFont(36f)); 
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 30, 10); 
        loginBox.add(lblLoginTitle, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(metropolisBody.deriveFont(16f));
        gbc.gridx = 0; gbc.gridy = 1; loginBox.add(lblUser, gbc);
        
        JTextField txtUser = new JTextField(15);
        txtUser.setFont(metropolisBody.deriveFont(16f));
        gbc.gridx = 1; gbc.gridy = 1; loginBox.add(txtUser, gbc);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(metropolisBody.deriveFont(16f));
        gbc.gridx = 0; gbc.gridy = 2; loginBox.add(lblPass, gbc);
        
        JPasswordField txtPass = new JPasswordField(15);
        txtPass.setFont(metropolisBody.deriveFont(16f));
        gbc.gridx = 1; gbc.gridy = 2; loginBox.add(txtPass, gbc);

        JButton btnSubmit = new JButton("SUBMIT");
        btnSubmit.setFont(metropolisBold.deriveFont(18f));
        btnSubmit.setBackground(new Color(255, 235, 59)); 
        btnSubmit.setForeground(Color.BLACK);
        btnSubmit.setFocusPainted(false); 
        btnSubmit.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10); 
        loginBox.add(btnSubmit, gbc);

        bgPanel.add(loginBox);
        loginFrame.add(bgPanel, BorderLayout.CENTER);

        btnSubmit.addActionListener(e -> {
            String username = txtUser.getText();
            String password = new String(txtPass.getPassword());

            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                 PreparedStatement stmt = conn.prepareStatement("SELECT role FROM users WHERE username = ? AND password = ?")) {
                
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    String role = rs.getString("role");
                    loginFrame.dispose();
                    new MainSystemUI(role).setVisible(true); 
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Invalid credentials.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(loginFrame, "Database Connection Failed!\nPlease ensure MySQL is running.", "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        loginFrame.setVisible(true);
    }
    
    // --- EXPORT DATABASE TO EXCEL (.CSV) ---
    private void exportToExcel() {
        String[] options = {"Past Week", "Past Month", "Past Year", "All Time", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this, 
                "Select the timeframe for the logs you want to export:", 
                "Export Logs to Excel",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 4 || choice == JOptionPane.CLOSED_OPTION) return; 

        String dateCondition = "1=1"; 
        if (choice == 0) dateCondition = "date_found >= DATE_SUB(CURDATE(), INTERVAL 1 WEEK)";
        else if (choice == 1) dateCondition = "date_found >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)";
        else if (choice == 2) dateCondition = "date_found >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)";

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Excel File");
        fileChooser.setSelectedFile(new java.io.File("UMak_LostAndFound_Logs.csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            
            try (java.io.FileWriter fw = new java.io.FileWriter(fileToSave);
                 Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM items WHERE " + dateCondition + " ORDER BY date_found DESC")) {

                fw.append("Item ID,Item Name,Category,Location Found,Date Found,Sender Name,Sender ID,Status\n");

                int rowCount = 0; 

                while (rs.next()) {
                    fw.append(rs.getString("item_id")).append(",")
                      .append("\"").append(rs.getString("item_name")).append("\",") 
                      .append(rs.getString("category")).append(",")
                      .append("\"").append(rs.getString("location_found")).append("\",")
                      .append(rs.getDate("date_found").toString()).append(",")
                      .append("\"").append(rs.getString("sender_name")).append("\",")
                      .append("\"").append(rs.getString("sender_id")).append("\",")
                      .append(rs.getString("status")).append("\n");
                    rowCount++;
                }

                JOptionPane.showMessageDialog(this, "Export Successful!\nSaved " + rowCount + " items to:\n" + fileToSave.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                System.err.println("=== EXPORT CRASHED ===");
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Export Failed!\nCheck Eclipse console for details.\nError: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // --- HELPER METHOD: CREATE SIDEBAR BUTTONS ---
    private JButton createSidebarButton(String text, String symbol, boolean isActive) {
        JButton btn = new JButton("<html><center><div style='font-size: 24px; margin-bottom: 5px;'>" + symbol + "</div>" + text + "</center></html>") {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                super.paintComponent(g);
            }
        };
        
        btn.setFont(metropolisBody.deriveFont(16f));
        btn.setFocusPainted(false);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        btn.setContentAreaFilled(false); 
        btn.setOpaque(true); 
        btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 

        Dimension btnSize = new Dimension(190, 90);
        btn.setPreferredSize(btnSize);
        btn.setMaximumSize(btnSize);
        btn.setMinimumSize(btnSize);
        
        Color activeColor = new Color(41, 128, 185);   
        Color inactiveColor = new Color(133, 179, 235); 
        Color hoverColor = new Color(100, 150, 220);    

        if (isActive) {
            btn.setBackground(activeColor);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(inactiveColor);
            btn.setForeground(Color.BLACK);
        }
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!btn.getBackground().equals(activeColor)) btn.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!btn.getBackground().equals(activeColor)) btn.setBackground(inactiveColor);
            }
        });

        return btn;
    }

    // --- HELPER METHOD: CHANGE ACTIVE BUTTON COLORS ---
    private void updateButtonColors(JButton activeBtn, JButton inactive1, JButton inactive2) {
        Color activeColor = new Color(41, 128, 185);
        Color inactiveColor = new Color(133, 179, 235);
        
        activeBtn.setBackground(activeColor);
        activeBtn.setForeground(Color.WHITE);
        
        inactive1.setBackground(inactiveColor);
        inactive1.setForeground(Color.BLACK);
        
        inactive2.setBackground(inactiveColor);
        inactive2.setForeground(Color.BLACK);
    }
    
    // --- MAIN METHOD ---
    public static void main(String[] args) {
        initFonts();
        
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            showLogin();
        });
    }
}