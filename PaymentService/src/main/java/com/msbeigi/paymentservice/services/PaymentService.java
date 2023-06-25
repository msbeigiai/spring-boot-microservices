package com.msbeigi.paymentservice.services;

import com.msbeigi.paymentservice.model.PaymentRequest;
import com.msbeigi.paymentservice.model.PaymentResponse;
import org.springframework.stereotype.Service;

public interface PaymentService {
    long doPayment(PaymentRequest paymentRequest);

    PaymentResponse getPaymentDetailsByOrderId(String orderId);
}
