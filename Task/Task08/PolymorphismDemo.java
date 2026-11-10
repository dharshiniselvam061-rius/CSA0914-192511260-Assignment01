/**
 * Task 8 - Polymorphism.
 *  Compile-time (static) polymorphism = method overloading, resolved by the compiler.
 *  Runtime (dynamic) polymorphism    = overriding, resolved by the JVM from the object's real class.
 * Also benchmarks: dynamic dispatch vs instanceof chain vs switch on a type field.
 */
public class PolymorphismDemo {

    static abstract class Vehicle {
        int type;                                    // only used by the switch alternative
        abstract double rate();
        double fee(int minutes) { return ((minutes + 59) / 60) * rate(); }            // overload 1
        double fee(int minutes, double discountPercent) {                             // overload 2
            return fee(minutes) * (100 - discountPercent) / 100;
        }
    }
    static class Bike extends Vehicle { Bike() { type = 0; } double rate() { return 10; } }
    static class Car  extends Vehicle { Car()  { type = 1; } double rate() { return 30; } }
    static class Ev   extends Vehicle { Ev()   { type = 2; } double rate() { return 40; } }

    // alternative 1: instanceof + casting
    static double feeInstanceof(Vehicle v, int m) {
        int h = (m + 59) / 60;
        if (v instanceof Bike b) return h * b.rate();
        else if (v instanceof Car c) return h * c.rate();
        else if (v instanceof Ev e) return h * e.rate();
        throw new IllegalArgumentException();
    }
    // alternative 2: switch on a type code
    static double feeSwitch(Vehicle v, int m) {
        int h = (m + 59) / 60;
        switch (v.type) {
            case 0: return h * 10;
            case 1: return h * 30;
            case 2: return h * 40;
            default: throw new IllegalArgumentException();
        }
    }

    public static void main(String[] args) {
        // compile-time: the compiler picks fee(int) or fee(int,double) from the argument list
        Vehicle declared = new Car();                    // declared type Vehicle, actual type Car
        System.out.println("overload fee(130)        = " + declared.fee(130));
        System.out.println("overload fee(130, 10%)   = " + declared.fee(130, 10));

        // runtime: trace of one call
        Vehicle v = new Ev();
        System.out.println("Declared type : " + Vehicle.class.getSimpleName());
        System.out.println("Actual type   : " + v.getClass().getSimpleName());
        System.out.println("rate() executed from: " + v.getClass().getSimpleName() + ".rate() -> " + v.rate());

        // benchmark
        Vehicle[] mix = {new Bike(), new Car(), new Ev()};
        int N = 30_000_000;
        double sink = 0;
        for (int w = 0; w < 3_000_000; w++) { Vehicle x = mix[w % 3]; sink += x.fee(130) + feeInstanceof(x, 130) + feeSwitch(x, 130); }
        long t0 = System.nanoTime();
        for (int i = 0; i < N; i++) sink += mix[i % 3].fee(130);
        double dyn = (System.nanoTime() - t0) / (double) N;
        t0 = System.nanoTime();
        for (int i = 0; i < N; i++) sink += feeInstanceof(mix[i % 3], 130);
        double ins = (System.nanoTime() - t0) / (double) N;
        t0 = System.nanoTime();
        for (int i = 0; i < N; i++) sink += feeSwitch(mix[i % 3], 130);
        double sw = (System.nanoTime() - t0) / (double) N;
        System.out.printf("ns per fee call (k=3 types): dispatch=%.2f  instanceof=%.2f  switch=%.2f  (sink=%.0f)%n", dyn, ins, sw, sink);
        System.out.println("Dispatch is O(1) (vtable); instanceof/switch chains are O(k) in the worst case,");
        System.out.println("and adding a type means editing the chain (more classes change) instead of adding a class.");
    }
}
