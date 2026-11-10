package com.campus.parking.model;

public class TwoWheeler extends Vehicle {
    public static final double RATE = 10;

    public TwoWheeler(String regNo) { super(regNo); }

    @Override protected double ratePerHour() { return RATE; }
}
