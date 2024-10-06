package uk.gov.dwp.uc.pairtest.util;



import uk.gov.dwp.uc.pairtest.constants.TicketConstants;
import uk.gov.dwp.uc.pairtest.model.TicketTypeRequest;

import java.util.List;
import java.util.Map;

public class TicketCalculatorUtil {

    // Method to calculate total amount
    public static int calculateTotalAmount(List<TicketTypeRequest> ticketTypeRequests, Map<String, Integer> ticketPrices) {
        int totalAmount = 0;

        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            int quantity = ticketTypeRequest.getQuantity();
            String ticketType = ticketTypeRequest.getTicketType().toUpperCase();

            // Add to total amount (Infants have £0 cost)
            totalAmount += ticketPrices.get(ticketType) * quantity;
        }

        return totalAmount;
    }

    // Method to calculate total seats
    public static int calculateTotalSeats(List<TicketTypeRequest> ticketTypeRequests) {
        int totalSeats = 0;

        for (TicketTypeRequest ticketTypeRequest : ticketTypeRequests) {
            String ticketType = ticketTypeRequest.getTicketType().toUpperCase();

            // Only count seats for non-infant tickets
            if (!TicketConstants.INFANT.equals(ticketType)) {
                totalSeats += ticketTypeRequest.getQuantity();
            }
        }

        return totalSeats;
    }
}

