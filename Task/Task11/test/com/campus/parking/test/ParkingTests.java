package com.campus.parking.test;

import com.campus.parking.model.Car;
import com.campus.parking.model.ElectricCar;
import com.campus.parking.model.ParkingTicket;
import com.campus.parking.model.PassType;
import com.campus.parking.model.TwoWheeler;
import com.campus.parking.model.UserType;
import com.campus.parking.model.Vehicle;
import com.campus.parking.service.ParkingException;
import com.campus.parking.service.ParkingLot;
import com.campus.parking.service.VehicleFactory;

import java.time.LocalDateTime;
import java.util.Arrays;

/** Plain-Java test table: id | description | expected | actual | result. Exit code 1 on any failure. */
public class ParkingTests {
    static int pass = 0, fail = 0;
    static final LocalDateTime T0 = LocalDateTime.of(2026, 11, 10, 10, 0);

    static void check(String id, String desc, Object expected, Object actual) {
        boolean ok = String.valueOf(expected).equals(String.valueOf(actual));
        if (ok) pass++; else fail++;
        System.out.printf("%-4s | %-52s | %-26s | %-26s | %s%n", id, desc, expected, actual, ok ? "PASS" : "FAIL");
    }

    static String thrown(Runnable r) {
        try { r.run(); return "no exception"; }
        catch (RuntimeException e) { return e.getClass().getSimpleName(); }
    }

    static double lotFee(Vehicle v, UserType u, PassType p, int minutes, int kWh) {
        ParkingLot lot = new ParkingLot(new int[] {5});
        lot.enter(v, u, p, T0);
        return lot.exit(v.getRegNo(), T0.plusMinutes(minutes), kWh);
    }

