package beans.management.system.GUI;

import beans.management.system.DAO.UserDAO;
import beans.management.system.Model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class EmployeeInputDialog extends JDialog {

    private JTextField firstNameField, lastNameField, emailField;
    private JPasswordField passwordField;
    private JButton saveButton, cancelButton;
    private boolean isEditMode;
    private User currentUser;  // Used to track the current employee for editing

    // Constructor for the dialog
    public EmployeeInputDialog(JFrame parent, boolean isEditMode, User user) {
        super(parent, "Employee Details", true);
        this.isEditMode = isEditMode;
        this.currentUser = user;  // The employee to edit, if available

        // Set layout for the dialog using GridBagLayout for alignment
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Set dialog background color to white
        getContentPane().setBackground(Color.WHITE);

        // Create and align the form fields
        gbc.insets = new Insets(10, 10, 10, 10);  // Add padding around components

        add(new JLabel("First Name:"), gbc);
        firstNameField = new JTextField(isEditMode ? user.getFirstName() : "");
        firstNameField.setHorizontalAlignment(JTextField.LEFT);  // Left-align the text field
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(firstNameField, gbc);

        gbc.gridx = 0;
        add(new JLabel("Last Name:"), gbc);
        lastNameField = new JTextField(isEditMode ? user.getLastName() : "");
        lastNameField.setHorizontalAlignment(JTextField.LEFT);
        gbc.gridx = 1;
        add(lastNameField, gbc);

        gbc.gridx = 0;
        add(new JLabel("Email:"), gbc);
        emailField = new JTextField(isEditMode ? user.getEmail() : "");
        emailField.setHorizontalAlignment(JTextField.LEFT);
        gbc.gridx = 1;
        add(emailField, gbc);

        gbc.gridx = 0;
        add(new JLabel("Password:"), gbc);
        passwordField = new JPasswordField(isEditMode ? user.getPassword() : "");
        passwordField.setHorizontalAlignment(JPasswordField.LEFT);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Create Save and Cancel buttons
        saveButton = new JButton(isEditMode ? "Update Employee" : "Add Employee");
        cancelButton = new JButton("Cancel");

        // Styling for the buttons
        styleButton(saveButton);
        styleButton(cancelButton);

        // Create a panel for the buttons and add them to the dialog
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // Add action listener to save button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveEmployee();
            }
        });

        // Add action listener to cancel button
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();  // Close the dialog without saving
            }
        });

        // Set dialog properties
        setSize(350, 300);  // Adjusted size for better fit
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    // Method to handle saving or updating the employee
    private void saveEmployee() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        User newUser = new User(isEditMode ? currentUser.getUserId() : 0, firstName, lastName, email, "Employee", password);
        boolean success;

        if (isEditMode) {
            success = new UserDAO().updateEmployee(newUser);  // Update existing employee
        } else {
            success = new UserDAO().addEmployee(newUser);  // Add new employee
        }

        if (success) {
            JOptionPane.showMessageDialog(this, isEditMode ? "Employee updated successfully." : "Employee added successfully.");
            dispose();  // Close the dialog
        } else {
            JOptionPane.showMessageDialog(this, "Error saving employee.");
        }
    }

    // Method to style buttons with custom background color, font, and text color
    private void styleButton(JButton button) {
        button.setBackground(new Color(8, 103, 147)); // Set the background color of the button
        button.setForeground(Color.WHITE); // Set the text color to white
        button.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Set font to Segoe UI, 14 pt, Bold
    }
}
