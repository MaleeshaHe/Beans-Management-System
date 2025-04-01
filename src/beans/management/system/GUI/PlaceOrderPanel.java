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

public class PlaceOrderPanel extends JPanel {

    private JTable itemsTable, customersTable, promotionsTable;
    private DefaultTableModel itemsTableModel, customersTableModel, promotionsTableModel;
    private JButton placeOrderButton;
    private ItemDAO itemDAO;
    private OrderDAO orderDAO;
    private CustomerDAO customerDAO;
    private PromotionDAO promotionDAO;
    private double totalAmount = 0.0;
    private int selectedItemId, selectedCustomerId, selectedPromoId;

    public PlaceOrderPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        itemDAO = new ItemDAO();
        orderDAO = new OrderDAO();
        customerDAO = new CustomerDAO();
        promotionDAO = new PromotionDAO();

        // Panel for displaying tables
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new GridLayout(1, 3));

        // Table for items
        String[] itemColumns = {"Item Name", "Price", "Description"};
        itemsTableModel = new DefaultTableModel(itemColumns, 0);
        itemsTable = new JTable(itemsTableModel);
        itemsTable.setRowHeight(30);
        itemsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane itemScrollPane = new JScrollPane(itemsTable);
        tablePanel.add(itemScrollPane);
        loadItems();

        // Table for customers
        String[] customerColumns = {"Customer Name", "Email"};
        customersTableModel = new DefaultTableModel(customerColumns, 0);
        customersTable = new JTable(customersTableModel);
        customersTable.setRowHeight(30);
        customersTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane customerScrollPane = new JScrollPane(customersTable);
        tablePanel.add(customerScrollPane);
        loadCustomers();

        // Table for promotions
        String[] promoColumns = {"Promo Code", "Discount"};
        promotionsTableModel = new DefaultTableModel(promoColumns, 0);
        promotionsTable = new JTable(promotionsTableModel);
        promotionsTable.setRowHeight(30);
        promotionsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane promoScrollPane = new JScrollPane(promotionsTable);
        tablePanel.add(promoScrollPane);
        loadPromotions();

        add(tablePanel, BorderLayout.CENTER);

        // Place Order Button
        placeOrderButton = new JButton("Place Order");
        placeOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placeOrder();
            }
        });
        styleButton(placeOrderButton);
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(placeOrderButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadItems() {
        List<Item> items = itemDAO.getAllItems();
        for (Item item : items) {
            itemsTableModel.addRow(new Object[]{item.getItemName(), item.getPrice(), item.getDescription()});
        }
    }

    private void loadCustomers() {
        List<Customer> customers = customerDAO.getAllCustomers();
        for (Customer customer : customers) {
            customersTableModel.addRow(new Object[]{customer.getFirstName() + " " + customer.getLastName(), customer.getEmail()});
        }
    }

    private void loadPromotions() {
        List<Promotion> promotions = promotionDAO.getAllPromotions();
        for (Promotion promotion : promotions) {
            promotionsTableModel.addRow(new Object[]{promotion.getPromoCode(), promotion.getDiscountPercentage()});
        }
    }

    private void placeOrder() {
        // Get selected data
        int selectedItemRow = itemsTable.getSelectedRow();
        int selectedCustomerRow = customersTable.getSelectedRow();
        int selectedPromoRow = promotionsTable.getSelectedRow();

        if (selectedItemRow == -1 || selectedCustomerRow == -1 || selectedPromoRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select item, customer, and promotion.");
            return;
        }

        selectedItemId = itemDAO.getItemIdByName(itemsTable.getValueAt(selectedItemRow, 0).toString());
        selectedCustomerId = customerDAO.getCustomerIdByName(customersTable.getValueAt(selectedCustomerRow, 0).toString());
        selectedPromoId = promotionDAO.getPromoIdByCode(promotionsTable.getValueAt(selectedPromoRow, 0).toString());

        // Calculate total amount
        int quantity = Integer.parseInt(JOptionPane.showInputDialog("Enter quantity for the selected item:"));
        Item selectedItem = itemDAO.getItemById(selectedItemId);
        totalAmount = selectedItem.getPrice() * quantity;

        // Create Order, OrderItem, and Receipt objects
        Order order = new Order(0, totalAmount, new Date(), "Placed", selectedCustomerId, 1, selectedPromoId);  // 1 is the employee_id for now
        OrderItem orderItem = new OrderItem(0, selectedItemId, quantity);
        Receipt receipt = new Receipt(0, "Cash", new Date(), totalAmount, 0);  // Use "Cash" for payment method as an example

        boolean success = orderDAO.placeOrder(order, Collections.singletonList(orderItem), receipt);
        if (success) {
            JOptionPane.showMessageDialog(this, "Order placed successfully!");
            // Optionally reset tables or perform other actions
        } else {
            JOptionPane.showMessageDialog(this, "Error placing order.");
        }
    }

    // Button styling method
    private void styleButton(JButton button) {
        button.setBackground(new Color(77, 46, 10));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
    }
}
