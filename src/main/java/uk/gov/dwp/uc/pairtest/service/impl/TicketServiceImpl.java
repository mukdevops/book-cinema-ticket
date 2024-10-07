package uk.gov.dwp.uc.pairtest.service.impl;

import lombok.extern.slf4j.Slf4j;
import uk.gov.dwp.uc.pairtest.service.TicketService;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.constants.TicketConstants;
import uk.gov.dwp.uc.pairtest.model.TicketTypeEnum;
import uk.gov.dwp.uc.pairtest.util.TicketCalculatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;

import java.util.Map;

@Slf4j
@Service
public class TicketServiceImpl implements TicketService {

  private final TicketPaymentService paymentService;

  private final SeatReservationService reservationService;

  private final Map<TicketTypeEnum, Integer> ticketPrices;

  private final Long maxTicketLimit;

  @Autowired
  public TicketServiceImpl(
      TicketPaymentService paymentService,
      SeatReservationService reservationService,
      Map<TicketTypeEnum, Integer> ticketPrices,
      Long maxTicketLimit) {
    this.paymentService = paymentService;
    this.reservationService = reservationService;
    this.ticketPrices = ticketPrices;
    this.maxTicketLimit = maxTicketLimit;
  }

  @Override
  public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

    validatePurchaseRequest(ticketTypeRequests);

    int totalAmount =
            TicketCalculatorUtil.calculateTotalAmount(ticketPrices, ticketTypeRequests);

    int totalSeats = TicketCalculatorUtil.calculateTotalSeats(ticketTypeRequests);

    makePayment(accountId, totalAmount);
    log.info(" Total Amount to pay :: {}",totalAmount);

    reserveSeats(accountId, totalSeats);
    log.info(" Total Seats Reserved :: {}",totalSeats);

  }


  // Method to validate the ticket purchase request
  private void validatePurchaseRequest(TicketTypeRequest... ticketTypeRequests) {
    int totalTickets = 0;
    int adultTickets = 0;

    for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
      int quantity = ticketTypeRequest.getNoOfTickets();
       TicketTypeEnum ticketTypeEnum = ticketTypeRequest.getType();

      // Ensure ticket type exists in configuration
      if (!ticketPrices.containsKey(ticketTypeEnum)) {
        throw new InvalidPurchaseException(TicketConstants.ERROR_INVALID_TICKET_TYPE + ticketTypeEnum);
      }

      // Count total tickets and adult tickets
      totalTickets += quantity;
      if (TicketTypeEnum.ADULT.equals(ticketTypeEnum)) {
        adultTickets += quantity;
      }
    }

    // Validation rules
    if (totalTickets > maxTicketLimit) { // Use maxTickets loaded from properties
      throw new InvalidPurchaseException(
          String.format(TicketConstants.ERROR_TOO_MANY_TICKETS, maxTicketLimit));
    }

    if (adultTickets == 0) {
      throw new InvalidPurchaseException(TicketConstants.ERROR_NO_ADULT_TICKET);
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
