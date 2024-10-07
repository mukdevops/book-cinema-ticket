package uk.gov.dwp.uc.pairtest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.dwp.uc.pairtest.constants.TicketConstants;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;
import uk.gov.dwp.uc.pairtest.model.TicketPurchaseRequest;
import uk.gov.dwp.uc.pairtest.model.TicketTypeEnum;
import uk.gov.dwp.uc.pairtest.service.impl.TicketServiceImpl;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ExtendWith(MockitoExtension.class)
class TicketBookingControllerTest {

    @InjectMocks
    private TicketBookingController ticketController;

    @Mock
    private TicketServiceImpl ticketBookingService;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();
        this.objectMapper = new ObjectMapper(); // Initialize ObjectMapper
    }

  @Test
  void testBookTickets_Success() throws Exception {
        // Given
        TicketTypeRequest[] ticketTypeRequests = {
                new TicketTypeRequest(1 , TicketTypeEnum.ADULT),
                new TicketTypeRequest(1 , TicketTypeEnum.CHILD)
        };

        // Mock service call
        lenient().doNothing().when(ticketBookingService).purchaseTickets(anyLong(), any(TicketTypeRequest[].class));

        // Convert ticketTypeRequests to JSON using ObjectMapper
        String ticketRequestJson =  objectMapper.writeValueAsString(TicketPurchaseRequest.builder()
                        .accountId(345L).ticketTypeRequests(Arrays.asList(ticketTypeRequests)).build());

        mockMvc.perform(post("/api/tickets/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ticketRequestJson))
                .andExpect(status().isOk())
                .andExpect(content().string(TicketConstants.SUCCESS_TICKET_BOOKED));

        verify(ticketBookingService, times(1)).purchaseTickets(anyLong(), any(TicketTypeRequest[].class));
    }

  @Test
  void testBookTickets_ShouldThrowException_WhenNoAdultTicket() throws Exception {
        // Given

        TicketTypeRequest[] ticketTypeRequests = {
                new TicketTypeRequest(1 , TicketTypeEnum.CHILD)
        };

        // Convert ticketTypeRequests to JSON using ObjectMapper
        String ticketRequestJson =  objectMapper.writeValueAsString(TicketPurchaseRequest.builder()
                .accountId(345L).ticketTypeRequests(Arrays.asList(ticketTypeRequests)).build());

    // Simulate the service throwing an exception
      doThrow(new InvalidPurchaseException(TicketConstants.ERROR_NO_ADULT_TICKET))
        .when(ticketBookingService)
        .purchaseTickets(anyLong(), any(TicketTypeRequest[].class));

        // When & Then
        mockMvc.perform(post("/api/tickets/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ticketRequestJson)) // Use JSON from ObjectMapper
                .andExpect(status().isBadRequest());

        // Verify that the service call was made and handled the exception
        verify(ticketBookingService).purchaseTickets(anyLong(), any(TicketTypeRequest[].class));
    }

  @Test
  void testBookTickets_ShouldThrowException_WhenTooManyTickets() throws Exception {

        // Create a ticket request with more than the maximum allowed tickets
        TicketTypeRequest[] ticketTypeRequests = new TicketTypeRequest[26];
        for (int i = 0; i < 26; i++) {
            ticketTypeRequests[i] = new TicketTypeRequest(1 , TicketTypeEnum.ADULT);
        }

        // Convert ticketTypeRequests to JSON using ObjectMapper
        String ticketRequestJson =  objectMapper.writeValueAsString(TicketPurchaseRequest.builder()
                .accountId(345L).ticketTypeRequests(Arrays.asList(ticketTypeRequests)).build());

        // Simulate service throwing an InvalidPurchaseException
        doThrow(new InvalidPurchaseException(TicketConstants.ERROR_TOO_MANY_TICKETS)).when(ticketBookingService)
                .purchaseTickets(anyLong(), any(TicketTypeRequest[].class));

        // When & Then
        mockMvc.perform(post("/api/tickets/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ticketRequestJson)) // Use JSON from ObjectMapper
                        .andExpect(status().isBadRequest());

        // Verify that the service was called
        verify(ticketBookingService).purchaseTickets(anyLong(), any(TicketTypeRequest[].class));
    }


    @Test
     void testBookTickets_MissingAccountId_ShouldReturnBadRequest() throws Exception {
        // Given ticket requests but missing `accountId` parameter
        TicketTypeRequest[] ticketTypeRequests = {
                new TicketTypeRequest(1 , TicketTypeEnum.ADULT),
                new TicketTypeRequest(1 , TicketTypeEnum.CHILD)
        };

        // Convert ticketTypeRequests to JSON using ObjectMapper
        String ticketRequestJson =  objectMapper.writeValueAsString(TicketPurchaseRequest.builder()
                .accountId(null).ticketTypeRequests(Arrays.asList(ticketTypeRequests)).build());
        // When & Then
        mockMvc.perform(post("/api/tickets/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ticketRequestJson)) // Use JSON from ObjectMapper
                        .andExpect(status().isBadRequest());

        // Ensure that the service is never called because the accountId is missing
        verify(ticketBookingService, times(0)).purchaseTickets(anyLong(), any(TicketTypeRequest[].class));
    }

    @Test
    void testBookTickets_EmptyTicketRequests_ShouldReturnBadRequest() throws Exception {
        // Given an empty array of ticket requests
        TicketTypeRequest[] ticketTypeRequests = {};

        // Convert ticketTypeRequests to JSON using ObjectMapper
        String ticketRequestJson = objectMapper.writeValueAsString(ticketTypeRequests);

        // When & Then
        mockMvc.perform(post("/api/tickets/book")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ticketRequestJson)) // Use JSON from ObjectMapper
                .andExpect(status().isBadRequest());

        // Ensure that the service is never called due to empty request
        verify(ticketBookingService, times(0)).purchaseTickets(anyLong(), any(TicketTypeRequest[].class));
    }
}

