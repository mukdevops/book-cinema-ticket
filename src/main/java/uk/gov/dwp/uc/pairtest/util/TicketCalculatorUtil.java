package uk.gov.dwp.uc.pairtest.util;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.model.TicketTypeEnum;
import java.util.Map;

public class TicketCalculatorUtil {

    private TicketCalculatorUtil() {
    }

    // Method to calculate total amount
    public static int calculateTotalAmount(Map<TicketTypeEnum, Integer> ticketPrices,TicketTypeRequest... ticketTypeRequests) {
        int totalAmount = 0;

        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            int quantity = ticketTypeRequest.getNoOfTickets();
            // Add to total amount (Infants have £0 cost)
            totalAmount += ticketPrices.get(ticketTypeRequest.getType()) * quantity;
        }

        return totalAmount;
    }

    // Method to calculate total seats
    public static int calculateTotalSeats(TicketTypeRequest... ticketTypeRequests) {
        int totalSeats = 0;

        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            // Only count seats for non-infant tickets
            if (!TicketTypeEnum.INFANT.equals(ticketTypeRequest.getType())) {
                totalSeats += ticketTypeRequest.getNoOfTickets();
            }
        }

        return totalSeats;
    }
}

