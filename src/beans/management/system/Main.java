package beans.management.system;

import beans.management.system.GUI.LoginFrame;
import javax.swing.SwingUtilities;
import utils.DBConnection;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true); // Launch login screen
        });
    }
}

