import java.util.HashSet;
import java.util.Set;

/**
 * Task 7 - Overriding, final, and Object-class methods.
 * (The PDF text for Task 7 is missing its heading; this follows the "Discuss" bullets
 *  on the Overriding page: calculateFee override, @Override, final, toString/equals/hashCode.)
 */
public class OverrideDemo {

    static abstract class Vehicle {
        private final String regNo;                         // final variable: identity never changes
        protected Vehicle(String regNo) { this.regNo = regNo; }
        public final String getRegNo() { return regNo; }    // final method: subclasses cannot change it
        protected abstract double ratePerHour();
        public double calculateFee(int minutes) { return ((minutes + 59) / 60) * ratePerHour(); }

        @Override public String toString() { return getClass().getSimpleName() + "[" + regNo + "]"; }
        @Override public boolean equals(Object o) { return o instanceof Vehicle v && regNo.equals(v.regNo); }
        @Override public int hashCode() { return regNo.hashCode(); }   // consistent with equals
    }

    static class Car extends Vehicle {
        Car(String r) { super(r); }
        @Override protected double ratePerHour() { return 30; }
    }

    /** Overrides calculateFee: same signature, same return type, access not narrower. */
    static class Bus extends Vehicle {
        Bus(String r) { super(r); }
        @Override protected double ratePerHour() { return 50; }
        @Override public double calculateFee(int minutes) { return Math.min(super.calculateFee(minutes), 300); }
        // @Override public int calculateFee(int m)  -> compile error: return type must match
        // @Override double calculateFee(int m)      -> compile error: weaker access than public
    }

    /** final class: nobody may subclass it (cannot be altered by inheritance). */
    static final class Money {
        final long paise;
        Money(long paise) { this.paise = paise; }
    }

    public static void main(String[] args) {
        Vehicle c = new Car("TN01A1");
        Vehicle b = new Bus("TN09B9");
        System.out.println(c + " 130 min -> " + c.calculateFee(130));
        System.out.println(b + " 130 min -> " + b.calculateFee(130) + ", 900 min -> " + b.calculateFee(900) + " (capped)");

        Set<Vehicle> set = new HashSet<>();
        set.add(new Car("TN01A1"));
        System.out.println("same regNo equal? " + new Car("TN01A1").equals(new Car("TN01A1")));
        System.out.println("HashSet contains equal object? " + set.contains(new Car("TN01A1")));
        System.out.println("different regNo equal? " + new Car("TN01A1").equals(new Car("TN01A2")));
        System.out.println("equals(null)=" + new Car("TN01A1").equals(null));
        System.out.println("money=" + new Money(2550).paise + " paise (final class)");
    }
}
