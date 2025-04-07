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
    private JButton placeOrderButton, clearButton, addCustomerButton;
    private JLabel totalLabel, discountLabel, finalTotalLabel, employeeLabel;
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
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        itemDAO = new ItemDAO();
        orderDAO = new OrderDAO();
        customerDAO = new CustomerDAO();
        promotionDAO = new PromotionDAO();

        // Page Heading
        JPanel headingPanel = new JPanel();
        headingPanel.setBackground(Color.WHITE);
        JLabel pageHeading = new JLabel("Place Order", JLabel.CENTER);
        pageHeading.setFont(new Font("Segoe UI", Font.BOLD, 24));
        pageHeading.setForeground(new Color(8, 103, 147));
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
        itemsHeader.setForeground(new Color(8, 103, 147));
        itemsHeader.setPreferredSize(new Dimension(600, 40));
        leftPanel.add(itemsHeader, BorderLayout.NORTH);
        
        String[] itemColumns = {"Item Name", "Price (SAR)", "Description", "Available Quantity", "Quantity"};
        itemsTableModel = new DefaultTableModel(itemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only allow editing in the "Quantity" column (index 4)
                return column == 4;
            }
        };


        itemsTable = new JTable(itemsTableModel);
        itemsTable.setRowHeight(30);
        itemsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        itemsTable.setSelectionBackground(new Color(8, 103, 147));
        itemsTable.setSelectionForeground(Color.WHITE);

        itemsTable.getTableHeader().setBackground(new Color(8, 103, 147));
        itemsTable.getTableHeader().setForeground(Color.WHITE);
        itemsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane itemScrollPane = new JScrollPane(itemsTable);
        itemScrollPane.setPreferredSize(new Dimension(600, 250));
        leftPanel.add(itemScrollPane, BorderLayout.CENTER);

        loadItems();
        itemsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addItemToOrder();
                }
            }
        });

        // Right panel for selected items and details
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        JPanel rightTopPanel = new JPanel();
        rightTopPanel.setLayout(new GridLayout(4, 2, 10, 10));

        JLabel rightHeader = new JLabel("Order Summary", JLabel.CENTER);
        rightHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rightHeader.setForeground(new Color(8, 103, 147));
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
        
        // Employee Name Label
        employeeLabel = new JLabel("Employee: ");
        rightTopPanel.add(employeeLabel);
        
        User currentUser = SessionManager.getCurrentUser();
        String employeeName = currentUser.getFirstName() + " " + currentUser.getLastName();
        employeeLabel.setText("Employee: " + employeeName);


        // Total Labels
        rightTopPanel.add(new JLabel("Total Amount (SAR):"));
        totalLabel = new JLabel("SAR 0.00");
        rightTopPanel.add(totalLabel);
        
        // Add Customer Button
        addCustomerButton = new JButton("Add Customer");
        addCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addCustomer();
            }
        });
        styleButton(addCustomerButton);
        rightTopPanel.add(addCustomerButton);

        rightPanel.add(rightTopPanel, BorderLayout.NORTH);

        // Add header for Order Summary Table
        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        JLabel tableHeader = new JLabel("Selected Items", JLabel.CENTER);
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableHeader.setForeground(new Color(8, 103, 147));
        tableHeader.setPreferredSize(new Dimension(600, 40));
        tableHeaderPanel.add(tableHeader, BorderLayout.NORTH);

        String[] selectedItemColumns = {"Item Name", "Price (SAR)", "Quantity", "Total (SAR)"};
        selectedItemsTableModel = new DefaultTableModel(selectedItemColumns, 0);
        selectedItemsTable = new JTable(selectedItemsTableModel);
        selectedItemsTable.setRowHeight(30);
        selectedItemsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        selectedItemsTable.setSelectionBackground(new Color(8, 103, 147));
        selectedItemsTable.setSelectionForeground(Color.WHITE);

        selectedItemsTable.getTableHeader().setBackground(new Color(8, 103, 147));
        selectedItemsTable.getTableHeader().setForeground(Color.WHITE);
        selectedItemsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane selectedItemsScrollPane = new JScrollPane(selectedItemsTable);
        selectedItemsScrollPane.setPreferredSize(new Dimension(600, 250));
        tableHeaderPanel.add(selectedItemsScrollPane, BorderLayout.CENTER);
        rightPanel.add(tableHeaderPanel, BorderLayout.CENTER);

        // Bottom panel for discount and final total
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        bottomPanel.setBackground(Color.WHITE);

        JLabel discountLabelTitle = new JLabel("Discount (SAR):");
        discountLabelTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        discountLabelTitle.setForeground(new Color(8, 103, 147));
        discountLabel = new JLabel("SAR 0.00");
        discountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        discountLabel.setForeground(new Color(8, 103, 147));
        gbc.gridx = 0;
        gbc.gridy = 0;
        bottomPanel.add(discountLabelTitle, gbc);
        gbc.gridx = 1;
        bottomPanel.add(discountLabel, gbc);

        JLabel finalTotalLabelTitle = new JLabel("Final Total (SAR):");
        finalTotalLabelTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        finalTotalLabelTitle.setForeground(new Color(8, 103, 147));
        finalTotalLabel = new JLabel("SAR 0.00");
        finalTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        finalTotalLabel.setForeground(new Color(8, 103, 147));
        gbc.gridx = 0;
        gbc.gridy = 1;
        bottomPanel.add(finalTotalLabelTitle, gbc);
        gbc.gridx = 1;
        bottomPanel.add(finalTotalLabel, gbc);

        // Place Order Button and Clear Button in the same line
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
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
        buttonPanel.setBackground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        bottomPanel.add(buttonPanel, gbc);

        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        add(splitPane, BorderLayout.CENTER);

        promotionsDropdown.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedPromoId = ((Promotion) promotionsDropdown.getSelectedItem()).getPromotionId();
                updateTotalAmount();
            }
        });
        
            itemsTable.getModel().addTableModelListener(e -> {
        // Check if the event is triggered by the Quantity column (index 4)
        if (e.getColumn() == 4) {
            int row = e.getFirstRow();
            int availableQty = (int) itemsTableModel.getValueAt(row, 3);  // Get the available quantity from column 3
            int enteredQty = 0;

            try {
                enteredQty = Integer.parseInt(itemsTableModel.getValueAt(row, 4).toString());  // Get the entered quantity
            } catch (NumberFormatException ex) {
                // If invalid entry, reset to 0
                itemsTableModel.setValueAt(0, row, 4);
                return;
            }

            // Ensure that the entered quantity does not exceed the available stock
            if (enteredQty > availableQty) {
                JOptionPane.showMessageDialog(this, "Entered quantity exceeds available stock!");
                // Set the entered quantity to the available quantity
                itemsTableModel.setValueAt(availableQty, row, 4);
            }

            // If valid, update the available stock (subtract entered quantity)
            itemsTableModel.setValueAt(availableQty - enteredQty, row, 3);  // Update available quantity
        }
    });
    }


    private void addCustomer() {
        // Pop-up a dialog for adding new customer
        new CustomerInputDialog((JFrame) SwingUtilities.getWindowAncestor(this), false, null);
        loadCustomers();
    }
    
    private void loadItems() {
        List<Item> items = itemDAO.getAllItemsWithQuantity();  // Fetch items with available quantity
        for (Item item : items) {
            // Adding a row in the table for each item including the available quantity
            itemsTableModel.addRow(new Object[]{
                item.getItemName(),
                "SAR " + item.getPrice(),
                item.getDescription(),
                item.getStockQuantity(),  // Displaying the available quantity in the "Available Quantity" column
                0  // Initial quantity set to 0
            });
        }
    }



    private void loadCustomers() {
        customersDropdown.removeAllItems();
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
        for (int i = 0; i < selectedItemsTableModel.getRowCount(); i++) {
            try {
                String totalStr = selectedItemsTableModel.getValueAt(i, 3).toString();
                totalStr = totalStr.replace("SAR", "").trim();
                totalAmount += Double.parseDouble(totalStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        if (selectedPromoId != -1) {
            double discountPercentage = promotionDAO.getDiscountById(selectedPromoId);
            discountAmount = totalAmount * (discountPercentage / 100);
            totalAmount -= discountAmount;
        }

        totalLabel.setText("SAR " + String.format("%.2f", totalAmount));
        discountLabel.setText("SAR " + String.format("%.2f", discountAmount));
        finalTotalLabel.setText("SAR " + String.format("%.2f", totalAmount));
        this.totalAmount = totalAmount;
    }
    
    private void addItemToOrder() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow != -1) {
            String itemName = (String) itemsTable.getValueAt(selectedRow, 0);
            double itemPrice = 0.0;
            try {
                itemPrice = Double.parseDouble(itemsTable.getValueAt(selectedRow, 1).toString().replace("SAR", "").trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid item price.");
                return;
            }

            selectedItemId = itemDAO.getItemIdByName(itemName);

            // Check if the item is already in the selected items
            for (OrderItem orderItem : selectedItems) {
                if (orderItem.getItemId() == selectedItemId) {
                    JOptionPane.showMessageDialog(this, "This item is already selected.");
                    return;  // Do not add the item again
                }
            }

            int quantity = 0;
            try {
                quantity = Integer.parseInt(itemsTable.getValueAt(selectedRow, 4).toString());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid quantity.");
                return;
            }

            if (quantity > 0) {
                selectedItems.add(new OrderItem(0, selectedItemId, quantity));
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
        String employeeName = currentUser.getFirstName() + " " + currentUser.getLastName();
        employeeLabel.setText("Employee: " + employeeName);

        Order order = new Order(0, totalAmount, new Date(), "Placed", selectedCustomerId, currentUser.getUserId(), selectedPromoId);
        Receipt receipt = new Receipt(0, "Cash", new Date(), totalAmount, 0);

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

    private void styleButton(JButton button) {
        button.setBackground(new Color(8, 103, 147));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(130, 30));
    }
}
