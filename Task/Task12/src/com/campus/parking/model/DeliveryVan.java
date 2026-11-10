package com.campus.parking.model;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * NEW: delivery van. Each started hour costs double the car rate when that hour STARTS
 * between 09:00 (inclusive) and 17:00 (exclusive), otherwise the car rate.
 * The same daily cap logic as Vehicle applies (per started 24 h, peak-rate based).
 */
public class DeliveryVan extends Vehicle {
    public static final int PEAK_START_HOUR = 9;
    public static final int PEAK_END_HOUR = 17;          // exclusive
    private static final int MIN_PER_DAY = 24 * 60;

    public DeliveryVan(String regNo) { super(regNo); }

    /** Off-peak hourly rate = car rate. */
    @Override protected double ratePerHour() { return Car.RATE; }

    /** Without times the peak cannot be known, so assume the worst case (all peak). */
    @Override public double calculateFee(int minutes) {
        return super.calculateFee(minutes) * 2;
    }

    @Override public double calculateFee(LocalDateTime in, LocalDateTime out) {
        long minutes = Duration.between(in, out).toMinutes();
        if (minutes < 0) throw new IllegalArgumentException("exit time is before entry time");
        int hours = (int) ((minutes + 59) / 60);
        double sum = 0;
        for (int h = 0; h < hours; h++) {
            int startHour = in.plusHours(h).getHour();
            boolean peak = startHour >= PEAK_START_HOUR && startHour < PEAK_END_HOUR;
            sum += peak ? 2 * Car.RATE : Car.RATE;
        }
        int daysStarted = (int) ((minutes + MIN_PER_DAY - 1) / MIN_PER_DAY);
        double cap = daysStarted * (2 * Car.RATE * DAILY_CAP_HOURS);
        return Math.min(sum, cap);
    }
}
