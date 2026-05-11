package lostandfound;

import javax.swing.*;
import java.awt.*;

/**
 * MainFrame acts as the "Master Window". 
 * It holds the Sidebar on the left, the Header on top, and uses a CardLayout
 * in the center to flip between the different screens (Dashboard, Inventory, etc.)
 */
public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    public String currentUserRole; // Public so InventoryPanel knows if it's an Admin or Staff

    // References to our custom screens
    private DashboardPanel dashboardPanel;
    private InventoryPanel inventoryPanel;
    private RegisterPanel registerPanel;
    private ProcessClaimPanel processClaimPanel;

    public MainFrame(String role) {
        this.currentUserRole = role;
        setTitle("UMak Lost & Found - Logged in as " + role);
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ==========================================
        // RESTORED: GLOBAL HEADER WITH LOGO
        // ==========================================
        JPanel globalHeader = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        globalHeader.setBackground(new Color(176, 205, 235));

        try {
            ImageIcon logoIcon = new ImageIcon(new ImageIcon("src/lostandfound/images/umak_logo.png").getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH));
            globalHeader.add(new JLabel(logoIcon));
        } catch (Exception e) { System.out.println("Logo missing."); }

        JLabel lblHeaderText = new JLabel("<html><div style='text-align: center; color: #1a1a1a;'><span style='font-size: 26px; font-family: Marcellus;'>University Of Makati</span><br><span style='font-size: 14px; font-family: Metropolis;'>Lost & Found Inventory System</span></div></html>");
        globalHeader.add(lblHeaderText);
        add(globalHeader, BorderLayout.NORTH);

        // ==========================================
        // RESTORED: SIDEBAR WITH ICONS & MENU TITLE
        // ==========================================
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(245, 245, 245));
        sidebar.setPreferredSize(new Dimension(230, 0)); // Slightly enlarged shape

        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        try {
            ImageIcon logoIcon = new ImageIcon(new ImageIcon("src/lostandfound/images/logo.png").getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
            JLabel lblSidebarLogo = new JLabel(logoIcon);
            lblSidebarLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
            sidebar.add(lblSidebarLogo);
        } catch (Exception e) {}

        
        JLabel lblMenu = new JLabel("MENU");
        lblMenu.setFont(AppConstants.metropolisBold.deriveFont(20f));
        lblMenu.setForeground(Color.DARK_GRAY);
        lblMenu.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblMenu);
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        // Restored Emojis!
        JButton btnDashboard = createSidebarBtn("Dashboard", "📊");
        JButton btnInventory = createSidebarBtn("Inventory", "📦");
        JButton btnRegister = createSidebarBtn("Add Found Item", "📝");
        JButton btnLogout = createSidebarBtn("LOG OUT", "🚪");

        sidebar.add(btnDashboard); sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(btnInventory); sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        sidebar.add(btnRegister); sidebar.add(Box.createVerticalGlue());
        sidebar.add(btnLogout);
        add(sidebar, BorderLayout.WEST);

        // ==========================================
        // CARD LAYOUT (Screen Flipper)
        // ==========================================
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        // Pass 'this' (MainFrame) to the panels so they can talk to each other
        dashboardPanel = new DashboardPanel();
        inventoryPanel = new InventoryPanel(this); 
        registerPanel = new RegisterPanel(this);
        processClaimPanel = new ProcessClaimPanel(this);

        mainContentPanel.add(dashboardPanel, "DASHBOARD");
        mainContentPanel.add(inventoryPanel, "INVENTORY");
        mainContentPanel.add(registerPanel, "REGISTER");
        mainContentPanel.add(processClaimPanel, "PROCESS_CLAIM");
        add(mainContentPanel, BorderLayout.CENTER);

        // --- NAVIGATION BUTTON LOGIC ---
        btnDashboard.addActionListener(e -> { dashboardPanel.refreshData(); cardLayout.show(mainContentPanel, "DASHBOARD"); });
        btnInventory.addActionListener(e -> { inventoryPanel.refreshData(); cardLayout.show(mainContentPanel, "INVENTORY"); });
        btnRegister.addActionListener(e -> cardLayout.show(mainContentPanel, "REGISTER"));
        btnLogout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Logout?", "Logout", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dispose(); new LoginFrame().setVisible(true);
            }
        });
        
        dashboardPanel.refreshData(); 
    }

    // --- GETTERS FOR PANELS TO COMMUNICATE ---
    public void switchToCard(String cardName) { cardLayout.show(mainContentPanel, cardName); }
    public ProcessClaimPanel getProcessClaimPanel() { return processClaimPanel; }
    public InventoryPanel getInventoryPanel() { return inventoryPanel; }
    public DashboardPanel getDashboardPanel() { return dashboardPanel; }

    // Helper to create the styled buttons with Icons
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