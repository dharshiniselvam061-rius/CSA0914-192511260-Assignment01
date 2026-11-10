import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * Task 3 - Alternative concept: keep a count of free slots per floor and a stack of
 * free slot numbers per floor. allocate() and release() become O(F) worst case
 * (find first floor with freeCount>0) and O(1) for the pop/push itself.
 * Extra space: O(F + S*F) = O(total slots) for the stacks, plus O(F) counts.
 *
 * NOTE: the stack hands out slots in LIFO order, so the first slot given is the
 * LOWEST one only because the stack is filled in descending order at start-up.
 * After releases, the most recently freed slot is reused first (differs from scan).
 */
public class AltFacility {
    private final String[][] slots;
    private final int[] freeCount;
    private final Deque<Integer>[] freeStack;

    @SuppressWarnings("unchecked")
    public AltFacility(int[] slotsPerFloor) {
        slots = new String[slotsPerFloor.length][];
        freeCount = new int[slotsPerFloor.length];
        freeStack = new Deque[slotsPerFloor.length];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            slots[f] = new String[slotsPerFloor[f]];
            freeCount[f] = slotsPerFloor[f];
            freeStack[f] = new ArrayDeque<>();
            for (int s = slotsPerFloor[f] - 1; s >= 0; s--) freeStack[f].push(s);
        }
    }

    public int[] allocate(String vehicleNo) {
        for (int f = 0; f < slots.length; f++) {           // O(F) worst case
            if (freeCount[f] > 0) {
                int s = freeStack[f].pop();                 // O(1)
                slots[f][s] = vehicleNo;
                freeCount[f]--;
                return new int[] {f, s};
            }
        }
        return null;
    }

    public boolean release(int floor, int slot) {            // O(1)
        if (floor < 0 || floor >= slots.length) return false;
        if (slot < 0 || slot >= slots[floor].length) return false;
        if (slots[floor][slot] == null) return false;
        slots[floor][slot] = null;
        freeStack[floor].push(slot);
        freeCount[floor]++;
        return true;
    }

    /** Measures worst-case allocate: every slot on floors 0..F-2 taken, so scan is long. */
    public static void main(String[] args) {
        int[][] configs = {{20}, {50, 50, 50}, {100, 100, 100, 100, 100}};
        System.out.println("config                 scan allocate (ns)   counts+stack allocate (ns)");
        for (int[] cfg : configs) {
            ParkingFacility scan = new ParkingFacility(cfg);
            AltFacility alt = new AltFacility(cfg);
            int total = Arrays.stream(cfg).sum();
            for (int i = 0; i < total - 1; i++) { scan.allocate("V" + i); alt.allocate("V" + i); }
            // one slot left: worst case for the scan (it walks every cell)
            int reps = 200_000;
            long tScan = 0, tAlt = 0;
            for (int w = 0; w < 20_000; w++) {              // warm-up
                int[] a = scan.allocate("X"); scan.release(a[0], a[1]);
                int[] b = alt.allocate("X");  alt.release(b[0], b[1]);
            }
            long t0 = System.nanoTime();
            for (int r = 0; r < reps; r++) { int[] a = scan.allocate("X"); scan.release(a[0], a[1]); }
            tScan = (System.nanoTime() - t0) / reps;
            t0 = System.nanoTime();
            for (int r = 0; r < reps; r++) { int[] b = alt.allocate("X"); alt.release(b[0], b[1]); }
            tAlt = (System.nanoTime() - t0) / reps;
            System.out.printf("%-22s %-20d %-20d%n", Arrays.toString(cfg).length() > 20 ? cfg.length + "x" + cfg[0] : Arrays.toString(cfg), tScan, tAlt);
        }
        // functional check of reuse order
        AltFacility a = new AltFacility(new int[] {2, 1});
        System.out.println(Arrays.toString(a.allocate("A")) + Arrays.toString(a.allocate("B")) + Arrays.toString(a.allocate("C")));
        System.out.println(a.release(0, 1) + " " + a.release(0, 1) + " " + a.release(5, 0));
        System.out.println(Arrays.toString(a.allocate("D")));
    }
}
