package beans.management.system.Model;

import java.sql.Timestamp;

public class Inventory {
    private int inventoryId;
    private int itemId;
    private int stockQuantity;
    private int reorderLevel;
    private Timestamp lastUpdated;

    public Inventory(int inventoryId, int itemId, int stockQuantity, int reorderLevel, Timestamp lastUpdated) {
        this.inventoryId = inventoryId;
        this.itemId = itemId;
        this.stockQuantity = stockQuantity;
        this.reorderLevel = reorderLevel;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
