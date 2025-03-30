package beans.management.system.DAO;

import beans.management.system.Model.Inventory;
import beans.management.system.Model.Item;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.DBConnection;

public class InventoryDAO {

    private Connection connection;

    public InventoryDAO() {
        connection = DBConnection.getConnection(); // Assuming DBConnection is a utility to get DB connection
    }

    // Get all inventory items
    public List<Inventory> getAllInventory() {
        List<Inventory> inventoryList = new ArrayList<>();
        String query = "SELECT * FROM Inventory WHERE is_deleted = 0";  // Only fetch non-deleted inventory items
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Inventory inventory = new Inventory(
                        rs.getInt("inventory_id"),
                        rs.getInt("item_id"),
                        rs.getInt("stock_quantity"),
                        rs.getInt("reorder_level"),
                        rs.getTimestamp("last_updated")
                );
                inventoryList.add(inventory);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventoryList;
    }

    // Add a new inventory item
    public boolean addInventory(Inventory inventory) {
        String query = "INSERT INTO Inventory (item_id, stock_quantity, reorder_level, last_updated) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, inventory.getItemId());
            pstmt.setInt(2, inventory.getStockQuantity());
            pstmt.setInt(3, inventory.getReorderLevel());
            pstmt.setTimestamp(4, inventory.getLastUpdated());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    inventory.setInventoryId(rs.getInt(1)); // Get the generated inventoryId
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    // Update inventory stock quantity
    public boolean updateInventory(Inventory inventory) {
        String query = "UPDATE Inventory SET item_id = ?, stock_quantity = ?, reorder_level = ?, last_updated = ? WHERE inventory_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, inventory.getItemId());  // Set item_id
            pstmt.setInt(2, inventory.getStockQuantity());  // Set stock_quantity
            pstmt.setInt(3, inventory.getReorderLevel());  // Set reorder_level
            pstmt.setTimestamp(4, inventory.getLastUpdated());  // Set last_updated timestamp
            pstmt.setInt(5, inventory.getInventoryId());  // Set inventory_id for the record to update

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    // Delete inventory item
    public boolean deleteInventory(int inventoryId) {
        String query = "DELETE FROM Inventory WHERE inventory_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, inventoryId); // Set the inventoryId
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;  // Return true if a row is deleted
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    // Get all items for the dropdown
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
}
