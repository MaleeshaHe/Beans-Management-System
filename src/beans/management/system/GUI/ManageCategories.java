package beans.management.system.GUI;

import beans.management.system.DAO.CategoryDAO;
import beans.management.system.Model.Category;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ManageCategories extends JPanel {

    private JTable categoriesTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton;
    private CategoryDAO categoryDAO;

    public ManageCategories() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);  // Set panel background to white

        // Initialize the CategoryDAO to interact with the database
        categoryDAO = new CategoryDAO();

        // Create table model and JTable for displaying categories
        String[] columnNames = {"Category Name"};
        tableModel = new DefaultTableModel(columnNames, 0);
        categoriesTable = new JTable(tableModel);

        // Customize JTable appearance
        categoriesTable.setBackground(Color.WHITE); // Set background to white
        categoriesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Set font to Segoe UI, 12pt
        categoriesTable.setRowHeight(30); // Set row height for better readability

        // Set the header background and font color
        categoriesTable.getTableHeader().setBackground(new Color(77, 46, 10)); // Set header background color
        categoriesTable.getTableHeader().setForeground(Color.WHITE); // Set header text color
        categoriesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12)); // Set header font

        // Add scroll pane to the table
        JScrollPane scrollPane = new JScrollPane(categoriesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create and add header label before the table
        JLabel headerLabel = new JLabel("Category Management", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(new Color(77, 46, 10));  // Set header text color
        headerLabel.setPreferredSize(new Dimension(600, 40));  // Set height for header
        add(headerLabel, BorderLayout.NORTH);  // Add the header label to the north of the panel

        // Create button panel for Add, Edit, and Delete buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);  // Set background to white

        // Add Category Button
        addButton = new JButton("Add Category");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCategory();
            }
        });
        styleButton(addButton);  // Apply style to button
        buttonPanel.add(addButton);

        // Edit Category Button
        editButton = new JButton("Edit Category");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editCategory();
            }
        });
        styleButton(editButton);  // Apply style to button
        buttonPanel.add(editButton);

        // Delete Category Button (Soft Delete)
        deleteButton = new JButton("Delete Category");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCategory();
            }
        });
        styleButton(deleteButton);  // Apply style to button
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load category data from the database
        loadCategoryData();
    }

    // Load category data from the database
    private void loadCategoryData() {
        // Clear existing rows before loading fresh data
        tableModel.setRowCount(0);

        List<Category> categories = categoryDAO.getAllCategories();
        for (Category category : categories) {
            tableModel.addRow(new Object[]{category.getCategoryName()});
        }
    }

    // Add a new category (using the database to persist data)
    private void addCategory() {
        // Open CategoryInputDialog in Add mode
        new CategoryInputDialog((JFrame) SwingUtilities.getWindowAncestor(this), false, null);
        loadCategoryData();  // Refresh the category list
    }

    // Edit selected category (using the database to persist changes)
    private void editCategory() {
        int selectedRow = categoriesTable.getSelectedRow();
        if (selectedRow != -1) {
            // Get the category data
            String categoryName = (String) tableModel.getValueAt(selectedRow, 0);

            // Get the category ID (same method as before)
            int categoryId = getCategoryIdByName(categoryName);

            // Create a Category object for editing
            Category categoryToEdit = new Category(categoryId, categoryName);

            // Open CategoryInputDialog in Edit mode
            new CategoryInputDialog((JFrame) SwingUtilities.getWindowAncestor(this), true, categoryToEdit);
            loadCategoryData();  // Refresh the category list
        } else {
            JOptionPane.showMessageDialog(this, "Please select a category to edit.");
        }
    }


    // Soft Delete selected category (mark as deleted)
    private void deleteCategory() {
        int selectedRow = categoriesTable.getSelectedRow();
        if (selectedRow != -1) {
            // Get the category name
            String categoryName = (String) tableModel.getValueAt(selectedRow, 0);

            // Get the category ID (from your DAO, assuming you load it with ID as well)
            // You should modify this to use the category's ID, which is usually passed along with the name.
            int categoryId = getCategoryIdByName(categoryName);  // Assuming a method exists for this

            boolean isDeleted = categoryDAO.softDeleteCategory(categoryId); // Use categoryId
            if (isDeleted) {
                JOptionPane.showMessageDialog(this, "Category marked as deleted successfully.");
                loadCategoryData();  // Refresh the category list
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting category.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a category to delete.");
        }
    }
    
    // Helper method to get Category ID by Category Name
    private int getCategoryIdByName(String categoryName) {
        for (Category category : categoryDAO.getAllCategories()) {
            if (category.getCategoryName().equals(categoryName)) {
                return category.getCategoryId();
            }
        }
        return -1;  // Return -1 if category is not found
    }



    // Method to style buttons with custom background color, font, and text color
    private void styleButton(JButton button) {
        button.setBackground(new Color(77, 46, 10)); // Set the background color of the button
        button.setForeground(Color.WHITE); // Set the text color to white
        button.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Set font to Segoe UI, 12 pt, Bold
    }

    public static void main(String[] args) {
        // Create and show the Manage Categories frame
        JFrame frame = new JFrame("Manage Categories");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.add(new ManageCategories());
        frame.setVisible(true);
    }
}
