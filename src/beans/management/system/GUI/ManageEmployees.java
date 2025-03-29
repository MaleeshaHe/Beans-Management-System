package beans.management.system.GUI;

import beans.management.system.DAO.UserDAO;
import beans.management.system.Model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ManageEmployees extends JPanel {

    private JTable employeesTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton;
    private UserDAO userDAO;

    public ManageEmployees() {
        setLayout(new BorderLayout());

        // Initialize the UserDAO to interact with the database
        userDAO = new UserDAO();

        // Create table model and JTable for displaying employees
        String[] columnNames = {"Employee ID", "First Name", "Last Name", "Email", "Role"};
        tableModel = new DefaultTableModel(columnNames, 0);
        employeesTable = new JTable(tableModel);

        // Add scroll pane to the table
        JScrollPane scrollPane = new JScrollPane(employeesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create button panel for Add, Edit, and Delete buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Add Employee Button
        addButton = new JButton("Add Employee");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEmployee();
            }
        });
        buttonPanel.add(addButton);

        // Edit Employee Button
        editButton = new JButton("Edit Employee");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editEmployee();
            }
        });
        buttonPanel.add(editButton);

        // Delete Employee Button (Soft Delete)
        deleteButton = new JButton("Delete Employee");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteEmployee();
            }
        });
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load employee data from the database
        loadEmployeeData();
    }

    // Load employee data from the database
    private void loadEmployeeData() {
        // Clear existing rows before loading fresh data
        tableModel.setRowCount(0);

        List<User> employees = userDAO.getAllEmployees();
        for (User user : employees) {
            tableModel.addRow(new Object[]{user.getUserId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole()});
        }
    }

    // Add a new employee (using the database to persist data)
    private void addEmployee() {
        // Get user input for new employee details
        String firstName = JOptionPane.showInputDialog(this, "Enter First Name:");
        String lastName = JOptionPane.showInputDialog(this, "Enter Last Name:");
        String email = JOptionPane.showInputDialog(this, "Enter Email:");
        String password = JOptionPane.showInputDialog(this, "Enter Password:");

        if (firstName != null && lastName != null && email != null && password != null) {
            User newUser = new User(0, firstName, lastName, email, "Employee", password);
            boolean isAdded = userDAO.addEmployee(newUser);
            if (isAdded) {
                JOptionPane.showMessageDialog(this, "Employee added successfully.");
                loadEmployeeData();  // Refresh the employee list
            } else {
                JOptionPane.showMessageDialog(this, "Error adding employee.");
            }
        }
    }

    // Edit selected employee (using the database to persist changes)
    private void editEmployee() {
        int selectedRow = employeesTable.getSelectedRow();
        if (selectedRow != -1) {
            int empId = (int) tableModel.getValueAt(selectedRow, 0);
            String firstName = (String) tableModel.getValueAt(selectedRow, 1);
            String lastName = (String) tableModel.getValueAt(selectedRow, 2);
            String email = (String) tableModel.getValueAt(selectedRow, 3);
            String role = (String) tableModel.getValueAt(selectedRow, 4);

            // Get new details for the employee
            String newFirstName = JOptionPane.showInputDialog(this, "Edit First Name:", firstName);
            String newLastName = JOptionPane.showInputDialog(this, "Edit Last Name:", lastName);
            String newEmail = JOptionPane.showInputDialog(this, "Edit Email:", email);

            if (newFirstName != null && newLastName != null && newEmail != null) {
                User updatedUser = new User(empId, newFirstName, newLastName, newEmail, role, null);
                boolean isUpdated = userDAO.updateEmployee(updatedUser);
                if (isUpdated) {
                    JOptionPane.showMessageDialog(this, "Employee updated successfully.");
                    loadEmployeeData();  // Refresh the employee list
                } else {
                    JOptionPane.showMessageDialog(this, "Error updating employee.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to edit.");
        }
    }

    // Soft Delete selected employee (mark as deleted)
    private void deleteEmployee() {
        int selectedRow = employeesTable.getSelectedRow();
        if (selectedRow != -1) {
            int empId = (int) tableModel.getValueAt(selectedRow, 0);
            boolean isDeleted = userDAO.softDeleteEmployee(empId);
            if (isDeleted) {
                JOptionPane.showMessageDialog(this, "Employee marked as deleted successfully.");
                loadEmployeeData();  // Refresh the employee list
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting employee.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.");
        }
    }

    public static void main(String[] args) {
        // Create and show the Manage Employees frame
        JFrame frame = new JFrame("Manage Employees");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.add(new ManageEmployees());
        frame.setVisible(true);
    }
}


