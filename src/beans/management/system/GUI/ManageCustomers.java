package beans.management.system.GUI;

import beans.management.system.DAO.CustomerDAO;
import beans.management.system.Model.Customer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ManageCustomers extends JPanel {

    private JTable customersTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton;
    private CustomerDAO customerDAO;  // Use CustomerDAO

    public ManageCustomers() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);  // Set panel background color
        customerDAO = new CustomerDAO();  // Initialize CustomerDAO

        // Create table model and JTable for displaying customers
        String[] columnNames = {"Customer ID", "First Name", "Last Name", "Email", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0);
        customersTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(customersTable);
        add(scrollPane, BorderLayout.CENTER);

        // Customize JTable appearance
        customersTable.setBackground(Color.WHITE); // Set background to white
        customersTable.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Set font to Segoe UI, 12pt
        customersTable.setRowHeight(30); // Set row height for better readability

        // Set the header background and font color
        customersTable.getTableHeader().setBackground(new Color(77, 46, 10)); // Set header background color
        customersTable.getTableHeader().setForeground(Color.WHITE); // Set header text color
        customersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12)); // Set header font

        // Create and add header label before the table
        JLabel headerLabel = new JLabel("Customer Management", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(new Color(77, 46, 10));  // Set header text color
        headerLabel.setPreferredSize(new Dimension(600, 40));  // Set height for header
        add(headerLabel, BorderLayout.NORTH);  // Add the header label to the north of the panel

        // Create button panel for Add, Edit, and Delete buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);  // Set background to white

        // Add Customer Button
        addButton = new JButton("Add Customer");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCustomer();
            }
        });
        styleButton(addButton);  // Apply style to button
        buttonPanel.add(addButton);

        // Edit Customer Button
        editButton = new JButton("Edit Customer");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editCustomer();
            }
        });
        styleButton(editButton);  // Apply style to button
        buttonPanel.add(editButton);

        // Delete Customer Button (Soft Delete)
        deleteButton = new JButton("Delete Customer");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCustomer();
            }
        });
        styleButton(deleteButton);  // Apply style to button
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load customer data
        loadCustomerData();
    }

    // Method to style buttons with custom background color, font, and text color
    private void styleButton(JButton button) {
        button.setBackground(new Color(77, 46, 10)); // Set the background color of the button
        button.setForeground(Color.WHITE); // Set the text color to white
        button.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Set font to Segoe UI, 12 pt, Bold
    }

    // Load customer data from the database
    private void loadCustomerData() {
        List<Customer> customers = customerDAO.getAllCustomers();  // Fetch data from DAO
        tableModel.setRowCount(0);  // Clear any existing rows

        for (Customer customer : customers) {
            tableModel.addRow(new Object[]{customer.getUserId(), customer.getFirstName(), customer.getLastName(), customer.getEmail(), customer.getRoleName()});
        }
    }

    // Add new customer
    private void addCustomer() {
        new CustomerInputDialog((JFrame) SwingUtilities.getWindowAncestor(this), false, null);
        loadCustomerData(); // Refresh the table
    }

    // Edit selected customer
    private void editCustomer() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow != -1) {
            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            String firstName = (String) tableModel.getValueAt(selectedRow, 1);
            String lastName = (String) tableModel.getValueAt(selectedRow, 2);
            String email = (String) tableModel.getValueAt(selectedRow, 3);
            String role = (String) tableModel.getValueAt(selectedRow, 4);

            Customer customerToEdit = new Customer(userId, firstName, lastName, email, null, 0, role);
            new CustomerInputDialog((JFrame) SwingUtilities.getWindowAncestor(this), true, customerToEdit);
            loadCustomerData(); // Refresh the table
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer to edit.");
        }
    }

    // Soft delete selected customer
    private void deleteCustomer() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow != -1) {
            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            boolean isDeleted = customerDAO.softDeleteCustomer(userId);
            if (isDeleted) {
                JOptionPane.showMessageDialog(this, "Customer marked as deleted successfully.");
                loadCustomerData(); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting customer.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.");
        }
    }
}
