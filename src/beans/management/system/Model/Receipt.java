package beans.management.system.Model;

import java.util.Date;

public class Receipt {
    private int receiptId;
    private String paymentMethod;
    private Date receiptDate;
    private double totalAmount;
    private int orderId;

    public Receipt() {
    }

    public Receipt(int receiptId, String paymentMethod, Date receiptDate, double totalAmount, int orderId) {
        this.receiptId = receiptId;
        this.paymentMethod = paymentMethod;
        this.receiptDate = receiptDate;
        this.totalAmount = totalAmount;
        this.orderId = orderId;
    }

    public int getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(int receiptId) {
        this.receiptId = receiptId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
 
    
}