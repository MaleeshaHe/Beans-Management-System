package beans.management.system.GUI;

import beans.management.system.DAO.BillDAO;
import beans.management.system.Model.Bill;
import beans.management.system.Model.BillItem;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageBills extends JPanel {

    private JTable billsTable;
    private DefaultTableModel tableModel;
    private JButton viewBillButton, generateReceiptButton;
    private BillDAO billDAO;

    public ManageBills() throws SQLException {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);  // Set panel background to white

        // Initialize the BillDAO to interact with the database
        billDAO = new BillDAO();

        // Create table model and JTable for displaying bills
        String[] billColumns = {"Bill ID", "Customer Name", "Bill Date", "Total Amount", "Payment Method"};
        tableModel = new DefaultTableModel(billColumns, 0);
        billsTable = new JTable(tableModel);

        // Customize JTable appearance for bills
        billsTable.setBackground(Color.WHITE); // Set background to white
        billsTable.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Set font to Segoe UI, 12pt
        billsTable.setRowHeight(30); // Set row height for better readability
        billsTable.getTableHeader().setBackground(new Color(77, 46, 10)); // Set header background color
        billsTable.getTableHeader().setForeground(Color.WHITE); // Set header text color
        billsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12)); // Set header font

        JScrollPane billsScrollPane = new JScrollPane(billsTable);
        add(billsScrollPane, BorderLayout.CENTER);

        // Create and add header label before the table
        JLabel headerLabel = new JLabel("Bill Management", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(new Color(77, 46, 10));  // Set header text color
        headerLabel.setPreferredSize(new Dimension(600, 40));  // Set height for header
        add(headerLabel, BorderLayout.NORTH);  // Add the header label to the north of the panel

        // Create button panel for "View Bill" and "Generate Receipt"
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);  // Set background to white

        // View Bill Button
        viewBillButton = new JButton("View Bill");
        viewBillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    viewBillDetails();
                } catch (SQLException ex) {
                    Logger.getLogger(ManageBills.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        styleButton(viewBillButton);  // Apply style to button
        buttonPanel.add(viewBillButton);

        // Generate Receipt Button
        generateReceiptButton = new JButton("Generate Receipt");
        generateReceiptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    generateReceipt();
                } catch (SQLException ex) {
                    Logger.getLogger(ManageBills.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        styleButton(generateReceiptButton);  // Apply style to button
        buttonPanel.add(generateReceiptButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load bill data from the database
        loadBillData();
    }

    // Load bill data from the database
    private void loadBillData() throws SQLException {
        // Clear existing rows before loading fresh data
        tableModel.setRowCount(0);

        // Retrieve all bills using BillDAO
        List<Bill> bills = billDAO.getAllBills();
        for (Bill bill : bills) {
            tableModel.addRow(new Object[]{
                bill.getOrderId(),
                bill.getCustomerName(),
                bill.getOrderDate(),
                bill.getTotalAmount(),
                bill.getPaymentMethod()
            });
        }
    }

    // View selected bill details
    private void viewBillDetails() throws SQLException {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow != -1) {
            int orderId = (int) tableModel.getValueAt(selectedRow, 0);
            // Retrieve bill details by orderId
            Bill bill = billDAO.generateBill(orderId);

            // Display the bill details in a dialog or new window
            if (bill != null) {
                StringBuilder billDetails = new StringBuilder();
                billDetails.append("Bill ID: ").append(bill.getOrderId())
                        .append("\nCustomer: ").append(bill.getCustomerName())
                        .append("\nBill Date: ").append(bill.getOrderDate())
                        .append("\nTotal Amount: ").append(bill.getTotalAmount())
                        .append("\nPayment Method: ").append(bill.getPaymentMethod())
                        .append("\n\nItems:\n");

                // List each item in the bill
                for (BillItem item : bill.getItems()) {
                    billDetails.append("Item: ").append(item.getItemName())
                            .append(", Quantity: ").append(item.getQuantity())
                            .append(", Total: ").append(item.getItemTotal())
                            .append("\n");
                }

                // Show bill details in a dialog
                JOptionPane.showMessageDialog(this, billDetails.toString());
            } else {
                JOptionPane.showMessageDialog(this, "Bill not found.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a bill to view.");
        }
    }

    // Generate receipt for the selected bill
    private void generateReceipt() throws SQLException {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow != -1) {
            int orderId = (int) tableModel.getValueAt(selectedRow, 0);
            // Retrieve bill details by orderId
            Bill bill = billDAO.generateBill(orderId);

            // Display the receipt details (or just confirm that the receipt is generated)
            if (bill != null) {
                JOptionPane.showMessageDialog(this, "Receipt for Bill ID: " + bill.getOrderId() +
                        "\nAmount: " + bill.getTotalAmount() +
                        "\nPayment Method: " + bill.getPaymentMethod() +
                        "\nReceipt Generated.");
            } else {
                JOptionPane.showMessageDialog(this, "Error generating receipt.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a bill to generate a receipt.");
        }
    }

    // Method to style buttons with custom background color, font, and text color
    private void styleButton(JButton button) {
        button.setBackground(new Color(77, 46, 10)); // Set the background color of the button
        button.setForeground(Color.WHITE); // Set the text color to white
        button.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Set font to Segoe UI, 12 pt, Bold
    }

    public static void main(String[] args) throws SQLException {
        // Create and show the Manage Bills frame
        JFrame frame = new JFrame("Manage Bills");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600); // Adjusted frame size
        frame.add(new ManageBills());
        frame.setVisible(true);
    }
}
