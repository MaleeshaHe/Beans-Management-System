package beans.management.system.DAO;

import beans.management.system.Model.Promotion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.DBConnection;

public class PromotionDAO {

    private Connection connection;

    public PromotionDAO() {
        connection = DBConnection.getConnection(); // Assuming DBConnection is a utility to get DB connection
    }

    // Get all promotions
    public List<Promotion> getAllPromotions() {
        List<Promotion> promotions = new ArrayList<>();
        String query = "SELECT * FROM Promotion WHERE is_deleted = FALSE"; // Assuming you have a column to mark soft delete

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Promotion promotion = new Promotion(
                        rs.getInt("promotion_id"),
                        rs.getString("promo_code"),
                        rs.getString("start_date"),
                        rs.getString("end_date"),
                        rs.getString("description"),
                        rs.getDouble("discount_percentage")
                );
                promotions.add(promotion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return promotions;
    }

    // Add a new promotion
    public boolean addPromotion(Promotion promotion) {
        String query = "INSERT INTO Promotion (promo_code, start_date, end_date, description, discount_percentage) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, promotion.getPromoCode());
            pstmt.setString(2, promotion.getStartDate());
            pstmt.setString(3, promotion.getEndDate());
            pstmt.setString(4, promotion.getDescription());
            pstmt.setDouble(5, promotion.getDiscountPercentage());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update an existing promotion
    public boolean updatePromotion(Promotion promotion) {
        String query = "UPDATE Promotion SET promo_code = ?, start_date = ?, end_date = ?, description = ?, discount_percentage = ? WHERE promotion_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, promotion.getPromoCode());
            pstmt.setString(2, promotion.getStartDate());
            pstmt.setString(3, promotion.getEndDate());
            pstmt.setString(4, promotion.getDescription());
            pstmt.setDouble(5, promotion.getDiscountPercentage());
            pstmt.setInt(6, promotion.getPromotionId());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Soft delete a promotion (mark it as deleted)
    public boolean softDeletePromotion(int promotionId) {
        String query = "UPDATE Promotion SET is_deleted = TRUE WHERE promotion_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, promotionId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // In PromotionDAO.java
    public int getPromoIdByCode(String promoCode) {
        int promoId = -1;
        String query = "SELECT promotion_id FROM Promotion WHERE promo_code = ? AND is_deleted = 0";  // Ensures the promotion is not soft-deleted

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, promoCode);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                promoId = rs.getInt("promotion_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return promoId;
    }
    
    public double getDiscountById(int promoId) {
        String query = "SELECT discount_percentage FROM Promotion WHERE promotion_id = ? AND is_deleted = FALSE"; 
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, promoId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("discount_percentage"); // Return the discount percentage
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Return 0 if no discount found
    }


}
