package com.cinema.ticket.controller;

import com.cinema.ticket.model.TicketPurchaseRequest;
import com.cinema.ticket.service.TicketBookingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@AllArgsConstructor
public class TicketBookingController {

    private final TicketBookingService bookingService;

    @PostMapping("/book")
    public ResponseEntity<String> bookTickets(@RequestBody TicketPurchaseRequest request) {
        try {
            String bookingStatus = bookingService.purchaseTickets(request);
            if (bookingStatus == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            return ResponseEntity.ok(bookingStatus);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}

