package beans.management.system.GUI;

import beans.management.system.DAO.ItemDAO;
import beans.management.system.Model.Item;
import beans.management.system.Model.Category;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ManageItems extends JPanel {

    private JTable itemsTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton;
    private ItemDAO itemDAO;

    public ManageItems() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);  // Set panel background to white

        // Initialize the ItemDAO to interact with the database
        itemDAO = new ItemDAO();

        // Create table model and JTable for displaying items
        String[] columnNames = {"Item Name", "Price", "Description", "Category"};
        tableModel = new DefaultTableModel(columnNames, 0);
        itemsTable = new JTable(tableModel);

        // Customize JTable appearance
        itemsTable.setBackground(Color.WHITE); // Set background to white
        itemsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Set font to Segoe UI, 12pt
        itemsTable.setRowHeight(30); // Set row height for better readability

        // Set the header background and font color
        itemsTable.getTableHeader().setBackground(new Color(8, 103, 147)); // Set header background color
        itemsTable.getTableHeader().setForeground(Color.WHITE); // Set header text color
        itemsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12)); // Set header font

        // Add scroll pane to the table
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create and add header label before the table
        JLabel headerLabel = new JLabel("Item Management", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(new Color(8, 103, 147));  // Set header text color
        headerLabel.setPreferredSize(new Dimension(600, 40));  // Set height for header
        add(headerLabel, BorderLayout.NORTH);  // Add the header label to the north of the panel

        // Create button panel for Add, Edit, and Delete buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);  // Set background to white

        // Add Item Button
        addButton = new JButton("Add Item");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItem();
            }
        });
        styleButton(addButton);  // Apply style to button
        buttonPanel.add(addButton);

        // Edit Item Button
        editButton = new JButton("Edit Item");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editItem();
            }
        });
        styleButton(editButton);  // Apply style to button
        buttonPanel.add(editButton);

        // Delete Item Button (Soft Delete)
        deleteButton = new JButton("Delete Item");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteItem();
            }
        });
        styleButton(deleteButton);  // Apply style to button
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load item data from the database
        loadItemData();
    }

    // Load item data from the database
    private void loadItemData() {
        // Clear existing rows before loading fresh data
        tableModel.setRowCount(0);

        List<Item> items = itemDAO.getAllItems();
        for (Item item : items) {
            // Get the category name by querying the CategoryDAO
            Category category = itemDAO.getAllCategories().stream()
                    .filter(cat -> cat.getCategoryId() == item.getCategoryId())
                    .findFirst().orElse(null);
            String categoryName = category != null ? category.getCategoryName() : "Unknown";

            tableModel.addRow(new Object[]{item.getItemName(), item.getPrice(), item.getDescription(), categoryName});
        }
    }


    // Add a new item (using the database to persist data)
    private void addItem() {
        // Open ItemInputDialog in Add mode
        new ItemInputDialog((JFrame) SwingUtilities.getWindowAncestor(this), false, null);
        loadItemData();  // Refresh the item list
    }

    // Edit selected item (using the database to persist changes)
      private void editItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow != -1) {
            // Get the item data from the selected row
            String itemName = (String) tableModel.getValueAt(selectedRow, 0);
            double price = (Double) tableModel.getValueAt(selectedRow, 1);
            String description = (String) tableModel.getValueAt(selectedRow, 2);
            String categoryName = (String) tableModel.getValueAt(selectedRow, 3);

            // Get the category ID based on the category name
            Category selectedCategory = itemDAO.getAllCategories().stream()
                    .filter(cat -> cat.getCategoryName().equals(categoryName))
                    .findFirst().orElse(null);

            // Get the item ID (ensure itemId is passed from the table, or get it based on itemName)
            int itemId = itemDAO.getItemIdByName(itemName);  // Ensure you get the item ID correctly

            // Create a new Item object for editing
            Item itemToEdit = new Item(itemId, itemName, price, description, selectedCategory != null ? selectedCategory.getCategoryId() : 0);

            // Open ItemInputDialog in Edit mode
            new ItemInputDialog((JFrame) SwingUtilities.getWindowAncestor(this), true, itemToEdit);

            // Refresh the item list after editing
            loadItemData();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to edit.");
        }
    }



    // Soft Delete selected item (mark as deleted)
    private void deleteItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow != -1) {
            // Get the item name from the selected row
            String itemName = (String) tableModel.getValueAt(selectedRow, 0);

            // Retrieve the item ID using the item name
            int itemId = itemDAO.getItemIdByName(itemName);

            // Check if the item ID is valid
            if (itemId != -1) {
                boolean isDeleted = itemDAO.softDeleteItem(itemId); // Soft delete the item using its ID
                if (isDeleted) {
                    JOptionPane.showMessageDialog(this, "Item marked as deleted successfully.");
                    loadItemData();  // Refresh the item list after deletion
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting item.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Item not found.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to delete.");
        }
    }


    // Method to style buttons with custom background color, font, and text color
    private void styleButton(JButton button) {
        button.setBackground(new Color(8, 103, 147)); // Set the background color of the button
        button.setForeground(Color.WHITE); // Set the text color to white
        button.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Set font to Segoe UI, 12 pt, Bold
    }

    public static void main(String[] args) {
        // Create and show the Manage Items frame
        JFrame frame = new JFrame("Manage Items");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.add(new ManageItems());
        frame.setVisible(true);
    }
}
