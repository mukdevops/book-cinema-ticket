package uk.gov.dwp.uc.pairtest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.paymentgateway.TicketPaymentServiceImpl;
import thirdparty.seatbooking.SeatReservationService;
import thirdparty.seatbooking.SeatReservationServiceImpl;
import uk.gov.dwp.uc.pairtest.model.TicketTypeEnum;

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
    public Map<TicketTypeEnum, Integer> ticketPrices() {
        return Map.of(
                TicketTypeEnum.INFANT, infantPrice,
                TicketTypeEnum.CHILD, childPrice,
                TicketTypeEnum.ADULT, adultPrice
        );
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cinema Ticket Booking API")
                        .version("1.0")
                        .description("API for booking cinema tickets, including rules for infants, children, and adults"));
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
