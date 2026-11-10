import java.lang.ref.WeakReference;
import java.util.Arrays;

/**
 * Task 5 - static members and the object life cycle.
 *  - static variable: ticket counter + total occupancy (belong to the class, not to a ticket)
 *  - static block: one-time initialisation of the rate table
 *  - static method: utility with no object state (ceilHours)
 *  - object passing: references are passed by value
 *  - garbage-collection eligibility after the vehicle leaves
 */
public class Task05Demo {

    static class Ticket {
        private static long counter;                 // one copy for the whole class
        private static int occupancy;                // tickets currently active
        static final double[] RATES;                 // filled by the static block

        static {                                     // runs once, when the class is loaded
            RATES = new double[] {10, 30, 40};       // two-wheeler, car, EV
            counter = 1000;                          // ticket numbers start at 1001
            System.out.println("[static block] Ticket class initialised, first number will be 1001");
        }

        final long no;
        final String reg;
        Ticket(String reg) { this.reg = reg; this.no = ++counter; occupancy++; }
        void close() { occupancy--; }
        static int occupancy() { return occupancy; }
        static int ceilHours(int minutes) { return (minutes + 59) / 60; }   // static utility
    }

    /** Receives a reference (copy of it). It can change the object but not the caller's variable. */
    static void rename(Ticket t, String[] holder) {
        holder[0] = t.reg + "-seen";                 // mutates the shared array object
        t = new Ticket("OTHER");                      // reassigns only the local copy
        t.close();
    }

    /** Returns an object to the caller. */
    static Ticket issue(String reg) { return new Ticket(reg); }

    public static void main(String[] args) throws Exception {
        System.out.println("before any object: occupancy=" + Ticket.occupancy());
        Ticket a = issue("TN01A1");
        Ticket b = issue("TN02B2");
        System.out.println("tickets " + a.no + ", " + b.no + " -> numbers are unique, counter is static");
        System.out.println("occupancy after 2 entries = " + Ticket.occupancy());
        b.close();
        System.out.println("occupancy after 1 exit    = " + Ticket.occupancy());
        System.out.println("ceilHours(130)=" + Ticket.ceilHours(130) + " (static method, no object needed)");
        System.out.println("rates=" + Arrays.toString(Ticket.RATES));

        String[] holder = {"x"};
        rename(a, holder);
        System.out.println("after rename(): a.reg=" + a.reg + " (unchanged), holder[0]=" + holder[0]);

        // Why an instance variable would be wrong for the counter: each ticket would start at 0
        class BadTicket { long counter; long no; BadTicket() { no = ++counter; } }
        System.out.println("instance counter -> numbers " + new BadTicket().no + ", " + new BadTicket().no + " (duplicates!)");

        // Garbage collection: after exit, drop the last reference
        Ticket gone = issue("TN03C3");
        WeakReference<Ticket> ref = new WeakReference<>(gone);
        gone.close();
        gone = null;                                  // no strong reference remains -> eligible for GC
        for (int i = 0; i < 5 && ref.get() != null; i++) { System.gc(); Thread.sleep(50); }
        System.out.println("ticket collected after reference dropped? " + (ref.get() == null)
                + "  (System.gc() is only a request; eligibility is what the language guarantees)");
    }
}
