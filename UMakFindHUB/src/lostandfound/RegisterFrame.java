package lostandfound;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class RegisterFrame extends JFrame {

    public RegisterFrame() {
        AppConstants.initFonts(); // Load custom fonts
        
        setTitle("UMak Security - Register Account");
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 1. HEADER ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        headerPanel.setBackground(new Color(133, 179, 235));
        headerPanel.add(new JLabel("<html><div style='text-align: center; color: #1a1a1a;'><span style='font-size: 26px; font-family: Marcellus;'>University Of Makati</span><br><span style='font-size: 14px; font-family: Metropolis;'>Lost & Found Inventory System</span></div></html>"));
        add(headerPanel, BorderLayout.NORTH);

        // --- 2. BACKGROUND PANEL ---
        Image mainBgImage = new ImageIcon("src/lostandfound/images/bg.png").getImage();
        JPanel bgPanel = new JPanel(new GridBagLayout()) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (mainBgImage != null) g.drawImage(mainBgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        bgPanel.setBackground(new Color(21, 35, 75));

        // --- 3. FROSTED GLASS BOX (Fixed Padding!) ---
        JPanel registerBox = new JPanel(new GridBagLayout());
        registerBox.setBackground(new Color(133, 179, 235, 180));
        // REDUCED PADDING to make the box tighter around the text fields
        registerBox.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30)); 
        registerBox.setOpaque(true);

        // ==========================================
        // WINDOWBUILDER SAFE UI COMPONENTS
        // ==========================================
        
        // Title
        JLabel lblTitle = new JLabel("CREATE ACCOUNT", SwingConstants.CENTER);
        lblTitle.setFont(AppConstants.marcellusHeader.deriveFont(32f));
        GridBagConstraints gbc_title = new GridBagConstraints();
        gbc_title.gridx = 0; gbc_title.gridy = 0; gbc_title.gridwidth = 2;
        gbc_title.insets = new Insets(10, 10, 20, 10);
        registerBox.add(lblTitle, gbc_title);

        // Username
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(AppConstants.metropolisBody.deriveFont(16f));
        GridBagConstraints gbc_lblUser = new GridBagConstraints();
        gbc_lblUser.gridx = 0; gbc_lblUser.gridy = 1; gbc_lblUser.anchor = GridBagConstraints.EAST;
        gbc_lblUser.insets = new Insets(10, 10, 10, 10);
        registerBox.add(lblUser, gbc_lblUser);

        JTextField txtUser = new JTextField(15);
        txtUser.setFont(AppConstants.metropolisBody.deriveFont(16f));
        GridBagConstraints gbc_txtUser = new GridBagConstraints();
        gbc_txtUser.gridx = 1; gbc_txtUser.gridy = 1;
        gbc_txtUser.fill = GridBagConstraints.HORIZONTAL; // FIX: Make it stretch!
        gbc_txtUser.insets = new Insets(10, 10, 10, 10);
        registerBox.add(txtUser, gbc_txtUser);

        // Password
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(AppConstants.metropolisBody.deriveFont(16f));
        GridBagConstraints gbc_lblPass = new GridBagConstraints();
        gbc_lblPass.gridx = 0; gbc_lblPass.gridy = 2; gbc_lblPass.anchor = GridBagConstraints.EAST;
        gbc_lblPass.insets = new Insets(10, 10, 10, 10);
        registerBox.add(lblPass, gbc_lblPass);

        JPasswordField txtPass = new JPasswordField(15);
        txtPass.setFont(AppConstants.metropolisBody.deriveFont(16f));
        GridBagConstraints gbc_txtPass = new GridBagConstraints();
        gbc_txtPass.gridx = 1; gbc_txtPass.gridy = 2;
        gbc_txtPass.fill = GridBagConstraints.HORIZONTAL; // FIX: Make it stretch!
        gbc_txtPass.insets = new Insets(10, 10, 10, 10);
        registerBox.add(txtPass, gbc_txtPass);

        // Confirm Password
        JLabel lblConfirmPass = new JLabel("Confirm Password:");
        lblConfirmPass.setFont(AppConstants.metropolisBody.deriveFont(16f));
        GridBagConstraints gbc_lblConfirmPass = new GridBagConstraints();
        gbc_lblConfirmPass.gridx = 0; gbc_lblConfirmPass.gridy = 3; gbc_lblConfirmPass.anchor = GridBagConstraints.EAST;
        gbc_lblConfirmPass.insets = new Insets(10, 10, 10, 10);
        registerBox.add(lblConfirmPass, gbc_lblConfirmPass);

        JPasswordField txtConfirmPass = new JPasswordField(15);
        txtConfirmPass.setFont(AppConstants.metropolisBody.deriveFont(16f));
        GridBagConstraints gbc_txtConfirmPass = new GridBagConstraints();
        gbc_txtConfirmPass.gridx = 1; gbc_txtConfirmPass.gridy = 3;
        gbc_txtConfirmPass.fill = GridBagConstraints.HORIZONTAL; // FIX: Make it stretch!
        gbc_txtConfirmPass.insets = new Insets(10, 10, 10, 10);
        registerBox.add(txtConfirmPass, gbc_txtConfirmPass);

        // Role Dropdown
        JLabel lblRole = new JLabel("Account Role:");
        lblRole.setFont(AppConstants.metropolisBody.deriveFont(16f));
        GridBagConstraints gbc_lblRole = new GridBagConstraints();
        gbc_lblRole.gridx = 0; gbc_lblRole.gridy = 4; gbc_lblRole.anchor = GridBagConstraints.EAST;
        gbc_lblRole.insets = new Insets(10, 10, 10, 10);
        registerBox.add(lblRole, gbc_lblRole);

        JComboBox<String> cbRole = new JComboBox<>(new String[]{"Staff", "Admin"});
        cbRole.setFont(AppConstants.metropolisBody.deriveFont(16f));
        GridBagConstraints gbc_cbRole = new GridBagConstraints();
        gbc_cbRole.gridx = 1; gbc_cbRole.gridy = 4; gbc_cbRole.fill = GridBagConstraints.HORIZONTAL;
        gbc_cbRole.insets = new Insets(10, 10, 10, 10);
        registerBox.add(cbRole, gbc_cbRole);

        // Show Password Checkbox
        JCheckBox chkShowPass = new JCheckBox("Show Passwords");
        chkShowPass.setFont(AppConstants.metropolisBody.deriveFont(12f));
        chkShowPass.setOpaque(false); 
        chkShowPass.setFocusPainted(false);
        GridBagConstraints gbc_chkShowPass = new GridBagConstraints();
        gbc_chkShowPass.gridx = 1; gbc_chkShowPass.gridy = 5; 
        gbc_chkShowPass.anchor = GridBagConstraints.WEST; 
        gbc_chkShowPass.insets = new Insets(0, 10, 10, 10);
        registerBox.add(chkShowPass, gbc_chkShowPass);

        chkShowPass.addActionListener(e -> {
            char echo = chkShowPass.isSelected() ? (char) 0 : '•';
            txtPass.setEchoChar(echo);
            txtConfirmPass.setEchoChar(echo);
        });

        // Register Button
        JButton btnRegister = new JButton("REGISTER");
        btnRegister.setFont(AppConstants.metropolisBold.deriveFont(18f));
        btnRegister.setBackground(new Color(46, 204, 113)); 
        btnRegister.setForeground(Color.BLACK);
        GridBagConstraints gbc_btnRegister = new GridBagConstraints();
        gbc_btnRegister.gridx = 0; gbc_btnRegister.gridy = 6; 
        gbc_btnRegister.gridwidth = 2;
        gbc_btnRegister.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnRegister.insets = new Insets(20, 10, 10, 10);
        registerBox.add(btnRegister, gbc_btnRegister);

        // Back to Login Button
        JButton btnBack = new JButton("Back to Login");
        btnBack.setFont(AppConstants.metropolisBold.deriveFont(14f));
        btnBack.setBackground(new Color(200, 200, 200));
        GridBagConstraints gbc_btnBack = new GridBagConstraints();
        gbc_btnBack.gridx = 0; gbc_btnBack.gridy = 7; 
        gbc_btnBack.gridwidth = 2;
        gbc_btnBack.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnBack.insets = new Insets(5, 10, 10, 10);
        registerBox.add(btnBack, gbc_btnBack);

        bgPanel.add(registerBox);
        add(bgPanel, BorderLayout.CENTER);

        // ==========================================
        // DATABASE LOGIC
        // ==========================================

        btnBack.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        btnRegister.addActionListener(e -> {
            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword());
            String confirmPass = new String(txtConfirmPass.getPassword());
            String role = cbRole.getSelectedItem().toString();

            
            

     // 1. Basic Validation (Empty fields or passwords don't match)
        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username and Password cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!pass.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ==========================================
        // NEW: PASSWORD STRENGTH VALIDATION
        // ==========================================
        // .length() <= 6        -> Blocks 6 or fewer characters
        // !pass.matches(".*[A-Z].*") -> Blocks if there is NO uppercase letter anywhere
        // !pass.matches(".*[^a-zA-Z0-9].*") -> Blocks if there are NO special characters (looks for anything that is NOT a letter or number)
        
        if (pass.length() <= 6 || !pass.matches(".*[A-Z].*") || !pass.matches(".*[^a-zA-Z0-9].*")) {
            JOptionPane.showMessageDialog(this, 
                "Weak Password! Please ensure your password meets these requirements:\n" +
                "• Must be more than 6 characters long\n" +
                "• Must contain at least 1 uppercase letter\n" +
                "• Must contain at least 1 special character (e.g., !, @, #, $)", 
                "Security Warning", 
                JOptionPane.WARNING_MESSAGE);
            return; // Stops the code so it doesn't save the weak password
        }
        // ==========================================
            try (Connection conn = DriverManager.getConnection(AppConstants.DB_URL, AppConstants.DB_USER, AppConstants.DB_PASS)) {
                
                PreparedStatement checkStmt = conn.prepareStatement("SELECT username FROM users WHERE username = ?");
                checkStmt.setString(1, user);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Username already exists. Please choose another.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                PreparedStatement insertStmt = conn.prepareStatement("INSERT INTO users (username, password, role) VALUES (?, ?, ?)");
                insertStmt.setString(1, user);
                insertStmt.setString(2, pass);
                insertStmt.setString(3, role);
                insertStmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Account Successfully Created!\nYou can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LoginFrame().setVisible(true);

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}