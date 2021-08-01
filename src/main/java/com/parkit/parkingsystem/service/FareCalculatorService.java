package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.dao.TicketDAO;

import java.util.List;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        long inHour = ticket.getInTime().getTime();
        long outHour = ticket.getOutTime().getTime();

        //TODO: Some tests are failing here. Need to check if this logic is correct
        double duration = outHour - inHour;
        double promotion = 0;
        TicketDAO ticketDao = new TicketDAO();
        String regNumber = ticket.getVehicleRegNumber();
        List<Ticket> allTicket = ticketDao.getAllTicketByRegNumber(regNumber);
        System.out.println("Print: " + allTicket.size() + " :Print");

        if (allTicket.size() >= 2)
            promotion = 0.5;

        if (duration <= 1800000) {
            ticket.setPrice(0);
        } else {
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    ticket.setPrice((duration / 3600000) * Fare.CAR_RATE_PER_HOUR / promotion);
                    break;
                }
                case BIKE: {
                    ticket.setPrice((duration / 3600000) * Fare.BIKE_RATE_PER_HOUR / promotion);
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown Parking Type");
            }
        }

    }
}