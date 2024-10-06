package com.cinema.ticket.service;

import com.cinema.ticket.model.TicketPurchaseRequest;
import com.cinema.ticket.model.TicketTypeRequest;
import com.cinema.ticket.constants.TicketConstants;
import com.cinema.ticket.util.TicketCalculatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;

import java.util.List;
import java.util.Map;

@Service
public class TicketBookingService {

  private final TicketPaymentService paymentService;

  private final SeatReservationService reservationService;

  private final Map<String, Integer> ticketPrices;

  private final Long maxTicketLimit;

  @Autowired
  public TicketBookingService(
      TicketPaymentService paymentService,
      SeatReservationService reservationService,
      Map<String, Integer> ticketPrices,
      Long maxTicketLimit) {
    this.paymentService = paymentService;
    this.reservationService = reservationService;
    this.ticketPrices = ticketPrices;
    this.maxTicketLimit = maxTicketLimit;
  }

  public String purchaseTickets(TicketPurchaseRequest request) {

    validatePurchaseRequest(request.getTicketTypeRequests());

    int totalAmount =
        TicketCalculatorUtil.calculateTotalAmount(request.getTicketTypeRequests(), ticketPrices);

    int totalSeats = TicketCalculatorUtil.calculateTotalSeats(request.getTicketTypeRequests());

    makePayment(request.getAccountId(), totalAmount);

    reserveSeats(request.getAccountId(), totalSeats);

    return TicketConstants.SUCCESS_TICKET_BOOKED;
  }

  // Method to validate the ticket purchase request
  private void validatePurchaseRequest(List<TicketTypeRequest> ticketTypeRequests) {
    int totalTickets = 0;
    int adultTickets = 0;

    for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
      int quantity = ticketTypeRequest.getQuantity();
      String ticketType = ticketTypeRequest.getTicketType().toUpperCase();

      // Ensure ticket type exists in configuration
      if (!ticketPrices.containsKey(ticketType)) {
        throw new IllegalArgumentException(TicketConstants.ERROR_INVALID_TICKET_TYPE + ticketType);
      }

      // Count total tickets and adult tickets
      totalTickets = totalTickets + quantity;
      if (TicketConstants.ADULT.equals(ticketType)) {
        adultTickets = adultTickets + quantity;
      }
    }

    // Validation rules
    if (totalTickets > maxTicketLimit) { // Use maxTickets loaded from properties
      throw new IllegalArgumentException(
          String.format(TicketConstants.ERROR_TOO_MANY_TICKETS, maxTicketLimit));
    }

    if (adultTickets == 0) {
      throw new IllegalArgumentException(TicketConstants.ERROR_NO_ADULT_TICKET);
    }
  }

  // Method to make payment
  private void makePayment(Long accountId, int totalAmount) {
    paymentService.makePayment(accountId, totalAmount);
  }

  // Method to reserve seats
  private void reserveSeats(Long accountId, int totalSeats) {
    reservationService.reserveSeat(accountId, totalSeats);
  }
}
