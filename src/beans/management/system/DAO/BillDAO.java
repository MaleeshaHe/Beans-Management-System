package beans.management.system.DAO;

import beans.management.system.Model.Bill;
import beans.management.system.Model.BillItem;
import beans.management.system.Model.Receipt;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import utils.DBConnection;

public class BillDAO {

    private Connection connection;

    public BillDAO() {
        this.connection = DBConnection.getConnection();
    }

    // Method to retrieve all bills from the database (bill overview)
    public List<Bill> getAllBills() throws SQLException {
        List<Bill> bills = new ArrayList<>();
        String query = "SELECT o.order_id, u.first_name AS customer_first_name, u.last_name AS customer_last_name, " +
                       "o.order_date, o.total_amount, r.payment_method " +
                       "FROM Orders o " +
                       "JOIN User u ON o.customer_id = u.user_id " +
                       "JOIN Receipt r ON o.order_id = r.order_id";
        PreparedStatement stmt = connection.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            int orderId = rs.getInt("order_id");
            String customerName = rs.getString("customer_first_name") + " " + rs.getString("customer_last_name");
            String orderDate = rs.getString("order_date");
            double totalAmount = rs.getDouble("total_amount");
            String paymentMethod = rs.getString("payment_method");

            bills.add(new Bill(orderId, customerName, null, orderDate, totalAmount, paymentMethod, null));
        }

        return bills;
    }

    // Method to generate bill details for a specific order_id
    public Bill generateBill(int orderId) throws SQLException {
        String query = "SELECT o.order_id, u.first_name AS customer_first_name, u.last_name AS customer_last_name, " +
                       "u.email AS customer_email, o.order_date, o.total_amount, r.payment_method, r.receipt_date, " +
                       "oi.item_id, i.item_name, oi.quantity, i.price AS item_price, " +
                       "(oi.quantity * i.price) AS item_total " +
                       "FROM Orders o " +
                       "JOIN User u ON o.customer_id = u.user_id " +
                       "JOIN Order_Item oi ON o.order_id = oi.order_id " +
                       "JOIN Item i ON oi.item_id = i.item_id " +
                       "JOIN Receipt r ON o.order_id = r.order_id " +
                       "WHERE o.order_id = ?";

        PreparedStatement stmt = connection.prepareStatement(query);
        stmt.setInt(1, orderId);
        ResultSet rs = stmt.executeQuery();

        Bill bill = null;
        List<BillItem> items = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  // Format for date to string

        while (rs.next()) {
            if (bill == null) {
                // Initialize bill object with basic information
                String customerName = rs.getString("customer_first_name") + " " + rs.getString("customer_last_name");
                String customerEmail = rs.getString("customer_email");
                String orderDate = rs.getString("order_date");
                double totalAmount = rs.getDouble("total_amount");
                String paymentMethod = rs.getString("payment_method");

                // Convert receiptDate from SQL Date to String
                String receiptDate = sdf.format(rs.getDate("receipt_date"));

                bill = new Bill(orderId, customerName, customerEmail, orderDate, totalAmount, paymentMethod, receiptDate);
            }

            // Create BillItem for each item in the bill
            int itemId = rs.getInt("item_id");
            String itemName = rs.getString("item_name");
            int quantity = rs.getInt("quantity");
            double itemPrice = rs.getDouble("item_price");
            double itemTotal = rs.getDouble("item_total");

            items.add(new BillItem(itemId, itemName, quantity, itemPrice, itemTotal));
        }

        if (bill != null) {
            bill.setItems(items);
        }

        return bill;
    }

    // Method to retrieve all receipts from the database (receipt overview)
    public List<Receipt> getAllReceipts() throws SQLException {
        List<Receipt> receipts = new ArrayList<>();
        String query = "SELECT r.receipt_id, r.payment_method, r.receipt_date, r.total_amount, r.order_id " +
                       "FROM Receipt r " +
                       "JOIN Orders o ON r.order_id = o.order_id";
        PreparedStatement stmt = connection.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            int receiptId = rs.getInt("receipt_id");
            String paymentMethod = rs.getString("payment_method");
            Date receiptDate = rs.getDate("receipt_date"); // This is the updated Date handling
            double totalAmount = rs.getDouble("total_amount");
            int orderId = rs.getInt("order_id");

            receipts.add(new Receipt(receiptId, paymentMethod, receiptDate, totalAmount, orderId));
        }

        return receipts;
    }
}
