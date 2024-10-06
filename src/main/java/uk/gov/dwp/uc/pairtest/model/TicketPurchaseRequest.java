package uk.gov.dwp.uc.pairtest.model;

import lombok.*;

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

