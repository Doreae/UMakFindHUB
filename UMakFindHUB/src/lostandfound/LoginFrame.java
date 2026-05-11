package lostandfound;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginFrame extends JFrame {

    public LoginFrame() {
        AppConstants.initFonts(); // Load fonts first
        
        setTitle("UMak Security Login");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        
        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        headerPanel.setBackground(new Color(133, 179, 235));
        headerPanel.add(new JLabel("<html><div style='text-align: center; color: #1a1a1a;'><span style='font-size: 26px; font-family: Marcellus;'>University Of Makati</span><br><span style='font-size: 14px; font-family: Metropolis;'>Lost & Found Inventory System</span></div></html>"));
        add(headerPanel, BorderLayout.NORTH);
        
        

        // Background Panel
        Image mainBgImage = new ImageIcon("src/lostandfound/images/bg.png").getImage();
        JPanel bgPanel = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (mainBgImage != null) g.drawImage(mainBgImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        bgPanel.setBackground(new Color(21, 35, 75));

        
        try {
            ImageIcon logoIcon = new ImageIcon(new ImageIcon("src/lostandfound/images/umak_logo.png").getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH));
            headerPanel.add(new JLabel(logoIcon));
        } catch (Exception e) { System.out.println("Logo missing."); }
        
        
        // Frosted Glass Login Box
        JPanel loginBox = new JPanel(new GridBagLayout());
        loginBox.setBackground(new Color(133, 179, 235, 180));
        loginBox.setBorder(BorderFactory.createEmptyBorder(30, 50, 40, 50));
        loginBox.setOpaque(true);

        // --- WINDOWBUILDER FIX: UNIQUE GBC FOR EVERY ELEMENT ---
        
        // 1. Title
        JLabel lblLoginTitle = new JLabel("LOGIN", SwingConstants.CENTER);
        lblLoginTitle.setFont(AppConstants.marcellusHeader.deriveFont(36f));
        GridBagConstraints gbc_title = new GridBagConstraints();
        gbc_title.gridx = 0; gbc_title.gridy = 0; gbc_title.gridwidth = 2;
        gbc_title.insets = new Insets(10, 10, 30, 10);
        loginBox.add(lblLoginTitle, gbc_title);

        // 2. Username Label
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(AppConstants.metropolisBody.deriveFont(16f));
        GridBagConstraints gbc_lblUser = new GridBagConstraints();
        gbc_lblUser.gridx = 0; gbc_lblUser.gridy = 1;
        gbc_lblUser.insets = new Insets(10, 10, 10, 10);
        loginBox.add(lblUser, gbc_lblUser);

        // 3. Username Text Field
        JTextField txtUser = new JTextField(15);
        txtUser.setFont(AppConstants.metropolisBody.deriveFont(16f));
        GridBagConstraints gbc_txtUser = new GridBagConstraints();
        gbc_txtUser.gridx = 1; gbc_txtUser.gridy = 1;
        gbc_txtUser.insets = new Insets(10, 10, 10, 10);
        loginBox.add(txtUser, gbc_txtUser);

        // 4. Password Label
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(AppConstants.metropolisBody.deriveFont(16f));
        GridBagConstraints gbc_lblPass = new GridBagConstraints();
        gbc_lblPass.gridx = 0; gbc_lblPass.gridy = 2;
        gbc_lblPass.insets = new Insets(10, 10, 10, 10);
        loginBox.add(lblPass, gbc_lblPass);

        // 5. Password Text Field
        JPasswordField txtPass = new JPasswordField(15);
        txtPass.setFont(AppConstants.metropolisBody.deriveFont(16f));
        GridBagConstraints gbc_txtPass = new GridBagConstraints();
        gbc_txtPass.gridx = 1; gbc_txtPass.gridy = 2;
        gbc_txtPass.insets = new Insets(10, 10, 10, 10);
        loginBox.add(txtPass, gbc_txtPass);

        // ==========================================
        // NEW: SHOW PASSWORD CHECKBOX
        // ==========================================
        JCheckBox chkShowPass = new JCheckBox("Show Password");
        chkShowPass.setFont(AppConstants.metropolisBody.deriveFont(12f));
        chkShowPass.setOpaque(false); // Makes the checkbox background transparent so it matches the frosted glass!
        chkShowPass.setFocusPainted(false);
        GridBagConstraints gbc_chkShowPass = new GridBagConstraints();
        gbc_chkShowPass.gridx = 1; gbc_chkShowPass.gridy = 3; // Put it in row 3, under the password box
        gbc_chkShowPass.anchor = GridBagConstraints.WEST; // Align it to the left side
        gbc_chkShowPass.insets = new Insets(0, 10, 10, 10);
        loginBox.add(chkShowPass, gbc_chkShowPass);

        // Logic for toggling the password visibility
        chkShowPass.addActionListener(e -> {
            if (chkShowPass.isSelected()) {
                txtPass.setEchoChar((char) 0); // Reveal text
            } else {
                txtPass.setEchoChar('•'); // Hide text behind bullets
            }
        });

        // 6. Submit Button (Moved down to Row 4)
        JButton btnSubmit = new JButton("SUBMIT");
        btnSubmit.setFont(AppConstants.metropolisBold.deriveFont(18f));
        btnSubmit.setBackground(new Color(255, 235, 59));
        GridBagConstraints gbc_btnSubmit = new GridBagConstraints();
        gbc_btnSubmit.gridx = 0; gbc_btnSubmit.gridy = 4; // Shifted from row 3 to row 4
        gbc_btnSubmit.gridwidth = 2;
        gbc_btnSubmit.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnSubmit.insets = new Insets(20, 10, 10, 10);
        loginBox.add(btnSubmit, gbc_btnSubmit);

        bgPanel.add(loginBox);
        add(bgPanel, BorderLayout.CENTER);

        // --- LOGIC ---
        btnSubmit.addActionListener(e -> {
            try (Connection conn = DriverManager.getConnection(AppConstants.DB_URL, AppConstants.DB_USER, AppConstants.DB_PASS);
                 PreparedStatement stmt = conn.prepareStatement("SELECT role FROM users WHERE username=? AND password=?")) {
                stmt.setString(1, txtUser.getText());
                stmt.setString(2, new String(txtPass.getPassword()));
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    dispose(); // Close login window
                    new MainFrame(rs.getString("role")).setVisible(true); // Open Main App
                } else {
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