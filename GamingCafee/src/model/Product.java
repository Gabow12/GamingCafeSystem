/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author GABRIEL DY
 */
public class Product {

    private String productId;
    private String productName;
    private String category;
    private double price;
    private int stock;

    public Product(
            String productId,
            String productName,
            String category,
            double price,
            int stock) {

        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.price = price;
        this.stock = stock;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(
            String productId) {

        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(
            String productName) {

        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(
            String category) {

        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(
            double price) {

        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(
            int stock) {

        this.stock = stock;
    }
}
