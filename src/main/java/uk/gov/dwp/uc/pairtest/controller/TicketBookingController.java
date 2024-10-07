package uk.gov.dwp.uc.pairtest.controller;

import lombok.extern.slf4j.Slf4j;
import uk.gov.dwp.uc.pairtest.constants.TicketConstants;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.model.TicketPurchaseRequest;
import uk.gov.dwp.uc.pairtest.service.impl.TicketServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/tickets")
@AllArgsConstructor
public class TicketBookingController {

    private final TicketServiceImpl bookingService;

    @Operation(summary = "Book cinema tickets", description = "Book cinema tickets with a combination of adult, child, and infant tickets.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Booking successful"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @Content)
    })
    @PostMapping("/book")
    public ResponseEntity<String> bookTickets(@RequestBody TicketPurchaseRequest request) {
        try {

            List<TicketTypeRequest> ticketTypeRequests = request.getTicketTypeRequests();

            if(request.getAccountId() == null){
                throw new InvalidPurchaseException(TicketConstants.ACCOUNT_ID_NOT_SUPPLIED);
            }

            bookingService.purchaseTickets(request.getAccountId(),
                    ticketTypeRequests.toArray(new TicketTypeRequest[ticketTypeRequests.size()]));
            return ResponseEntity.ok(TicketConstants.SUCCESS_TICKET_BOOKED);
        } catch (InvalidPurchaseException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

