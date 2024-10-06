package com.cinema.ticket.controller;

import com.cinema.ticket.constants.TicketConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import com.cinema.ticket.model.TicketPurchaseRequest;
import com.cinema.ticket.model.TicketTypeRequest;
import com.cinema.ticket.service.TicketBookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cinema.ticket.util.TicketTestUtil.getTicketPurchaseRequest;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketBookingControllerTest {

    @Mock
    private TicketBookingService bookingService;

    @InjectMocks
    private TicketBookingController bookingController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testSuccessfulBooking() throws Exception {

        TicketPurchaseRequest ticketPurchaseRequest = getTicketPurchaseRequest(1,2,0);

        when(bookingService.purchaseTickets(any(TicketPurchaseRequest.class))).thenReturn(TicketConstants.SUCCESS_TICKET_BOOKED);

        mockMvc.perform(post("/api/tickets/book")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(ticketPurchaseRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(TicketConstants.SUCCESS_TICKET_BOOKED));

        verify(bookingService, times(1)).purchaseTickets(any(TicketPurchaseRequest.class));
    }



    @Test
    void testBookingFailsWithoutAdultTicket() throws Exception {

        TicketPurchaseRequest ticketPurchaseRequest = getTicketPurchaseRequest(0,2,0);


        doThrow(new IllegalArgumentException("At least one adult ticket must be purchased."))
                .when(bookingService).purchaseTickets(any(TicketPurchaseRequest.class));

        mockMvc.perform(post("/api/tickets/book")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(ticketPurchaseRequest)))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("At least one adult ticket must be purchased."));

        verify(bookingService, times(1)).purchaseTickets(any(TicketPurchaseRequest.class));
    }
}

