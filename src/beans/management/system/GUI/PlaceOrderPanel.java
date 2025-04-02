package beans.management.system.GUI;

import beans.management.system.DAO.OrderDAO;
import beans.management.system.DAO.ItemDAO;
import beans.management.system.DAO.CustomerDAO;
import beans.management.system.DAO.PromotionDAO;
import beans.management.system.Model.Order;
import beans.management.system.Model.OrderItem;
import beans.management.system.Model.Receipt;
import beans.management.system.Model.Item;
import beans.management.system.Model.Customer;
import beans.management.system.Model.Promotion;
import beans.management.system.Model.User;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.List;
import utils.SessionManager;

public class PlaceOrderPanel extends JPanel {

    private JTable itemsTable, selectedItemsTable;
    private DefaultTableModel itemsTableModel, selectedItemsTableModel;
    private JComboBox<Customer> customersDropdown;
    private JComboBox<Promotion> promotionsDropdown;
    private JButton placeOrderButton, clearButton;
    private JLabel totalLabel, discountLabel, finalTotalLabel;
    private ItemDAO itemDAO;
    private OrderDAO orderDAO;
    private CustomerDAO customerDAO;
    private PromotionDAO promotionDAO;
    private double totalAmount = 0.0;
    private double discountAmount = 0.0;
    private int selectedCustomerId, selectedPromoId;
    private List<OrderItem> selectedItems = new ArrayList<>();
    private int selectedItemId;

    public PlaceOrderPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Add padding around the content

        itemDAO = new ItemDAO();
        orderDAO = new OrderDAO();
        customerDAO = new CustomerDAO();
        promotionDAO = new PromotionDAO();

        // Page Heading
        JPanel headingPanel = new JPanel();
        headingPanel.setBackground(Color.WHITE);
        JLabel pageHeading = new JLabel("Place Order", JLabel.CENTER);
        pageHeading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        pageHeading.setForeground(new Color(77, 46, 10));
        headingPanel.add(pageHeading);
        add(headingPanel, BorderLayout.NORTH);

        // Split layout: Left side for items, right side for order summary and customer/promotion
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setDividerSize(10);

