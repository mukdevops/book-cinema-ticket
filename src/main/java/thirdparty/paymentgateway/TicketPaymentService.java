package thirdparty.paymentgateway;

import org.springframework.stereotype.Service;

@Service
public interface TicketPaymentService {

    void makePayment(long accountId, int totalAmountToPay);

}
