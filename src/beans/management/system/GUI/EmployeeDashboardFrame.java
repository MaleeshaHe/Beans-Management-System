package beans.management.system.GUI;

import beans.management.system.Model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EmployeeDashboardFrame extends JFrame {

    private JButton viewItemsButton;
    private JButton processOrdersButton;
    private JLabel welcomeLabel;

    public EmployeeDashboardFrame(User user) {
        setTitle("Employee Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Center the window on the screen

        // Set the layout
        setLayout(new FlowLayout());

        // Welcome message
        welcomeLabel = new JLabel("Welcome, " + user.getFirstName() + " " + user.getLastName() + " (Employee)");
        add(welcomeLabel);

        // View Items Button
        viewItemsButton = new JButton("View Items");
        viewItemsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open item management window (You can create a new JFrame for viewing items)
                JOptionPane.showMessageDialog(EmployeeDashboardFrame.this, "Opening Item Management...");
            }
        });
        add(viewItemsButton);

        // Process Orders Button
        processOrdersButton = new JButton("Process Orders");
        processOrdersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open order processing window
                JOptionPane.showMessageDialog(EmployeeDashboardFrame.this, "Opening Order Processing...");
            }
        });
        add(processOrdersButton);
    }

    public static void main(String[] args) {
        // Create a dummy user for testing purposes
        User employeeUser = new User();
        employeeUser.setFirstName("Jane");
        employeeUser.setLastName("Smith");
        
        // Open the employee dashboard frame
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new EmployeeDashboardFrame(employeeUser).setVisible(true);
            }
        });
    }
}

