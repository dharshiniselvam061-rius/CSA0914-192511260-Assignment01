package com.campus.parking.app;

import com.campus.parking.model.ParkingTicket;
import com.campus.parking.model.PassType;
import com.campus.parking.model.UserType;
import com.campus.parking.model.Vehicle;
import com.campus.parking.service.ParkingException;
import com.campus.parking.service.ParkingLot;
import com.campus.parking.service.VehicleFactory;

import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Reads commands from standard input (Scanner), one per line:
 *   FLOORS 2 1 3                                    (first line: slots on each floor)
 *   ENTER  <type> <regNo> <STUDENT|STAFF|VISITOR> <NONE|MONTHLY|DAY> <yyyy-MM-ddTHH:mm>
 *   EXIT   <regNo> <yyyy-MM-ddTHH:mm> [kWh]
 *   STATUS
 * Lines starting with # are comments.
 */
public class ParkingApp {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        ParkingLot lot = null;
        while (in.hasNextLine()) {
            String line = in.nextLine().trim();
            if (line.isEmpty() || line.startsWith("#")) continue;
            String[] p = line.split("\\s+");
            try {
                switch (p[0].toUpperCase()) {
                    case "FLOORS": {
                        int[] sizes = new int[p.length - 1];
                        for (int i = 1; i < p.length; i++) sizes[i - 1] = Integer.parseInt(p[i]);
                        lot = new ParkingLot(sizes);
                        System.out.println("Facility created: " + sizes.length + " floors, capacity " + lot.capacity());
                        break;
                    }
                    case "ENTER": {
                        Vehicle v = VehicleFactory.create(p[1], p[2]);
                        ParkingTicket t = lot.enter(v, UserType.valueOf(p[3].toUpperCase()),
                                PassType.valueOf(p[4].toUpperCase()), LocalDateTime.parse(p[5]));
                        System.out.println("ENTER ok  -> " + t);
                        break;
                    }
                    case "EXIT": {
                        int kWh = p.length > 3 ? Integer.parseInt(p[3]) : 0;
                        double fee = lot.exit(p[1], LocalDateTime.parse(p[2]), kWh);
                        System.out.println("EXIT ok   -> " + p[1].toUpperCase() + " pays Rs " + fee);
                        break;
                    }
                    case "STATUS":
                        System.out.println("STATUS    -> " + lot.activeCount() + "/" + lot.capacity() + " slots in use");
                        break;
                    default:
                        System.out.println("ERROR     -> unknown command " + p[0]);
                }
            } catch (ParkingException | IllegalArgumentException e) {
                System.out.println("REJECTED  -> " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("BAD INPUT -> " + line + " (" + e.getClass().getSimpleName() + ")");
            }
        }
    }
}
