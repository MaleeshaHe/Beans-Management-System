package beans.management.system.GUI;

import javax.swing.*;
import java.awt.*;
import beans.management.system.DAO.OrderDAO;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class ViewSalesReports extends JPanel {

    private OrderDAO orderDAO;

    public ViewSalesReports() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));  // Light background color

        // Initialize the DAO class for accessing data
        orderDAO = new OrderDAO();

        // Header label for the entire panel
        JLabel label = new JLabel("Sales Reports", JLabel.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 24));  // Larger font size for the title
        label.setForeground(new Color(8, 103, 147));  // Dark brown color
        label.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));  // Padding around title
        add(label, BorderLayout.NORTH);  // Add the heading at the top

        // Create a panel for charts with 2x2 grid layout
        JPanel chartPanel = new JPanel();
        chartPanel.setLayout(new GridLayout(2, 2, 10, 10));  // 2 rows, 2 columns with 10px gap between components
        chartPanel.add(createChartPanel());  // Bar Chart (Sales by Day)
        chartPanel.add(createLineChartPanel());  // Line Chart (Revenue Over Time)
        chartPanel.add(createSalesCategoryPieChartPanel());  // Pie Chart (Sales Categories)
        chartPanel.add(createBarChartByRegionPanel());  // Bar Chart (Orders by Region)
        chartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));  // Padding around chart panel
        add(chartPanel, BorderLayout.CENTER);  // Add chartPanel in the center part of the layout

        // Create a panel for statistics (cards) and place it below the chartPanel
        JPanel statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.SOUTH);  // Add statsPanel to the bottom part of the layout
    }

    // Create and return a panel for statistics with larger cards, center-aligned
    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));  // Center-align the cards with reduced gaps
        statsPanel.setBackground(new Color(240, 240, 240));

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
        cardPanel.setPreferredSize(new Dimension(150, 80));  // Slightly reduced size for cards to fit better
        cardPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true));  // Light gray border with rounded corners
        cardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding inside the card

        // Title Label
        JLabel titleLabel = new JLabel(title, JLabel.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));  // Font size increased for title
        titleLabel.setForeground(new Color(8, 103, 147));  // Dark brown color

        // Value Label
        JLabel valueLabel = new JLabel(value, JLabel.CENTER);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));  // Increased font size for the value
        valueLabel.setForeground(new Color(8, 103, 147));  // Dark brown color

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

    // Method to create and return a bar chart panel for sales by day
    private JPanel createChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // Adding daily sales data (Replace with actual data from orderDAO)
        dataset.addValue(100, "Orders", "Day 1");
        dataset.addValue(150, "Orders", "Day 2");
        dataset.addValue(200, "Orders", "Day 3");
        dataset.addValue(250, "Orders", "Day 4");

        JFreeChart barChart = ChartFactory.createBarChart(
                "Sales by Day",  // Chart title
                "Day",  // X-Axis Label
                "Orders",  // Y-Axis Label
                dataset,  // Dataset
                PlotOrientation.VERTICAL,
                true,  // Show legend
                true,  // Show tooltips
                false  // No URLs
        );

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(200, 130)); // Slightly reduced size for chart
        return chartPanel;
    }

    // Method to create and return a line chart panel for revenue over time
    private JPanel createLineChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // Adding sample data (Replace with actual data from orderDAO)
        dataset.addValue(1000, "Revenue", "Day 1");
        dataset.addValue(1500, "Revenue", "Day 2");
        dataset.addValue(1800, "Revenue", "Day 3");
        dataset.addValue(2200, "Revenue", "Day 4");

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Revenue Over Time",  // Chart title
                "Day",  // X-Axis Label
                "Revenue (SAR)",  // Y-Axis Label
                dataset,  // Dataset
                PlotOrientation.VERTICAL,
                true,  // Show legend
                true,  // Show tooltips
                false  // No URLs
        );

        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new Dimension(200, 130)); // Slightly reduced size for chart
        return chartPanel;
    }

    // Method to create and return a pie chart panel for sales categories (e.g., Product Sales, Service Sales)
    private JPanel createSalesCategoryPieChartPanel() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        // Adding sales category data (Replace with actual data from orderDAO)
        dataset.setValue("Product Sales", 40);
        dataset.setValue("Service Sales", 60);

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Sales Categories",  // Chart title
                dataset,  // Dataset
                true,  // Include legend
                true,  // Show tooltips
                false  // No URLs
        );

        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setPreferredSize(new Dimension(200, 130)); // Slightly reduced size for chart
        return chartPanel;
    }

    // Method to create and return a bar chart panel for orders by region
    private JPanel createBarChartByRegionPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        // Adding sample data (Replace with actual data from orderDAO)
        dataset.addValue(120, "Orders", "North");
        dataset.addValue(150, "Orders", "South");
        dataset.addValue(180, "Orders", "East");
        dataset.addValue(100, "Orders", "West");

        JFreeChart barChart = ChartFactory.createBarChart(
                "Orders by Region",  // Chart title
                "Region",  // X-Axis Label
                "Orders",  // Y-Axis Label
                dataset,  // Dataset
                PlotOrientation.VERTICAL,
                true,  // Show legend
                true,  // Show tooltips
                false  // No URLs
        );

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(200, 130)); // Slightly reduced size for chart
        return chartPanel;
    }
}
