package com.campus.parking.service;

import com.campus.parking.model.Chargeable;
import com.campus.parking.model.ParkingTicket;
import com.campus.parking.model.PassType;
import com.campus.parking.model.UserType;
import com.campus.parking.model.Vehicle;

import java.time.LocalDateTime;

/**
 * Entry and exit processing. Active tickets are kept in a plain array (compacted on exit),
 * so finding a ticket by registration number is a linear scan, O(n).
 */
public class ParkingLot {
    private final ParkingFacility facility;
    private final FeePolicy policy;
    private final ParkingTicket[] active;
    private int activeCount = 0;

    public ParkingLot(int[] slotsPerFloor) {
        this(slotsPerFloor, new FeePolicy());
    }

    public ParkingLot(int[] slotsPerFloor, FeePolicy policy) {
        this.facility = new ParkingFacility(slotsPerFloor);
        this.policy = policy;
        this.active = new ParkingTicket[facility.getCapacity()];
    }

    public ParkingTicket enter(Vehicle vehicle, UserType user, PassType pass, LocalDateTime time) {
        if (vehicle == null) throw new IllegalArgumentException("vehicle is required");
        if (indexOf(vehicle.getRegNo()) >= 0) {
            throw new ParkingException("Vehicle " + vehicle.getRegNo() + " is already parked");
        }
        int[] pos = facility.allocate(vehicle.getRegNo());
        if (pos == null) throw new ParkingException("Parking facility is full");
        ParkingTicket ticket = new ParkingTicket(vehicle, pos[0], pos[1], user, pass, time);
        active[activeCount++] = ticket;
        return ticket;
    }

    /** Releases the slot and returns parking fee + charging fee (EVs only, kWh &gt; 0). */
    public double exit(String regNo, LocalDateTime time, int kWh) {
        int idx = indexOf(regNo == null ? null : regNo.trim().toUpperCase());
        if (idx < 0) throw new ParkingException("No active ticket for " + regNo);
        ParkingTicket t = active[idx];
        double fee = policy.parkingFee(t, time);          // may throw: then nothing is released
        if (kWh > 0 && t.getVehicle() instanceof Chargeable c) fee += c.chargingFee(kWh);
        facility.release(t.getFloor(), t.getSlot());
        active[idx] = active[--activeCount];              // move last ticket into the gap
        active[activeCount] = null;                       // ticket becomes eligible for GC
        return fee;
    }

    public int activeCount() { return activeCount; }
    public int capacity() { return facility.getCapacity(); }
    public ParkingFacility facility() { return facility; }

    private int indexOf(String regNo) {
        for (int i = 0; i < activeCount; i++) {
            if (active[i].getVehicle().getRegNo().equals(regNo)) return i;
        }
        return -1;
    }
}
