package beans.management.system.GUI;

import beans.management.system.DAO.InventoryDAO;
import beans.management.system.Model.Inventory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UpdateStockDialog extends JDialog {

    private JTextField stockQuantityField, reorderLevelField;
    private JButton saveButton, cancelButton;
    private Inventory currentInventory;

    // Constructor
    public UpdateStockDialog(JFrame parent, Inventory inventory) {
        super(parent, "Update Stock", true);
        this.currentInventory = inventory;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        getContentPane().setBackground(Color.WHITE);

        // Set GridBag constraints for uniform padding
        gbc.insets = new Insets(10, 10, 10, 10); // Adding padding around components

        // Stock Quantity
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Stock Quantity: "), gbc);
        stockQuantityField = new JTextField(String.valueOf(currentInventory.getStockQuantity()), 15);
        gbc.gridx = 1;
        add(stockQuantityField, gbc);

        // Reorder Level
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Reorder Level: "), gbc);
        reorderLevelField = new JTextField(String.valueOf(currentInventory.getReorderLevel()), 15);
        gbc.gridx = 1;
        add(reorderLevelField, gbc);

        // Save and Cancel Buttons
        saveButton = new JButton("Update Stock");
        cancelButton = new JButton("Cancel");

        // Styling for the buttons
        styleButton(saveButton);
        styleButton(cancelButton);

        // Add action listeners to the buttons
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveUpdatedStock();
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
        gbc.gridwidth = 2; // Span the buttons across two columns
        gbc.gridy = 2;
        add(buttonPanel, gbc);

        // Set dialog properties
        setSize(350, 200);  // Adjusted for better fit
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    // Method to handle saving or updating the inventory
    private void saveUpdatedStock() {
        int stockQuantity, reorderLevel;

        try {
            stockQuantity = Integer.parseInt(stockQuantityField.getText());
            reorderLevel = Integer.parseInt(reorderLevelField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid quantities.");
            return;
        }

        // Create the inventory object with updated stock and reorder level
        Inventory updatedInventory = new Inventory(
                currentInventory.getInventoryId(),
                currentInventory.getItemId(),  // Retain existing itemId
                stockQuantity,
                reorderLevel,
                new java.sql.Timestamp(System.currentTimeMillis())  // Set the updated timestamp
        );

        boolean success = new InventoryDAO().updateInventory(updatedInventory);

        if (success) {
            JOptionPane.showMessageDialog(this, "Stock updated successfully.");
            dispose();  // Close the dialog
        } else {
            JOptionPane.showMessageDialog(this, "Error updating stock.");
        }
    }

    // Method to style buttons with custom background color, font, and text color
    private void styleButton(JButton button) {
        button.setBackground(new Color(8, 103, 147)); // Set the background color of the button
        button.setForeground(Color.WHITE); // Set the text color to white
        button.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Set font to Segoe UI, 14 pt, Bold
        button.setPreferredSize(new Dimension(120, 40)); // Increase button size for better visibility
    }
}
