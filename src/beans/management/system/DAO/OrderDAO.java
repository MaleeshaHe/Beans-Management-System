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

                    // Insert each order item into Order_Item table and update inventory
                    String itemQuery = "INSERT INTO Order_Item (order_id, item_id, quantity) VALUES (?, ?, ?)";
                    String inventoryUpdateQuery = "UPDATE Inventory SET stock_quantity = stock_quantity - ? WHERE item_id = ?";

                    try (PreparedStatement itemStmt = connection.prepareStatement(itemQuery);
                         PreparedStatement inventoryStmt = connection.prepareStatement(inventoryUpdateQuery)) {

                        for (OrderItem item : orderItems) {
                            itemStmt.setInt(1, orderId);
                            itemStmt.setInt(2, item.getItemId());
                            itemStmt.setInt(3, item.getQuantity());
                            itemStmt.addBatch();

                            // Update inventory
                            inventoryStmt.setInt(1, item.getQuantity());
                            inventoryStmt.setInt(2, item.getItemId());
                            inventoryStmt.addBatch();
                        }

                        itemStmt.executeBatch();
                        inventoryStmt.executeBatch();
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

    
        // Method to get the total number of orders
    public int getTotalOrders() {
        String query = "SELECT COUNT(*) FROM orders";  // SQL query to get the count of orders
        int totalOrders = 0;
        
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                totalOrders = resultSet.getInt(1);  // Get the count from the first column
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return totalOrders;
    }

    // Method to get the total revenue
    public double getTotalRevenue() {
        String query = "SELECT SUM(total_amount) FROM orders";  // SQL query to get the total revenue
        double totalRevenue = 0.0;
        
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next()) {
                totalRevenue = resultSet.getDouble(1);  // Get the sum from the first column
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return totalRevenue;
    }
   

    // Method to get the total number of customers who placed orders
    public int getTotalCustomers() {
        String query = "SELECT COUNT(DISTINCT customer_id) FROM orders";
        return executeQuery(query);
    }

    // Method to get the total orders per day (example query for today’s orders)
    public int getTotalOrdersPerDay() {
        String query = "SELECT COUNT(*) FROM orders WHERE DATE(order_date) = CURDATE()"; // Assuming 'order_date' is the field in orders table
        return executeQuery(query);
    }

    // Helper method to execute the query and return an integer result
    private int executeQuery(String query) {
        int result = 0;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                result = rs.getInt(1);  // Get the count result
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Helper method to execute the query for total revenue and return a double result
    private double executeQueryForRevenue(String query) {
        double result = 0.0;
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                result = rs.getDouble(1);  // Get the revenue result
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