        // Left panel for items table
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);

        // Header label for items table
        JLabel itemsHeader = new JLabel("Select Items to Order", JLabel.CENTER);
        itemsHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        itemsHeader.setForeground(new Color(77, 46, 10));
        itemsHeader.setPreferredSize(new Dimension(600, 40));
        leftPanel.add(itemsHeader, BorderLayout.NORTH);

        String[] itemColumns = {"Item Name", "Price (SAR)", "Description", "Quantity"};
        itemsTableModel = new DefaultTableModel(itemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only allow editing quantity
            }
        };
        itemsTable = new JTable(itemsTableModel);
        itemsTable.setRowHeight(30);
        itemsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        itemsTable.setSelectionBackground(new Color(77, 46, 10));  // Highlight selected rows with the same color as buttons
        itemsTable.setSelectionForeground(Color.WHITE); // Text color for selected row

        // Set table header styles
        itemsTable.getTableHeader().setBackground(new Color(77, 46, 10)); // Header background color
        itemsTable.getTableHeader().setForeground(Color.WHITE); // Header text color
        itemsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14)); // Header font style

        JScrollPane itemScrollPane = new JScrollPane(itemsTable);
        itemScrollPane.setPreferredSize(new Dimension(600, 250));  // Set fixed height for table
        leftPanel.add(itemScrollPane, BorderLayout.CENTER);

        loadItems();
        itemsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // Double-click to add item to order list
                    addItemToOrder();
                }
            }
        });

        // Right panel for selected items and details
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        JPanel rightTopPanel = new JPanel();
        rightTopPanel.setLayout(new GridLayout(3, 2, 10, 10));

        // Header label for the right panel
        JLabel rightHeader = new JLabel("Order Summary", JLabel.CENTER);
        rightHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rightHeader.setForeground(new Color(77, 46, 10));
        rightHeader.setPreferredSize(new Dimension(600, 40));
        rightPanel.add(rightHeader, BorderLayout.NORTH);

        // Customer Dropdown
        rightTopPanel.add(new JLabel("Select Customer:"));
        customersDropdown = new JComboBox<>();
        loadCustomers();
        rightTopPanel.add(customersDropdown);

        // Promotion Dropdown
        rightTopPanel.add(new JLabel("Select Promotion:"));
        promotionsDropdown = new JComboBox<>();
        loadPromotions();
        rightTopPanel.add(promotionsDropdown);

        // Total Labels
        rightTopPanel.add(new JLabel("Total Amount (SAR):"));
        totalLabel = new JLabel("SAR 0.00");
        rightTopPanel.add(totalLabel);

        rightPanel.add(rightTopPanel, BorderLayout.NORTH);

        // Add header for Order Summary Table
        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        JLabel tableHeader = new JLabel("Selected Items", JLabel.CENTER);
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableHeader.setForeground(new Color(77, 46, 10));
        tableHeader.setPreferredSize(new Dimension(600, 40));
        tableHeaderPanel.add(tableHeader, BorderLayout.NORTH);

        // Table for selected items (Right Side)
        String[] selectedItemColumns = {"Item Name", "Price (SAR)", "Quantity", "Total (SAR)"};
        selectedItemsTableModel = new DefaultTableModel(selectedItemColumns, 0);
        selectedItemsTable = new JTable(selectedItemsTableModel);
        selectedItemsTable.setRowHeight(30);
        selectedItemsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        selectedItemsTable.setSelectionBackground(new Color(77, 46, 10));
        selectedItemsTable.setSelectionForeground(Color.WHITE);

        // Set table header styles for selected items table
        selectedItemsTable.getTableHeader().setBackground(new Color(77, 46, 10));
        selectedItemsTable.getTableHeader().setForeground(Color.WHITE);
        selectedItemsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane selectedItemsScrollPane = new JScrollPane(selectedItemsTable);
        selectedItemsScrollPane.setPreferredSize(new Dimension(600, 250));  // Set fixed height for table
        tableHeaderPanel.add(selectedItemsScrollPane, BorderLayout.CENTER);
        rightPanel.add(tableHeaderPanel, BorderLayout.CENTER);

        // Bottom panel for discount and final total
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Add padding between elements
        gbc.anchor = GridBagConstraints.WEST;
        bottomPanel.setBackground(Color.WHITE);

        // Discount Label
        JLabel discountLabelTitle = new JLabel("Discount (SAR):");
        discountLabelTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        discountLabelTitle.setForeground(new Color(77, 46, 10));
        discountLabel = new JLabel("SAR 0.00");
        discountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        discountLabel.setForeground(new Color(77, 46, 10));
        gbc.gridx = 0;
        gbc.gridy = 0;
        bottomPanel.add(discountLabelTitle, gbc);
        gbc.gridx = 1;
        bottomPanel.add(discountLabel, gbc);

        // Final Total Label
        JLabel finalTotalLabelTitle = new JLabel("Final Total (SAR):");
        finalTotalLabelTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        finalTotalLabelTitle.setForeground(new Color(77, 46, 10));
        finalTotalLabel = new JLabel("SAR 0.00");
        finalTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        finalTotalLabel.setForeground(new Color(77, 46, 10));
        gbc.gridx = 0;
        gbc.gridy = 1;
        bottomPanel.add(finalTotalLabelTitle, gbc);
        gbc.gridx = 1;
        bottomPanel.add(finalTotalLabel, gbc);

        // Place Order Button and Clear Button in the same line
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)); // FlowLayout for horizontal alignment
        placeOrderButton = new JButton("Place Order");
        placeOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeOrder();
            }
        });
        styleButton(placeOrderButton);

        clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetForm();
            }
        });
        styleButton(clearButton);

        buttonPanel.add(placeOrderButton);
        buttonPanel.add(clearButton);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2; // Make the button panel span both columns
        bottomPanel.add(buttonPanel, gbc);

        // Adjust the bottom panel size and add to the right panel
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);

        // Method to update the discount when a promotion is selected
        promotionsDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedPromoId = ((Promotion) promotionsDropdown.getSelectedItem()).getPromotionId();
                updateTotalAmount();  // Recalculate and update total and discount whenever promotion is changed
            }
        });
    }

    // Existing code for loadItems(), loadCustomers(), loadPromotions(), updateTotalAmount(), addItemToOrder(), placeOrder(), and resetForm()


    private void loadItems() {
        List<Item> items = itemDAO.getAllItems();
        for (Item item : items) {
            itemsTableModel.addRow(new Object[]{item.getItemName(), item.getPrice(), item.getDescription(), 0});
        }
    }

    private void loadCustomers() {
        List<Customer> customers = customerDAO.getAllCustomers();
        for (Customer customer : customers) {
            customersDropdown.addItem(customer);
        }
    }

    private void loadPromotions() {
        List<Promotion> promotions = promotionDAO.getAllPromotions();
        for (Promotion promotion : promotions) {
            promotionsDropdown.addItem(promotion);
        }
    }

    private void updateTotalAmount() {
        double totalAmount = 0.0;
        // Calculate total amount for items in the order
        for (int i = 0; i < selectedItemsTableModel.getRowCount(); i++) {
            try {
                // Get the item total price from the table (remove the "SAR " prefix)
                String totalStr = selectedItemsTableModel.getValueAt(i, 3).toString();
                totalStr = totalStr.replace("SAR", "").trim(); // Remove "SAR"
                totalAmount += Double.parseDouble(totalStr);  // Add to total amount
            } catch (NumberFormatException e) {
                e.printStackTrace();  // Handle invalid data
            }
        }

        // Apply discount if promotion is selected
        if (selectedPromoId != -1) {
            // Get selected promo code and discount percentage
            double discountPercentage = promotionDAO.getDiscountById(selectedPromoId);
            discountAmount = totalAmount * (discountPercentage / 100);
            totalAmount -= discountAmount;  // Apply discount to total amount
        }

        // Update the total amount and discount in the UI
        totalLabel.setText("SAR " + String.format("%.2f", totalAmount));
        discountLabel.setText("SAR " + String.format("%.2f", discountAmount));
        finalTotalLabel.setText("SAR " + String.format("%.2f", totalAmount));
        this.totalAmount = totalAmount;  // Store the total amount for placing the order
    }

    private void addItemToOrder() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow != -1) {
            String itemName = (String) itemsTable.getValueAt(selectedRow, 0); // Item Name
            double itemPrice = 0.0;

            // Get the item price from the table (it should be a Double)
            try {
                itemPrice = Double.parseDouble(itemsTable.getValueAt(selectedRow, 1).toString().replace("SAR", "").trim()); // Remove "SAR" and parse the price
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid item price.");
                return;
            }

            // Get the item ID by its name
            selectedItemId = itemDAO.getItemIdByName(itemName); // Fetch the item ID

            int quantity = 0;
            try {
                quantity = Integer.parseInt(itemsTable.getValueAt(selectedRow, 3).toString()); // Fetch quantity from table
            } catch (NumberFormatException e) {
                // Handle invalid quantity input gracefully
                JOptionPane.showMessageDialog(this, "Please enter a valid quantity.");
                return;
            }

            // Ensure that quantity is greater than 0 before adding the item
            if (quantity > 0) {
                // Create the OrderItem and add it to the selected items list
                selectedItems.add(new OrderItem(0, selectedItemId, quantity));

                // Add the selected item to the right table
                selectedItemsTableModel.addRow(new Object[]{
                        itemName, "SAR " + itemPrice, quantity, "SAR " + (itemPrice * quantity)
                });

                updateTotalAmount();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid quantity.");
            }
        }
    }


    private void placeOrder() {
        selectedCustomerId = ((Customer) customersDropdown.getSelectedItem()).getUserId();
        selectedPromoId = ((Promotion) promotionsDropdown.getSelectedItem()).getPromotionId();

        if (selectedItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add items to the order.");
            return;
        }
        
        User currentUser = SessionManager.getCurrentUser();

        // Create Order, OrderItem, and Receipt objects
        Order order = new Order(0, totalAmount, new Date(), "Placed", selectedCustomerId, currentUser.getUserId(), selectedPromoId);
        Receipt receipt = new Receipt(0, "Cash", new Date(), totalAmount, 0);  // Use "Cash" for payment method as an example

        boolean success = orderDAO.placeOrder(order, selectedItems, receipt);
        if (success) {
            JOptionPane.showMessageDialog(this, "Order placed successfully!");
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this, "Error placing order.");
        }
    }

    private void resetForm() {
        selectedItems.clear();
        selectedItemsTableModel.setRowCount(0);
        totalLabel.setText("SAR 0.00");
        discountLabel.setText("SAR 0.00");
        finalTotalLabel.setText("SAR 0.00");
        customersDropdown.setSelectedIndex(0);
        promotionsDropdown.setSelectedIndex(0);
    }

    // Button styling method
    private void styleButton(JButton button) {
        button.setBackground(new Color(77, 46, 10));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(130, 30));
    }
}
