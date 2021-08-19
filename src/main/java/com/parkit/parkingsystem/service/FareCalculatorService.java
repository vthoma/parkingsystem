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
        double originalPrice = 0;

        if (duration <= 1800000) {
            ticket.setPrice(0);
        } else {
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR: {
                    originalPrice = ((duration / 3600000) * Fare.CAR_RATE_PER_HOUR);
                    ticket.setPrice(originalPrice - (calculateDiscount(ticket, originalPrice, 0.05)));
                    break;
                }
                case BIKE: {
                    originalPrice = ((duration / 3600000) * Fare.BIKE_RATE_PER_HOUR);
                    ticket.setPrice(originalPrice - (calculateDiscount(ticket, originalPrice, 0.05)));
                    break;
                }
                default:
                    throw new IllegalArgumentException("Unknown Parking Type");
            }
        }
    }
    public double calculateDiscount(Ticket ticket, double originalPrice, double promotion) {
        TicketDAO ticketDao = new TicketDAO();
        String regNumber = ticket.getVehicleRegNumber();
        List<Ticket> allTicket = ticketDao.getAllTicketByRegNumber(regNumber);
        if (allTicket.size() >= 2)
            return originalPrice * promotion;
        else
            return  0;
    }
}