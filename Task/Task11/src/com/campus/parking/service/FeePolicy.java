package com.campus.parking.service;

import com.campus.parking.model.ParkingTicket;
import com.campus.parking.model.PassType;
import com.campus.parking.model.UserType;

import java.time.Duration;
import java.time.LocalDateTime;

/** The single place where "who pays what" rules live. */
public class FeePolicy {

    /** Students and staff with a valid monthly pass park free; visitors always pay. */
    public boolean isWaived(UserType user, PassType pass) {
        return pass == PassType.MONTHLY && (user == UserType.STUDENT || user == UserType.STAFF);
    }

    public double parkingFee(ParkingTicket ticket, LocalDateTime exit) {
        long minutes = Duration.between(ticket.getEntryTime(), exit).toMinutes();
        if (minutes < 0) throw new IllegalArgumentException("exit time is before entry time");
        if (isWaived(ticket.getUserType(), ticket.getPassType())) return 0.0;
        return ticket.getVehicle().calculateFee((int) minutes);
    }
}
