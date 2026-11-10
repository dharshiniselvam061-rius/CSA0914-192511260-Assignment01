package com.campus.parking.model;

/** NEW: e-bike, charged a flat fee per visit (any stay longer than 0 minutes). */
public class EBike extends Vehicle {
    public static final double FLAT_FEE = 20;

    public EBike(String regNo) { super(regNo); }

    /** Flat fee: there is no hourly rate. */
    @Override protected double ratePerHour() { return 0; }

    @Override public double calculateFee(int minutes) {
        if (minutes < 0) throw new IllegalArgumentException("negative duration");
        return minutes == 0 ? 0.0 : FLAT_FEE;
    }
}
