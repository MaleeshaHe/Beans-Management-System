package beans.management.system.GUI;

import beans.management.system.DAO.BillDAO;
import beans.management.system.Model.Bill;
import beans.management.system.Model.BillItem;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManageBills extends JPanel {

    private JTable billsTable;
    private DefaultTableModel tableModel;
    private JButton viewBillButton, generateReceiptButton, generatePDFButton;
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
        billsTable.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12)); // Set font for JTable
        billsTable.setRowHeight(30); // Set row height for better readability
        billsTable.getTableHeader().setBackground(new Color(8, 103, 147)); // Set header background color
        billsTable.getTableHeader().setForeground(Color.WHITE); // Set header text color
        billsTable.getTableHeader().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12)); // Set header font

        JScrollPane billsScrollPane = new JScrollPane(billsTable);
        add(billsScrollPane, BorderLayout.CENTER);

        // Create and add header label before the table
        JLabel headerLabel = new JLabel("Bill Management", JLabel.CENTER);
        headerLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 16));
        headerLabel.setForeground(new Color(8, 103, 147));  // Set header text color
        headerLabel.setPreferredSize(new Dimension(600, 40));  // Set height for header
        add(headerLabel, BorderLayout.NORTH);  // Add the header label to the north of the panel

        // Create button panel for "View Bill", "Generate Receipt", and "Generate PDF"
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
                    ex.printStackTrace();
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
                    ex.printStackTrace();
                }
            }
        });
        styleButton(generateReceiptButton);  // Apply style to button
        buttonPanel.add(generateReceiptButton);

        // Generate PDF Button
        generatePDFButton = new JButton("Generate PDF");
        generatePDFButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    generatePDF();
                } catch (DocumentException | FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (SQLException ex) {
                    Logger.getLogger(ManageBills.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        styleButton(generatePDFButton);  // Apply style to button
        buttonPanel.add(generatePDFButton);

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

    // Generate PDF for the selected bill
    private void generatePDF() throws DocumentException, FileNotFoundException, SQLException {
        int selectedRow = billsTable.getSelectedRow();
        if (selectedRow != -1) {
            int orderId = (int) tableModel.getValueAt(selectedRow, 0);
            Bill bill = billDAO.generateBill(orderId);

            if (bill != null) {
                // Creating a new Document
                Document document = new Document();
                String fileName = "Bill_" + bill.getOrderId() + ".pdf";
                PdfWriter.getInstance(document, new FileOutputStream(fileName));

                // Open the document for writing
                document.open();

                // Add content to the document
                document.add(new Paragraph("Bill ID: " + bill.getOrderId()));
                document.add(new Paragraph("Customer: " + bill.getCustomerName()));
                document.add(new Paragraph("Bill Date: " + bill.getOrderDate()));
                document.add(new Paragraph("Total Amount: " + bill.getTotalAmount()));
                document.add(new Paragraph("Payment Method: " + bill.getPaymentMethod()));
                document.add(new Paragraph("\nItems:"));
                for (BillItem item : bill.getItems()) {
                    document.add(new Paragraph("Item: " + item.getItemName() + ", Quantity: " + item.getQuantity() +
                            ", Total: " + item.getItemTotal()));
                }

                // Close the document
                document.close();

                // Show confirmation dialog
                JOptionPane.showMessageDialog(this, "Bill PDF generated successfully: " + fileName);
            } else {
                JOptionPane.showMessageDialog(this, "Error generating PDF.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a bill to generate PDF.");
        }
    }
    
    

    // Method to style buttons with custom background color, font, and text color
    private void styleButton(JButton button) {
        button.setBackground(new Color(8, 103, 147)); // Set the background color of the button
        button.setForeground(Color.WHITE); // Set the text color to white
        //button.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Set font to Segoe UI, 12 pt, Bold
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