    public static void main(String[] args) {
        System.out.println("ID   | Test case                                            | Expected                   | Actual                     | Result");
        System.out.println("-----+------------------------------------------------------+----------------------------+----------------------------+-------");

        // ---- normal operation
        check("N1", "Two-wheeler, 130 min", 30.0, lotFee(new TwoWheeler("TN01A1"), UserType.VISITOR, PassType.NONE, 130, 0));
        check("N2", "Car, 130 min", 90.0, lotFee(new Car("TN02B2"), UserType.VISITOR, PassType.NONE, 130, 0));
        check("N3", "EV, 130 min + 10 kWh charging", 210.0, lotFee(new ElectricCar("TN03C3"), UserType.VISITOR, PassType.NONE, 130, 10));
        check("N4", "Student with monthly pass parks free", 0.0, lotFee(new Car("TN04D4"), UserType.STUDENT, PassType.MONTHLY, 300, 0));
        check("N5", "Staff with monthly pass parks free", 0.0, lotFee(new Car("TN05E5"), UserType.STAFF, PassType.MONTHLY, 300, 0));
        check("N6", "Visitor with monthly pass still pays (car 60 min)", 30.0, lotFee(new Car("TN06F6"), UserType.VISITOR, PassType.MONTHLY, 60, 0));
        check("N7", "Staff + monthly pass EV pays only charging (5 kWh)", 45.0, lotFee(new ElectricCar("TN07G7"), UserType.STAFF, PassType.MONTHLY, 120, 5));
        check("N8", "Student without pass pays (car 61 min)", 60.0, lotFee(new Car("TN08H8"), UserType.STUDENT, PassType.NONE, 61, 0));

        // ---- boundary cases
        check("B1", "Zero-minute parking (car)", 0.0, lotFee(new Car("TN11A1"), UserType.VISITOR, PassType.NONE, 0, 0));
        check("B2", "Exactly 60 min (car) = 1 hour", 30.0, lotFee(new Car("TN12A1"), UserType.VISITOR, PassType.NONE, 60, 0));
        check("B3", "61 min (car) = 2 hours", 60.0, lotFee(new Car("TN13A1"), UserType.VISITOR, PassType.NONE, 61, 0));
        check("B4", "1 min (car) = 1 hour", 30.0, lotFee(new Car("TN14A1"), UserType.VISITOR, PassType.NONE, 1, 0));
        check("B5", "Exactly 480 min (car) = 8 h = daily cap", 240.0, lotFee(new Car("TN15A1"), UserType.VISITOR, PassType.NONE, 480, 0));
        check("B6", "481 min (car) capped at daily max", 240.0, lotFee(new Car("TN16A1"), UserType.VISITOR, PassType.NONE, 481, 0));
        check("B7", "Exactly 1440 min (car) = 1 full day", 240.0, lotFee(new Car("TN17A1"), UserType.VISITOR, PassType.NONE, 1440, 0));
        check("B8", "1441 min (car) = 1 day + 1 hour", 270.0, lotFee(new Car("TN18A1"), UserType.VISITOR, PassType.NONE, 1441, 0));
        check("B9", "EV with 0 kWh adds no charging fee (60 min)", 40.0, lotFee(new ElectricCar("TN19A1"), UserType.VISITOR, PassType.NONE, 60, 0));

        // ---- last free slot / allocation order / reuse
        ParkingLot one = new ParkingLot(new int[] {1});
        ParkingTicket t1 = one.enter(new Car("TN21A1"), UserType.VISITOR, PassType.NONE, T0);
        check("S1", "Last free slot is allocated", "[0,0]", "[" + t1.getFloor() + "," + t1.getSlot() + "]");
        check("S2", "Entering when full is rejected", "ParkingException", thrown(() -> one.enter(new Car("TN22A1"), UserType.VISITOR, PassType.NONE, T0)));
        one.exit("TN21A1", T0.plusMinutes(10), 0);
        check("S3", "Slot is reusable after exit", "ok", thrown(() -> one.enter(new Car("TN22A1"), UserType.VISITOR, PassType.NONE, T0)).equals("no exception") ? "ok" : "fail");
        ParkingLot two = new ParkingLot(new int[] {2, 1});
        ParkingTicket a = two.enter(new Car("TN31A1"), UserType.VISITOR, PassType.NONE, T0);
        ParkingTicket b = two.enter(new Car("TN32A1"), UserType.VISITOR, PassType.NONE, T0);
        ParkingTicket c = two.enter(new Car("TN33A1"), UserType.VISITOR, PassType.NONE, T0);
        check("S4", "Slots filled floor by floor", "[0,0][0,1][1,0]",
                "[" + a.getFloor() + "," + a.getSlot() + "][" + b.getFloor() + "," + b.getSlot() + "][" + c.getFloor() + "," + c.getSlot() + "]");
        check("S5", "Tickets numbers strictly increasing/unique", true, a.getTicketNo() < b.getTicketNo() && b.getTicketNo() < c.getTicketNo());
        two.exit("TN32A1", T0.plusMinutes(5), 0);
        ParkingTicket d = two.enter(new Car("TN34A1"), UserType.VISITOR, PassType.NONE, T0);
        check("S6", "Freed slot [0,1] is reused", "[0,1]", "[" + d.getFloor() + "," + d.getSlot() + "]");
        check("S7", "Ticket number never reused after exit", true, d.getTicketNo() > c.getTicketNo());
        check("S8", "Active count after 4 entries and 1 exit", 3, two.activeCount());

        // ---- invalid cases
        check("I1", "Unknown vehicle type", "IllegalArgumentException", thrown(() -> VehicleFactory.create("Helicopter", "TN41A1")));
        check("I2", "Null vehicle type", "IllegalArgumentException", thrown(() -> VehicleFactory.create(null, "TN41A1")));
        check("I3", "Blank registration number", "IllegalArgumentException", thrown(() -> new Car("  ")));
        ParkingLot dup = new ParkingLot(new int[] {3});
        dup.enter(new Car("TN42A1"), UserType.VISITOR, PassType.NONE, T0);
        check("I4", "Duplicate registration number", "ParkingException", thrown(() -> dup.enter(new TwoWheeler("tn42a1"), UserType.VISITOR, PassType.NONE, T0)));
        check("I5", "Duplicate rejected without using a slot", 1, dup.activeCount());
        check("I6", "release(5,0) invalid floor", false, dup.facility().release(5, 0));
        check("I7", "release(-1,0) negative floor", false, dup.facility().release(-1, 0));
        check("I8", "release(0,99) invalid slot", false, dup.facility().release(0, 99));
        check("I9", "release(0,2) already-free slot", false, dup.facility().release(0, 2));
        check("I10", "Exit of unknown vehicle", "ParkingException", thrown(() -> dup.exit("NOPE", T0.plusMinutes(5), 0)));
        check("I11", "Exit before entry time", "IllegalArgumentException", thrown(() -> dup.exit("TN42A1", T0.minusMinutes(5), 0)));
        check("I12", "Failed exit keeps vehicle parked", 1, dup.activeCount());
        check("I13", "Negative duration in calculateFee", "IllegalArgumentException", thrown(() -> new Car("TN43A1").calculateFee(-1)));
        check("I14", "Facility with zero-size floor", "IllegalArgumentException", thrown(() -> new ParkingLot(new int[] {3, 0})));
        check("I15", "Negative kWh", "IllegalArgumentException", thrown(() -> new ElectricCar("TN44A1").chargingFee(-5)));

        // ---- object contract
        check("O1", "equals: same regNo, different class", true, new Car("TN51A1").equals(new ElectricCar("TN51A1")));
        check("O2", "hashCode consistent with equals", true, new Car("TN51A1").hashCode() == new ElectricCar("tn51a1").hashCode());
        check("O3", "different regNo not equal", false, new Car("TN51A1").equals(new Car("TN51A2")));

        System.out.println();
        System.out.println("Total: " + (pass + fail) + "  Passed: " + pass + "  Failed: " + fail);
        if (fail > 0) System.exit(1);
    }
}
