/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author GABRIEL DY
 */
public class PC extends Entity {

    private String pcName;
    private double hourlyRate;
    private String status;

    public PC() {
        super("");
    }

    public PC(String pcId, String pcName, double hourlyRate, String status) {

        super(pcId);

        this.pcName = pcName;
        this.hourlyRate = hourlyRate;
        this.status = status;
    }

    public String getPcId() {
        return id;
    }

    public void setPcId(String pcId) {
        this.id = pcId;
    }

    public String getPcName() {
        return pcName;
    }

    public void setPcName(String pcName) {
        this.pcName = pcName;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getDetails() {
        return id + " | " + pcName + " | " + status;
    }
}
