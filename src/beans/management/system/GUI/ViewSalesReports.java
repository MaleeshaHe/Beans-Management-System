package beans.management.system.GUI;

import javax.swing.*;
import java.awt.*;
import beans.management.system.DAO.OrderDAO;

public class ViewSalesReports extends JPanel {

    private OrderDAO orderDAO;

    public ViewSalesReports() {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 250, 250));  // Light background color

        // Header label for the panel
        JLabel label = new JLabel("Sales Reports", JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));
        label.setForeground(new Color(77, 46, 10));  // Dark brown color
        add(label, BorderLayout.NORTH);

        // Initialize the DAO class for accessing data
        orderDAO = new OrderDAO();

        // Create a panel to display the statistics
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));  // FlowLayout for fixed placement of cards
        statsPanel.setBackground(new Color(250, 250, 250));

        // Set a fixed height for statsPanel using setPreferredSize
        statsPanel.setPreferredSize(new Dimension(1100, 180)); // Fixed height and width (adjust as needed)

        // Create small rounded cards for each statistic
        JPanel totalOrdersCard = createStatsCard("Total Orders", String.valueOf(orderDAO.getTotalOrders()));
        JPanel totalRevenueCard = createStatsCard("Total Revenue (SAR)", String.format("%.2f", orderDAO.getTotalRevenue()));
        JPanel avgOrderValueCard = createStatsCard("Avg Order Value (SAR)", String.format("%.2f", orderDAO.getTotalRevenue() / orderDAO.getTotalOrders()));
        JPanel totalCustomersCard = createStatsCard("Total Customers", String.valueOf(orderDAO.getTotalCustomers()));
        JPanel ordersPerCustomerCard = createStatsCard("Orders Per Customer", String.valueOf(orderDAO.getTotalOrders() / orderDAO.getTotalCustomers()));
        JPanel ordersPerDayCard = createStatsCard("Orders Per Day", String.valueOf(orderDAO.getTotalOrdersPerDay()));

        // Add cards to stats panel
        statsPanel.add(totalOrdersCard);
        statsPanel.add(totalRevenueCard);
        statsPanel.add(avgOrderValueCard);
        statsPanel.add(totalCustomersCard);
        statsPanel.add(ordersPerCustomerCard);
        statsPanel.add(ordersPerDayCard);

        // Add the stats panel to the main panel
        add(statsPanel, BorderLayout.CENTER);

        // Style the panel with padding and borders
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    // Helper method to create smaller stats cards with rounded borders
    private JPanel createStatsCard(String title, String value) {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBackground(new Color(255, 255, 255));  // White background for cards
        cardPanel.setPreferredSize(new Dimension(150, 60));  // Further reduced card size (smaller width & height)
        cardPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));  // Light gray border with rounded corners
        cardPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5)); // Padding inside the card

        // Title Label
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));  // Smaller font size for title
        titleLabel.setForeground(new Color(77, 46, 10));  // Dark brown color

        // Value Label
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));  // Slightly larger font for the value
        valueLabel.setForeground(new Color(77, 46, 10));  // Dark brown color

        // Adding components to the card
        cardPanel.add(titleLabel, BorderLayout.NORTH);
        cardPanel.add(valueLabel, BorderLayout.CENTER);

        // Rounded corners
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)  // Adding padding
        ));

        // Adding hover effect (mouse listener)
        cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cardPanel.setBackground(new Color(240, 240, 240));  // Lighter background on hover
                cardPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100), 1));  // Darker border on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cardPanel.setBackground(new Color(255, 255, 255));  // Reset background color
                cardPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));  // Reset border color
            }
        });

        return cardPanel;
    }
}
