package lostandfound;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AppConstants {
    // --- DATABASE CREDENTIALS ---
    public static final String DB_URL = "jdbc:mysql://localhost:3306/umak_lostfound_db";
    public static final String DB_USER = "root";
    public static final String DB_PASS = "";

    // --- GLOBAL FONTS ---
    public static Font marcellusHeader;
    public static Font metropolisBody;
    public static Font metropolisBold;

    // Call this once when the app starts to load fonts into memory
    public static void initFonts() {
        try {
            InputStream marcellusStream = AppConstants.class.getResourceAsStream("/lostandfound/fonts/Marcellus-Regular.ttf");
            InputStream metropolisStream = AppConstants.class.getResourceAsStream("/lostandfound/fonts/Metropolis-Regular.ttf");
            InputStream metropolisBoldStream = AppConstants.class.getResourceAsStream("/lostandfound/fonts/Metropolis-Bold.ttf");

            if (marcellusStream != null) {
                marcellusHeader = Font.createFont(Font.TRUETYPE_FONT, marcellusStream).deriveFont(26f);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(marcellusHeader);
            } else { marcellusHeader = new Font("Serif", Font.PLAIN, 26); }

            if (metropolisStream != null) {
                metropolisBody = Font.createFont(Font.TRUETYPE_FONT, metropolisStream).deriveFont(14f);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(metropolisBody);
            } else { metropolisBody = new Font("SansSerif", Font.PLAIN, 14); }

            if (metropolisBoldStream != null) {
                metropolisBold = Font.createFont(Font.TRUETYPE_FONT, metropolisBoldStream).deriveFont(Font.BOLD, 14f);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(metropolisBold);
            } else { metropolisBold = new Font("SansSerif", Font.BOLD, 14); }

        } catch (Exception e) {
            System.err.println("Fonts not found! Defaulting to system fonts.");
            marcellusHeader = new Font("Serif", Font.PLAIN, 26);
            metropolisBody = new Font("SansSerif", Font.PLAIN, 14);
            metropolisBold = new Font("SansSerif", Font.BOLD, 14);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
	}
