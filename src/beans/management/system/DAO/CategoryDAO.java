package beans.management.system.DAO;

import beans.management.system.Model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.DBConnection;

public class CategoryDAO {

    private Connection connection;

    public CategoryDAO() {
        connection = DBConnection.getConnection(); // Assuming DBConnection is a utility to get DB connection
    }

    // Get all categories that are not soft deleted
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String query = "SELECT * FROM Category WHERE is_deleted = 0";  // Filter out soft deleted categories

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Category category = new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name")
                );
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    // Add a new category
    public boolean addCategory(Category category) {
        String query = "INSERT INTO Category (category_name) VALUES (?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, category.getCategoryName());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update an existing category
    public boolean updateCategory(Category category) {
        String query = "UPDATE Category SET category_name = ? WHERE category_id = ? AND is_deleted = 0";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, category.getCategoryName());  // Set category name
            pstmt.setInt(2, category.getCategoryId());      // Set category ID for update
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean softDeleteCategory(int categoryId) {
       String query = "UPDATE Category SET is_deleted = 1 WHERE category_id = ?";
       try (PreparedStatement pstmt = connection.prepareStatement(query)) {
           pstmt.setInt(1, categoryId);  // Set the category ID for soft delete
           int rowsAffected = pstmt.executeUpdate();
           return rowsAffected > 0;
       } catch (SQLException e) {
           e.printStackTrace();
       }
       return false;
   }

}
