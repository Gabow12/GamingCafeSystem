/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author GABRIEL DY
 */
public class Session {

    private String sessionId;
    private String customerId;
    private String pcId;

    private double hours;
    private double subtotal;
    private double discount;
    private double total;

    private String date;

    public Session(
            String sessionId,
            String customerId,
            String pcId,
            double hours,
            String date) {

        this.sessionId = sessionId;
        this.customerId = customerId;
        this.pcId = pcId;
        this.hours = hours;
        this.date = date;
    }

    public void calculatePayment(double hourlyRate,String membershipType) {

        subtotal = hourlyRate * hours;

        if(membershipType.equalsIgnoreCase("VIP")) {

            discount = subtotal * 0.10;

        } else {

            discount = 0;
        }

        total = subtotal - discount;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getPcId() {
        return pcId;
    }

    public double getHours() {
        return hours;
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
    
    public void setSubtotal(double subtotal) {
    this.subtotal = subtotal;
}

public void setDiscount(double discount) {
    this.discount = discount;
}

public void setTotal(double total) {
    this.total = total;
}
}