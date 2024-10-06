package com.cinema.ticket.constants;

public class TicketConstants {

    // Ticket Types
    public static final String INFANT = "INFANT";
    public static final String CHILD = "CHILD";
    public static final String ADULT = "ADULT";

    // Error Messages
    public static final String ERROR_INVALID_TICKET_TYPE = "Invalid ticket type: ";
    public static final String ERROR_TOO_MANY_TICKETS = "Cannot purchase more than %d tickets at a time.";
    public static final String ERROR_NO_ADULT_TICKET = "At least one adult ticket must be purchased.";

    // Success Messages
    public static final String SUCCESS_TICKET_BOOKED = "Success: Ticket Booked";

    private TicketConstants() {
        // Private constructor to prevent instantiation
    }
}

