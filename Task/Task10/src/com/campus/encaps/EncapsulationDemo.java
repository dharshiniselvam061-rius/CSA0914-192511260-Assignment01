package com.campus.encaps;

import java.util.ArrayList;
import java.util.List;

public class EncapsulationDemo {

    /** The alternative: public fields, so every caller must validate - and may forget. */
    static class OpenSlot { public int number; public String vehicleNo; }

    public static void main(String[] args) {
        Slot s = new Slot(5);
        s.park("TN01A1");
        System.out.println(s.describe());                    // same package -> allowed
        try { s.park("TN02B2"); } catch (IllegalStateException e) { System.out.println("blocked: " + e.getMessage()); }
        try { s.setNumber(-3); } catch (IllegalArgumentException e) { System.out.println("blocked: " + e.getMessage()); }
        // s.number = -3;   // would not compile: number has private access

        OpenSlot o = new OpenSlot();
        o.number = -3;                                       // nothing stops the invalid value
        o.vehicleNo = "";                                    // nor an empty registration number
        System.out.println("OpenSlot silently holds number=" + o.number + ", vehicleNo='" + o.vehicleNo + "'");

        // Wrapper classes and autoboxing
        Integer a = 127, b = 127, c = 128, d = 128;           // autoboxing int -> Integer
        System.out.println("127 == 127 (Integer) : " + (a == b) + "  (cached small values)");
        System.out.println("128 == 128 (Integer) : " + (c == d) + "  (different objects! use equals)");
        System.out.println("128 equals 128       : " + c.equals(d));
        List<Integer> floors = new ArrayList<>();
        floors.add(3);                                       // autobox
        int first = floors.get(0);                           // auto-unbox
        System.out.println("autobox/unbox round trip: " + first);
        Integer missing = null;
        try { int x = missing; } catch (NullPointerException e) { System.out.println("unboxing null -> NullPointerException"); }
        List<Integer> ids = new ArrayList<>(List.of(10, 20, 30));
        ids.remove(Integer.valueOf(10));                     // remove by value
        ids.remove(0);                                       // remove by index (overload trap)
        System.out.println("after remove(Integer 10) and remove(index 0): " + ids);
    }
}
