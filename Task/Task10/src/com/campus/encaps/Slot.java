package com.campus.encaps;

/**
 * Task 10 - Encapsulation: private fields, validating setters, package-private helper.
 */
public class Slot {
    private int number;                 // private: only this class can touch it
    private String vehicleNo;           // null = free

    public Slot(int number) { setNumber(number); }

    public int getNumber() { return number; }

    public void setNumber(int number) {
        if (number < 0) throw new IllegalArgumentException("slot number must be >= 0, was " + number);
        this.number = number;
    }

    public String getVehicleNo() { return vehicleNo; }

    public void park(String vehicleNo) {
        if (vehicleNo == null || vehicleNo.isBlank()) throw new IllegalArgumentException("vehicleNo required");
        if (this.vehicleNo != null) throw new IllegalStateException("slot already holds " + this.vehicleNo);
        this.vehicleNo = vehicleNo;
    }

    public void leave() { this.vehicleNo = null; }

    public boolean isFree() { return vehicleNo == null; }

    /** package-private: usable inside com.campus.encaps only (a "friend" for same-package classes). */
    String describe() { return "Slot " + number + (isFree() ? " free" : " holds " + vehicleNo); }
}
