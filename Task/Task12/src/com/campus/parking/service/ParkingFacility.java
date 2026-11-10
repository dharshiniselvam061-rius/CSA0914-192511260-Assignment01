package com.campus.parking.service;

/** Floors and numbered slots. A jagged array lets every floor have its own size. */
public class ParkingFacility {
    // slots[floor][slot] holds a vehicle number, or null if the slot is free
    private final String[][] slots;
    private final int capacity;

    public ParkingFacility(int[] slotsPerFloor) {
        if (slotsPerFloor == null || slotsPerFloor.length == 0) {
            throw new IllegalArgumentException("at least one floor is required");
        }
        slots = new String[slotsPerFloor.length][];
        int total = 0;
        for (int f = 0; f < slotsPerFloor.length; f++) {
            if (slotsPerFloor[f] <= 0) throw new IllegalArgumentException("floor " + f + " needs >= 1 slot");
            slots[f] = new String[slotsPerFloor[f]];
            total += slotsPerFloor[f];
        }
        capacity = total;
    }

    /** Returns {floor, slot} of the first free slot, or null if it is full. */
    public int[] allocate(String vehicleNo) {
        if (vehicleNo == null) throw new IllegalArgumentException("vehicleNo must not be null");
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (slots[f][s] == null) {
                    slots[f][s] = vehicleNo;
                    return new int[] {f, s};
                }
            }
        }
        return null;
    }

    /** Frees a slot. Returns false if the position is invalid or already free. */
    public boolean release(int floor, int slot) {
        if (floor < 0 || floor >= slots.length) return false;
        if (slot < 0 || slot >= slots[floor].length) return false;
        if (slots[floor][slot] == null) return false;
        slots[floor][slot] = null;
        return true;
    }

    public int getCapacity() { return capacity; }
    public int getFloorCount() { return slots.length; }
    public int getSlotCount(int floor) { return slots[floor].length; }

    /** Vehicle number parked at the position, or null if free/invalid. */
    public String occupant(int floor, int slot) {
        if (floor < 0 || floor >= slots.length || slot < 0 || slot >= slots[floor].length) return null;
        return slots[floor][slot];
    }
}
