/**
 * Task 9 - Abstraction: abstract class vs interface.
 *  - Vehicle  (abstract class)  : has state (regNo), a constructor and shared code.
 *  - Chargeable (interface)     : a capability that unrelated classes can add.
 *  - Payable (interface)        : default + static methods.
 *  - ElectricCar obtains MULTIPLE behaviours by implementing two interfaces.
 */
public class InterfaceDemo {

    interface Chargeable {
        double RATE_PER_KWH = 9.0;                         // implicitly public static final
        double chargingFee(int kWh);
    }

    interface Payable {
        double amountDue();
        default String receipt() { return "Receipt: Rs " + amountDue(); }     // default method
        static double withGst(double amount) { return amount * 1.05; }        // static method
    }

    static abstract class Vehicle {
        private final String regNo;
        protected Vehicle(String regNo) { this.regNo = regNo; }
        String regNo() { return regNo; }
        protected abstract double ratePerHour();
        double fee(int minutes) { return ((minutes + 59) / 60) * ratePerHour(); }
    }

    static class ElectricCar extends Vehicle implements Chargeable, Payable {
        private final int minutes, kWh;
        ElectricCar(String r, int minutes, int kWh) { super(r); this.minutes = minutes; this.kWh = kWh; }
        @Override protected double ratePerHour() { return 40; }
        @Override public double chargingFee(int k) { return k * RATE_PER_KWH; }
        @Override public double amountDue() { return fee(minutes) + chargingFee(kWh); }
    }

    /** A charging station is NOT a vehicle but can still be Chargeable - impossible with a class. */
    static class WallSocket implements Chargeable {
        @Override public double chargingFee(int kWh) { return kWh * 6.0; }
    }

    public static void main(String[] args) {
        ElectricCar ev = new ElectricCar("TN03C3", 130, 10);
        System.out.println(ev.regNo() + " " + ev.receipt());
        System.out.println("with GST: " + Payable.withGst(ev.amountDue()));
        Chargeable c = new WallSocket();
        System.out.println("WallSocket 10 kWh = " + c.chargingFee(10));
        System.out.println("ElectricCar is Vehicle? " + (ev instanceof Vehicle)
                + ", Chargeable? " + (ev instanceof Chargeable) + ", Payable? " + (ev instanceof Payable));
    }
}
