package uk.gov.dwp.uc.pairtest.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.gov.dwp.uc.pairtest.model.TicketTypeEnum;

/**
 * Immutable Object
 */

@AllArgsConstructor
@Getter
public class TicketTypeRequest {

    private int noOfTickets;

    private TicketTypeEnum type;

}
