package com.cinema.ticket.config;

import com.cinema.ticket.constants.TicketConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;

import java.util.Map;

@Configuration
public class TicketConfig {

    @Value("${ticket.price.infant}")
    private int infantPrice;

    @Value("${ticket.price.child}")
    private int childPrice;

    @Value("${ticket.price.adult}")
    private int adultPrice;

    @Value("${ticket-booking.max-tickets}")
    private Long maxTickets;

    @Bean
    public Long maxTicketLimit(){
        return maxTickets;
    }

    @Bean
    public Map<String, Integer> ticketPrices() {
        return Map.of(
                TicketConstants.INFANT, infantPrice,
                TicketConstants.CHILD, childPrice,
                TicketConstants.ADULT, adultPrice
        );
    }

    @Bean
    public TicketPaymentService  ticketPaymentService(){
        return new TicketPaymentServiceImpl();
    }

    @Bean
    public SeatReservationService seatReservationService(){
         return new SeatReservationServiceImpl();
    }

}
