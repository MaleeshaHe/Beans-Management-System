package beans.management.system.GUI;

import beans.management.system.DAO.BillDAO;
import beans.management.system.DAO.ReceiptDAO;
import beans.management.system.Model.Bill;
import beans.management.system.Model.BillItem;
import beans.management.system.Model.Receipt;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ManageBills extends JPanel {

    private JTable billsTable;
    private DefaultTableModel tableModel;
    private JTable receiptTable;
    private DefaultTableModel receiptTableModel;
    private JButton viewBillButton, generateReceiptButton, viewReceiptsButton;
    private BillDAO billDAO;
    private ReceiptDAO receiptDAO;

    public ManageBills() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);  // Set panel background to white

        // Initialize the BillDAO and ReceiptDAO to interact with the database
        billDAO = new BillDAO();
        receiptDAO = new ReceiptDAO();

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

        // Create table model and JTable for displaying receipts
        String[] receiptColumns = {"Receipt ID", "Payment Method", "Receipt Date", "Total Amount", "Order ID"};
        receiptTableModel = new DefaultTableModel(receiptColumns, 0);
        receiptTable = new JTable(receiptTableModel);

        // Customize JTable appearance for receipts
        receiptTable.setBackground(Color.WHITE); // Set background to white
        receiptTable.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // Set font to Segoe UI, 12pt
        receiptTable.setRowHeight(30); // Set row height for better readability
        receiptTable.getTableHeader().setBackground(new Color(77, 46, 10)); // Set header background color
        receiptTable.getTableHeader().setForeground(Color.WHITE); // Set header text color
        receiptTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12)); // Set header font

        JScrollPane receiptScrollPane = new JScrollPane(receiptTable);
        add(receiptScrollPane, BorderLayout.EAST); // Adding receipt table to the EAST side of the panel

        // Create and add header label before the table
        JLabel headerLabel = new JLabel("Bill Management", JLabel.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        headerLabel.setForeground(new Color(77, 46, 10));  // Set header text color
        headerLabel.setPreferredSize(new Dimension(600, 40));  // Set height for header
        add(headerLabel, BorderLayout.NORTH);  // Add the header label to the north of the panel

        // Create button panel for "View Bill", "Generate Receipt" and "View Receipts"
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);  // Set background to white

        // View Bill Button
        viewBillButton = new JButton("View Bill");
        viewBillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewBillDetails();
            }
        });
        styleButton(viewBillButton);  // Apply style to button
        buttonPanel.add(viewBillButton);

        // Generate Receipt Button
        generateReceiptButton = new JButton("Generate Receipt");
        generateReceiptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReceipt();
            }
        });
        styleButton(generateReceiptButton);  // Apply style to button
        buttonPanel.add(generateReceiptButton);

        // View Receipts Button
        viewReceiptsButton = new JButton("View Receipts");
        viewReceiptsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadReceiptsData();
            }
        });
        styleButton(viewReceiptsButton);  // Apply style to button
        buttonPanel.add(viewReceiptsButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load bill data from the database
        loadBillData();

        // Add double-click listener to receipt table to generate and display bill
        receiptTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = receiptTable.getSelectedRow();
                    if (selectedRow != -1) {
                        int receiptId = (int) receiptTableModel.getValueAt(selectedRow, 0);
                        int orderId = (int) receiptTableModel.getValueAt(selectedRow, 4); // Get order_id
                        generateBillFromReceipt(orderId);
                    }
                }
            }
        });
    }

    // Load bill data from the database
    private void loadBillData() {
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
    private void viewBillDetails() {
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
    private void generateReceipt() {
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

    // Load receipt data from the database
    private void loadReceiptsData() {
        // Clear existing rows before loading fresh data
        receiptTableModel.setRowCount(0);

        // Retrieve all receipts using ReceiptDAO
        List<Receipt> receipts = receiptDAO.getAllReceipts();
        for (Receipt receipt : receipts) {
            receiptTableModel.addRow(new Object[]{
                receipt.getReceiptId(),
                receipt.getPaymentMethod(),
                receipt.getReceiptDate(),
                receipt.getTotalAmount(),
                receipt.getOrderId()
            });
        }
    }

    // Generate bill from receipt (triggered by double-click)
    private void generateBillFromReceipt(int orderId) {
        // Generate the bill using the orderId (this could trigger the bill generation logic)
        Bill bill = billDAO.generateBill(orderId);

        // Display the bill details in a popup
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
            JOptionPane.showMessageDialog(this, "Error generating bill from receipt.");
        }
    }

    // Method to style buttons with custom background color, font, and text color
    private void styleButton(JButton button) {
        button.setBackground(new Color(77, 46, 10)); // Set the background color of the button
        button.setForeground(Color.WHITE); // Set the text color to white
        button.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Set font to Segoe UI, 12 pt, Bold
    }

    public static void main(String[] args) {
        // Create and show the Manage Bills frame
        JFrame frame = new JFrame("Manage Bills");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600); // Adjusted frame size to fit both tables
        frame.add(new ManageBills());
        frame.setVisible(true);
    }
}
