package uk.gov.dwp.uc.pairtest.model;

import lombok.*;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketPurchaseRequest {

    private Long accountId;

    private List<TicketTypeRequest> ticketTypeRequests;

}

