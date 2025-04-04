package beans.management.system.GUI;

import beans.management.system.DAO.CustomerDAO;
import beans.management.system.Model.Customer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddCustomerDialog extends JDialog {

    private JTextField firstNameField, lastNameField, emailField, passwordField;
    private JComboBox<String> roleComboBox;
    private JButton addButton, cancelButton;
    private CustomerDAO customerDAO;

    public AddCustomerDialog(JFrame parent) {
        super(parent, "Add New Customer", true);  // Set modal to true to block interaction with the parent window
        setLayout(new BorderLayout());

        // Initialize DAO for customer-related database operations
        customerDAO = new CustomerDAO();

        // Panel for form fields
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(5, 2, 10, 10));  // 5 rows, 2 columns

        // First Name
        formPanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        formPanel.add(firstNameField);

        // Last Name
        formPanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        formPanel.add(lastNameField);

        // Email
        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        // Password
        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        // Role (dropdown)
        formPanel.add(new JLabel("Role:"));
        roleComboBox = new JComboBox<>(new String[]{"Customer", "Admin"});
        formPanel.add(roleComboBox);

        add(formPanel, BorderLayout.CENTER);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());

        // Add Button
        addButton = new JButton("Add Customer");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCustomer();
            }
        });
        buttonPanel.add(addButton);

        // Cancel Button
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();  // Close the dialog
            }
        });
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setSize(400, 300);
        setLocationRelativeTo(parent);  // Center dialog on the parent window
        setVisible(true);
    }

    private void addCustomer() {
        // Get customer details from the fields
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(((JPasswordField) passwordField).getPassword()).trim();
        String role = (String) roleComboBox.getSelectedItem();

        // Basic validation for the fields
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        // Create a Customer object
        Customer newCustomer = new Customer(0, firstName, lastName, email, password, role.equals("Admin") ? 1 : 2, role);

        // Add customer using CustomerDAO
        boolean success = customerDAO.addCustomer(newCustomer);
        if (success) {
            JOptionPane.showMessageDialog(this, "Customer added successfully.");
            dispose();  // Close the dialog after adding the customer
        } else {
            JOptionPane.showMessageDialog(this, "Error adding customer. Please try again.");
        }
    }
}
