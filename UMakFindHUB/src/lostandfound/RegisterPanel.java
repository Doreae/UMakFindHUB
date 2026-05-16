package lostandfound;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterPanel extends JPanel {
    private MainFrame mainController;
    private Image mainBgImage;

    public RegisterPanel(MainFrame mainController) {
        this.mainController = mainController;
        this.mainBgImage = new ImageIcon("src/lostandfound/images/bg.png").getImage();
        
        setLayout(new GridBagLayout());
        setBackground(new Color(21, 35, 75)); // Fallback color

        JPanel formBox = new JPanel(new GridBagLayout());
        formBox.setBackground(new Color(133, 179, 235, 230)); 
        formBox.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        formBox.setOpaque(true);

        // --- 1. TITLE ---
        JLabel lblTitle = new JLabel("Register New Found Item", SwingConstants.CENTER);
        lblTitle.setFont(AppConstants.marcellusHeader.deriveFont(42f));
        lblTitle.setForeground(Color.BLACK);
        GridBagConstraints gbcTitle = new GridBagConstraints();
        gbcTitle.fill = GridBagConstraints.HORIZONTAL;
        gbcTitle.insets = new Insets(10, 10, 40, 10);
        gbcTitle.gridx = 0; gbcTitle.gridy = 0; gbcTitle.gridwidth = 2;
        formBox.add(lblTitle, gbcTitle);

        // --- 2. ITEM NAME ---
        JLabel lblItem = new JLabel("Item Name:");
        lblItem.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbcItemLbl = new GridBagConstraints();
        gbcItemLbl.fill = GridBagConstraints.HORIZONTAL;
        gbcItemLbl.insets = new Insets(10, 10, 10, 10);
        gbcItemLbl.gridx = 0; gbcItemLbl.gridy = 1;
        formBox.add(lblItem, gbcItemLbl);

        JTextField txtItemName = new JTextField(20);
        txtItemName.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbcItemTxt = new GridBagConstraints();
        gbcItemTxt.fill = GridBagConstraints.HORIZONTAL;
        gbcItemTxt.insets = new Insets(10, 10, 10, 10);
        gbcItemTxt.gridx = 1; gbcItemTxt.gridy = 1;
        formBox.add(txtItemName, gbcItemTxt);

        // --- 3. CATEGORY ---
        JLabel lblCat = new JLabel("Category:");
        lblCat.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbcCatLbl = new GridBagConstraints();
        gbcCatLbl.fill = GridBagConstraints.HORIZONTAL;
        gbcCatLbl.insets = new Insets(10, 10, 10, 10);
        gbcCatLbl.gridx = 0; gbcCatLbl.gridy = 2;
        formBox.add(lblCat, gbcCatLbl);

        String[] categories = {"Electronics", "IDs/Documents", "Bags", "Clothing", "Valuables"};
        JComboBox<String> cbCategory = new JComboBox<>(categories);
        cbCategory.setFont(AppConstants.metropolisBody);
        cbCategory.setBackground(Color.WHITE);
        GridBagConstraints gbcCatCb = new GridBagConstraints();
        gbcCatCb.fill = GridBagConstraints.HORIZONTAL;
        gbcCatCb.insets = new Insets(10, 10, 10, 10);
        gbcCatCb.gridx = 1; gbcCatCb.gridy = 2;
        formBox.add(cbCategory, gbcCatCb);

        // --- 4. LOCATION ---
        JLabel lblLoc = new JLabel("Location Found:");
        lblLoc.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbcLocLbl = new GridBagConstraints();
        gbcLocLbl.fill = GridBagConstraints.HORIZONTAL;
        gbcLocLbl.insets = new Insets(10, 10, 10, 10);
        gbcLocLbl.gridx = 0; gbcLocLbl.gridy = 3;
        formBox.add(lblLoc, gbcLocLbl);

        JTextField txtLocation = new JTextField(20);
        txtLocation.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbcLocTxt = new GridBagConstraints();
        gbcLocTxt.fill = GridBagConstraints.HORIZONTAL;
        gbcLocTxt.insets = new Insets(10, 10, 10, 10);
        gbcLocTxt.gridx = 1; gbcLocTxt.gridy = 3;
        formBox.add(txtLocation, gbcLocTxt);

        // --- 5. DATE ---
        JLabel lblDate = new JLabel("Date Found (YYYY-MM-DD):");
        lblDate.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbcDateLbl = new GridBagConstraints();
        gbcDateLbl.fill = GridBagConstraints.HORIZONTAL;
        gbcDateLbl.insets = new Insets(10, 10, 10, 10);
        gbcDateLbl.gridx = 0; gbcDateLbl.gridy = 4;
        formBox.add(lblDate, gbcDateLbl);

        JTextField txtDate = new JTextField(20);
        txtDate.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbcDateTxt = new GridBagConstraints();
        gbcDateTxt.fill = GridBagConstraints.HORIZONTAL;
        gbcDateTxt.insets = new Insets(10, 10, 10, 10);
        gbcDateTxt.gridx = 1; gbcDateTxt.gridy = 4;
        formBox.add(txtDate, gbcDateTxt);

        // --- 6. SENDER NAME (Restored!) ---
        JLabel lblSenderName = new JLabel("Found By (Sender Name):");
        lblSenderName.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbcSenderNameLbl = new GridBagConstraints();
        gbcSenderNameLbl.fill = GridBagConstraints.HORIZONTAL;
        gbcSenderNameLbl.insets = new Insets(10, 10, 10, 10);
        gbcSenderNameLbl.gridx = 0; gbcSenderNameLbl.gridy = 5;
        formBox.add(lblSenderName, gbcSenderNameLbl);

        JTextField txtSenderName = new JTextField(20);
        txtSenderName.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbcSenderNameTxt = new GridBagConstraints();
        gbcSenderNameTxt.fill = GridBagConstraints.HORIZONTAL;
        gbcSenderNameTxt.insets = new Insets(10, 10, 10, 10);
        gbcSenderNameTxt.gridx = 1; gbcSenderNameTxt.gridy = 5;
        formBox.add(txtSenderName, gbcSenderNameTxt);

     // --- 7. SENDER ID (Restored!) ---
        JLabel lblSenderID = new JLabel("Sender's ID:");
        lblSenderID.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbcSenderIDLbl = new GridBagConstraints();
        gbcSenderIDLbl.fill = GridBagConstraints.HORIZONTAL;
        gbcSenderIDLbl.insets = new Insets(10, 10, 10, 10);
        gbcSenderIDLbl.gridx = 0; gbcSenderIDLbl.gridy = 6;
        formBox.add(lblSenderID, gbcSenderIDLbl);

        JTextField txtSenderID = new JTextField(20);
        txtSenderID.setFont(AppConstants.metropolisBody);
        GridBagConstraints gbcSenderIDTxt = new GridBagConstraints();
        gbcSenderIDTxt.fill = GridBagConstraints.HORIZONTAL;
        gbcSenderIDTxt.insets = new Insets(10, 10, 10, 10);
        gbcSenderIDTxt.gridx = 1; gbcSenderIDTxt.gridy = 6;
        formBox.add(txtSenderID, gbcSenderIDTxt);
        
        ((javax.swing.text.AbstractDocument) txtSenderID.getDocument()).setDocumentFilter(new javax.swing.text.DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
                // Check what the string will look like after they type
                int currentLength = fb.getDocument().getLength();
                int projectedLength = currentLength - length + text.length();
                
                // Only allow letters and numbers, and cap it at 10 characters maximum
                if (projectedLength <= 10 && text.matches("[a-zA-Z0-9]*")) {
                    // super.replace applies the text, and .toUpperCase() makes it look clean!
                    super.replace(fb, offset, length, text.toUpperCase(), attrs);
                } else {
                    // Play a little error sound if they try to type symbols or go over 10 chars
                    Toolkit.getDefaultToolkit().beep(); 
                }
            }
        });
        
        // --- 8. SAVE BUTTON ---
        JButton btnSave = new JButton("SAVE");
        btnSave.setBackground(new Color(255, 240, 50));
        btnSave.setForeground(Color.BLACK);
        btnSave.setFont(AppConstants.marcellusHeader.deriveFont(28f));
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.setMargin(new Insets(10, 40, 10, 40));

        GridBagConstraints gbcSaveBtn = new GridBagConstraints();
        gbcSaveBtn.fill = GridBagConstraints.NONE;
        gbcSaveBtn.anchor = GridBagConstraints.CENTER;
        gbcSaveBtn.insets = new Insets(30, 10, 10, 10);
        gbcSaveBtn.gridx = 0; gbcSaveBtn.gridy = 7; gbcSaveBtn.gridwidth = 2;
        formBox.add(btnSave, gbcSaveBtn);

        // Add form box to this panel
        GridBagConstraints gbcForm = new GridBagConstraints();
        gbcForm.gridx = 0; gbcForm.gridy = 0;
        add(formBox, gbcForm);

        // --- DATABASE LOGIC (Restored with Sender fields!) ---
        btnSave.addActionListener(e -> {
            String name = txtItemName.getText();
            String category = cbCategory.getSelectedItem().toString();
            String location = txtLocation.getText();
            String dateStr = txtDate.getText();
            String senderName = txtSenderName.getText();
            String senderId = txtSenderID.getText().trim();
            
            
            if(name.isEmpty() || location.isEmpty() || dateStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }


            if (!senderId.matches("^[A-Za-z0-9]{1,9}$")) {
                JOptionPane.showMessageDialog(
                    this,
                    "Invalid ID Format! The Sender ID must be letters and/or digits, up to 9 characters.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
         
            try (Connection conn = DriverManager.getConnection(AppConstants.DB_URL, AppConstants.DB_USER, AppConstants.DB_PASS);
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO items (item_name, category, location_found, date_found, status, sender_name, sender_id) VALUES (?, ?, ?, ?, 'UNCLAIMED', ?, ?)")) {
                
                stmt.setString(1, name);
                stmt.setString(2, category);
                stmt.setString(3, location);
                stmt.setDate(4, java.sql.Date.valueOf(dateStr)); 
                stmt.setString(5, senderName); 
                stmt.setString(6, senderId);   
                
                stmt.executeUpdate();
                
                AuditController.logAction(Session.currentUser, "REGISTER", 0, "Registered a new " + category + ": " + name);
                
                JOptionPane.showMessageDialog(this, "Item '" + name + "' added successfully!");
                txtItemName.setText(""); txtLocation.setText(""); txtDate.setText("");
                txtSenderName.setText(""); txtSenderID.setText("");
                
                if (mainController != null) {
                    mainController.getInventoryPanel().refreshData();
                }

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Date Format. Please use YYYY-MM-DD.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
        if (mainBgImage != null) g.drawImage(mainBgImage, 0, 0, getWidth(), getHeight(), this);
    }
}