package lostandfound;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public LoginFrame() {
        AppConstants.initFonts(); // Load fonts first
        
        setTitle("UMak Security Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        setSize(1280, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setLayout(new BorderLayout());

        // --- 1. HEADER ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        headerPanel.setBackground(new Color(133, 179, 235));
        headerPanel.add(new JLabel("<html><div style='text-align: center; color: #1a1a1a;'><span style='font-size: 26px; font-family: Marcellus;'>University Of Makati</span><br><span style='font-size: 14px; font-family: Metropolis;'>Lost & Found Inventory System</span></div></html>"));
        getContentPane().add(headerPanel, BorderLayout.NORTH);

        // --- 2. BACKGROUND PANEL ---
        Image mainBgImage = new ImageIcon("src/lostandfound/images/bg.png").getImage();
        JPanel bgPanel = new JPanel(new GridBagLayout()) {
            private static final long serialVersionUID = 1L;
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (mainBgImage != null) g.drawImage(mainBgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        bgPanel.setBackground(new Color(21, 35, 75));
        
             // --- 3. FROSTED GLASS BOX ---
                JPanel loginBox = new JPanel(new GridBagLayout());
                loginBox.setBackground(new Color(133, 179, 235, 180));
                loginBox.setBorder(BorderFactory.createEmptyBorder(60, 100, 60, 100));
                loginBox.setOpaque(true);
                
                // ==========================================
                // WINDOWBUILDER SAFE UI COMPONENTS
                // ==========================================
                
                // Title
                JLabel lblLoginTitle = new JLabel("LOGIN", SwingConstants.CENTER);
                lblLoginTitle.setFont(AppConstants.marcellusHeader.deriveFont(36f));
                GridBagConstraints gbc_title = new GridBagConstraints();
                gbc_title.gridx = 0; 
                gbc_title.gridy = 0; 
                gbc_title.gridwidth = 2;
                gbc_title.insets = new Insets(10, 10, 30, 10);
                loginBox.add(lblLoginTitle, gbc_title);
                
                        // Username
                        JLabel lblUser = new JLabel("Username:");
                        lblUser.setFont(AppConstants.metropolisBody.deriveFont(16f));
                        GridBagConstraints gbc_lblUser = new GridBagConstraints();
                        gbc_lblUser.gridx = 0; 
                        gbc_lblUser.gridy = 1;
                        gbc_lblUser.anchor = GridBagConstraints.EAST;
                        gbc_lblUser.insets = new Insets(10, 10, 10, 10);
                        loginBox.add(lblUser, gbc_lblUser);
                        
                                JTextField txtUser = new JTextField(15);
                                txtUser.setFont(AppConstants.metropolisBody.deriveFont(16f));
                                GridBagConstraints gbc_txtUser = new GridBagConstraints();
                                gbc_txtUser.gridx = 1; 
                                gbc_txtUser.gridy = 1;
                                gbc_txtUser.fill = GridBagConstraints.HORIZONTAL;
                                gbc_txtUser.insets = new Insets(10, 10, 10, 10);
                                loginBox.add(txtUser, gbc_txtUser);
                                
                                        // Password
                                        JLabel lblPass = new JLabel("Password:");
                                        lblPass.setFont(AppConstants.metropolisBody.deriveFont(16f));
                                        GridBagConstraints gbc_lblPass = new GridBagConstraints();
                                        gbc_lblPass.gridx = 0; 
                                        gbc_lblPass.gridy = 2;
                                        gbc_lblPass.anchor = GridBagConstraints.EAST;
                                        gbc_lblPass.insets = new Insets(10, 10, 10, 10);
                                        loginBox.add(lblPass, gbc_lblPass);
                                        
                                                JPasswordField txtPass = new JPasswordField(15);
                                                txtPass.setFont(AppConstants.metropolisBody.deriveFont(16f));
                                                GridBagConstraints gbc_txtPass = new GridBagConstraints();
                                                gbc_txtPass.gridx = 1; 
                                                gbc_txtPass.gridy = 2;
                                                gbc_txtPass.fill = GridBagConstraints.HORIZONTAL;
                                                gbc_txtPass.insets = new Insets(10, 10, 10, 10);
                                                loginBox.add(txtPass, gbc_txtPass);
                                                
                                                        // Show Password Checkbox
                                                        JCheckBox chkShowPass = new JCheckBox("Show Password");
                                                        chkShowPass.setFont(AppConstants.metropolisBody.deriveFont(12f));
                                                        chkShowPass.setOpaque(false); 
                                                        chkShowPass.setFocusPainted(false);
                                                        GridBagConstraints gbc_chkShowPass = new GridBagConstraints();
                                                        gbc_chkShowPass.gridx = 1; 
                                                        gbc_chkShowPass.gridy = 3; 
                                                        gbc_chkShowPass.anchor = GridBagConstraints.WEST; 
                                                        gbc_chkShowPass.insets = new Insets(0, 10, 10, 10);
                                                        loginBox.add(chkShowPass, gbc_chkShowPass);
                                                        
                                                                chkShowPass.addActionListener(e -> {
                                                                    if (chkShowPass.isSelected()) {
                                                                        txtPass.setEchoChar((char) 0); 
                                                                    } else {
                                                                        txtPass.setEchoChar('•'); 
                                                                    }
                                                                });
                                                                
                                                                        // Submit Button
                                                                        JButton btnSubmit = new JButton("SUBMIT");
                                                                        btnSubmit.setFont(AppConstants.metropolisBold.deriveFont(18f));
                                                                        btnSubmit.setBackground(new Color(255, 235, 59));
                                                                        GridBagConstraints gbc_btnSubmit = new GridBagConstraints();
                                                                        gbc_btnSubmit.gridx = 0; 
                                                                        gbc_btnSubmit.gridy = 4; 
                                                                        gbc_btnSubmit.gridwidth = 2;
                                                                        gbc_btnSubmit.fill = GridBagConstraints.HORIZONTAL;
                                                                        gbc_btnSubmit.insets = new Insets(20, 10, 10, 10);
                                                                        loginBox.add(btnSubmit, gbc_btnSubmit);
                                                                        
                                                                                // ==========================================
                                                                                // NEW: REGISTER BUTTON
                                                                                // ==========================================
                                                                                JButton btnRegister = new JButton("Create an Account");
                                                                                btnRegister.setFont(AppConstants.metropolisBold.deriveFont(14f));
                                                                                btnRegister.setBackground(new Color(200, 200, 200));
                                                                                GridBagConstraints gbc_btnRegister = new GridBagConstraints();
                                                                                gbc_btnRegister.gridx = 0; 
                                                                                gbc_btnRegister.gridy = 5; // Put it right under Submit
                                                                                gbc_btnRegister.gridwidth = 2;
                                                                                gbc_btnRegister.fill = GridBagConstraints.HORIZONTAL;
                                                                                gbc_btnRegister.insets = new Insets(5, 10, 10, 10);
                                                                                loginBox.add(btnRegister, gbc_btnRegister);
                                                                                
                                                                                        bgPanel.add(loginBox);
                                                                                        getContentPane().add(bgPanel, BorderLayout.CENTER);

        // --- BUTTON ACTIONS ---
        
        // Register Action
        btnRegister.addActionListener(e -> {
            dispose(); // Close login window
            new RegisterFrame().setVisible(true); // Open the new Register screen
        });

        btnSubmit.addActionListener(e -> {
            String usernameInput = txtUser.getText().trim();
            String passwordInput = new String(txtPass.getPassword());

            // 1. Basic empty field check
            if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DriverManager.getConnection(AppConstants.DB_URL, AppConstants.DB_USER, AppConstants.DB_PASS);
                 // Step 1: Look up the user by USERNAME ONLY first, to get their lockout status
                 PreparedStatement checkStmt = conn.prepareStatement("SELECT password, role, failed_attempts, lockout_time FROM users WHERE username=?")) {
                
                checkStmt.setString(1, usernameInput);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    // User exists! Let's pull their security data from the database
                    String dbPass = rs.getString("password");
                    String role = rs.getString("role");
                    int failedAttempts = rs.getInt("failed_attempts");
                    Timestamp lockoutTime = rs.getTimestamp("lockout_time");

                    // ==========================================
                    // SECURITY CHECK 1: IS THE ACCOUNT LOCKED?
                    // ==========================================
                    if (lockoutTime != null && lockoutTime.getTime() > System.currentTimeMillis()) {
                        // Calculate how many minutes are left
                        long remainingMillis = lockoutTime.getTime() - System.currentTimeMillis();
                        long remainingMinutes = (remainingMillis / 1000) / 60;
                        
                        JOptionPane.showMessageDialog(this, 
                            "Account locked due to multiple failed attempts.\nPlease try again in " + (remainingMinutes + 1) + " minutes.", 
                            "Security Lockout", JOptionPane.ERROR_MESSAGE);
                        return; // Stop the code right here, don't let them log in!
                    }

                    // ==========================================
                    // SECURITY CHECK 2: DOES THE PASSWORD MATCH?
                    // ==========================================
                    if (passwordInput.equals(dbPass)) {
                        // SUCCESS! 
                        
                        // Reset their failed attempts back to 0 in the database
                        try (PreparedStatement resetStmt = conn.prepareStatement("UPDATE users SET failed_attempts = 0, lockout_time = NULL WHERE username = ?")) {
                            resetStmt.setString(1, usernameInput);
                            resetStmt.executeUpdate();
                        }

                        // Your original success logic:
                        Session.currentUser = usernameInput; 
                        dispose(); 
                        new MainFrame(role).setVisible(true); // Passed the role exactly how you had it!
                        
                    } else {
                        // FAILURE! Wrong Password.
                        failedAttempts++; // Add 1 to their strike count
                        
                        if (failedAttempts >= 3) {
                            // Strike 3! Lock the account for 5 minutes
                            try (PreparedStatement lockStmt = conn.prepareStatement("UPDATE users SET failed_attempts = ?, lockout_time = DATE_ADD(NOW(), INTERVAL 5 MINUTE) WHERE username = ?")) {
                                lockStmt.setInt(1, failedAttempts);
                                lockStmt.setString(2, usernameInput);
                                lockStmt.executeUpdate();
                            }
                            JOptionPane.showMessageDialog(this, "3 Failed Attempts. Account locked for 5 minutes.", "Security Alert", JOptionPane.WARNING_MESSAGE);
                        } else {
                            // Strike 1 or 2. Just update the database and warn them.
                            try (PreparedStatement failStmt = conn.prepareStatement("UPDATE users SET failed_attempts = ? WHERE username = ?")) {
                                failStmt.setInt(1, failedAttempts);
                                failStmt.setString(2, usernameInput);
                                failStmt.executeUpdate();
                            }
                            JOptionPane.showMessageDialog(this, "Invalid password. Attempt " + failedAttempts + " of 3.", "Login Failed", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                } else {
                    // The username doesn't even exist in the database
                    JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database Connection Error!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}