package lostandfound;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AuditLogPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private DefaultTableModel tableModel;
    private JTable logTable;

    public AuditLogPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(21, 35, 75));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. Header
        JLabel lblTitle = new JLabel("System Audit Logs", SwingConstants.CENTER);
        lblTitle.setFont(AppConstants.marcellusHeader.deriveFont(32f));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        // 2. Table Setup
        String[] columns = {"Log ID", "Date & Time", "User", "Action", "Item ID", "Details"};
        tableModel = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent Admin from accidentally editing the logs
            }
        };

        logTable = new JTable(tableModel);
        logTable.setFont(AppConstants.metropolisBody.deriveFont(14f));
        logTable.setRowHeight(30);
        logTable.getTableHeader().setFont(AppConstants.metropolisBold.deriveFont(14f));
        
        JScrollPane scrollPane = new JScrollPane(logTable);
        add(scrollPane, BorderLayout.CENTER);

        // 3. Refresh Button
        JButton btnRefresh = new JButton("Refresh Logs");
        btnRefresh.setFont(AppConstants.metropolisBold.deriveFont(16f));
        btnRefresh.setBackground(new Color(133, 179, 235));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        bottomPanel.add(btnRefresh);
        add(bottomPanel, BorderLayout.SOUTH);

        btnRefresh.addActionListener(e -> refreshLogs());

        // Load data on startup
        refreshLogs();
    }

    /**
     * Fetches the logs from the database and populates the table.
     */
    public void refreshLogs() {
        tableModel.setRowCount(0); // Clear old data

        // Query orders by timestamp DESC so newest actions are at the very top
        String query = "SELECT * FROM audit_logs ORDER BY timestamp DESC";

        try (Connection conn = AppConstants.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                    rs.getInt("log_id"),
                    rs.getTimestamp("timestamp"),
                    rs.getString("performed_by"),
                    rs.getString("action_type"),
                    rs.getInt("item_id") == 0 ? "N/A" : rs.getInt("item_id"),
                    rs.getString("action_details")
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load audit logs.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}