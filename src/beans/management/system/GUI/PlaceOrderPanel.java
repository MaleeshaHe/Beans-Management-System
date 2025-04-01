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
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.List;

public class PlaceOrderPanel extends JPanel {

    private JTable itemsTable, selectedItemsTable;
    private DefaultTableModel itemsTableModel, selectedItemsTableModel;
    private JComboBox<Customer> customersDropdown;
    private JComboBox<Promotion> promotionsDropdown;
    private JButton placeOrderButton;
    private JLabel totalLabel, discountLabel, finalTotalLabel;
    private ItemDAO itemDAO;
    private OrderDAO orderDAO;
    private CustomerDAO customerDAO;
    private PromotionDAO promotionDAO;
    private double totalAmount = 0.0;
    private double discountAmount = 0.0;
    private int selectedCustomerId, selectedPromoId;
    private List<OrderItem> selectedItems = new ArrayList<>();
    private int selectedItemId ;

    public PlaceOrderPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        itemDAO = new ItemDAO();
        orderDAO = new OrderDAO();
        customerDAO = new CustomerDAO();
        promotionDAO = new PromotionDAO();

        // Split layout: Left side for items, right side for order summary and customer/promotion
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setDividerSize(10);

        // Left panel for items table
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        String[] itemColumns = {"Item Name", "Price", "Description", "Quantity"};
        itemsTableModel = new DefaultTableModel(itemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only allow editing quantity
            }
        };
        itemsTable = new JTable(itemsTableModel);
        itemsTable.setRowHeight(30);
        itemsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane itemScrollPane = new JScrollPane(itemsTable);
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
        rightTopPanel.add(new JLabel("Total Amount:"));
        totalLabel = new JLabel("$0.00");
        rightTopPanel.add(totalLabel);

        rightPanel.add(rightTopPanel, BorderLayout.NORTH);

        // Table for selected items (Right Side)
        String[] selectedItemColumns = {"Item Name", "Price", "Quantity", "Total"};
        selectedItemsTableModel = new DefaultTableModel(selectedItemColumns, 0);
        selectedItemsTable = new JTable(selectedItemsTableModel);
        selectedItemsTable.setRowHeight(30);
        selectedItemsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane selectedItemsScrollPane = new JScrollPane(selectedItemsTable);
        rightPanel.add(selectedItemsScrollPane, BorderLayout.CENTER);

        // Bottom panel for discount and final total
        JPanel bottomPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        bottomPanel.add(new JLabel("Discount:"));
        discountLabel = new JLabel("$0.00");
        bottomPanel.add(discountLabel);
        bottomPanel.add(new JLabel("Final Total:"));
        finalTotalLabel = new JLabel("$0.00");
        bottomPanel.add(finalTotalLabel);

        // Place Order Button
        placeOrderButton = new JButton("Place Order");
        placeOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeOrder();
            }
        });
        styleButton(placeOrderButton);
        bottomPanel.add(placeOrderButton);

        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);
    }

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

    private void addItemToOrder() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow != -1) {
            String itemName = (String) itemsTable.getValueAt(selectedRow, 0); // Item Name
            double itemPrice = (Double) itemsTable.getValueAt(selectedRow, 1); // Item Price

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
                    itemName, itemPrice, quantity, itemPrice * quantity
                });

                updateTotalAmount();
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid quantity.");
            }
        }
    }


    private void updateTotalAmount() {
        totalAmount = 0;
        discountAmount = 0;
        for (OrderItem item : selectedItems) {
            totalAmount += item.getQuantity() * itemDAO.getItemById(item.getItemId()).getPrice();
        }
        // Assuming promotions and discount logic is added later
        finalTotalLabel.setText("$" + totalAmount);
        totalLabel.setText("$" + totalAmount);
        discountLabel.setText("$" + discountAmount);
    }

    private void placeOrder() {
        selectedCustomerId = ((Customer) customersDropdown.getSelectedItem()).getUserId();
        selectedPromoId = ((Promotion) promotionsDropdown.getSelectedItem()).getPromotionId();

        if (selectedItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add items to the order.");
            return;
        }

        // Create Order, OrderItem, and Receipt objects
        Order order = new Order(0, totalAmount, new Date(), "Placed", selectedCustomerId, 1, selectedPromoId);
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
        totalLabel.setText("$0.00");
        discountLabel.setText("$0.00");
        finalTotalLabel.setText("$0.00");
        customersDropdown.setSelectedIndex(0);
        promotionsDropdown.setSelectedIndex(0);
    }

    // Button styling method
    private void styleButton(JButton button) {
        button.setBackground(new Color(77, 46, 10));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
    }
}
