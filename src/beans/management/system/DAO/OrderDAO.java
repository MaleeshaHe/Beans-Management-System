package beans.management.system.DAO;

import beans.management.system.Model.Order;
import beans.management.system.Model.OrderItem;
import beans.management.system.Model.Receipt;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.DBConnection;

public class OrderDAO {
    private Connection connection;

    public OrderDAO() {
        connection = DBConnection.getConnection();
    }

    // Retrieve order history for a given employee (based on employee_id)
    public List<Order> getOrderHistoryByEmployeeId(int employeeId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT * FROM Orders WHERE employee_id = ? ORDER BY order_date DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, employeeId); // Use employeeId to filter orders
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getDouble("total_amount"),
                        rs.getDate("order_date"),
                        rs.getString("status"),
                        rs.getInt("customer_id"),
                        rs.getInt("employee_id"),
                        rs.getInt("promotion_id")
                );
                orders.add(order);  // Add the order to the list
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;  // Return the list of orders for the given employee
    }

    // Place an order: insert into Orders, then Order_Item, then Receipt (existing code unchanged)
    public boolean placeOrder(Order order, List<OrderItem> orderItems, Receipt receipt) {
        boolean success = false;
        try {
            connection.setAutoCommit(false);

            // Insert into Orders table
            String orderQuery = "INSERT INTO Orders (total_amount, order_date, status, customer_id, employee_id, promotion_id) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement orderStmt = connection.prepareStatement(orderQuery, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setDouble(1, order.getTotalAmount());
                orderStmt.setDate(2, new java.sql.Date(order.getOrderDate().getTime()));
                orderStmt.setString(3, order.getStatus());
                orderStmt.setInt(4, order.getCustomerId());
                orderStmt.setInt(5, order.getEmployeeId());
                orderStmt.setInt(6, order.getPromotionId());

                int rowsAffected = orderStmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new SQLException("Creating order failed, no rows affected.");
                }

                ResultSet generatedKeys = orderStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int orderId = generatedKeys.getInt(1);
                    order.setOrderId(orderId);

                    // Insert each order item into Order_Item table
                    String itemQuery = "INSERT INTO Order_Item (order_id, item_id, quantity) VALUES (?, ?, ?)";
                    try (PreparedStatement itemStmt = connection.prepareStatement(itemQuery)) {
                        for (OrderItem item : orderItems) {
                            itemStmt.setInt(1, orderId);
                            itemStmt.setInt(2, item.getItemId());  // Ensure you get the correct item_id
                            itemStmt.setInt(3, item.getQuantity());
                            itemStmt.addBatch();
                        }
                        itemStmt.executeBatch();
                    }

                    // Insert receipt into Receipt table
                    String receiptQuery = "INSERT INTO Receipt (payment_method, receipt_date, total_amount, order_id) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement receiptStmt = connection.prepareStatement(receiptQuery)) {
                        receiptStmt.setString(1, receipt.getPaymentMethod());
                        receiptStmt.setDate(2, new java.sql.Date(receipt.getReceiptDate().getTime()));
                        receiptStmt.setDouble(3, receipt.getTotalAmount());
                        receiptStmt.setInt(4, orderId);
                        receiptStmt.executeUpdate();
                    }
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }

            connection.commit();
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }
}
