package beans.management.system.DAO;

import beans.management.system.Model.User;
import utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection conn;

    public UserDAO() {
        conn = (Connection) DBConnection.getConnection();  // Establish DB connection
    }

    public User authenticateUser(String email, String password) {
        String query = "SELECT u.user_id, u.first_name, u.last_name, u.email, r.role_name "
                     + "FROM User u "
                     + "JOIN Role r ON u.role_id = r.role_id "
                     + "WHERE u.email = ? AND u.password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // If user is found, create a User object and set the role
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role_name"));  // Store role in the User object

                return user;  // Return authenticated user with role
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // User not found or invalid credentials
    }
    
    // Check if email already exists (including deleted users)
    public boolean isEmailExists(String email) {
        String query = "SELECT COUNT(*) FROM User WHERE email = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Sign up new user (only if role is Employee or Manager and email doesn't exist)
    public boolean signUpUser(User user, String roleName) {
        if (!roleName.equals("Employee") && !roleName.equals("Manager")) {
            System.out.println("Invalid role. Only 'Employee' or 'Manager' allowed.");
            return false;
        }

        if (isEmailExists(user.getEmail())) {
            System.out.println("Email already exists. Choose another email.");
            return false;
        }

        String query = "INSERT INTO User (first_name, last_name, email, password, role_id, is_deleted) " +
                       "VALUES (?, ?, ?, ?, (SELECT role_id FROM Role WHERE role_name = ?), FALSE)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());  // You should hash this before saving!
            stmt.setString(5, roleName);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    
    // Fetch all employees (users with "Employee" role, excluding deleted ones)
    public List<User> getAllEmployees() {
        List<User> employees = new ArrayList<>();
        String query = "SELECT u.user_id, u.first_name, u.last_name, u.email, r.role_name " +
                       "FROM User u JOIN Role r ON u.role_id = r.role_id " +
                       "WHERE r.role_name = 'Employee' AND u.is_deleted = FALSE";  // Only non-deleted employees

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                User user = new User(
                    rs.getInt("user_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("role_name"),
                    "" // Provide password or fetch as needed
                );
                employees.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    // Soft delete an employee (set is_deleted = TRUE)
    public boolean softDeleteEmployee(int userId) {
        String query = "UPDATE User SET is_deleted = TRUE WHERE user_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Add a new employee
    public boolean addEmployee(User user) {
        String query = "INSERT INTO User (first_name, last_name, email, password, role_id, is_deleted) " +
                       "VALUES (?, ?, ?, ?, (SELECT role_id FROM Role WHERE role_name = 'Employee'), FALSE)";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            return stmt.executeUpdate() > 0;  // Execute the insert query
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Edit an employee's information
    public boolean updateEmployee(User user) {
        String query = "UPDATE User SET first_name = ?, last_name = ?, email = ? " +
                       "WHERE user_id = ? AND is_deleted = FALSE";  // Ensure the user is not deleted
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setInt(4, user.getUserId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean emailExists(String email) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM user WHERE email = ?")) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public User getUserById(int userId) {
    User user = null;
    String query = "SELECT user_id, first_name, last_name, email, role_id FROM user WHERE user_id = ? AND is_deleted = 0";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("user_id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String email = rs.getString("email");
                String role = rs.getString("role_id");

                user = new User(id, firstName, lastName, email, role, null); // Set password to null if not needed
            }

        } catch (Exception e) {
            e.printStackTrace(); // Or use logging
        }

        return user;
    }


    
}
