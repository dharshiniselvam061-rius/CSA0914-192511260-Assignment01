/**
 * Task 6 - trace of constructor execution order when an ElectricCar is created.
 * Expected order: Object -> Vehicle -> ElectricCar (superclass first, always).
 */
public class ConstructorTrace {

    static abstract class Vehicle {
        private final String regNo;
        { System.out.println("2. Vehicle instance initialiser runs (before Vehicle constructor body)"); }
        protected Vehicle(String regNo) {
            // implicit super() -> Object() has already finished here
            System.out.println("3. Vehicle constructor body: regNo = " + regNo);
            this.regNo = regNo;
            System.out.println("   (ratePerHour() seen from here = " + ratePerHour() + " -> subclass method, but its fields are not initialised yet)");
        }
        protected abstract double ratePerHour();
    }

    static class ElectricCar extends Vehicle {
        private double rate = 40;                    // field initialiser runs AFTER super(...) returns
        { System.out.println("4. ElectricCar instance initialiser runs, rate field = " + rate); }
        ElectricCar(String regNo) {
            super(regNo);                            // explicit super(...) call: must be the first statement
            System.out.println("5. ElectricCar constructor body finished");
        }
        @Override protected double ratePerHour() { return rate; }
    }

    public static void main(String[] args) {
        System.out.println("1. new ElectricCar(\"TN03C3\") called; Object() runs first (implicit)");
        new ElectricCar("TN03C3");
    }
}
