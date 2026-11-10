package com.campus.parking.model;

/** Capability: a vehicle that can use an EV charging point (charged separately from parking). */
public interface Chargeable {
    double RATE_PER_KWH = 9.0;

    double chargingFee(int kWh);
}
