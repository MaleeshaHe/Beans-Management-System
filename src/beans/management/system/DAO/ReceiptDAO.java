package beans.management.system.DAO;

import beans.management.system.Model.Receipt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.DBConnection;

public class ReceiptDAO {

    private Connection connection;

    public ReceiptDAO() {
        this.connection = DBConnection.getConnection();
    }

    // Method to retrieve all receipts from the database (receipt overview)
    public List<Receipt> getAllReceipts() {
        List<Receipt> receipts = new ArrayList<>();
        String query = "SELECT r.receipt_id, r.payment_method, r.receipt_date, r.total_amount, r.order_id " +
                       "FROM Receipt r " +
                       "JOIN Orders o ON r.order_id = o.order_id";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int receiptId = rs.getInt("receipt_id");
                String paymentMethod = rs.getString("payment_method");
                Date receiptDate = rs.getDate("receipt_date");
                double totalAmount = rs.getDouble("total_amount");
                int orderId = rs.getInt("order_id");

                receipts.add(new Receipt(receiptId, paymentMethod, receiptDate, totalAmount, orderId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return receipts;
    }

    // Method to get a specific receipt by receipt ID
    public Receipt getReceiptById(int receiptId) {
        String query = "SELECT r.receipt_id, r.payment_method, r.receipt_date, r.total_amount, r.order_id " +
                       "FROM Receipt r " +
                       "WHERE r.receipt_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, receiptId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String paymentMethod = rs.getString("payment_method");
                    Date receiptDate = rs.getDate("receipt_date");
                    double totalAmount = rs.getDouble("total_amount");
                    int orderId = rs.getInt("order_id");

                    return new Receipt(receiptId, paymentMethod, receiptDate, totalAmount, orderId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Return null if no receipt found
    }

    // Method to retrieve receipts by order ID (if needed)
    public List<Receipt> getReceiptsByOrderId(int orderId) {
        List<Receipt> receipts = new ArrayList<>();
        String query = "SELECT r.receipt_id, r.payment_method, r.receipt_date, r.total_amount " +
                       "FROM Receipt r " +
                       "WHERE r.order_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int receiptId = rs.getInt("receipt_id");
                    String paymentMethod = rs.getString("payment_method");
                    Date receiptDate = rs.getDate("receipt_date");
                    double totalAmount = rs.getDouble("total_amount");

                    receipts.add(new Receipt(receiptId, paymentMethod, receiptDate, totalAmount, orderId));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return receipts;
    }

}
