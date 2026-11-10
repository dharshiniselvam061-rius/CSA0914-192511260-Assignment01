package com.campus.parking.service;

import com.campus.parking.model.Car;
import com.campus.parking.model.DeliveryVan;
import com.campus.parking.model.EBike;
import com.campus.parking.model.ElectricCar;
import com.campus.parking.model.TwoWheeler;
import com.campus.parking.model.Vehicle;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Creates a vehicle from the text a gate operator types.
 * A registry replaces the old switch: a new vehicle type needs ONE register(...) line here
 * (or a call from outside) and no other existing class changes.
 */
public final class VehicleFactory {
    private static final Map<String, Function<String, Vehicle>> REGISTRY = new HashMap<>();

    static {
        register("TWOWHEELER", TwoWheeler::new);
        register("CAR", Car::new);
        register("ELECTRICCAR", ElectricCar::new);
        register("EBIKE", EBike::new);                       // new
        register("DELIVERYVAN", DeliveryVan::new);           // new
    }

    private VehicleFactory() { }

    public static void register(String type, Function<String, Vehicle> constructor) {
        REGISTRY.put(type.trim().toUpperCase(), constructor);
    }

    public static Vehicle create(String type, String regNo) {
        if (type == null) throw new IllegalArgumentException("vehicle type is required");
        Function<String, Vehicle> f = REGISTRY.get(type.trim().toUpperCase());
        if (f == null) throw new IllegalArgumentException("Unknown vehicle type: " + type);
        return f.apply(regNo);
    }
}
