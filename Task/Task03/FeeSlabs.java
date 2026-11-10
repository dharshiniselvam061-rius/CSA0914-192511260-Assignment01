/**
 * Task 3 - Fee slab logic with if-else and switch.
 * Slabs (assumption): first started hour Rs 20, every additional started hour Rs 10,
 * daily maximum Rs 100 per 24 h; a stay of 0 minutes is free.
 */
public class FeeSlabs {
    static final int FIRST_HOUR = 20, EXTRA_HOUR = 10, DAILY_MAX = 100, MIN_PER_DAY = 1440;

    /** Fee for one stay using if-else. All arithmetic is integer, no casting needed. */
    static int fee(int minutes) {
        if (minutes < 0) throw new IllegalArgumentException("negative duration");
        int days = minutes / MIN_PER_DAY;
        int rem = minutes % MIN_PER_DAY;
        int hours = (rem + 59) / 60;               // started hours in the last (partial) day
        int last;
        if (hours == 0) {
            last = 0;
        } else if (hours == 1) {
            last = FIRST_HOUR;
        } else {
            last = FIRST_HOUR + (hours - 1) * EXTRA_HOUR;
        }
        if (last > DAILY_MAX) last = DAILY_MAX;     // daily maximum
        return days * DAILY_MAX + last;
    }

    /** Hourly rate by vehicle type using switch. */
    static int ratePerHour(String type) {
        switch (type) {
            case "TWO_WHEELER": return 10;
            case "CAR":         return 30;
            case "EV":          return 40;
            default: throw new IllegalArgumentException("unknown vehicle type: " + type);
        }
    }

    public static void main(String[] args) {
        int[] tests = {0, 1, 60, 61, 120, 121, 300, 301, 600, 1440, 1441};
        System.out.println("minutes -> fee");
        for (int m : tests) System.out.println(m + " -> Rs " + fee(m));
        System.out.println("rate(CAR)=" + ratePerHour("CAR") + ", rate(EV)=" + ratePerHour("EV"));
    }
}
