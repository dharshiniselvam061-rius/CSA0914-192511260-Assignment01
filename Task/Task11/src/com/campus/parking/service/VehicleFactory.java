package com.campus.parking.service;

import com.campus.parking.model.Car;
import com.campus.parking.model.ElectricCar;
import com.campus.parking.model.TwoWheeler;
import com.campus.parking.model.Vehicle;

/** Creates a vehicle from the text a gate operator types. */
public final class VehicleFactory {
    private VehicleFactory() { }

    public static Vehicle create(String type, String regNo) {
        if (type == null) throw new IllegalArgumentException("vehicle type is required");
        switch (type.trim().toUpperCase()) {
            case "TWOWHEELER": return new TwoWheeler(regNo);
            case "CAR":        return new Car(regNo);
            case "ELECTRICCAR": return new ElectricCar(regNo);
            default: throw new IllegalArgumentException("Unknown vehicle type: " + type);
        }
    }
}
