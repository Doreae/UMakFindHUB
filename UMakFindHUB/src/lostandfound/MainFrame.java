package lostandfound;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    public String currentUserRole;

    private DashboardPanel dashboardPanel;
    private InventoryPanel inventoryPanel;
    private RegisterPanel registerPanel;
    private ProcessClaimPanel processClaimPanel;
    private AuditLogPanel auditLogPanel; // Added reference for the new panel

    public MainFrame(String role) {
        this.currentUserRole = role;
        setTitle("UMak Lost & Found - Logged in as " + role);
        
        // 1. This makes the window take up the full screen automatically
        setExtendedState(JFrame.MAXIMIZED_BOTH); 
        
        // 2. This is your "Fallback" size in case the user 'restores' the window
        setSize(1280, 800); 
        
   
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- GLOBAL HEADER ---
        JPanel globalHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        globalHeader.setBackground(new Color(176, 205, 235));
        try {
            ImageIcon logoIcon = new ImageIcon(new ImageIcon("src/lostandfound/images/umak_logo.png").getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH));
            globalHeader.add(new JLabel(logoIcon));
        } catch (Exception e) { System.out.println("Logo missing."); }
        JLabel lblHeaderText = new JLabel("<html><div style='text-align: center; color: #1a1a1a;'><span style='font-size: 26px; font-family: Marcellus;'>University Of Makati</span><br><span style='font-size: 14px; font-family: Metropolis;'>Lost & Found Inventory System</span></div></html>");
        globalHeader.add(lblHeaderText);
        add(globalHeader, BorderLayout.NORTH);

        // --- SIDEBAR ---
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(245, 245, 245));
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        try {
            ImageIcon logoIcon = new ImageIcon(new ImageIcon("src/lostandfound/images/logo.png").getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
            JLabel lblSidebarLogo = new JLabel(logoIcon);
            lblSidebarLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(lblSidebarLogo);
        } catch (Exception e) {}

        sidebar.add(Box.createRigidArea(new Dimension(0, 10))); 

        JLabel lblMenu = new JLabel("MENU");
        lblMenu.setFont(AppConstants.metropolisBold.deriveFont(20f));
        lblMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblMenu);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        // Create standard buttons
        JButton btnDashboard = createSidebarBtn("Dashboard", "📊");
        JButton btnInventory = createSidebarBtn("Inventory", "📦");
        JButton btnRegister = createSidebarBtn("Add Found Item", "📝");
        
        // --- NEW: AUDIT LOG BUTTON ---
        JButton btnAudit = createSidebarBtn("Audit Logs", "📜");
        
        // Only show Audit Log button if the user is an Admin
        btnAudit.setVisible(role.equalsIgnoreCase("Admin"));

        sidebar.add(btnDashboard); sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(btnInventory); sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(btnRegister); sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Add Audit button to sidebar (it will only be visible to Admins)
        sidebar.add(btnAudit); 
        
        sidebar.add(Box.createVerticalGlue());
        JButton btnLogout = createSidebarBtn("LOG OUT", "🚪");
        sidebar.add(btnLogout);
        add(sidebar, BorderLayout.WEST);

        // --- CARD LAYOUT (Screen Flipper) ---
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        dashboardPanel = new DashboardPanel();
        inventoryPanel = new InventoryPanel(this); 
        registerPanel = new RegisterPanel(this);
        processClaimPanel = new ProcessClaimPanel(this);
        auditLogPanel = new AuditLogPanel(); // Initialize the log panel

        mainContentPanel.add(dashboardPanel, "DASHBOARD");
        mainContentPanel.add(inventoryPanel, "INVENTORY");
        mainContentPanel.add(registerPanel, "REGISTER");
        mainContentPanel.add(processClaimPanel, "PROCESS_CLAIM");
        mainContentPanel.add(auditLogPanel, "AUDIT_LOGS"); // Add it to the flipper
        
        add(mainContentPanel, BorderLayout.CENTER);

        // --- NAVIGATION BUTTON LOGIC ---
        btnDashboard.addActionListener(e -> { dashboardPanel.refreshData(); cardLayout.show(mainContentPanel, "DASHBOARD"); });
        btnInventory.addActionListener(e -> { inventoryPanel.refreshData(); cardLayout.show(mainContentPanel, "INVENTORY"); });
        btnRegister.addActionListener(e -> cardLayout.show(mainContentPanel, "REGISTER"));
        
        // Audit Logic: Refresh the logs whenever the button is clicked
        btnAudit.addActionListener(e -> { auditLogPanel.refreshLogs(); cardLayout.show(mainContentPanel, "AUDIT_LOGS"); });
        
        btnLogout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Logout?", "Logout", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dispose(); new LoginFrame().setVisible(true);
            }
        });
        
        dashboardPanel.refreshData(); 
        
        
    }

    public void switchToCard(String cardName) { cardLayout.show(mainContentPanel, cardName); }
    public ProcessClaimPanel getProcessClaimPanel() { return processClaimPanel; }
    public InventoryPanel getInventoryPanel() { return inventoryPanel; }
    public DashboardPanel getDashboardPanel() { return dashboardPanel; }

    private JButton createSidebarBtn(String text, String symbol) {
        JButton btn = new JButton("<html><center><div style='font-size: 24px; margin-bottom: 5px;'>" + symbol + "</div>" + text + "</center></html>") {
            @Override protected void paintComponent(Graphics g) { g.setColor(getBackground()); g.fillRect(0, 0, getWidth(), getHeight()); super.paintComponent(g); }
        };
        btn.setFont(AppConstants.metropolisBody.deriveFont(16f));
        btn.setFocusPainted(false); btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setContentAreaFilled(false); btn.setOpaque(true);
        btn.setPreferredSize(new Dimension(200, 90)); btn.setMaximumSize(new Dimension(200, 90));
        btn.setBackground(new Color(133, 179, 235)); btn.setForeground(Color.BLACK);
        return btn;
    }
}