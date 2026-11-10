package com.campus.parking.model;

public class Car extends Vehicle {
    public static final double RATE = 30;

    public Car(String regNo) { super(regNo); }

    @Override protected double ratePerHour() { return RATE; }
}
