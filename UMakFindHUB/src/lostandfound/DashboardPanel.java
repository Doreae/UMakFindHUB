package lostandfound;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.io.File;
import java.io.FileWriter;

public class DashboardPanel extends JPanel {
    private JLabel lblTotalFound, lblTotalClaimed, lblTotalUnclaimed;
    private DefaultTableModel activityTableModel;
    private Image mainBgImage; // Stores the campus background
    
    // NEW: The Smart Alert Label
    private JLabel lblDisposalAlert; 

    public DashboardPanel() {
        mainBgImage = new ImageIcon("src/lostandfound/images/bg.png").getImage();
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ==========================================
        // NEW: SMART ALERT SETUP
        // ==========================================
        lblDisposalAlert = new JLabel("");
        lblDisposalAlert.setFont(AppConstants.metropolisBold.deriveFont(16f));
        lblDisposalAlert.setForeground(new Color(255, 50, 50)); // Bright Red for urgency
        lblDisposalAlert.setHorizontalAlignment(SwingConstants.CENTER);
        lblDisposalAlert.setVisible(false); // Hidden by default until data is checked

        // ==========================================
        // RESTORED: STAT CARDS (Transparent Background)
        // ==========================================
        JPanel statsContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        statsContainer.setOpaque(false); // Let the image show through

        lblTotalFound = new JLabel("0", SwingConstants.CENTER);
        lblTotalClaimed = new JLabel("0", SwingConstants.CENTER);
        lblTotalUnclaimed = new JLabel("0", SwingConstants.CENTER);

        statsContainer.add(createStatCard("Total Found Items", lblTotalFound));
        statsContainer.add(createStatCard("Successfully Claimed", lblTotalClaimed));
        statsContainer.add(createStatCard("Unclaimed Items", lblTotalUnclaimed));
        
        // Wrap the Alert and the Stats together at the top
        JPanel topWrapper = new JPanel(new BorderLayout(0, 10));
        topWrapper.setOpaque(false);
        topWrapper.add(lblDisposalAlert, BorderLayout.NORTH);
        topWrapper.add(statsContainer, BorderLayout.CENTER);
        add(topWrapper, BorderLayout.NORTH);

        
        // ==========================================
        // RESTORED: RECENT ACTIVITY & EXPORT BUTTON
        // ==========================================
        JPanel activityPanel = new JPanel(new BorderLayout(0, 10));
        activityPanel.setOpaque(false);
        activityPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.WHITE));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Recent Activity:");
        lblTitle.setFont(AppConstants.marcellusHeader.deriveFont(24f));
        lblTitle.setForeground(Color.WHITE);
        titlePanel.add(lblTitle, BorderLayout.WEST);

        
        // The Green Export Button
        JButton btnExport = new JButton("📥 Export to Excel");
        btnExport.setBackground(new Color(46, 204, 113));
        btnExport.setForeground(Color.BLACK);
        btnExport.setFont(AppConstants.metropolisBold);
        btnExport.addActionListener(e -> exportToExcel());
        
        // Archive Button
        JButton btnArchive = new JButton("Archive Old Items 🗑️");
        btnArchive.setFont(AppConstants.metropolisBold.deriveFont(14f));
        btnArchive.setBackground(new Color(255, 102, 102)); 
        btnArchive.setForeground(Color.WHITE);
        btnArchive.addActionListener(e -> archiveOldItems());

        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnWrapper.setOpaque(false);
        
        // Add Archive first, then Export
        btnWrapper.add(btnArchive);
        btnWrapper.add(btnExport);
        
        titlePanel.add(btnWrapper, BorderLayout.EAST);

        activityPanel.add(titlePanel, BorderLayout.NORTH);

        // Restored Table Styling
        activityTableModel = new DefaultTableModel(null, new String[]{"Date Logged", "Item Description", "Status", "Location"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(activityTableModel);
        table.setRowHeight(40);
        table.setFont(AppConstants.metropolisBody);
        table.setShowGrid(true);
        table.setGridColor(new Color(100, 150, 220)); 
        table.setBackground(new Color(224, 247, 250)); 
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(133, 179, 235));
        activityPanel.add(scroll, BorderLayout.CENTER);
        add(activityPanel, BorderLayout.CENTER);
    }

    // Overrides standard painting to stretch the background image
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mainBgImage != null) g.drawImage(mainBgImage, 0, 0, getWidth(), getHeight(), this);
    }

    private JPanel createStatCard(String title, JLabel num) {
        JPanel card = new JPanel(new BorderLayout());
        card.setPreferredSize(new Dimension(200, 130)); card.setBackground(Color.WHITE);
        card.add(new JLabel(title, SwingConstants.CENTER), BorderLayout.NORTH); 
        num.setFont(AppConstants.metropolisBold.deriveFont(42f));
        num.setForeground(new Color(41, 128, 185));
        card.add(num, BorderLayout.CENTER);
        return card;
    }

    public void refreshData() {
        try (Connection conn = DriverManager.getConnection(AppConstants.DB_URL, AppConstants.DB_USER, AppConstants.DB_PASS);
             Statement stmt = conn.createStatement()) {
            
            // Existing Stat Queries
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM items");
            if (rs.next()) lblTotalFound.setText(rs.getString(1));
            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM items WHERE status = 'CLAIMED'");
            if (rs.next()) lblTotalClaimed.setText(rs.getString(1));
            
            rs = stmt.executeQuery("SELECT COUNT(*) FROM items WHERE status = 'UNCLAIMED'");
            if (rs.next()) lblTotalUnclaimed.setText(rs.getString(1));

            // ==========================================
            // 1. FETCH CURRENT USER'S ROLE
            // ==========================================
            String userRole = "Staff"; // Default to lowest privilege
            String roleQuery = "SELECT role FROM users WHERE username = ?";
            try (PreparedStatement roleStmt = conn.prepareStatement(roleQuery)) {
                roleStmt.setString(1, Session.currentUser);
                try (ResultSet roleRs = roleStmt.executeQuery()) {
                    if (roleRs.next()) {
                        userRole = roleRs.getString("role");
                    }
                }
            }

            // ==========================================
            // 2. SMART ALERT LOGIC (ADMIN ONLY)
            // ==========================================
            String alertQuery = "SELECT COUNT(*) FROM items WHERE status = 'UNCLAIMED' AND date_found <= DATE_SUB(CURDATE(), INTERVAL 30 DAY)";
            rs = stmt.executeQuery(alertQuery);
            if (rs.next()) {
                int expiredCount = rs.getInt(1);
                
                // Show ONLY if there are expired items AND the user is an Admin
                if (expiredCount > 0 && "Admin".equalsIgnoreCase(userRole)) {
                    lblDisposalAlert.setText("🚨 ACTION REQUIRED: [ " + expiredCount + " ] Items are overdue for Disposal.");
                    lblDisposalAlert.setVisible(true); 
                } else {
                    lblDisposalAlert.setVisible(false); 
                }
            }

            // Table Refresh
            activityTableModel.setRowCount(0);
            rs = stmt.executeQuery("SELECT date_found, item_name, category, status, location_found FROM items ORDER BY date_found DESC LIMIT 10");
            while (rs.next()) {
                activityTableModel.addRow(new Object[]{
                    rs.getDate("date_found"), 
                    rs.getString("category") + " - " + rs.getString("item_name"), 
                    rs.getString("status"), 
                    rs.getString("location_found")
                });
            }
        } catch (Exception ex) { 
            ex.printStackTrace(); 
        }
    }
    
    private void archiveOldItems() {
        // ==========================================
        // 1. BACKEND SECURITY CHECK (Defense in Depth)
        // ==========================================
        if (!"Admin".equalsIgnoreCase(Session.currentUser)) {
            JOptionPane.showMessageDialog(this, 
                "Unauthorized Action!\nOnly System Administrators can archive inventory data.", 
                "Access Denied", JOptionPane.ERROR_MESSAGE);
            
            // Log the unauthorized attempt to the Audit Trail
            AuditController.logAction(Session.currentUser, "FAILED_ACCESS", 0, "Attempted to archive items without Admin privileges.");
            return; // Stops the code dead in its tracks
        }

        // 2. Confirm with the Admin first
        int confirm = JOptionPane.showConfirmDialog(this, 
            "This will set all UNCLAIMED items older than 30 days to 'DISPOSED'.\nAre you sure?", 
            "Confirm Data Cleanup", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        // 3. The SQL Magic
        String query = "UPDATE items SET status = 'DISPOSED' " +
                       "WHERE status = 'UNCLAIMED' AND date_found <= DATE_SUB(CURDATE(), INTERVAL 30 DAY)";

        try (Connection conn = AppConstants.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                // 4. Log the action for the Audit Trail
                AuditController.logAction(
                    Session.currentUser, 
                    "DELETE", 
                    0, 
                    "System Cleanup: Archived " + rowsAffected + " expired items to DISPOSED status."
                );

                JOptionPane.showMessageDialog(this, "Successfully archived " + rowsAffected + " items.");
                refreshData(); // Refresh the counts on the dashboard 
            } else {
                JOptionPane.showMessageDialog(this, "No expired items found to archive.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Cleanup Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // ==========================================
    // RESTORED: EXCEL/CSV EXPORT LOGIC
    // ==========================================
    private void exportToExcel() {
        String[] options = {"Past Week", "Past Month", "Past Year", "All Time", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this, "Select timeframe:", "Export Logs",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 4 || choice == JOptionPane.CLOSED_OPTION) return; 

        String dateCondition = "1=1"; 
        if (choice == 0) dateCondition = "date_found >= DATE_SUB(CURDATE(), INTERVAL 1 WEEK)";
        else if (choice == 1) dateCondition = "date_found >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)";
        else if (choice == 2) dateCondition = "date_found >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR)";

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("UMak_LostAndFound_Logs.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(fileChooser.getSelectedFile());
                 Connection conn = DriverManager.getConnection(AppConstants.DB_URL, AppConstants.DB_USER, AppConstants.DB_PASS);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM items WHERE " + dateCondition + " ORDER BY item_id ASC")) {

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
                JOptionPane.showMessageDialog(this, "Export Successful!\nSaved " + rowCount + " items.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}