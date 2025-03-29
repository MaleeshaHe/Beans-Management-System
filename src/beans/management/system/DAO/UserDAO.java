package beans.management.system.DAO;

import beans.management.system.Model.User;
import utils.DBConnection;
import java.sql.*;

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
}
