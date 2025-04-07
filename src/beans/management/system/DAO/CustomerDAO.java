package beans.management.system.DAO;

import beans.management.system.Model.Customer;
import java.sql.*;
import java.util.*;
import utils.DBConnection;

public class CustomerDAO {

    private Connection connection;

    // Constructor
    public CustomerDAO() {
        connection = DBConnection.getConnection();
    }

    // Fetch all customers
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String query = "SELECT u.user_id, u.first_name, u.last_name, u.phone_number, u.role_id, r.role_name " +
                       "FROM User u JOIN Role r ON u.role_id = r.role_id " +
                       "WHERE r.role_name = 'Customer' AND u.is_deleted = 0";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Customer customer = new Customer(
                    rs.getInt("user_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("phone_number"),
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

    // Add new customer
    public boolean addCustomer(Customer customer) {
        String query = "INSERT INTO User (first_name, last_name, phone_number, email, password, role_id, is_deleted) " +
                       "VALUES (?, ?, ?, ?, ?, ?, 0)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getPhoneNumber());
            pstmt.setString(4, "placeholder@example.com");  // Default email
            pstmt.setString(5, "default123");  // Default password
            pstmt.setInt(6, customer.getRoleId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    // Update customer
    public boolean updateCustomer(Customer customer) {
        String query = "UPDATE User SET first_name = ?, last_name = ?, phone_number = ?, role_id = ? " +
                       "WHERE user_id = ? AND is_deleted = 0";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, customer.getFirstName());
            pstmt.setString(2, customer.getLastName());
            pstmt.setString(3, customer.getPhoneNumber());
            pstmt.setInt(4, customer.getRoleId());
            pstmt.setInt(5, customer.getUserId());

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

    // Get Customer ID by First Name
    public int getCustomerIdByName(String firstName) {
        int customerId = -1;
        String query = "SELECT user_id FROM User WHERE first_name = ? AND is_deleted = 0";

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
