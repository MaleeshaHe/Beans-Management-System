package beans.management.system.GUI;

import beans.management.system.DAO.InventoryDAO;
import beans.management.system.Model.Inventory;
import beans.management.system.Model.Item;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class InventoryInputDialog extends JDialog {

    private JComboBox<Item> itemDropdown;
    private JTextField stockQuantityField, reorderLevelField;
    private JButton saveButton, cancelButton;
    private boolean isEditMode;
    private Inventory currentInventory;

    // Constructor
    public InventoryInputDialog(JFrame parent, boolean isEditMode, Inventory inventory) {
        super(parent, "Inventory Details", true);
        this.isEditMode = isEditMode;
        this.currentInventory = inventory;

        // Set the dialog layout to GridBagLayout for better alignment
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        getContentPane().setBackground(Color.WHITE);

        // Create Item dropdown
        itemDropdown = new JComboBox<>();
        List<Item> items = new InventoryDAO().getAllItems();
        for (Item item : items) {
            itemDropdown.addItem(item);
        }
        if (isEditMode) {
            itemDropdown.setSelectedItem(new Item(currentInventory.getItemId(), null, 0, null, 0)); // Set selected item
        }

        gbc.insets = new Insets(10, 10, 10, 10);  // Set padding between components

        // Item Dropdown
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Item:"), gbc);
        gbc.gridx = 1;
        add(itemDropdown, gbc);

        // Stock Quantity
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Stock Quantity:"), gbc);
        stockQuantityField = new JTextField(isEditMode ? String.valueOf(currentInventory.getStockQuantity()) : "");
        stockQuantityField.setPreferredSize(new Dimension(200, 30)); // Increased width for stock quantity field
        gbc.gridx = 1;
        add(stockQuantityField, gbc);

        // Reorder Level
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Reorder Level:"), gbc);
        reorderLevelField = new JTextField(isEditMode ? String.valueOf(currentInventory.getReorderLevel()) : "");
        reorderLevelField.setPreferredSize(new Dimension(200, 30)); // Increased width for reorder level field
        gbc.gridx = 1;
        add(reorderLevelField, gbc);

        // Save and Cancel buttons
        saveButton = new JButton(isEditMode ? "Update Inventory" : "Add Inventory");
        cancelButton = new JButton("Cancel");

        // Style buttons
        styleButton(saveButton);
        styleButton(cancelButton);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;  // Take both columns
        add(buttonPanel, gbc);

        // Action Listeners for buttons
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveInventory();
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Set dialog properties
        setSize(400, 300);
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    // Method to save inventory (either add or update)
    private void saveInventory() {
        Item selectedItem = (Item) itemDropdown.getSelectedItem();
        int stockQuantity;
        int reorderLevel;

        // Validate fields
        try {
            stockQuantity = Integer.parseInt(stockQuantityField.getText());
            reorderLevel = Integer.parseInt(reorderLevelField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid quantities.");
            return;
        }

        // Prepare the inventory object for update or addition
        Inventory newInventory = new Inventory(
                isEditMode ? currentInventory.getInventoryId() : 0,  // Use current inventory ID for update
                selectedItem.getItemId(),
                stockQuantity,
                reorderLevel,
                new java.sql.Timestamp(System.currentTimeMillis()) // Set last updated timestamp
        );

        boolean success;
        if (isEditMode) {
            // If in edit mode, update the inventory
            success = new InventoryDAO().updateInventory(newInventory);
        } else {
            // If in add mode, insert a new inventory record
            success = new InventoryDAO().addInventory(newInventory);
        }

        if (success) {
            JOptionPane.showMessageDialog(this, isEditMode ? "Inventory updated successfully." : "Inventory added successfully.");
            dispose();  // Close the dialog
        } else {
            JOptionPane.showMessageDialog(this, "Error saving inventory.");
        }
    }



    // Method to style buttons with custom background color, font, and text color
    private void styleButton(JButton button) {
        button.setBackground(new Color(77, 46, 10)); // Set background color of button
        button.setForeground(Color.WHITE); // Set text color to white
        button.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Set font
        button.setPreferredSize(new Dimension(135, 35));  // Set button size
    }
}
