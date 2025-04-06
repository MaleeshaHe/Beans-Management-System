package beans.management.system.DAO;

import beans.management.system.Model.Customer;  // Change to Customer model
import java.sql.*;
import java.util.*;
import utils.DBConnection;

public class CustomerDAO {

    private Connection connection;

    // Constructor to initialize connection
    public CustomerDAO() {
        connection = DBConnection.getConnection();
    }

    // Fetch all customers (Users)
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT u.user_id, u.first_name, u.last_name, u.phone_number, u.password, u.role_id, r.role_name FROM User u JOIN Role r ON u.role_id = r.role_id WHERE r.role_name = 'Customer' AND u.is_deleted = 0"; // Soft delete check
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getInt("user_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("phone_number"),
                        rs.getString("password"),
                        rs.getInt("role_id"),
                        rs.getString("role_name")
                );
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }

    // Add new customer (User)
    public boolean addCustomer(Customer customer) {
        String query = "INSERT INTO User (first_name, last_name, email, password, role_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getPassword());
            pstmt.setInt(5, customer.getRoleId());  // Set the role_id (roleId from the combo box)

            return pstmt.executeUpdate() > 0;  // If rows are inserted, return true
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update customer details
    public boolean updateCustomer(Customer customer) {
        String query = "UPDATE User SET first_name = ?, last_name = ?, email = ?, password = ?, role_id = ? WHERE user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getPassword());
            pstmt.setInt(5, customer.getRoleId());
            pstmt.setInt(6, customer.getUserId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Soft delete customer
    public boolean softDeleteCustomer(int userId) {
        String query = "UPDATE User SET is_deleted = 1 WHERE user_id = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // In CustomerDAO.java
    public int getCustomerIdByName(String firstName) {
        int customerId = -1;
        String query = "SELECT user_id FROM User WHERE first_name = ? AND is_deleted = 0";  // Ensures the customer is not soft-deleted

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, firstName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                customerId = rs.getInt("user_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customerId;
    }

}
