package com.campus.parking.model;

import java.time.LocalDateTime;

/** Issued on entry. Immutable, so a ticket can never be half-built or changed later. */
public final class ParkingTicket {
    private static long counter = 0;                   // shared by all tickets -> never reused

    private final long ticketNo;
    private final Vehicle vehicle;
    private final int floor;
    private final int slot;
    private final UserType userType;
    private final PassType passType;
    private final LocalDateTime entryTime;

    public ParkingTicket(Vehicle vehicle, int floor, int slot) {
        this(vehicle, floor, slot, UserType.VISITOR, PassType.NONE);
    }

    public ParkingTicket(Vehicle vehicle, int floor, int slot, UserType userType, PassType passType) {
        this(vehicle, floor, slot, userType, passType, LocalDateTime.now());
    }

    public ParkingTicket(Vehicle vehicle, int floor, int slot, UserType userType,
                         PassType passType, LocalDateTime entryTime) {
        if (vehicle == null || userType == null || passType == null || entryTime == null) {
            throw new IllegalArgumentException("ticket fields must not be null");
        }
        if (floor < 0 || slot < 0) throw new IllegalArgumentException("invalid slot position");
        this.ticketNo = nextNumber();                  // only reached when all checks passed
        this.vehicle = vehicle;
        this.floor = floor;
        this.slot = slot;
        this.userType = userType;
        this.passType = passType;
        this.entryTime = entryTime;
    }

    private static synchronized long nextNumber() { return ++counter; }

    public long getTicketNo() { return ticketNo; }
    public Vehicle getVehicle() { return vehicle; }
    public int getFloor() { return floor; }
    public int getSlot() { return slot; }
    public UserType getUserType() { return userType; }
    public PassType getPassType() { return passType; }
    public LocalDateTime getEntryTime() { return entryTime; }

    @Override public String toString() {
        return "Ticket#" + ticketNo + " " + vehicle + " slot[" + floor + "," + slot + "] "
                + userType + "/" + passType + " in " + entryTime;
    }
}
