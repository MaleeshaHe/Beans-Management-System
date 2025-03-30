package beans.management.system.GUI;

import beans.management.system.DAO.InventoryDAO;
import beans.management.system.Model.Inventory;
import beans.management.system.Model.Item;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ManageInventory extends JPanel {

    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton;
    private InventoryDAO inventoryDAO;

    public ManageInventory() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);  // Set panel background to white

        // Initialize the InventoryDAO to interact with the database
        inventoryDAO = new InventoryDAO();

        // Create table model and JTable for displaying inventory items
        String[] columnNames = {"Item Name", "Stock Quantity", "Reorder Level", "Last Updated", "Inventory ID"};
        tableModel = new DefaultTableModel(columnNames, 0);
        inventoryTable = new JTable(tableModel);

        // Customize JTable appearance
        inventoryTable.setBackground(Color.WHITE);
        inventoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Set font to Segoe UI, 12pt
        inventoryTable.setRowHeight(30);  // Set row height for better readability

        // Set the header background and font color
        inventoryTable.getTableHeader().setBackground(new Color(77, 46, 10));
        inventoryTable.getTableHeader().setForeground(Color.WHITE);
        inventoryTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Add scroll pane to the table
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create and add header label before the table
        JLabel headerLabel = new JLabel("Inventory Management", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(new Color(77, 46, 10));
        headerLabel.setPreferredSize(new Dimension(600, 40));
        add(headerLabel, BorderLayout.NORTH);

        // Create button panel for Add, Edit, and Delete buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        // Add Inventory Button
        addButton = new JButton("Add Inventory");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addInventory();
            }
        });
        styleButton(addButton);
        buttonPanel.add(addButton);

        // Edit Inventory Button
        editButton = new JButton("Edit Inventory");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editInventory();
            }
        });
        styleButton(editButton);
        buttonPanel.add(editButton);

        // Delete Inventory Button
        deleteButton = new JButton("Delete Inventory");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteInventory();
            }
        });
        styleButton(deleteButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load inventory data from the database
        loadInventoryData();
    }

    // Load inventory data from the database
    private void loadInventoryData() {
        tableModel.setRowCount(0);  // Clear existing rows

        List<Inventory> inventoryItems = inventoryDAO.getAllInventory();
        for (Inventory inventory : inventoryItems) {
            // Get item details for each inventory entry
            Item item = inventoryDAO.getAllItems().stream()
                    .filter(i -> i.getItemId() == inventory.getItemId())
                    .findFirst().orElse(null);

            String itemName = item != null ? item.getItemName() : "Unknown";

            // Add inventory data to the table, include inventoryId in the last column
            tableModel.addRow(new Object[]{
                    itemName, inventory.getStockQuantity(), inventory.getReorderLevel(), inventory.getLastUpdated(), inventory.getInventoryId()
            });
        }
    }



    // Add a new inventory item
    private void addInventory() {
        new InventoryInputDialog((JFrame) SwingUtilities.getWindowAncestor(this), false, null);
        loadInventoryData();  // Refresh the inventory list
    }

    // Edit selected inventory item
    private void editInventory() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow != -1) {
            // Get the inventoryId from the selected row (the 5th column, index 4)
            int inventoryId = (Integer) tableModel.getValueAt(selectedRow, 4);  // Correct index for inventoryId
            String itemName = (String) tableModel.getValueAt(selectedRow, 0);  // Get the item name from the table
            int stockQuantity = (Integer) tableModel.getValueAt(selectedRow, 1);  // Get stock quantity
            int reorderLevel = (Integer) tableModel.getValueAt(selectedRow, 2);  // Get reorder level

            // Get the item based on the itemName
            Item selectedItem = inventoryDAO.getAllItems().stream()
                    .filter(item -> item.getItemName().equals(itemName))
                    .findFirst().orElse(null);

            // Create the inventory object for editing
            Inventory inventoryToEdit = new Inventory(inventoryId, selectedItem.getItemId(), stockQuantity, reorderLevel, null);

            // Open InventoryInputDialog in Edit mode
            new InventoryInputDialog((JFrame) SwingUtilities.getWindowAncestor(this), true, inventoryToEdit);
            loadInventoryData();  // Refresh the inventory list after editing
        } else {
            JOptionPane.showMessageDialog(this, "Please select an inventory item to edit.");
        }
    }



    // Delete selected inventory item
    private void deleteInventory() {
        int selectedRow = inventoryTable.getSelectedRow();  // Get selected row from table
        if (selectedRow != -1) {
            // Get the inventoryId from the selected row (the last column)
            int inventoryId = (Integer) tableModel.getValueAt(selectedRow, 4); // Correct index for inventoryId
            // Delete the inventory item using the DAO
            boolean isDeleted = inventoryDAO.deleteInventory(inventoryId);
            if (isDeleted) {
                JOptionPane.showMessageDialog(this, "Inventory item deleted successfully.");
                loadInventoryData();  // Refresh the inventory list after deletion
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting inventory item.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an inventory item to delete.");
        }
    }

    // Method to style buttons with custom background color, font, and text color
    private void styleButton(JButton button) {
        button.setBackground(new Color(77, 46, 10)); // Set background color of button
        button.setForeground(Color.WHITE); // Set text color to white
        button.setFont(new Font("Segoe UI", Font.BOLD, 14)); // Set font
    }

    public static void main(String[] args) {
        // Create and show the Manage Inventory frame
        JFrame frame = new JFrame("Manage Inventory");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.add(new ManageInventory());
        frame.setVisible(true);
    }
}
