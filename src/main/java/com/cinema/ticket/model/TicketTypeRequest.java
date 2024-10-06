package com.cinema.ticket.model;

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

