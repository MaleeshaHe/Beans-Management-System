package beans.management.system.GUI;

import beans.management.system.DAO.CustomerDAO;
import beans.management.system.Model.Customer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CustomerInputDialog extends JDialog {

    private JTextField firstNameField, lastNameField, emailField;
    private JPasswordField passwordField;
    private JButton saveButton, cancelButton;
    private boolean isEditMode;
    private Customer currentCustomer;

    // Constructor for the dialog (Add or Edit Mode)
    public CustomerInputDialog(JFrame parent, boolean isEditMode, Customer customer) {
        super(parent, "Customer Details", true);
        this.isEditMode = isEditMode;
        this.currentCustomer = customer;  // The customer to edit, if available

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        getContentPane().setBackground(Color.WHITE);

        // Create and align the form fields
        gbc.insets = new Insets(10, 10, 10, 10);

        // First Name Field
        add(new JLabel("First Name: "), gbc);
        firstNameField = new JTextField(isEditMode ? customer.getFirstName() : "");
        firstNameField.setPreferredSize(new Dimension(250, 30)); // Set a width for the text field
        gbc.gridx = 1;
        add(firstNameField, gbc);

        // Last Name Field
        gbc.gridx = 0;
        add(new JLabel("Last Name: "), gbc);
        lastNameField = new JTextField(isEditMode ? customer.getLastName() : "");
        lastNameField.setPreferredSize(new Dimension(250, 30)); // Set a width for the text field
        gbc.gridx = 1;
        add(lastNameField, gbc);

        // Email Field
        gbc.gridx = 0;
        add(new JLabel("Email: "), gbc);
        emailField = new JTextField(isEditMode ? customer.getEmail() : "");
        emailField.setPreferredSize(new Dimension(250, 30)); // Set a width for the text field
        gbc.gridx = 1;
        add(emailField, gbc);

        // Password Field
        gbc.gridx = 0;
        add(new JLabel("Password: "), gbc);
        passwordField = new JPasswordField(isEditMode ? customer.getPassword() : "");
        passwordField.setPreferredSize(new Dimension(250, 30)); // Set a width for the text field
        gbc.gridx = 1;
        add(passwordField, gbc);

        // No Role selection needed, set role as Customer by default
        // Role is assumed to be "Customer" for all users

        // Save and Cancel Buttons
        saveButton = new JButton(isEditMode ? "Update Customer" : "Add Customer");
        cancelButton = new JButton("Cancel");

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCustomer();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        setSize(400, 350);  // Adjust the dialog size for better fit
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    // Method to handle saving or updating the customer
    private void saveCustomer() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        // Role ID is hardcoded as 2 for "Customer"
        int roleId = 1;  // "Customer" role is assumed to have role_id = 2
        String roleName = "Customer";  // Default role name

        // Create a new customer object
        Customer newCustomer = new Customer(
                isEditMode ? currentCustomer.getUserId() : 0,  // If in edit mode, use the current customer ID
                firstName, lastName, email, password, roleId, roleName
        );

        boolean success;
        CustomerDAO customerDAO = new CustomerDAO();

        // Check if we are in add mode or edit mode
        if (isEditMode) {
            success = customerDAO.updateCustomer(newCustomer);  // Update existing customer
        } else {
            success = customerDAO.addCustomer(newCustomer);  // Add new customer
        }

        if (success) {
            JOptionPane.showMessageDialog(this, isEditMode ? "Customer updated successfully." : "Customer added successfully.");
            dispose();  // Close the dialog
        } else {
            JOptionPane.showMessageDialog(this, "Error saving customer.");
        }
    }
}
