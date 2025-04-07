package beans.management.system;

import beans.management.system.GUI.LoginFrame;
import java.awt.Color;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import utils.DBConnection;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UIManager.put("Button.background", new Color(8, 103, 147));
            UIManager.put("Button.foreground", Color.WHITE);
            new LoginFrame().setVisible(true); // Launch login screen
        });
    }
}

