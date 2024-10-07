package uk.gov.dwp.uc.pairtest.service;

import org.junit.jupiter.api.function.Executable;
import uk.gov.dwp.uc.pairtest.constants.TicketConstants;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.model.TicketPurchaseRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.model.TicketTypeEnum;
import uk.gov.dwp.uc.pairtest.service.impl.TicketServiceImpl;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.dwp.uc.pairtest.util.TicketTestUtil.getTicketPurchaseRequest;

@SpringBootTest
class TicketServiceImplTest {

    @Mock
    private TicketPaymentService paymentServiceMock;

    @Mock
    private SeatReservationService reservationServiceMock;

    @InjectMocks
    private TicketServiceImpl ticketServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ticketServiceImpl = new TicketServiceImpl(paymentServiceMock, reservationServiceMock, Map.of(
                TicketTypeEnum.INFANT, 0,
                TicketTypeEnum.CHILD, 5,
                TicketTypeEnum.ADULT, 15
        ), 25L);
        // Set max tickets to 25 for testing
        ReflectionTestUtils.setField(ticketServiceImpl, "maxTicketLimit", 25L);
    }

    @Test
    void testValidatePurchaseRequestSuccess() {

        TicketPurchaseRequest ticketPurchaseRequest = getTicketPurchaseRequest(2,2,1);
        ticketPurchaseRequest.setAccountId(123L);

        assertDoesNotThrow(() -> ticketServiceImpl.purchaseTickets(123L,ticketPurchaseRequest.getTicketTypeRequests().toArray(new TicketTypeRequest[]{})));
        verify(paymentServiceMock, times(1)).makePayment(anyLong(), anyInt());
        verify(reservationServiceMock, times(1)).reserveSeat(anyLong(), anyInt());

    }

    @Test
    void testValidatePurchaseRequestFailsWhenNoAdultTicket() {
        TicketPurchaseRequest ticketPurchaseRequest = getTicketPurchaseRequest(0,2,1);

        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, getExecutable(ticketPurchaseRequest));
        assertEquals(TicketConstants.ERROR_NO_ADULT_TICKET, exception.getMessage());
        verify(paymentServiceMock, times(0)).makePayment(anyLong(), anyInt());
        verify(reservationServiceMock, times(0)).reserveSeat(anyLong(), anyInt());

    }

    @Test
    void testValidatePurchaseRequestFailsWhenMoreThanMaxTickets() {
        TicketPurchaseRequest ticketPurchaseRequest = getTicketPurchaseRequest(26,0,0);


        InvalidPurchaseException exception = assertThrows(InvalidPurchaseException.class, getExecutable(ticketPurchaseRequest));
        assertEquals(String.format(TicketConstants.ERROR_TOO_MANY_TICKETS, 25), exception.getMessage());
        verify(paymentServiceMock, times(0)).makePayment(anyLong(), anyInt());
        verify(reservationServiceMock, times(0)).reserveSeat(anyLong(), anyInt());

    }

    private Executable getExecutable(TicketPurchaseRequest ticketPurchaseRequest) {
        return () -> {
            ticketServiceImpl.purchaseTickets(123L, ticketPurchaseRequest.getTicketTypeRequests().toArray(new TicketTypeRequest[]{}));
        };
    }
}
