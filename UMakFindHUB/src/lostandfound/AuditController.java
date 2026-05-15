package lostandfound;

import java.sql.*;

public class AuditController {

    /**
     * Silently saves an action to the audit_logs table.
     */
    public static void logAction(String username, String actionType, int itemId, String details) {
        String query = "INSERT INTO audit_logs (performed_by, action_type, item_id, action_details, timestamp) VALUES (?, ?, ?, ?, NOW())";
        
        try (Connection conn = AppConstants.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            stmt.setString(2, actionType);
            
            // If there is no specific item (e.g., general login), we save it as NULL
            if (itemId > 0) {
                stmt.setInt(3, itemId);
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            
            stmt.setString(4, details);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Warning: Failed to save audit log.");
        }
    }
}