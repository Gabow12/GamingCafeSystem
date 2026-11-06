/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author GABRIEL DY
 */
public class Order {

    private String orderId;
    private String customerId;

    private String productId;

    private int quantity;

    private String date;

    private double subtotal;
    private double discount;
    private double total;

    public Order(
            String orderId,
            String customerId,
            String productId,
            int quantity,
            String date) {

        this.orderId = orderId;
        this.customerId = customerId;
        this.productId = productId;
        this.quantity = quantity;
        this.date = date;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getDate() {
        return date;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDiscount() {
        return discount;
    }

    public double getTotal() {
        return total;
    }

    public void setSubtotal(
            double subtotal) {

        this.subtotal = subtotal;
    }

    public void setDiscount(
            double discount) {

        this.discount = discount;
    }

    public void setTotal(
            double total) {

        this.total = total;
    }

    public void calculatePayment(double productPrice,String membershipType) {

        subtotal = productPrice * quantity;

        if (membershipType.equalsIgnoreCase("VIP")) {

            discount = subtotal * 0.10;

        } else {

            discount = 0;
        }

        total = subtotal - discount;
    }
}
