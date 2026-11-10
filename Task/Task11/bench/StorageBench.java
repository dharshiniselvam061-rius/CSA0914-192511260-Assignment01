import com.campus.parking.model.Car;
import com.campus.parking.model.ParkingTicket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Task 11 - alternative concept: store active tickets in array vs ArrayList vs HashMap
 * keyed by registration number. Measures the time to FIND a ticket on exit.
 * Space for n parked vehicles: all three hold n tickets, O(n); the HashMap adds an entry
 * object + table slot per ticket (roughly 32-48 bytes more per ticket than an array reference).
 */
public class StorageBench {
    public static void main(String[] args) {
        int[] sizes = {100, 1_000, 10_000, 50_000};
        Random rnd = new Random(7);
        System.out.println("n        array(ns/find)  ArrayList(ns/find)  HashMap(ns/find)");
        for (int n : sizes) {
            ParkingTicket[] arr = new ParkingTicket[n];
            ArrayList<ParkingTicket> list = new ArrayList<>(n);
            HashMap<String, ParkingTicket> map = new HashMap<>();
            String[] regs = new String[n];
            for (int i = 0; i < n; i++) {
                regs[i] = "TN" + String.format("%08d", i);
                ParkingTicket t = new ParkingTicket(new Car(regs[i]), 0, i);
                arr[i] = t; list.add(t); map.put(regs[i], t);
            }
            int lookups = Math.max(2_000, 4_000_000 / n);
            String[] targets = new String[lookups];
            for (int i = 0; i < lookups; i++) targets[i] = regs[rnd.nextInt(n)];
            long sink = 0;
            for (int w = 0; w < 2; w++) {                              // warm-up
                for (String r : targets) { sink += scanArray(arr, r) + scanList(list, r) + map.get(r).getSlot(); }
            }
            long t0 = System.nanoTime();
            for (String r : targets) sink += scanArray(arr, r);
            long a = (System.nanoTime() - t0) / lookups;
            t0 = System.nanoTime();
            for (String r : targets) sink += scanList(list, r);
            long l = (System.nanoTime() - t0) / lookups;
            t0 = System.nanoTime();
            for (String r : targets) sink += map.get(r).getSlot();
            long m = (System.nanoTime() - t0) / lookups;
            System.out.printf("%-8d %-15d %-19d %-16d (check=%d)%n", n, a, l, m, sink > 0 ? 1 : 0);
        }
        System.out.println("array/ArrayList find = O(n) scan; HashMap find = O(1) average.");
    }

    static int scanArray(ParkingTicket[] a, String reg) {
        for (int i = 0; i < a.length; i++) if (a[i].getVehicle().getRegNo().equals(reg)) return a[i].getSlot();
        return -1;
    }

    static int scanList(ArrayList<ParkingTicket> l, String reg) {
        for (int i = 0; i < l.size(); i++) if (l.get(i).getVehicle().getRegNo().equals(reg)) return l.get(i).getSlot();
        return -1;
    }
}
