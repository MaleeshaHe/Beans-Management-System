/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package beans.management.system.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ManagerDashboardFrame extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Buttons for navigation
    private JButton viewReportsButton;
    private JButton manageEmployeesButton;
    private JButton createPromotionButton;

    // Constructor to initialize the Manager Dashboard
    public ManagerDashboardFrame() {
        setTitle("Manager Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);  // Center the window on the screen

        // Set layout for the frame
        setLayout(new BorderLayout());

        // Side navigation panel (slider)
        JPanel navPanel = createNavigationPanel();
        add(navPanel, BorderLayout.WEST);

        // Content area where different forms will be loaded
        contentPanel = new JPanel();
        cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);

        // Add the initial empty panel (or a welcome message)
        contentPanel.add(new JLabel("Welcome to the Manager Dashboard", JLabel.CENTER), "home");

        // Add this contentPanel to the main frame
        add(contentPanel, BorderLayout.CENTER);
    }

    // Create the navigation slider panel
    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));  // Vertical layout

        // Create buttons for navigation
        viewReportsButton = new JButton("View Sales Reports");
        manageEmployeesButton = new JButton("Manage Employees");
        createPromotionButton = new JButton("Create Promotion");

        // Add action listeners to buttons
        viewReportsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadForm(new ViewSalesReports());
            }
        });

        manageEmployeesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadForm(new ManageEmployees());
            }
        });

        createPromotionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadForm(new CreatePromotion());
            }
        });

        // Add buttons to the navigation panel
        navPanel.add(viewReportsButton);
        navPanel.add(manageEmployeesButton);
        navPanel.add(createPromotionButton);

        return navPanel;
    }

    // Load a new form inside the content area (contentPanel)
    private void loadForm(JPanel formPanel) {
        contentPanel.removeAll();  // Remove any existing form
        contentPanel.add(formPanel);  // Add the new form
        cardLayout.show(contentPanel, "form");  // Display the new form
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ManagerDashboardFrame().setVisible(true);  // Launch Manager Dashboard
            }
        });
    }
}

