package uk.gov.dwp.uc.pairtest.util;

import uk.gov.dwp.uc.pairtest.constants.TicketConstants;
import uk.gov.dwp.uc.pairtest.model.TicketPurchaseRequest;

import java.util.Arrays;
import java.util.List;

public class TicketTestUtil {

    public static TicketPurchaseRequest getTicketPurchaseRequest(int adultTicketQty, int childTicketQty, int infantTicketQty) {
        TicketTypeRequest adultTicketTypeRequest = TicketTypeRequest.builder()
                .ticketType(TicketConstants.ADULT)
                .quantity(adultTicketQty)
                .build();

        TicketTypeRequest childTicketTypeRequest = TicketTypeRequest.builder()
                .ticketType(TicketConstants.CHILD)
                .quantity(childTicketQty)
                .build();

        TicketTypeRequest infantTicketTypeRequest = TicketTypeRequest.builder()
                .ticketType(TicketConstants.INFANT)
                .quantity(infantTicketQty)
                .build();

        List<TicketTypeRequest> ticketTypeRequestList = Arrays.asList(adultTicketTypeRequest, childTicketTypeRequest, infantTicketTypeRequest);
        TicketPurchaseRequest ticketPurchaseRequest = TicketPurchaseRequest.builder()
                .accountId(345L)
                .ticketTypeRequests(ticketTypeRequestList)
                .build();
        return ticketPurchaseRequest;
    }
}
