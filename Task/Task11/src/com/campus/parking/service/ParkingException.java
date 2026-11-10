package com.campus.parking.service;

/** A business-rule violation (facility full, duplicate vehicle, unknown ticket ...). */
public class ParkingException extends RuntimeException {
    public ParkingException(String message) { super(message); }
}
