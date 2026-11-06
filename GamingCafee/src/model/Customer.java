/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author GABRIEL DY
 */
public class Customer extends Entity {

    private String name;
    private String contactNumber;
    private String membershipType;

    public Customer() {
        super("");
    }

    public Customer(String customerId,
            String name,
            String contactNumber,
            String membershipType) {

        super(customerId);

        this.name = name;
        this.contactNumber = contactNumber;
        this.membershipType = membershipType;
    }

    public String getCustomerId() {
        return id;
    }

    public void setCustomerId(String customerId) {
        this.id = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getMembershipType() {
        return membershipType;
    }

    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    @Override
    public String getDetails() {
        return id + " | " + name + " | " + membershipType;
    }
}
