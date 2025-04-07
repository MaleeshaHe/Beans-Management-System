package beans.management.system.Model;

public class Promotion {
    
    private int promotionId;
    private String promoCode;
    private String startDate;
    private String endDate;
    private String description;
    private double discountPercentage;

    public Promotion() {
    }

    public Promotion(int promotionId, String promoCode, String startDate, String endDate, String description, double discountPercentage) {
        this.promotionId = promotionId;
        this.promoCode = promoCode;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.discountPercentage = discountPercentage;
    }

    public Promotion(int i, String select_Promotion, Object object, int i0) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public int getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(int promotionId) {
        this.promotionId = promotionId;
    }

    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    @Override
    public String toString() {
        return promoCode;
    }
    
    
}
