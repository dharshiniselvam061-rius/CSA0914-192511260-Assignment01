package com.campus.parking.test;

import com.campus.parking.model.Car;
import com.campus.parking.model.DeliveryVan;
import com.campus.parking.model.EBike;
import com.campus.parking.model.ElectricCar;
import com.campus.parking.model.ParkingTicket;
import com.campus.parking.model.PassType;
import com.campus.parking.model.UserType;
import com.campus.parking.model.Vehicle;
import com.campus.parking.service.ParkingException;
import com.campus.parking.service.ParkingLot;
import com.campus.parking.service.VehicleFactory;

import java.time.LocalDateTime;

/** Tests for the Task 12 requirements: e-bike, delivery van, one-day pass, 5 uneven floors. */
public class NewRequirementTests {
    static int pass = 0, fail = 0;
    static final LocalDateTime D = LocalDateTime.of(2026, 11, 10, 0, 0);

    static LocalDateTime at(int hour, int minute) { return D.plusHours(hour).plusMinutes(minute); }

    static void check(String id, String desc, Object expected, Object actual) {
        boolean ok = String.valueOf(expected).equals(String.valueOf(actual));
        if (ok) pass++; else fail++;
        System.out.printf("%-4s | %-56s | %-24s | %-24s | %s%n", id, desc, expected, actual, ok ? "PASS" : "FAIL");
    }

    static String thrown(Runnable r) {
        try { r.run(); return "no exception"; } catch (RuntimeException e) { return e.getClass().getSimpleName(); }
    }

    /** Park v from 'in' to 'out' on a fresh lot and return the total fee. */
    static double fee(Vehicle v, UserType u, PassType p, LocalDateTime in, LocalDateTime out, int kWh) {
        ParkingLot lot = new ParkingLot(new int[] {3});
        lot.enter(v, u, p, in);
        return lot.exit(v.getRegNo(), out, kWh);
    }

    static double visitor(Vehicle v, LocalDateTime in, LocalDateTime out) {
        return fee(v, UserType.VISITOR, PassType.NONE, in, out, 0);
    }

