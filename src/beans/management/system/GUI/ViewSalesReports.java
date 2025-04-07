package beans.management.system.GUI;

import javax.swing.*;
import java.awt.*;
import beans.management.system.DAO.OrderDAO;

public class ViewSalesReports extends JPanel {

    private OrderDAO orderDAO;

    public ViewSalesReports() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 255));  // Light background color

        // Initialize the DAO class for accessing data
        orderDAO = new OrderDAO();

        // Header label for the entire panel
        JLabel label = new JLabel("Sales Reports", JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));  // Larger font size for the title
        label.setForeground(new Color(8, 103, 147));  // Dark brown color
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));  // Padding around title
        add(label, BorderLayout.NORTH);  // Add the heading at the top

        // Create a panel for statistics (cards) and place it below the chartPanel
        JPanel statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.CENTER);  // Add statsPanel to the bottom part of the layout
    }

    // Create and return a panel for statistics with larger cards, center-aligned
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));  // Center-align the cards with reduced gaps
        statsPanel.setBackground(new Color(255, 255, 255));

        // Set a fixed height for statsPanel using setPreferredSize
        statsPanel.setPreferredSize(new Dimension(900, 200)); // Adjusted size to minimize extra space

        // Create larger rounded cards for each statistic
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

        // Reduced top and bottom padding to eliminate extra space
        statsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));  // Reduced top and bottom padding

        return statsPanel;
    }

    // Helper method to create larger stats cards with rounded borders and shadow effect
    private JPanel createStatsCard(String title, String value) {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBackground(new Color(255, 255, 255));  // White background for cards
        cardPanel.setPreferredSize(new Dimension(250, 100));  // Slightly reduced size for cards to fit better
        cardPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));  // Light gray border with rounded corners
        cardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding inside the card

        // Title Label
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));  // Font size increased for title
        titleLabel.setForeground(new Color(0,0,0));  // Dark brown color

        // Value Label
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));  // Increased font size for the value
        valueLabel.setForeground(new Color(131, 131, 131));  // Dark brown color

        // Adding components to the card
        cardPanel.add(titleLabel, BorderLayout.NORTH);
        cardPanel.add(valueLabel, BorderLayout.CENTER);

        // Rounded corners with shadow effect
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true), 
            BorderFactory.createEmptyBorder(10, 10, 10, 10)  // Increased padding
        ));

        // Adding hover effect (mouse listener)
        cardPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cardPanel.setBackground(new Color(240, 240, 240));  // Lighter background on hover
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cardPanel.setBackground(new Color(255, 255, 255));  // Reset background color
            }
        });

        return cardPanel;
    }

}
