package beans.management.system.DAO;

import beans.management.system.Model.Item;
import beans.management.system.Model.Category;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.DBConnection;

public class ItemDAO {

    private Connection connection;

    public ItemDAO() {
        connection = DBConnection.getConnection(); // Assuming DBConnection is a utility to get DB connection
    }

    // Get all items that are not soft deleted
    public List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        String query = "SELECT * FROM Item WHERE is_deleted = 0";  // Filter out soft deleted items

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Item item = new Item(
                        rs.getInt("item_id"),
                        rs.getString("item_name"),
                        rs.getDouble("price"),
                        rs.getString("description"),
                        rs.getInt("category_id")
                );
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    // Add a new item
    public boolean addItem(Item item) {
        String query = "INSERT INTO Item (item_name, price, description, category_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, item.getItemName());
            pstmt.setDouble(2, item.getPrice());
            pstmt.setString(3, item.getDescription());
            pstmt.setInt(4, item.getCategoryId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update an existing item
    public boolean updateItem(Item item) {
        String query = "UPDATE Item SET item_name = ?, price = ?, description = ?, category_id = ? WHERE item_id = ? AND is_deleted = 0";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, item.getItemName());  // Set item name
            pstmt.setDouble(2, item.getPrice());  // Set price
            pstmt.setString(3, item.getDescription());  // Set description
            pstmt.setInt(4, item.getCategoryId());  // Set category ID
            pstmt.setInt(5, item.getItemId());  // Set item ID to identify the item to update
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    // Soft delete an item (set is_deleted = 1)
    public boolean softDeleteItem(int itemId) {
        String query = "UPDATE Item SET is_deleted = 1 WHERE item_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, itemId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get all categories for the dropdown
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
    
    // Method to get item ID by item name
    public int getItemIdByName(String itemName) {
        String query = "SELECT item_id FROM Item WHERE item_name = ? AND is_deleted = 0";  // Only fetch non-deleted items
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, itemName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("item_id");  // Return the item_id if found
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;  // Return -1 if item name not found or an error occurs
    }

}
