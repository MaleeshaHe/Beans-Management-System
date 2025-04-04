package beans.management.system.GUI;

import beans.management.system.DAO.OrderDAO;
import beans.management.system.Model.Order;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.util.List;

public class EmployeeOrderHistoryPanel extends JPanel {

    private JTable ordersTable;
    private DefaultTableModel tableModel;
    private OrderDAO orderDAO;
    private int employeeId; // Employee's ID to filter orders

    public EmployeeOrderHistoryPanel(int employeeId) {
        this.employeeId = employeeId;  // Set the employee ID
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        orderDAO = new OrderDAO();

        // Columns for the orders table: Order ID, Total Amount, Order Date, Status
        String[] columnNames = {"Order ID", "Total Amount", "Order Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0);
        ordersTable = new JTable(tableModel);
        ordersTable.setRowHeight(30);
        ordersTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Set alternating row colors for better readability
        ordersTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (row % 2 == 0) {
                    c.setBackground(new Color(245, 245, 245)); // Light grey for even rows
                } else {
                    c.setBackground(Color.WHITE); // White for odd rows
                }
                if (isSelected) {
                    c.setBackground(new Color(8, 103, 147)); // Dark background for selected rows
                    c.setForeground(Color.WHITE); // White text for selected rows
                }
                return c;
            }
        });

        // Set table header style (background color, bold font, white text)
        JTableHeader header = ordersTable.getTableHeader();
        header.setBackground(new Color(8, 103, 147)); // Dark brown header background
        header.setForeground(Color.WHITE); // White text for header
        header.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Bold font for header

        JScrollPane scrollPane = new JScrollPane(ordersTable);
        add(scrollPane, BorderLayout.CENTER);

        // Title header with better styling
        JLabel headerLabel = new JLabel("Order History", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(new Color(8, 103, 147));
        headerLabel.setPreferredSize(new Dimension(600, 40));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Add some padding
        add(headerLabel, BorderLayout.NORTH);

        loadOrdersByEmployeeId(); // Load orders for the employee
    }

    private void loadOrdersByEmployeeId() {
        // Fetch order history from the database using employee ID
        List<Order> orders = orderDAO.getOrderHistoryByEmployeeId(employeeId);
        tableModel.setRowCount(0);  // Clear existing data before adding new rows

        // Add orders to the table model
        for (Order order : orders) {
            tableModel.addRow(new Object[]{
                    order.getOrderId(),
                    "$" + String.format("%.2f", order.getTotalAmount()),  // Format total amount
                    order.getOrderDate(),
                    order.getStatus()
            });
        }
    }
}
