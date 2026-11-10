package com.campus.parking.model;

public class ElectricCar extends Vehicle implements Chargeable {
    public static final double RATE = 40;

    public ElectricCar(String regNo) { super(regNo); }

    @Override protected double ratePerHour() { return RATE; }

    @Override public double chargingFee(int kWh) {
        if (kWh < 0) throw new IllegalArgumentException("negative kWh");
        return kWh * RATE_PER_KWH;
    }
}
