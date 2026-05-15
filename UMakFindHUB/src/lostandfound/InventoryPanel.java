package lostandfound;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class InventoryPanel extends JPanel {
    private MainFrame mainController;
    private DefaultTableModel tableModel;
    
    // UI Elements for Filters
    private JTextField txtSearch, txtDateFrom, txtDateTo;
    private JComboBox<String> cbCategoryFilter, cbStatus;

    public InventoryPanel(MainFrame mainController) {
        this.mainController = mainController;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(21, 35, 75));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel innerContainer = new JPanel(new BorderLayout(0, 15));
        innerContainer.setBackground(new Color(133, 179, 235));
        innerContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // ==========================================
        // TOP FILTER BAR (Edit Button Removed!)
        // ==========================================
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topPanel.setBackground(new Color(133, 179, 235));

        txtSearch = new JTextField("Search", 15);
        txtSearch.setForeground(Color.GRAY);
        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) { if(txtSearch.getText().equals("Search")){ txtSearch.setText(""); txtSearch.setForeground(Color.BLACK);}}
            public void focusLost(java.awt.event.FocusEvent evt) { if(txtSearch.getText().isEmpty()){ txtSearch.setText("Search"); txtSearch.setForeground(Color.GRAY);}}
        });
        topPanel.add(txtSearch);

        topPanel.add(new JLabel("Category:"));
        cbCategoryFilter = new JComboBox<>(new String[]{"All", "Electronics", "IDs/Documents", "Bags", "Clothing", "Valuables"});
        topPanel.add(cbCategoryFilter);

        topPanel.add(new JLabel("Status:"));
        cbStatus = new JComboBox<>(new String[]{"All", "UNCLAIMED", "CLAIMED", "DISPOSED"});
        topPanel.add(cbStatus);

        txtDateFrom = new JTextField(10);
        txtDateTo = new JTextField(10);
        JButton btnDateFilter = new JButton("📅 Date");
        btnDateFilter.addActionListener(e -> {
            JPanel datePanel = new JPanel(new GridLayout(2, 2, 5, 10));
            datePanel.add(new JLabel("From (YYYY-MM-DD): ")); datePanel.add(txtDateFrom);
            datePanel.add(new JLabel("To (YYYY-MM-DD): ")); datePanel.add(txtDateTo);
            if (JOptionPane.showConfirmDialog(this, datePanel, "Filter Date", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) refreshData();
        });
        topPanel.add(btnDateFilter);

        JButton btnFilter = new JButton("🔍 Filter");
        btnFilter.setBackground(new Color(255, 235, 59));
        btnFilter.addActionListener(e -> refreshData());
        topPanel.add(btnFilter);

        JButton btnReset = new JButton("✖ Reset");
        btnReset.setBackground(new Color(255, 100, 100));
        btnReset.addActionListener(e -> {
            txtSearch.setText("Search"); txtSearch.setForeground(Color.GRAY);
            cbCategoryFilter.setSelectedIndex(0); cbStatus.setSelectedIndex(0);
            txtDateFrom.setText(""); txtDateTo.setText("");
            refreshData();
        });
        topPanel.add(btnReset);
        
        innerContainer.add(topPanel, BorderLayout.NORTH);

        // ==========================================
        // TABLE SETUP
        // ==========================================
        tableModel = new DefaultTableModel(null, new String[]{"Item ID", "Name", "Category", "Location", "Date Found", "Status", "Actions"}) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        
        JTable table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setShowGrid(true); table.setGridColor(new Color(100, 150, 220));

        // Renders the Yellow Action Button inside the table cell
        javax.swing.table.DefaultTableCellRenderer renderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER); 
                if (!isSelected) { c.setBackground(new Color(153, 195, 245)); c.setForeground(Color.BLACK); } 
                else { c.setBackground(new Color(100, 150, 220)); }

                if (column == 5 && value != null) {
                    if (value.toString().equals("CLAIMED")) c.setForeground(new Color(34, 139, 34)); 
                    else if (value.toString().equals("UNCLAIMED")) c.setForeground(Color.RED); 
                }

                if (column == 6 && value != null) {
                    c.setBackground(new Color(255, 235, 59)); 
                    c.setForeground(Color.BLACK);
                    ((JComponent)c).setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5,10,5,10), BorderFactory.createLineBorder(Color.GRAY,1)));
                } else {
                    ((JComponent)c).setBorder(BorderFactory.createEmptyBorder(0,0,0,0)); 
                }
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        table.getTableHeader().setBackground(new Color(100, 150, 240));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(133, 179, 235)); 
        innerContainer.add(scrollPane, BorderLayout.CENTER);

        // ==========================================
        // UNIFIED CLICK LOGIC
        // ==========================================
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (table.columnAtPoint(e.getPoint()) == 6) { // Action Column clicked
                    String id = table.getValueAt(row, 0).toString();
                    String status = table.getValueAt(row, 5).toString();
                    
                    // No matter what the status is, open the Unified Master Popup!
                    showUnifiedManagePopup(id, status);
                }
            }
        });
        
        add(innerContainer, BorderLayout.CENTER);
    }

    // Fetches data for the table based on active filters
    public void refreshData() {
        try (Connection conn = AppConstants.getConnection()) {
            tableModel.setRowCount(0);
            
            String keyword = txtSearch.getText().trim();
            String cat = cbCategoryFilter.getSelectedItem().toString();
            String stat = cbStatus.getSelectedItem().toString();
            
            // Use ? placeholders to prevent SQL Injection
            StringBuilder sql = new StringBuilder("SELECT * FROM items WHERE 1=1");
            if (!keyword.isEmpty() && !keyword.equals("Search")) {
                sql.append(" AND (item_name LIKE ? OR location_found LIKE ?)");
            }
            if (!cat.equals("All")) sql.append(" AND category = ?");
            if (!stat.equals("All")) sql.append(" AND status = ?");
            if (!txtDateFrom.getText().isEmpty()) sql.append(" AND date_found >= ?");
            if (!txtDateTo.getText().isEmpty()) sql.append(" AND date_found <= ?");
            
            sql.append(" ORDER BY item_id DESC");

            PreparedStatement stmt = conn.prepareStatement(sql.toString());
            int paramIndex = 1;

            // Fill in the ? placeholders in the exact order they were added
            if (!keyword.isEmpty() && !keyword.equals("Search")) {
                stmt.setString(paramIndex++, "%" + keyword + "%");
                stmt.setString(paramIndex++, "%" + keyword + "%");
            }
            if (!cat.equals("All")) stmt.setString(paramIndex++, cat);
            if (!stat.equals("All")) stmt.setString(paramIndex++, stat); // This handles DISPOSED automatically
            if (!txtDateFrom.getText().isEmpty()) stmt.setString(paramIndex++, txtDateFrom.getText());
            if (!txtDateTo.getText().isEmpty()) stmt.setString(paramIndex++, txtDateTo.getText());

            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                String status = rs.getString("status");
                
                // Logic for Action Text
                String actionText = "VIEW DETAILS"; 
                if (status.equals("UNCLAIMED")) {
                    actionText = "[PROCESS CLAIM]";
                } else if (status.equals("DISPOSED")) {
                    actionText = "VIEW RECORD"; // Archived items don't need claiming
                }

                tableModel.addRow(new Object[]{
                    rs.getString("item_id"), 
                    rs.getString("item_name"), 
                    rs.getString("category"), 
                    rs.getString("location_found"), 
                    rs.getString("date_found"), 
                    status, 
                    actionText
                });
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    // =================================================================================
    // NEW UNIFIED MASTER POPUP: Handles Viewing, Editing, and Undoing for ALL items!
    // =================================================================================
    private void showUnifiedManagePopup(String itemID, String currentStatus) {
        boolean isAdmin = "Admin".equalsIgnoreCase(mainController.currentUserRole);
        
        // 1. Fetch Item Data
        String dbName = "", dbCat = "", dbLoc = "", dbDate = "", dbSenderN = "", dbSenderID = "";
        try (Connection c = DriverManager.getConnection(AppConstants.DB_URL, AppConstants.DB_USER, AppConstants.DB_PASS);
             PreparedStatement s = c.prepareStatement("SELECT * FROM items WHERE item_id=?")) {
            s.setInt(1, Integer.parseInt(itemID)); ResultSet r = s.executeQuery();
            if(r.next()) { 
                dbName = r.getString("item_name"); dbCat = r.getString("category"); dbLoc = r.getString("location_found");
                dbDate = r.getString("date_found"); dbSenderN = r.getString("sender_name"); dbSenderID = r.getString("sender_id");
            }
        } catch(Exception ex){ ex.printStackTrace(); }

        // 2. Fetch Claim Data (Only if CLAIMED)
        String dbClaimantN = "", dbClaimantID = "", dbDateClaimed = "";
        if (currentStatus.equals("CLAIMED")) {
            try (Connection c = DriverManager.getConnection(AppConstants.DB_URL, AppConstants.DB_USER, AppConstants.DB_PASS);
                 PreparedStatement s = c.prepareStatement("SELECT * FROM claims WHERE item_id=?")) {
                s.setInt(1, Integer.parseInt(itemID)); ResultSet r = s.executeQuery();
                if(r.next()) { 
                    dbClaimantN = r.getString("claimant_name"); dbClaimantID = r.getString("student_id"); dbDateClaimed = r.getString("date_claimed");
                }
            } catch(Exception ex){ ex.printStackTrace(); }
        }

        // 3. Build the WindowBuilder-Safe UI
        JPanel popupPanel = new JPanel(new GridBagLayout());
        
        // --- Section 1: Item Details ---
        JLabel lblHeader1 = new JLabel("--- ITEM DATABASE INFO ---");
        lblHeader1.setFont(AppConstants.metropolisBold);
        GridBagConstraints gbc_h1 = new GridBagConstraints();
        gbc_h1.gridx = 0; gbc_h1.gridy = 0; gbc_h1.gridwidth = 2; gbc_h1.insets = new Insets(5, 5, 10, 5);
        popupPanel.add(lblHeader1, gbc_h1);

        GridBagConstraints gbc_ln = new GridBagConstraints(); gbc_ln.gridx = 0; gbc_ln.gridy = 1; gbc_ln.anchor = GridBagConstraints.EAST; gbc_ln.insets = new Insets(2,5,2,5);
        popupPanel.add(new JLabel("Name:"), gbc_ln);
        JTextField txtName = new JTextField(dbName, 15); txtName.setEditable(isAdmin);
        GridBagConstraints gbc_tn = new GridBagConstraints(); gbc_tn.gridx = 1; gbc_tn.gridy = 1; gbc_tn.fill = GridBagConstraints.HORIZONTAL; gbc_tn.insets = new Insets(2,5,2,5);
        popupPanel.add(txtName, gbc_tn);

        GridBagConstraints gbc_lc = new GridBagConstraints(); gbc_lc.gridx = 0; gbc_lc.gridy = 2; gbc_lc.anchor = GridBagConstraints.EAST; gbc_lc.insets = new Insets(2,5,2,5);
        popupPanel.add(new JLabel("Category:"), gbc_lc);
        JComboBox<String> cbCat = new JComboBox<>(new String[]{"Electronics", "IDs/Documents", "Bags", "Clothing", "Valuables"});
        cbCat.setSelectedItem(dbCat); cbCat.setEnabled(isAdmin);
        GridBagConstraints gbc_tc = new GridBagConstraints(); gbc_tc.gridx = 1; gbc_tc.gridy = 2; gbc_tc.fill = GridBagConstraints.HORIZONTAL; gbc_tc.insets = new Insets(2,5,2,5);
        popupPanel.add(cbCat, gbc_tc);

        GridBagConstraints gbc_ll = new GridBagConstraints(); gbc_ll.gridx = 0; gbc_ll.gridy = 3; gbc_ll.anchor = GridBagConstraints.EAST; gbc_ll.insets = new Insets(2,5,2,5);
        popupPanel.add(new JLabel("Location:"), gbc_ll);
        JTextField txtLoc = new JTextField(dbLoc, 15); txtLoc.setEditable(isAdmin);
        GridBagConstraints gbc_tl = new GridBagConstraints(); gbc_tl.gridx = 1; gbc_tl.gridy = 3; gbc_tl.fill = GridBagConstraints.HORIZONTAL; gbc_tl.insets = new Insets(2,5,2,5);
        popupPanel.add(txtLoc, gbc_tl);

        GridBagConstraints gbc_ld = new GridBagConstraints(); gbc_ld.gridx = 0; gbc_ld.gridy = 4; gbc_ld.anchor = GridBagConstraints.EAST; gbc_ld.insets = new Insets(2,5,2,5);
        popupPanel.add(new JLabel("Date (YYYY-MM-DD):"), gbc_ld);
        JTextField txtDate = new JTextField(dbDate, 15); txtDate.setEditable(isAdmin);
        GridBagConstraints gbc_td = new GridBagConstraints(); gbc_td.gridx = 1; gbc_td.gridy = 4; gbc_td.fill = GridBagConstraints.HORIZONTAL; gbc_td.insets = new Insets(2,5,2,5);
        popupPanel.add(txtDate, gbc_td);

        GridBagConstraints gbc_lsn = new GridBagConstraints(); gbc_lsn.gridx = 0; gbc_lsn.gridy = 5; gbc_lsn.anchor = GridBagConstraints.EAST; gbc_lsn.insets = new Insets(2,5,2,5);
        popupPanel.add(new JLabel("Sender Name:"), gbc_lsn);
        JTextField txtSenderN = new JTextField(dbSenderN, 15); txtSenderN.setEditable(isAdmin);
        GridBagConstraints gbc_tsn = new GridBagConstraints(); gbc_tsn.gridx = 1; gbc_tsn.gridy = 5; gbc_tsn.fill = GridBagConstraints.HORIZONTAL; gbc_tsn.insets = new Insets(2,5,2,5);
        popupPanel.add(txtSenderN, gbc_tsn);

        GridBagConstraints gbc_lsid = new GridBagConstraints(); gbc_lsid.gridx = 0; gbc_lsid.gridy = 6; gbc_lsid.anchor = GridBagConstraints.EAST; gbc_lsid.insets = new Insets(2,5,2,5);
        popupPanel.add(new JLabel("Sender ID:"), gbc_lsid);
        JTextField txtSenderID = new JTextField(dbSenderID, 15); txtSenderID.setEditable(isAdmin);
        GridBagConstraints gbc_tsid = new GridBagConstraints(); gbc_tsid.gridx = 1; gbc_tsid.gridy = 6; gbc_tsid.fill = GridBagConstraints.HORIZONTAL; gbc_tsid.insets = new Insets(2,5,2,5);
        popupPanel.add(txtSenderID, gbc_tsid);

        JTextField txtClaimantName = new JTextField(15); 
        JTextField txtClaimantID = new JTextField(15); 

        // --- Section 2: Claim Details (Only added if the item is CLAIMED) ---
        if (currentStatus.equals("CLAIMED")) {
            JLabel lblHeader2 = new JLabel("--- CLAIM DATABASE INFO ---");
            lblHeader2.setFont(AppConstants.metropolisBold);
            GridBagConstraints gbc_h2 = new GridBagConstraints();
            gbc_h2.gridx = 0; gbc_h2.gridy = 7; gbc_h2.gridwidth = 2; gbc_h2.insets = new Insets(15, 5, 10, 5);
            popupPanel.add(lblHeader2, gbc_h2);

            GridBagConstraints gbc_lcn = new GridBagConstraints(); gbc_lcn.gridx = 0; gbc_lcn.gridy = 8; gbc_lcn.anchor = GridBagConstraints.EAST; gbc_lcn.insets = new Insets(2,5,2,5);
            popupPanel.add(new JLabel("Claimant Name:"), gbc_lcn);
            txtClaimantName.setText(dbClaimantN); txtClaimantName.setEditable(isAdmin);
            GridBagConstraints gbc_tcn = new GridBagConstraints(); gbc_tcn.gridx = 1; gbc_tcn.gridy = 8; gbc_tcn.fill = GridBagConstraints.HORIZONTAL; gbc_tcn.insets = new Insets(2,5,2,5);
            popupPanel.add(txtClaimantName, gbc_tcn);

            GridBagConstraints gbc_lcid = new GridBagConstraints(); gbc_lcid.gridx = 0; gbc_lcid.gridy = 9; gbc_lcid.anchor = GridBagConstraints.EAST; gbc_lcid.insets = new Insets(2,5,2,5);
            popupPanel.add(new JLabel("Claimant ID:"), gbc_lcid);
            txtClaimantID.setText(dbClaimantID); txtClaimantID.setEditable(isAdmin);
            GridBagConstraints gbc_tcid = new GridBagConstraints(); gbc_tcid.gridx = 1; gbc_tcid.gridy = 9; gbc_tcid.fill = GridBagConstraints.HORIZONTAL; gbc_tcid.insets = new Insets(2,5,2,5);
            popupPanel.add(txtClaimantID, gbc_tcid);

            GridBagConstraints gbc_ldc = new GridBagConstraints(); gbc_ldc.gridx = 0; gbc_ldc.gridy = 10; gbc_ldc.anchor = GridBagConstraints.EAST; gbc_ldc.insets = new Insets(2,5,2,5);
            popupPanel.add(new JLabel("Date Claimed:"), gbc_ldc);
            JLabel lblDateClaimedVal = new JLabel("<html><b>" + dbDateClaimed + "</b></html>");
            GridBagConstraints gbc_tdc = new GridBagConstraints(); gbc_tdc.gridx = 1; gbc_tdc.gridy = 10; gbc_tdc.anchor = GridBagConstraints.WEST; gbc_tdc.insets = new Insets(2,5,2,5);
            popupPanel.add(lblDateClaimedVal, gbc_tdc);
        }

        // 4. Determine Buttons Based on Role and Status
        Object[] options;
        if (currentStatus.equals("UNCLAIMED")) {
            if (isAdmin) options = new Object[]{"Process Claim", "Save Item Changes", "Cancel"};
            else options = new Object[]{"Process Claim", "Cancel"};
        } else {
            if (isAdmin) options = new Object[]{"Save All Changes", "Undo Claim", "Cancel"};
            else options = new Object[]{"Close"};
        }

        // 5. Show Dialog
        int choice = JOptionPane.showOptionDialog(this, popupPanel, "Manage Item #" + itemID, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        // 6. Handle Interaction Logic
     // 6. Handle Interaction Logic
        try (Connection c = DriverManager.getConnection(AppConstants.DB_URL, AppConstants.DB_USER, AppConstants.DB_PASS)) {
            String selectedOption = (choice >= 0 && choice < options.length) ? options[choice].toString() : "Cancel";

            if (selectedOption.equals("Process Claim")) {
                // Route to the standard claim processor screen
                mainController.getProcessClaimPanel().loadItemData(itemID, dbName, dbSenderN, dbSenderID);
                mainController.switchToCard("PROCESS_CLAIM");

            } else if (selectedOption.equals("Save Item Changes")) {
                // Admin updating an UNCLAIMED item
                PreparedStatement s = c.prepareStatement("UPDATE items SET item_name=?, category=?, location_found=?, date_found=?, sender_name=?, sender_id=? WHERE item_id=?");
                s.setString(1, txtName.getText()); s.setString(2, cbCat.getSelectedItem().toString()); s.setString(3, txtLoc.getText());
                s.setDate(4, java.sql.Date.valueOf(txtDate.getText())); s.setString(5, txtSenderN.getText()); s.setString(6, txtSenderID.getText()); s.setInt(7, Integer.parseInt(itemID));
                s.executeUpdate();
                
                // --- AUDIT LOG ADDED HERE ---
                // Records that an Admin updated the basic details of an unclaimed item.
                AuditController.logAction(Session.currentUser, "UPDATE", Integer.parseInt(itemID), "Updated details for unclaimed item: " + txtName.getText());
                
                JOptionPane.showMessageDialog(this, "Item changes saved!");
                refreshData();

            } else if (selectedOption.equals("Save All Changes")) {
                // Admin updating a CLAIMED item (Updates both tables)
                c.setAutoCommit(false);
                try {
                    PreparedStatement s1 = c.prepareStatement("UPDATE items SET item_name=?, category=?, location_found=?, date_found=?, sender_name=?, sender_id=? WHERE item_id=?");
                    s1.setString(1, txtName.getText()); s1.setString(2, cbCat.getSelectedItem().toString()); s1.setString(3, txtLoc.getText());
                    s1.setDate(4, java.sql.Date.valueOf(txtDate.getText())); s1.setString(5, txtSenderN.getText()); s1.setString(6, txtSenderID.getText()); s1.setInt(7, Integer.parseInt(itemID));
                    s1.executeUpdate();

                    PreparedStatement s2 = c.prepareStatement("UPDATE claims SET claimant_name=?, student_id=? WHERE item_id=?");
                    s2.setString(1, txtClaimantName.getText()); s2.setString(2, txtClaimantID.getText()); s2.setInt(3, Integer.parseInt(itemID));
                    s2.executeUpdate();

                    c.commit();
                    
                    // --- AUDIT LOG ADDED HERE ---
                    // Records the manual correction of a claim record (High importance for accountability).
                    AuditController.logAction(Session.currentUser, "UPDATE", Integer.parseInt(itemID), "Modified existing claim record for: " + txtClaimantName.getText());
                    
                    JOptionPane.showMessageDialog(this, "Both Item and Claim details updated!");
                    refreshData();
                } catch (SQLException ex) { c.rollback(); throw ex; }

            } else if (selectedOption.equals("Undo Claim")) {
                // Admin removing a claim
                if (JOptionPane.showConfirmDialog(this, "Cancel this claim? Item will return to inventory.", "Confirm Undo", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    c.setAutoCommit(false);
                    try {
                        PreparedStatement d = c.prepareStatement("DELETE FROM claims WHERE item_id=?"); d.setInt(1, Integer.parseInt(itemID)); d.executeUpdate();
                        PreparedStatement u = c.prepareStatement("UPDATE items SET status='UNCLAIMED' WHERE item_id=?"); u.setInt(1, Integer.parseInt(itemID)); u.executeUpdate();
                        c.commit();
                        
                        // --- AUDIT LOG ADDED HERE ---
                        // Tracking an 'UNDO' is critical because it changes the status of a released item back to inventory.
                        AuditController.logAction(Session.currentUser, "UNDO", Integer.parseInt(itemID), "Reversed claim. Item returned to UNCLAIMED status.");
                        
                        JOptionPane.showMessageDialog(this, "Claim undone. Item returned to inventory.");
                        refreshData();
                    } catch (SQLException ex) { c.rollback(); throw ex; }
                }
            }
            
        }  catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Date Format! Use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Database Error.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}