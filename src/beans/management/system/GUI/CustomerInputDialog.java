package beans.management.system.GUI;

import beans.management.system.DAO.CustomerDAO;
import beans.management.system.Model.Customer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CustomerInputDialog extends JDialog {

    private JTextField firstNameField, lastNameField, phoneNumberField;
    private JButton saveButton, cancelButton;
    private boolean isEditMode;
    private Customer currentCustomer;

    // Constructor
    public CustomerInputDialog(JFrame parent, boolean isEditMode, Customer customer) {
        super(parent, "Customer Details", true);
        this.isEditMode = isEditMode;
        this.currentCustomer = customer;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        getContentPane().setBackground(Color.WHITE);
        gbc.insets = new Insets(10, 10, 10, 10);

        // First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("First Name: "), gbc);

        firstNameField = new JTextField(isEditMode ? customer.getFirstName() : "");
        firstNameField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        add(firstNameField, gbc);

        // Last Name
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Last Name: "), gbc);

        lastNameField = new JTextField(isEditMode ? customer.getLastName() : "");
        lastNameField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        add(lastNameField, gbc);

        // Phone Number
        gbc.gridx = 0;
        gbc.gridy++;
        add(new JLabel("Phone Number: "), gbc);

        phoneNumberField = new JTextField(isEditMode ? customer.getPhoneNumber() : "");
        phoneNumberField.setPreferredSize(new Dimension(250, 30));
        gbc.gridx = 1;
        add(phoneNumberField, gbc);

        // Save & Cancel Buttons
        saveButton = new JButton(isEditMode ? "Update Customer" : "Add Customer");
        cancelButton = new JButton("Cancel");

        styleButton(saveButton);
        styleButton(cancelButton);

        saveButton.addActionListener(e -> saveCustomer());
        cancelButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        add(buttonPanel, gbc);

        setSize(400, 300);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void saveCustomer() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phone = phoneNumberField.getText().trim();

        // Validate first name: only letters and at least 3 characters
        if (!firstName.matches("[a-zA-Z]+") || firstName.length() < 3) {
            JOptionPane.showMessageDialog(this, "First name must contain only letters and be at least 3 characters long.");
            return;
        }

        // Validate last name: only letters and at least 3 characters
        if (!lastName.matches("[a-zA-Z]+") || lastName.length() < 3) {
            JOptionPane.showMessageDialog(this, "Last name must contain only letters and be at least 3 characters long.");
            return;
        }

        // Validate phone number (must contain only numbers and a plus sign)
        if (!phone.matches("^[+]?[0-9]*$")) {
            JOptionPane.showMessageDialog(this, "Phone number must contain only numbers and optionally start with a plus sign.");
            return;
        }

        if (firstName.isEmpty() || lastName.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        int roleId = 1;
        String roleName = "Customer";

        Customer newCustomer = new Customer(
            isEditMode ? currentCustomer.getUserId() : 0,
            firstName, lastName, phone, roleId, roleName
        );

        boolean success;
        CustomerDAO customerDAO = new CustomerDAO();

        if (isEditMode) {
            success = customerDAO.updateCustomer(newCustomer);
        } else {
            success = customerDAO.addCustomer(newCustomer);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, isEditMode ? "Customer updated successfully." : "Customer added successfully.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error saving customer.");
        }
    }


    private void styleButton(JButton button) {
        button.setBackground(new Color(8, 103, 147));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setPreferredSize(new Dimension(135, 35));
    }
}
