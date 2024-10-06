package uk.gov.dwp.uc.pairtest.service;

import uk.gov.dwp.uc.pairtest.constants.TicketConstants;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.model.TicketPurchaseRequest;
import uk.gov.dwp.uc.pairtest.model.TicketTypeRequest;
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
import static uk.gov.dwp.uc.pairtest.util.TicketTestUtil.getTicketPurchaseRequest;

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

        TicketPurchaseRequest ticketPurchaseRequest = getTicketPurchaseRequest(2,2,1);
        ticketPurchaseRequest.setAccountId(123L);

        assertDoesNotThrow(() -> ticketBookingService.purchaseTickets(ticketPurchaseRequest));
        verify(paymentServiceMock, times(1)).makePayment(anyLong(), anyInt());
        verify(reservationServiceMock, times(1)).reserveSeat(anyLong(), anyInt());

    }

    @Test
    void testValidatePurchaseRequestFailsWhenNoAdultTicket() {
        TicketPurchaseRequest ticketPurchaseRequest = getTicketPurchaseRequest(0,2,1);

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, () -> {
            ticketBookingService.purchaseTickets(ticketPurchaseRequest);
        });
        assertEquals(TicketConstants.ERROR_NO_ADULT_TICKET, exception.getMessage());
        verify(paymentServiceMock, times(0)).makePayment(anyLong(), anyInt());
        verify(reservationServiceMock, times(0)).reserveSeat(anyLong(), anyInt());

    }

    @Test
    void testValidatePurchaseRequestFailsWhenMoreThanMaxTickets() {
        TicketPurchaseRequest ticketPurchaseRequest = getTicketPurchaseRequest(26,0,0);


        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, () -> {
            ticketBookingService.purchaseTickets(ticketPurchaseRequest);
        });
        assertEquals(String.format(TicketConstants.ERROR_TOO_MANY_TICKETS, 25), exception.getMessage());
        verify(paymentServiceMock, times(0)).makePayment(anyLong(), anyInt());
        verify(reservationServiceMock, times(0)).reserveSeat(anyLong(), anyInt());

    }
}
