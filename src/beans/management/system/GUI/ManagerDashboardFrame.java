package beans.management.system.GUI;

import beans.management.system.Model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManagerDashboardFrame extends JFrame {

    private JButton viewReportsButton;
    private JButton manageEmployeesButton;
    private JButton createPromotionButton;
    private JLabel welcomeLabel;

    public ManagerDashboardFrame(User user) {
        setTitle("Manager Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Center the window on the screen

        // Set the layout
        setLayout(new FlowLayout());

        // Welcome message
        welcomeLabel = new JLabel("Welcome, " + user.getFirstName() + " " + user.getLastName() + " (Manager)");
        add(welcomeLabel);

        // View Reports Button
        viewReportsButton = new JButton("View Sales Reports");
        viewReportsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the sales reports window (You can create a new JFrame for viewing reports)
                JOptionPane.showMessageDialog(ManagerDashboardFrame.this, "Opening Sales Reports...");
            }
        });
        add(viewReportsButton);

        // Manage Employees Button
        manageEmployeesButton = new JButton("Manage Employees");
        manageEmployeesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open employee management window (You can create a new JFrame for employee management)
                JOptionPane.showMessageDialog(ManagerDashboardFrame.this, "Opening Employee Management...");
            }
        });
        add(manageEmployeesButton);

        // Create Promotion Button
        createPromotionButton = new JButton("Create Promotion");
        createPromotionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open promotion creation window
                JOptionPane.showMessageDialog(ManagerDashboardFrame.this, "Opening Promotion Creation...");
            }
        });
        add(createPromotionButton);
    }

    public static void main(String[] args) {
        // Create a dummy user for testing purposes
        User managerUser = new User();
        managerUser.setFirstName("John");
        managerUser.setLastName("Doe");
        
        // Open the manager dashboard frame
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ManagerDashboardFrame(managerUser).setVisible(true);
            }
        });
    }
}
