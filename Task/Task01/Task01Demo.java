import java.util.Random;

/**
 * Task 1 - Alternative concept: vehicle as an object vs. plain Strings in parallel arrays.
 * Compares search by registration number (time) for growing n.
 */
public class Task01Demo {

    /** Design A: each vehicle is an object. Adding an attribute = add one field here. */
    static class VehicleRec {
        final String regNo;
        final String type;
        final long entryTime;
        VehicleRec(String regNo, String type, long entryTime) {
            this.regNo = regNo; this.type = type; this.entryTime = entryTime;
        }
    }

    static int findObjects(VehicleRec[] v, int n, String reg) {
        for (int i = 0; i < n; i++) if (v[i].regNo.equals(reg)) return i;
        return -1;
    }

    /** Design B: parallel arrays. Index i in every array describes the same vehicle. */
    static int findParallel(String[] regNo, int n, String reg) {
        for (int i = 0; i < n; i++) if (regNo[i].equals(reg)) return i;
        return -1;
    }

    public static void main(String[] args) {
        int[] sizes = {1_000, 10_000, 100_000};
        Random rnd = new Random(42);
        System.out.println("n        objects(ns/search)  parallel(ns/search)");
        for (int n : sizes) {
            VehicleRec[] objs = new VehicleRec[n];
            String[] regNo = new String[n];
            String[] type = new String[n];
            long[] entry = new long[n];
            for (int i = 0; i < n; i++) {
                String reg = "TN" + String.format("%02d", i % 100) + "AB" + String.format("%06d", i);
                objs[i] = new VehicleRec(reg, i % 3 == 0 ? "CAR" : "BIKE", rnd.nextInt(1000));
                regNo[i] = reg; type[i] = objs[i].type; entry[i] = objs[i].entryTime;
            }
            String worst = regNo[n - 1];          // worst case: last element -> n comparisons
            int reps = Math.max(20, 2_000_000 / n);
            // warm-up
            for (int r = 0; r < reps; r++) { findObjects(objs, n, worst); findParallel(regNo, n, worst); }
            long t0 = System.nanoTime();
            int sink = 0;
            for (int r = 0; r < reps; r++) sink += findObjects(objs, n, worst);
            long tObj = (System.nanoTime() - t0) / reps;
            t0 = System.nanoTime();
            for (int r = 0; r < reps; r++) sink += findParallel(regNo, n, worst);
            long tPar = (System.nanoTime() - t0) / reps;
            System.out.printf("%-8d %-19d %-19d (check=%d)%n", n, tObj, tPar, sink > 0 ? 1 : 0);
        }
        System.out.println();
        System.out.println("Both searches are O(n) in time (linear scan, worst case n comparisons).");
        System.out.println("Adding an attribute: objects -> add 1 field (+ constructor arg);");
        System.out.println("parallel arrays -> new array + change every place that inserts/removes/sorts.");
    }
}
