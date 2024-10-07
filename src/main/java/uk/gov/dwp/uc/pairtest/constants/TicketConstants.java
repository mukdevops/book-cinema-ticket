package uk.gov.dwp.uc.pairtest.constants;

public class TicketConstants {

    // Error Messages
    public static final String ERROR_INVALID_TICKET_TYPE = "Invalid ticket type: ";
    public static final String ERROR_TOO_MANY_TICKETS = "Cannot purchase more than %d tickets at a time.";
    public static final String ERROR_NO_ADULT_TICKET = "At least one adult ticket must be purchased.";
    public static final String ACCOUNT_ID_NOT_SUPPLIED = "Account Id not provided";

    // Success Messages
    public static final String SUCCESS_TICKET_BOOKED = "Success: Ticket Booked";

    private TicketConstants() {
        // Private constructor to prevent instantiation
    }
}

