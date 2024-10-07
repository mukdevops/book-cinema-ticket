package uk.gov.dwp.uc.pairtest.util;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.model.TicketPurchaseRequest;
import uk.gov.dwp.uc.pairtest.model.TicketTypeEnum;

import java.util.Arrays;
import java.util.List;

public class TicketTestUtil {

    public static TicketPurchaseRequest getTicketPurchaseRequest(int adultTicketQty, int childTicketQty, int infantTicketQty) {
        TicketTypeRequest adultTicketTypeRequest = new TicketTypeRequest(adultTicketQty ,TicketTypeEnum.ADULT);

        TicketTypeRequest childTicketTypeRequest = new TicketTypeRequest(childTicketQty ,TicketTypeEnum.CHILD);

        TicketTypeRequest infantTicketTypeRequest = new TicketTypeRequest(infantTicketQty ,TicketTypeEnum.INFANT);

        List<TicketTypeRequest> ticketTypeRequestList = Arrays.asList(adultTicketTypeRequest, childTicketTypeRequest, infantTicketTypeRequest);
        return TicketPurchaseRequest.builder()
                .accountId(345L)
                .ticketTypeRequests(ticketTypeRequestList)
                .build();
    }
}
