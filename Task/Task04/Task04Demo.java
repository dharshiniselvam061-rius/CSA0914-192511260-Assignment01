import java.time.LocalDateTime;

/**
 * Task 4 - Classes, objects and constructors.
 * ParkingTicket uses overloaded constructors chained with this(...).
 * The demo also compares: default constructor + setters, and a static factory,
 * and measures the cost of creating n tickets with each.
 */
public class Task04Demo {

    // ---- Approach 1 (used in the design): overloaded + chained constructors ------
    static final class ParkingTicket {
        private static long counter = 0;               // shared by all tickets (Task 5)
        private final long ticketNo;
        private final String regNo;
        private final int floor, slot;
        private final LocalDateTime entryTime;

        ParkingTicket(String regNo, int floor, int slot) {           // 1: minimal
            this(regNo, floor, slot, LocalDateTime.now());           // chains to 2
        }

        ParkingTicket(String regNo, int floor, int slot, LocalDateTime entryTime) {   // 2: full
            if (regNo == null || regNo.isBlank()) throw new IllegalArgumentException("regNo");
            if (floor < 0 || slot < 0) throw new IllegalArgumentException("slot position");
            if (entryTime == null) throw new IllegalArgumentException("entryTime");
            this.ticketNo = ++counter;                 // always valid once constructed
            this.regNo = regNo; this.floor = floor; this.slot = slot; this.entryTime = entryTime;
            if (TRACE) System.out.println("  full constructor ran for ticket " + ticketNo);
        }

        long getTicketNo() { return ticketNo; }
        @Override public String toString() {
            return "Ticket#" + ticketNo + " " + regNo + " @[" + floor + "," + slot + "] " + entryTime;
        }
    }
    static boolean TRACE = false;

    // ---- Approach 2: default constructor + setters (object may be half-built) ----
    static final class MutableTicket {
        long ticketNo; String regNo; int floor = -1, slot = -1; LocalDateTime entryTime;
        void setRegNo(String r) { regNo = r; }
        void setPos(int f, int s) { floor = f; slot = s; }
        void setEntry(LocalDateTime t) { entryTime = t; }
    }

    // ---- Approach 3: static factory method ---------------------------------------
    static final class FactoryTicket {
        final String regNo; final int floor, slot;
        private FactoryTicket(String r, int f, int s) { regNo = r; floor = f; slot = s; }
        static FactoryTicket of(String r, int f, int s) {
            if (r == null || r.isBlank() || f < 0 || s < 0) throw new IllegalArgumentException();
            return new FactoryTicket(r, f, s);
        }
    }

    public static void main(String[] args) {
        System.out.println("--- constructor chaining trace ---");
        TRACE = true;
        ParkingTicket t = new ParkingTicket("TN01AB1234", 0, 3);   // 3-arg -> this(...) -> 4-arg
        System.out.println(t);
        TRACE = false;

        System.out.println("--- half-built object with setters ---");
        MutableTicket m = new MutableTicket();
        m.setRegNo("TN01AB1234");                                  // forgot setPos/setEntry
        System.out.println("MutableTicket created with floor=" + m.floor + ", entryTime=" + m.entryTime
                + "  -> invalid object is possible");

        int[] ns = {100_000, 1_000_000, 5_000_000};
        System.out.println();
        System.out.println("n          chained-ctor(ms)  default+setters(ms)  factory(ms)");
        LocalDateTime now = LocalDateTime.now();
        for (int n : ns) {
            Object keep = null;
            for (int w = 0; w < 2; w++) {                          // warm-up round
                for (int i = 0; i < n / 10; i++) keep = new ParkingTicket("TN01AB1234", 0, 1, now);
            }
            long t0 = System.nanoTime();
            for (int i = 0; i < n; i++) keep = new ParkingTicket("TN01AB1234", 0, 1, now);
            long a = (System.nanoTime() - t0) / 1_000_000;
            t0 = System.nanoTime();
            for (int i = 0; i < n; i++) {
                MutableTicket x = new MutableTicket(); x.setRegNo("TN01AB1234"); x.setPos(0, 1); x.setEntry(now); keep = x;
            }
            long b = (System.nanoTime() - t0) / 1_000_000;
            t0 = System.nanoTime();
            for (int i = 0; i < n; i++) keep = FactoryTicket.of("TN01AB1234", 0, 1);
            long c = (System.nanoTime() - t0) / 1_000_000;
            System.out.printf("%-10d %-17d %-20d %-12d (%s)%n", n, a, b, c, keep != null);
        }
        System.out.println("All three create n objects in O(n) time and O(1) extra space per object;");
        System.out.println("the guarantee of validity is what differs (constructor/factory vs setters).");
    }
}
