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
    private CustomerDAO customerDAO;

    public ManageCustomers() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        customerDAO = new CustomerDAO();

        // Updated column headers
        String[] columnNames = {"Customer ID", "First Name", "Last Name", "Phone Number", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0);
        customersTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(customersTable);
        add(scrollPane, BorderLayout.CENTER);

        // JTable appearance
        customersTable.setBackground(Color.WHITE);
        customersTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        customersTable.setRowHeight(30);
        customersTable.getTableHeader().setBackground(new Color(8, 103, 147));
        customersTable.getTableHeader().setForeground(Color.WHITE);
        customersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Header label
        JLabel headerLabel = new JLabel("Customer Management", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(new Color(8, 103, 147));
        headerLabel.setPreferredSize(new Dimension(600, 40));
        add(headerLabel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        addButton = new JButton("Add Customer");
        addButton.addActionListener(e -> addCustomer());
        styleButton(addButton);
        buttonPanel.add(addButton);

        editButton = new JButton("Edit Customer");
        editButton.addActionListener(e -> editCustomer());
        styleButton(editButton);
        buttonPanel.add(editButton);

        deleteButton = new JButton("Delete Customer");
        deleteButton.addActionListener(e -> deleteCustomer());
        styleButton(deleteButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        loadCustomerData();
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(8, 103, 147));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
    }

    private void loadCustomerData() {
        List<Customer> customers = customerDAO.getAllCustomers();
        tableModel.setRowCount(0);

        for (Customer customer : customers) {
            tableModel.addRow(new Object[]{
                customer.getUserId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPhoneNumber(),
                customer.getRoleName()
            });
        }
    }

    private void addCustomer() {
        new CustomerInputDialog((JFrame) SwingUtilities.getWindowAncestor(this), false, null);
        loadCustomerData();
    }

    private void editCustomer() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow != -1) {
            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            String firstName = (String) tableModel.getValueAt(selectedRow, 1);
            String lastName = (String) tableModel.getValueAt(selectedRow, 2);
            String phoneNumber = (String) tableModel.getValueAt(selectedRow, 3);
            String role = (String) tableModel.getValueAt(selectedRow, 4);

            Customer customerToEdit = new Customer(userId, firstName, lastName, phoneNumber, 0, role);
            new CustomerInputDialog((JFrame) SwingUtilities.getWindowAncestor(this), true, customerToEdit);
            loadCustomerData();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer to edit.");
        }
    }

    private void deleteCustomer() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow != -1) {
            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            boolean isDeleted = customerDAO.softDeleteCustomer(userId);
            if (isDeleted) {
                JOptionPane.showMessageDialog(this, "Customer marked as deleted successfully.");
                loadCustomerData();
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting customer.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.");
        }
    }
}
