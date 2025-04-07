package beans.management.system.GUI;

import beans.management.system.DAO.UserDAO;
import beans.management.system.Model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ManageEmployees extends JPanel {

    private JTable employeesTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton;
    private UserDAO userDAO;

    public ManageEmployees() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);  // Set panel background to white

        // Initialize the UserDAO to interact with the database
        userDAO = new UserDAO();

        // Create table model and JTable for displaying employees
        String[] columnNames = {"Employee ID", "First Name", "Last Name", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            // Override isCellEditable to prevent editing all cells
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // All cells are non-editable
            }
        };

        employeesTable = new JTable(tableModel);

        // Customize JTable appearance
        employeesTable.setBackground(Color.WHITE); // Set background to white
        employeesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Set font to Segoe UI, 12pt
        employeesTable.setRowHeight(30); // Set row height for better readability

        // Set the header background and font color
        employeesTable.getTableHeader().setBackground(new Color(8, 103, 147)); // Set header background color
        employeesTable.getTableHeader().setForeground(Color.WHITE); // Set header text color
        employeesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12)); // Set header font

        // Add scroll pane to the table
        JScrollPane scrollPane = new JScrollPane(employeesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create and add header label before the table
        JLabel headerLabel = new JLabel("Employee Management", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(new Color(8, 103, 147));  // Set header text color
        headerLabel.setPreferredSize(new Dimension(600, 40));  // Set height for header
        add(headerLabel, BorderLayout.NORTH);  // Add the header label to the north of the panel

        // Create button panel for Add, Edit, and Delete buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);  // Set background to white

        // Add Employee Button
        addButton = new JButton("Add Employee");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEmployee();
            }
        });
        styleButton(addButton);  // Apply style to button
        buttonPanel.add(addButton);

        // Edit Employee Button
        editButton = new JButton("Edit Employee");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editEmployee();
            }
        });
        styleButton(editButton);  // Apply style to button
        buttonPanel.add(editButton);

        // Delete Employee Button (Soft Delete)
        deleteButton = new JButton("Delete Employee");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteEmployee();
            }
        });
        styleButton(deleteButton);  // Apply style to button
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
            tableModel.addRow(new Object[]{user.getUserId(), user.getFirstName(), user.getLastName(), user.getEmail()});
        }
    }

    // Add a new employee (using the database to persist data)
    private void addEmployee() {
        // Open EmployeeInputDialog in Add mode
        new EmployeeInputDialog((JFrame) SwingUtilities.getWindowAncestor(this), false, null);
        loadEmployeeData();  // Refresh the employee list
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

            // Create a User object for editing
            User userToEdit = new User(empId, firstName, lastName, email, role, null);
            // Open EmployeeInputDialog in Edit mode
            new EmployeeInputDialog((JFrame) SwingUtilities.getWindowAncestor(this), true, userToEdit);
            loadEmployeeData();  // Refresh the employee list
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

    // Method to style buttons with custom background color, font, and text color
    private void styleButton(JButton button) {
        button.setBackground(new Color(8, 103, 147)); // Set the background color of the button
        button.setForeground(Color.WHITE); // Set the text color to white
        button.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Set font to Segoe UI, 12 pt, Bold
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
