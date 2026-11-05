/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author GABRIEL DY
 */
public class Reservation extends Entity {

    private String status;

    private int hours;

    private String customerId;
    private String pcId;

    private String date;
    private String startTime;
    private String endTime;

    private double subtotal;
    private double discount;
    private double vat;
    private double total;

    public Reservation() {
        super("");
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public Reservation(String reservationId,
            String customerId,
            String pcId,
            String date,
            String startTime,
            String endTime) {

        super(reservationId);

        this.customerId = customerId;
        this.pcId = pcId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;

        this.status = "ACTIVE";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReservationId() {
        return id;
    }

    public void setReservationId(String reservationId) {
        this.id = reservationId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getPcId() {
        return pcId;
    }

    public void setPcId(String pcId) {
        this.pcId = pcId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDiscount() {
        return discount;
    }

    public double getVat() {
        return vat;
    }

    public double getTotal() {
        return total;
    }

    public void calculatePayment(double hours, double hourlyRate, String membershipType) {

        this.hours = (int) hours;

        subtotal = hours * hourlyRate;

        if (membershipType.equalsIgnoreCase("VIP")) {

            discount = subtotal * 0.10;

        } else {

            discount = 0;
        }
        vat = (subtotal - discount) * 0.12;

        total = (subtotal - discount) + vat;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    @Override
    public String getDetails() {
        return id + " | " + customerId + " | " + pcId;
    }
}
