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
        setLayout(new BorderLayout());

        // --- 1. HEADER ---
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        headerPanel.setBackground(new Color(133, 179, 235));
        headerPanel.add(new JLabel("<html><div style='text-align: center; color: #1a1a1a;'><span style='font-size: 26px; font-family: Marcellus;'>University Of Makati</span><br><span style='font-size: 14px; font-family: Metropolis;'>Lost & Found Inventory System</span></div></html>"));
        add(headerPanel, BorderLayout.NORTH);

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
        gbc_title.gridx = 0; gbc_title.gridy = 0; gbc_title.gridwidth = 2;
        gbc_title.insets = new Insets(10, 10, 30, 10);
        loginBox.add(lblLoginTitle, gbc_title);

        // Username
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(AppConstants.metropolisBody.deriveFont(16f));
        GridBagConstraints gbc_lblUser = new GridBagConstraints();
        gbc_lblUser.gridx = 0; gbc_lblUser.gridy = 1;
        gbc_lblUser.anchor = GridBagConstraints.EAST;
        gbc_lblUser.insets = new Insets(10, 10, 10, 10);
        loginBox.add(lblUser, gbc_lblUser);

        JTextField txtUser = new JTextField(15);
        txtUser.setFont(AppConstants.metropolisBody.deriveFont(16f));
        GridBagConstraints gbc_txtUser = new GridBagConstraints();
        gbc_txtUser.gridx = 1; gbc_txtUser.gridy = 1;
        gbc_txtUser.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtUser.insets = new Insets(10, 10, 10, 10);
        loginBox.add(txtUser, gbc_txtUser);

        // Password
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(AppConstants.metropolisBody.deriveFont(16f));
        GridBagConstraints gbc_lblPass = new GridBagConstraints();
        gbc_lblPass.gridx = 0; gbc_lblPass.gridy = 2;
        gbc_lblPass.anchor = GridBagConstraints.EAST;
        gbc_lblPass.insets = new Insets(10, 10, 10, 10);
        loginBox.add(lblPass, gbc_lblPass);

        JPasswordField txtPass = new JPasswordField(15);
        txtPass.setFont(AppConstants.metropolisBody.deriveFont(16f));
        GridBagConstraints gbc_txtPass = new GridBagConstraints();
        gbc_txtPass.gridx = 1; gbc_txtPass.gridy = 2;
        gbc_txtPass.fill = GridBagConstraints.HORIZONTAL;
        gbc_txtPass.insets = new Insets(10, 10, 10, 10);
        loginBox.add(txtPass, gbc_txtPass);

        // Show Password Checkbox
        JCheckBox chkShowPass = new JCheckBox("Show Password");
        chkShowPass.setFont(AppConstants.metropolisBody.deriveFont(12f));
        chkShowPass.setOpaque(false); 
        chkShowPass.setFocusPainted(false);
        GridBagConstraints gbc_chkShowPass = new GridBagConstraints();
        gbc_chkShowPass.gridx = 1; gbc_chkShowPass.gridy = 3; 
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
        gbc_btnSubmit.gridx = 0; gbc_btnSubmit.gridy = 4; 
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
        gbc_btnRegister.gridx = 0; gbc_btnRegister.gridy = 5; // Put it right under Submit
        gbc_btnRegister.gridwidth = 2;
        gbc_btnRegister.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnRegister.insets = new Insets(5, 10, 10, 10);
        loginBox.add(btnRegister, gbc_btnRegister);

        bgPanel.add(loginBox);
        add(bgPanel, BorderLayout.CENTER);

        // --- BUTTON ACTIONS ---
        
        // Register Action
        btnRegister.addActionListener(e -> {
            dispose(); // Close login window
            new RegisterFrame().setVisible(true); // Open the new Register screen
        });

        btnSubmit.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection(AppConstants.DB_URL, AppConstants.DB_USER, AppConstants.DB_PASS);
                 PreparedStatement stmt = conn.prepareStatement("SELECT role FROM users WHERE username=? AND password=?")) {
                
                stmt.setString(1, txtUser.getText());
                stmt.setString(2, new String(txtPass.getPassword()));
                
                // This creates 'rs' by asking the database for the user
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    // SUCCESS! The user exists. 
                    // 1. Save their username to the Session globally:
                    Session.currentUser = txtUser.getText(); 
                    
                    // 2. Open the MainFrame and pass the role:
                    dispose(); 
                    new MainFrame(rs.getString("role")).setVisible(true); 
                } else {
                    // FAILURE! Wrong password or username.
                    JOptionPane.showMessageDialog(this, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}