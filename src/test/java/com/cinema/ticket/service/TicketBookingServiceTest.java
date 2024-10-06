package com.cinema.ticket.service;

import com.cinema.ticket.constants.TicketConstants;
import com.cinema.ticket.model.TicketPurchaseRequest;
import com.cinema.ticket.model.TicketTypeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class TicketBookingServiceTest {

    @Mock
    private TicketPaymentService paymentServiceMock;

    @Mock
    private SeatReservationService reservationServiceMock;

    @InjectMocks
    private TicketBookingService ticketBookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ticketBookingService = new TicketBookingService(paymentServiceMock, reservationServiceMock, Map.of(
                TicketConstants.INFANT, 0,
                TicketConstants.CHILD, 5,
                TicketConstants.ADULT, 15
        ), 25L);
        // Set max tickets to 25 for testing
        ReflectionTestUtils.setField(ticketBookingService, "maxTicketLimit", 25L);
    }

    @Test
    void testValidatePurchaseRequestSuccess() {
        TicketPurchaseRequest request = new TicketPurchaseRequest();
        request.setAccountId(123L);
        request.setTicketTypeRequests(Arrays.asList(
                new TicketTypeRequest(TicketConstants.ADULT, 2),
                new TicketTypeRequest(TicketConstants.CHILD, 2),
                new TicketTypeRequest(TicketConstants.INFANT, 1)
        ));

        assertDoesNotThrow(() -> ticketBookingService.purchaseTickets(request));
        verify(paymentServiceMock, times(1)).makePayment(anyLong(), anyInt());
        verify(reservationServiceMock, times(1)).reserveSeat(anyLong(), anyInt());

    }

    @Test
    void testValidatePurchaseRequestFailsWhenNoAdultTicket() {
        TicketPurchaseRequest request = new TicketPurchaseRequest();
        request.setTicketTypeRequests(Collections.singletonList(
                new TicketTypeRequest(TicketConstants.CHILD, 2)
        ));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ticketBookingService.purchaseTickets(request);
        });
        assertEquals(TicketConstants.ERROR_NO_ADULT_TICKET, exception.getMessage());
        verify(paymentServiceMock, times(0)).makePayment(anyLong(), anyInt());
        verify(reservationServiceMock, times(0)).reserveSeat(anyLong(), anyInt());

    }

    @Test
    void testValidatePurchaseRequestFailsWhenMoreThanMaxTickets() {
        TicketPurchaseRequest request = new TicketPurchaseRequest();
        request.setTicketTypeRequests(Collections.singletonList(
                new TicketTypeRequest(TicketConstants.ADULT, 26)
        ));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            ticketBookingService.purchaseTickets(request);
        });
        assertEquals(String.format(TicketConstants.ERROR_TOO_MANY_TICKETS, 25), exception.getMessage());
        verify(paymentServiceMock, times(0)).makePayment(anyLong(), anyInt());
        verify(reservationServiceMock, times(0)).reserveSeat(anyLong(), anyInt());

    }
}
