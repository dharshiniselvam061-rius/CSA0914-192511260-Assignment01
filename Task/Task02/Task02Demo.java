import java.util.ArrayList;
import java.util.Scanner;

/**
 * Task 2 - Data representation.
 *  (a) Input: command-line arguments if given, otherwise Scanner.
 *  (b) Casting / integer-division risk in fee calculation.
 *  (c) Alternative slot storage: jagged array vs 1-D array vs ArrayList<String[]>.
 *
 * Run:  java Task02Demo 130 30          (minutes ratePerHour)
 *   or  java Task02Demo   and type: 130 30
 */
public class Task02Demo {

    // ---- (b) fee calculations -------------------------------------------------
    /** WRONG: integer division truncates 130/60 = 2, so the started 3rd hour is lost. */
    static double feeWrongIntDivision(int minutes, double rate) {
        int hours = minutes / 60;
        return hours * rate;
    }

    /** WRONG: the cast is applied AFTER the integer division, so it changes nothing. */
    static double feeWrongCastPlacement(int minutes, double rate) {
        double hours = (double) (minutes / 60);
        return hours * rate;
    }

    /** WRONG: (int) cast of a fractional rate truncates Rs 12.5 to Rs 12. */
    static double feeWrongRateCast(int minutes, double rate) {
        int hours = (minutes + 59) / 60;
        return hours * (int) rate;
    }

    /** CORRECT: ceiling by integer arithmetic only, rate kept as double. */
    static double feeCorrect(int minutes, double rate) {
        int hours = (minutes + 59) / 60;     // ceil(minutes / 60)
        return hours * rate;
    }

    // ---- (c) slot storage alternatives ----------------------------------------
    public static void main(String[] args) {
        int minutes;
        double rate;
        if (args.length >= 2) {
            minutes = Integer.parseInt(args[0]);
            rate = Double.parseDouble(args[1]);
        } else {
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter minutes and rate per hour: ");
            minutes = sc.nextInt();
            rate = sc.nextDouble();
        }
        System.out.println("Input: minutes=" + minutes + ", rate=" + rate);
        System.out.println("wrong (int division)   : " + feeWrongIntDivision(minutes, rate));
        System.out.println("wrong (cast placement) : " + feeWrongCastPlacement(minutes, rate));
        System.out.println("wrong (int cast of rate): " + feeWrongRateCast(minutes, 12.5) + "  (rate 12.5 truncated to 12)");
        System.out.println("correct                : " + feeCorrect(minutes, rate));
        System.out.println("correct with rate 12.5 : " + feeCorrect(minutes, 12.5));
        System.out.println();

        // jagged array (unequal floors)
        int[] slotsPerFloor = {30, 50, 40, 60, 20};
        String[][] jagged = new String[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) jagged[f] = new String[slotsPerFloor[f]];

        // 1-D array: for unequal floors the formula floor*slotsPerFloor+slot FAILS,
        // so a prefix-offset array is needed.
        int[] offset = new int[slotsPerFloor.length + 1];
        for (int f = 0; f < slotsPerFloor.length; f++) offset[f + 1] = offset[f] + slotsPerFloor[f];
        String[] flat = new String[offset[slotsPerFloor.length]];

        // ArrayList<String[]>
        ArrayList<String[]> list = new ArrayList<>();
        for (int s : slotsPerFloor) list.add(new String[s]);

        // Store the same vehicle in all three at floor 3, slot 59 and read it back
        jagged[3][59] = "TN01AB1234";
        flat[offset[3] + 59] = "TN01AB1234";
        list.get(3)[59] = "TN01AB1234";
        System.out.println("jagged[3][59]   = " + jagged[3][59]);
        System.out.println("flat[offset[3]+59] = " + flat[offset[3] + 59] + "  (index " + (offset[3] + 59) + ")");
        System.out.println("list.get(3)[59] = " + list.get(3)[59]);

        // Naive formula with uniform slotsPerFloor=30 would give the wrong cell
        int naive = 3 * 30 + 59;
        System.out.println("naive 1-D index 3*30+59 = " + naive + " -> " + flat[naive] + "  (WRONG for unequal floors)");

        // time to locate: all O(1); 1-D needs offset lookup. Space: jagged = F row headers.
        int total = offset[slotsPerFloor.length];
        System.out.println("total slots = " + total + ", jagged rows = " + jagged.length
                + " (F array headers), 1-D = 1 array + (F+1) ints of offsets");
    }
}
