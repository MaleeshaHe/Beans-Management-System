package beans.management.system.GUI;

import beans.management.system.DAO.PromotionDAO;
import beans.management.system.Model.Promotion;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ManagePromotions extends JPanel {

    private JTable promotionsTable;
    private DefaultTableModel tableModel;
    private JButton addButton, editButton, deleteButton;
    private PromotionDAO promotionDAO;

    public ManagePromotions() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);  // Set panel background to white

        // Initialize the PromotionDAO to interact with the database
        promotionDAO = new PromotionDAO();

        // Create table model and JTable for displaying promotions
        String[] columnNames = {"Promotion ID", "Promo Code", "Start Date", "End Date", "Description", "Discount %"};
        tableModel = new DefaultTableModel(columnNames, 0);
        promotionsTable = new JTable(tableModel);

        // Customize JTable appearance
        promotionsTable.setBackground(Color.WHITE); // Set background to white
        promotionsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Set font to Segoe UI, 12pt
        promotionsTable.setRowHeight(30); // Set row height for better readability

        // Set the header background and font color
        promotionsTable.getTableHeader().setBackground(new Color(8, 103, 147)); // Set header background color
        promotionsTable.getTableHeader().setForeground(Color.WHITE); // Set header text color
        promotionsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12)); // Set header font

        // Add scroll pane to the table
        JScrollPane scrollPane = new JScrollPane(promotionsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create and add header label before the table
        JLabel headerLabel = new JLabel("Promotion Management", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(new Color(8, 103, 147));  // Set header text color
        headerLabel.setPreferredSize(new Dimension(600, 40));  // Set height for header
        add(headerLabel, BorderLayout.NORTH);  // Add the header label to the north of the panel

        // Create button panel for Add, Edit, and Delete buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);  // Set background to white

        // Add Promotion Button
        addButton = new JButton("Add Promotion");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPromotion();
            }
        });
        styleButton(addButton);  // Apply style to button
        buttonPanel.add(addButton);

        // Edit Promotion Button
        editButton = new JButton("Edit Promotion");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editPromotion();
            }
        });
        styleButton(editButton);  // Apply style to button
        buttonPanel.add(editButton);

        // Delete Promotion Button (Soft Delete)
        deleteButton = new JButton("Delete Promotion");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deletePromotion();
            }
        });
        styleButton(deleteButton);  // Apply style to button
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load promotion data from the database
        loadPromotionData();
    }

    // Load promotion data from the database
    private void loadPromotionData() {
        // Clear existing rows before loading fresh data
        tableModel.setRowCount(0);

        List<Promotion> promotions = promotionDAO.getAllPromotions();
        for (Promotion promotion : promotions) {
            tableModel.addRow(new Object[]{
                promotion.getPromotionId(), promotion.getPromoCode(), promotion.getStartDate(),
                promotion.getEndDate(), promotion.getDescription(), promotion.getDiscountPercentage()
            });
        }
    }

    // Add a new promotion (using the database to persist data)
    private void addPromotion() {
        // Open PromotionInputDialog in Add mode
        new PromotionInputDialog((JFrame) SwingUtilities.getWindowAncestor(this), false, null);
        loadPromotionData();  // Refresh the promotion list
    }

    // Edit selected promotion (using the database to persist changes)
    private void editPromotion() {
        int selectedRow = promotionsTable.getSelectedRow();
        if (selectedRow != -1) {
            int promotionId = (int) tableModel.getValueAt(selectedRow, 0);
            String promoCode = (String) tableModel.getValueAt(selectedRow, 1);
            String startDate = (String) tableModel.getValueAt(selectedRow, 2);
            String endDate = (String) tableModel.getValueAt(selectedRow, 3);
            String description = (String) tableModel.getValueAt(selectedRow, 4);
            Double discountPercentage = (Double) tableModel.getValueAt(selectedRow, 5);

            // Create a Promotion object for editing
            Promotion promotionToEdit = new Promotion(promotionId, promoCode, startDate, endDate, description, discountPercentage);
            // Open PromotionInputDialog in Edit mode
            new PromotionInputDialog((JFrame) SwingUtilities.getWindowAncestor(this), true, promotionToEdit);
            loadPromotionData();  // Refresh the promotion list
        } else {
            JOptionPane.showMessageDialog(this, "Please select a promotion to edit.");
        }
    }

    // Soft Delete selected promotion (mark as deleted)
    private void deletePromotion() {
        int selectedRow = promotionsTable.getSelectedRow();
        if (selectedRow != -1) {
            int promotionId = (int) tableModel.getValueAt(selectedRow, 0);
            boolean isDeleted = promotionDAO.softDeletePromotion(promotionId);
            if (isDeleted) {
                JOptionPane.showMessageDialog(this, "Promotion marked as deleted successfully.");
                loadPromotionData();  // Refresh the promotion list
            } else {
                JOptionPane.showMessageDialog(this, "Error deleting promotion.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a promotion to delete.");
        }
    }

    // Method to style buttons with custom background color, font, and text color
    private void styleButton(JButton button) {
        button.setBackground(new Color(8, 103, 147)); // Set the background color of the button
        button.setForeground(Color.WHITE); // Set the text color to white
        button.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Set font to Segoe UI, 12 pt, Bold
    }

    public static void main(String[] args) {
        // Create and show the Manage Promotions frame
        JFrame frame = new JFrame("Manage Promotions");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.add(new ManagePromotions());
        frame.setVisible(true);
    }
}
