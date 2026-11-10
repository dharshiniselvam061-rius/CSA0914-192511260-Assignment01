package com.campus.parking.model;

/**
 * A vehicle that can park. Identity is the registration number.
 * Fee rule: every started hour is charged in full, capped at DAILY_CAP_HOURS hours
 * per started-and-completed 24 h block (the remainder after whole days is capped too).
 */
public abstract class Vehicle {
    public static final int DAILY_CAP_HOURS = 8;
    private static final int MIN_PER_DAY = 24 * 60;

    private final String regNo;

    protected Vehicle(String regNo) {
        if (regNo == null || regNo.isBlank()) {
            throw new IllegalArgumentException("registration number is required");
        }
        this.regNo = regNo.trim().toUpperCase();
    }

    public final String getRegNo() { return regNo; }

    /** Hourly rate in rupees for this kind of vehicle. */
    protected abstract double ratePerHour();

    /** Parking fee for a stay of the given length. */
    public double calculateFee(int minutes) {
        if (minutes < 0) throw new IllegalArgumentException("negative duration");
        int days = minutes / MIN_PER_DAY;
        int rem = minutes % MIN_PER_DAY;
        int hours = (rem + 59) / 60;                       // ceiling, integer arithmetic only
        double dailyMax = ratePerHour() * DAILY_CAP_HOURS;
        return days * dailyMax + Math.min(hours * ratePerHour(), dailyMax);
    }

    @Override public boolean equals(Object o) {
        return o instanceof Vehicle && regNo.equals(((Vehicle) o).regNo);
    }

    @Override public int hashCode() { return regNo.hashCode(); }

    @Override public String toString() { return getClass().getSimpleName() + "[" + regNo + "]"; }
}
