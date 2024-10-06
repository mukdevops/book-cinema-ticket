package uk.gov.dwp.uc.pairtest.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TicketTypeRequest {

    private String ticketType;

    private int quantity;

}

