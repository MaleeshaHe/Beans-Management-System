package beans.management.system.Model;

public class Item {
    
    private int itemId;
    private String itemName;
    private double price;
    private String description;
    private int categoryId;

    public Item() {
    }

    public Item(int itemId, String itemName, double price, String description, int categoryId) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.description = description;
        this.categoryId = categoryId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    
    
}
