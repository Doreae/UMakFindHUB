package lostandfound;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ProcessClaimPanel extends JPanel {
    private MainFrame mainController;
    private JTextField txtID, txtName, txtSenderName, txtSenderID, txtClaimantID, txtClaimantName;

    public ProcessClaimPanel(MainFrame mainController) {
        this.mainController = mainController;
        setLayout(new GridBagLayout());
        setBackground(new Color(21, 35, 75));

        JPanel formBox = new JPanel(new GridBagLayout());
        formBox.setBackground(new Color(133, 179, 235, 230)); 
        formBox.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        formBox.setOpaque(true);

        // 1. Title
        JLabel lblTitle = new JLabel("Process Item Claim", SwingConstants.CENTER);
        lblTitle.setFont(AppConstants.marcellusHeader.deriveFont(32f));
        GridBagConstraints gbc_title = new GridBagConstraints();
        gbc_title.gridx = 0; gbc_title.gridy = 0; gbc_title.gridwidth = 2;
        gbc_title.insets = new Insets(10, 10, 40, 10);
        formBox.add(lblTitle, gbc_title);

        // 2. ID (Read Only)
        JLabel lblID = new JLabel("Item ID:");
        lblID.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbc_lblID = new GridBagConstraints();
        gbc_lblID.gridx = 0; gbc_lblID.gridy = 1; gbc_lblID.insets = new Insets(10, 10, 10, 10);
        formBox.add(lblID, gbc_lblID);

        txtID = new JTextField(20); txtID.setEditable(false);
        txtID.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbc_txtID = new GridBagConstraints();
        gbc_txtID.gridx = 1; gbc_txtID.gridy = 1; gbc_txtID.insets = new Insets(10, 10, 10, 10);
        formBox.add(txtID, gbc_txtID);

        // 3. Name (Read Only)
        JLabel lblName = new JLabel("Item Name:");
        lblName.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbc_lblName = new GridBagConstraints();
        gbc_lblName.gridx = 0; gbc_lblName.gridy = 2; gbc_lblName.insets = new Insets(10, 10, 10, 10);
        formBox.add(lblName, gbc_lblName);

        txtName = new JTextField(20); txtName.setEditable(false);
        txtName.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbc_txtName = new GridBagConstraints();
        gbc_txtName.gridx = 1; gbc_txtName.gridy = 2; gbc_txtName.insets = new Insets(10, 10, 10, 10);
        formBox.add(txtName, gbc_txtName);

        // 4. Sender Name (Read Only)
        JLabel lblSenderN = new JLabel("Found By (Sender):");
        lblSenderN.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbc_lblSenderN = new GridBagConstraints();
        gbc_lblSenderN.gridx = 0; gbc_lblSenderN.gridy = 3; gbc_lblSenderN.insets = new Insets(10, 10, 10, 10);
        formBox.add(lblSenderN, gbc_lblSenderN);

        txtSenderName = new JTextField(20); txtSenderName.setEditable(false);
        txtSenderName.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbc_txtSenderN = new GridBagConstraints();
        gbc_txtSenderN.gridx = 1; gbc_txtSenderN.gridy = 3; gbc_txtSenderN.insets = new Insets(10, 10, 10, 10);
        formBox.add(txtSenderName, gbc_txtSenderN);

        // 5. Sender ID (Read Only)
        JLabel lblSenderID = new JLabel("Sender ID:");
        lblSenderID.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbc_lblSenderID = new GridBagConstraints();
        gbc_lblSenderID.gridx = 0; gbc_lblSenderID.gridy = 4; gbc_lblSenderID.insets = new Insets(10, 10, 10, 10);
        formBox.add(lblSenderID, gbc_lblSenderID);

        txtSenderID = new JTextField(20); txtSenderID.setEditable(false);
        txtSenderID.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbc_txtSenderID = new GridBagConstraints();
        gbc_txtSenderID.gridx = 1; gbc_txtSenderID.gridy = 4; gbc_txtSenderID.insets = new Insets(10, 10, 10, 10);
        formBox.add(txtSenderID, gbc_txtSenderID);

        // 6. Claimant ID
        JLabel lblCID = new JLabel("Claimant ID:");
        lblCID.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbc_lblCID = new GridBagConstraints();
        gbc_lblCID.gridx = 0; gbc_lblCID.gridy = 5; gbc_lblCID.insets = new Insets(10, 10, 10, 10);
        formBox.add(lblCID, gbc_lblCID);

        txtClaimantID = new JTextField(20);
        txtClaimantID.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbc_txtCID = new GridBagConstraints();
        gbc_txtCID.gridx = 1; gbc_txtCID.gridy = 5; gbc_txtCID.insets = new Insets(10, 10, 10, 10);
        formBox.add(txtClaimantID, gbc_txtCID);

        // 7. Claimant Name
        JLabel lblCName = new JLabel("Claimant Name:");
        lblCName.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbc_lblCName = new GridBagConstraints();
        gbc_lblCName.gridx = 0; gbc_lblCName.gridy = 6; gbc_lblCName.insets = new Insets(10, 10, 10, 10);
        formBox.add(lblCName, gbc_lblCName);

        txtClaimantName = new JTextField(20);
        txtClaimantName.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbc_txtCName = new GridBagConstraints();
        gbc_txtCName.gridx = 1; gbc_txtCName.gridy = 6; gbc_txtCName.insets = new Insets(10, 10, 10, 10);
        formBox.add(txtClaimantName, gbc_txtCName);

        // 8. Button
        JButton btnConfirm = new JButton("RELEASE ITEM");
        btnConfirm.setBackground(new Color(255, 240, 50)); 
        btnConfirm.setFont(AppConstants.metropolisBold.deriveFont(18f));
        btnConfirm.setFocusPainted(false);
        GridBagConstraints gbc_btnConfirm = new GridBagConstraints();
        gbc_btnConfirm.gridx = 0; gbc_btnConfirm.gridy = 7; gbc_btnConfirm.gridwidth = 2;
        gbc_btnConfirm.insets = new Insets(30, 10, 10, 10);
        formBox.add(btnConfirm, gbc_btnConfirm);

        GridBagConstraints gbc_main = new GridBagConstraints();
        gbc_main.gridx = 0; gbc_main.gridy = 0;
        add(formBox, gbc_main);

        // Database logic
        btnConfirm.addActionListener(e -> {
            if(txtClaimantID.getText().isEmpty() || txtClaimantName.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Student ID and Name are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DriverManager.getConnection(AppConstants.DB_URL, AppConstants.DB_USER, AppConstants.DB_PASS)) {
                conn.setAutoCommit(false);
                PreparedStatement iStmt = conn.prepareStatement("INSERT INTO claims (item_id, claimant_name, student_id, date_claimed) VALUES (?,?,?, CURDATE())");
                iStmt.setString(1, txtID.getText()); 
                iStmt.setString(2, txtClaimantName.getText()); 
                iStmt.setString(3, txtClaimantID.getText());
                iStmt.executeUpdate();

                PreparedStatement uStmt = conn.prepareStatement("UPDATE items SET status='CLAIMED' WHERE item_id=?");
                uStmt.setString(1, txtID.getText()); 
                uStmt.executeUpdate();
                
                conn.commit();
                
                JOptionPane.showMessageDialog(this, "Item successfully released!");
                mainController.getInventoryPanel().refreshData();
                if (mainController.getDashboardPanel() != null) mainController.getDashboardPanel().refreshData();
                mainController.switchToCard("INVENTORY");
            } catch (Exception ex) { 
                ex.printStackTrace(); 
                JOptionPane.showMessageDialog(this, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // ==========================================================
    // THIS IS THE FIX! This method populates the UI fields
    // ==========================================================
    public void loadItemData(String itemID, String dbName, String dbSenderN, String dbSenderID) {
        txtID.setText(itemID);
        txtName.setText(dbName);
        txtSenderName.setText(dbSenderN);
        txtSenderID.setText(dbSenderID);
        
        // Clear claimant fields from the previous person
        txtClaimantID.setText("");
        txtClaimantName.setText("");
    }
}