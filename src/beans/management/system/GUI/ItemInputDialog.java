package beans.management.system.GUI;

import beans.management.system.Model.Item;
import beans.management.system.DAO.ItemDAO;
import beans.management.system.Model.Category;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import java.util.List;

public class ItemInputDialog extends JDialog {

    private JTextField itemNameField, priceField, descriptionField;
    private JComboBox<Category> categoryDropdown;
    private JButton saveButton, cancelButton;
    private boolean isEditMode;
    private Item currentItem;
    private ItemDAO itemDAO;

    public ItemInputDialog(JFrame parent, boolean isEditMode, Item item) {
        super(parent, "Item Details", true);
        this.isEditMode = isEditMode;
        this.currentItem = item;  // The item to edit, if available
        itemDAO = new ItemDAO();

        // Set layout for the dialog using GridBagLayout for alignment
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Set dialog background color to white
        getContentPane().setBackground(Color.WHITE);

        // Add padding around components
        gbc.insets = new Insets(10, 10, 10, 10);

        // Add Item Name label and text field
        add(new JLabel("Item Name:"), gbc);
        itemNameField = new JTextField(isEditMode ? item.getItemName() : "");
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(itemNameField, gbc);

        // Add Price label and text field
        gbc.gridx = 0;
        add(new JLabel("Price:"), gbc);
        priceField = new JTextField(isEditMode ? String.valueOf(item.getPrice()) : "");
        gbc.gridx = 1;
        add(priceField, gbc);

        // Add Description label and text field
        gbc.gridx = 0;
        add(new JLabel("Description:"), gbc);
        descriptionField = new JTextField(isEditMode ? item.getDescription() : "");
        gbc.gridx = 1;
        add(descriptionField, gbc);

        // Add Category label and dropdown
        gbc.gridx = 0;
        add(new JLabel("Category:"), gbc);

        List<Category> categories = itemDAO.getAllCategories();
        categoryDropdown = new JComboBox<>(categories.toArray(new Category[0]));
        categoryDropdown.setSelectedIndex(isEditMode ? item.getCategoryId() : 0); // Set selected category for editing
        gbc.gridx = 1;
        add(categoryDropdown, gbc);

        // Create Save and Cancel buttons
        saveButton = new JButton(isEditMode ? "Update Item" : "Add Item");
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
                saveItem();
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
        setSize(350, 350);  // Adjusted size for better fit
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    // Method to handle saving or updating the item
    private void saveItem() {
        String itemName = itemNameField.getText();
        double price;
        String description = descriptionField.getText();
        Category selectedCategory = (Category) categoryDropdown.getSelectedItem();

        try {
            price = Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid price.");
            return;
        }

        if (itemName.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        // Update the Item object based on the mode (add or edit)
        Item newItem = new Item(isEditMode ? currentItem.getItemId() : 0, itemName, price, description, selectedCategory.getCategoryId());

        boolean success;

        if (isEditMode) {
            success = itemDAO.updateItem(newItem);  // Update existing item
        } else {
            success = itemDAO.addItem(newItem);  // Add new item
        }

        if (success) {
            JOptionPane.showMessageDialog(this, isEditMode ? "Item updated successfully." : "Item added successfully.");
            dispose();  // Close the dialog
        } else {
            JOptionPane.showMessageDialog(this, "Error saving item.");
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