    public static void main(String[] args) {
        System.out.println("ID   | Test case                                                | Expected                 | Actual                   | Result");
        System.out.println("-----+----------------------------------------------------------+--------------------------+--------------------------+-------");

        // ---- e-bike: flat fee
        check("E1", "E-bike 10 min -> flat Rs 20", 20.0, visitor(new EBike("KL01E1"), at(10, 0), at(10, 10)));
        check("E2", "E-bike 500 min -> still flat Rs 20", 20.0, visitor(new EBike("KL01E2"), at(8, 0), at(16, 20)));
        check("E3", "E-bike 0 min -> Rs 0", 0.0, visitor(new EBike("KL01E3"), at(10, 0), at(10, 0)));
        check("E4", "E-bike 1441 min -> flat Rs 20", 20.0, visitor(new EBike("KL01E4"), at(10, 0), at(10, 0).plusMinutes(1441)));
        check("E5", "Student + monthly pass e-bike -> free", 0.0, fee(new EBike("KL01E5"), UserType.STUDENT, PassType.MONTHLY, at(10, 0), at(12, 0), 0));

        // ---- delivery van: double car rate when the hour starts 09:00-16:59
        check("V1", "Van 10:00-12:10 (3 peak hours) = 3 x 60", 180.0, visitor(new DeliveryVan("TN70V1"), at(10, 0), at(12, 10)));
        check("V2", "Van 08:30-10:30: 08h off-peak 30 + 09h peak 60", 90.0, visitor(new DeliveryVan("TN70V2"), at(8, 30), at(10, 30)));
        check("V3", "Van 16:00-18:00: 16h peak 60 + 17h off-peak 30", 90.0, visitor(new DeliveryVan("TN70V3"), at(16, 0), at(18, 0)));
        check("V4", "Van 17:00-18:00 (starts at 17:00, off-peak)", 30.0, visitor(new DeliveryVan("TN70V4"), at(17, 0), at(18, 0)));
        check("V5", "Van 08:00-09:00 (starts 08:00, off-peak)", 30.0, visitor(new DeliveryVan("TN70V5"), at(8, 0), at(9, 0)));
        check("V6", "Van 09:00-10:00 (starts at 09:00, peak)", 60.0, visitor(new DeliveryVan("TN70V6"), at(9, 0), at(10, 0)));
        check("V7", "Van 16:59 start, 1 h (peak, last minute)", 60.0, visitor(new DeliveryVan("TN70V7"), at(16, 59), at(17, 59)));
        check("V8", "Van 22:00-01:00 (all off-peak)", 90.0, visitor(new DeliveryVan("TN70V8"), at(22, 0), at(25, 0)));
        check("V9", "Van 0 min", 0.0, visitor(new DeliveryVan("TN70V9"), at(10, 0), at(10, 0)));
        check("V10", "Van 09:00-17:00 = 8 peak hours = cap 480", 480.0, visitor(new DeliveryVan("TN70VA"), at(9, 0), at(17, 0)));
        check("V11", "Van 09:00-19:00 capped at 480 (uncapped 540)", 480.0, visitor(new DeliveryVan("TN70VB"), at(9, 0), at(19, 0)));
        check("V12", "Staff + monthly pass van -> free", 0.0, fee(new DeliveryVan("TN70VC"), UserType.STAFF, PassType.MONTHLY, at(10, 0), at(14, 0), 0));
        check("V13", "Car is unchanged by the new rule (10:00-12:10)", 90.0, visitor(new Car("TN71C1"), at(10, 0), at(12, 10)));

        // ---- one-day visitor pass
        check("P1", "Visitor car, day pass, 10 h -> Rs 100", 100.0, fee(new Car("TN72A1"), UserType.VISITOR, PassType.DAY, at(8, 0), at(18, 0), 0));
        check("P2", "Visitor car, day pass, exactly 24 h -> Rs 100", 100.0, fee(new Car("TN72A2"), UserType.VISITOR, PassType.DAY, at(8, 0), at(8, 0).plusDays(1), 0));
        check("P3", "Visitor car, day pass, 24 h 1 min -> Rs 200", 200.0, fee(new Car("TN72A3"), UserType.VISITOR, PassType.DAY, at(8, 0), at(8, 0).plusDays(1).plusMinutes(1), 0));
        check("P4", "Visitor EV, day pass, 10 kWh -> 100 + 90", 190.0, fee(new ElectricCar("TN72A4"), UserType.VISITOR, PassType.DAY, at(8, 0), at(12, 0), 10));
        check("P5", "Visitor van, day pass -> flat 100 (not 480)", 100.0, fee(new DeliveryVan("TN72A5"), UserType.VISITOR, PassType.DAY, at(9, 0), at(17, 0), 0));
        check("P6", "Student with DAY pass gets no discount (30 min car)", 30.0, fee(new Car("TN72A6"), UserType.STUDENT, PassType.DAY, at(8, 0), at(8, 30), 0));
        check("P7", "Visitor with MONTHLY pass still pays full (60 min car)", 30.0, fee(new Car("TN72A7"), UserType.VISITOR, PassType.MONTHLY, at(8, 0), at(9, 0), 0));

        // ---- five floors with different slot counts
        int[] floors = {30, 50, 40, 60, 20};
        ParkingLot lot = new ParkingLot(floors);
        int[][] first = new int[200][];
        for (int i = 0; i < 200; i++) {
            ParkingTicket t = lot.enter(new Car(String.format("TN80%04d", i)), UserType.VISITOR, PassType.NONE, at(9, 0));
            first[i] = new int[] {t.getFloor(), t.getSlot()};
        }
        check("F1", "Capacity of 30+50+40+60+20 floors", 200, lot.capacity());
        check("F2", "31st vehicle goes to floor 1 slot 0", "[1,0]", "[" + first[30][0] + "," + first[30][1] + "]");
        check("F3", "81st vehicle goes to floor 2 slot 0", "[2,0]", "[" + first[80][0] + "," + first[80][1] + "]");
        check("F4", "121st vehicle goes to floor 3 slot 0", "[3,0]", "[" + first[120][0] + "," + first[120][1] + "]");
        check("F5", "181st vehicle goes to floor 4 slot 0", "[4,0]", "[" + first[180][0] + "," + first[180][1] + "]");
        check("F6", "200th vehicle (last free slot) = floor 4 slot 19", "[4,19]", "[" + first[199][0] + "," + first[199][1] + "]");
        check("F7", "201st vehicle rejected: full", "ParkingException", thrown(() -> lot.enter(new Car("TN809999"), UserType.VISITOR, PassType.NONE, at(9, 0))));
        check("F8", "Slot [3,59] valid; [3,60] invalid", "true/false", lot.facility().release(3, 59) + "/" + lot.facility().release(3, 60));
        check("F9", "Floors reported", 5, lot.facility().getFloorCount());

        // ---- factory and future extension
        check("X1", "Factory: 'ebike' -> EBike", "EBike", VehicleFactory.create("ebike", "KL02X1").getClass().getSimpleName());
        check("X2", "Factory: 'DeliveryVan' -> DeliveryVan", "DeliveryVan", VehicleFactory.create("DeliveryVan", "KL02X2").getClass().getSimpleName());
        check("X3", "Factory: still rejects unknown type", "IllegalArgumentException", thrown(() -> VehicleFactory.create("Helicopter", "KL02X3")));
        // A FUTURE type (truck) added without touching any existing class:
        class Truck extends Vehicle {
            Truck(String r) { super(r); }
            @Override protected double ratePerHour() { return 100; }
        }
        VehicleFactory.register("TRUCK", Truck::new);
        check("X4", "Future 'Truck' type: new class + 1 register line", 100.0, visitor(VehicleFactory.create("truck", "KL02X4"), at(10, 0), at(11, 0)));
        check("X5", "equals still by regNo across new types", true, new EBike("KL03A1").equals(new DeliveryVan("KL03A1")));

        System.out.println();
        System.out.println("Total: " + (pass + fail) + "  Passed: " + pass + "  Failed: " + fail);
        if (fail > 0) System.exit(1);
    }
}
