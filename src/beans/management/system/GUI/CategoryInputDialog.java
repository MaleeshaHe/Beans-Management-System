package beans.management.system.GUI;

import beans.management.system.DAO.CategoryDAO;
import beans.management.system.Model.Category;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CategoryInputDialog extends JDialog {

    private JTextField categoryNameField;
    private JButton saveButton, cancelButton;
    private boolean isEditMode;
    private Category currentCategory;

    // Constructor for the dialog
    public CategoryInputDialog(JFrame parent, boolean isEditMode, Category category) {
        super(parent, "Category Details", true);
        this.isEditMode = isEditMode;
        this.currentCategory = category;  // The category to edit, if available

        // Set layout for the dialog using GridBagLayout for alignment
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Set dialog background color to white
        getContentPane().setBackground(Color.WHITE);

        // Add padding around components
        gbc.insets = new Insets(10, 10, 10, 10);

        // Category Name label and text field
        add(new JLabel("Category Name:"), gbc);
        categoryNameField = new JTextField(isEditMode ? category.getCategoryName() : "");
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(categoryNameField, gbc);

        // Create Save and Cancel buttons
        saveButton = new JButton(isEditMode ? "Update Category" : "Add Category");
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
                saveCategory();
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
        setSize(350, 200);  // Adjusted size for better fit
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    // Method to handle saving or updating the category
    private void saveCategory() {
        String categoryName = categoryNameField.getText();

        if (categoryName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        Category newCategory = new Category(isEditMode ? currentCategory.getCategoryId() : 0, categoryName);
        boolean success;

        if (isEditMode) {
            success = new CategoryDAO().updateCategory(newCategory);  // Update existing category
        } else {
            success = new CategoryDAO().addCategory(newCategory);  // Add new category
        }

        if (success) {
            JOptionPane.showMessageDialog(this, isEditMode ? "Category updated successfully." : "Category added successfully.");
            dispose();  // Close the dialog
        } else {
            JOptionPane.showMessageDialog(this, "Error saving category.");
        }
    }

    // Method to style buttons with custom background color, font, and text color
    private void styleButton(JButton button) {
        button.setBackground(new Color(77, 46, 10)); // Set the background color of the button
        button.setForeground(Color.WHITE); // Set the text color to white
        button.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Set font to Segoe UI, 14 pt, Bold
        button.setPreferredSize(new Dimension(120, 40)); // Ensure buttons are of uniform size
    }
}
