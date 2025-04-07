package beans.management.system.GUI;

import beans.management.system.DAO.CustomerDAO;
import beans.management.system.Model.Customer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddCustomerDialog extends JDialog {

    private JTextField firstNameField, lastNameField, phoneNumberField;
    private JComboBox<String> roleComboBox;
    private JButton addButton, cancelButton;
    private CustomerDAO customerDAO;

    public AddCustomerDialog(JFrame parent) {
        super(parent, "Add New Customer", true);
        setLayout(new BorderLayout());

        customerDAO = new CustomerDAO();

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 2, 10, 10));  // 4 rows for input

        // First Name
        formPanel.add(new JLabel("First Name:"));
        firstNameField = new JTextField();
        formPanel.add(firstNameField);

        // Last Name
        formPanel.add(new JLabel("Last Name:"));
        lastNameField = new JTextField();
        formPanel.add(lastNameField);

        // Phone Number
        formPanel.add(new JLabel("Phone Number:"));
        phoneNumberField = new JTextField();
        formPanel.add(phoneNumberField);

        // Role (dropdown)
        formPanel.add(new JLabel("Role:"));
        roleComboBox = new JComboBox<>(new String[]{"Customer", "Admin"});
        formPanel.add(roleComboBox);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());

        addButton = new JButton("Add Customer");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCustomer();
            }
        });
        buttonPanel.add(addButton);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setSize(400, 250);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private void addCustomer() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();
        String role = (String) roleComboBox.getSelectedItem();

        if (firstName.isEmpty() || lastName.isEmpty() || phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.");
            return;
        }

        int roleId = role.equals("Admin") ? 1 : 2;

        Customer newCustomer = new Customer(0, firstName, lastName, phoneNumber, roleId, role);

        boolean success = customerDAO.addCustomer(newCustomer);
        if (success) {
            JOptionPane.showMessageDialog(this, "Customer added successfully.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error adding customer. Please try again.");
        }
    }
}
