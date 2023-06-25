package com.msbeigi.orderservice.services;

import com.msbeigi.orderservice.entity.Order;
import com.msbeigi.orderservice.exception.CustomException;
import com.msbeigi.orderservice.external.client.PaymentService;
import com.msbeigi.orderservice.external.client.ProductService;
import com.msbeigi.orderservice.external.request.PaymentRequest;
import com.msbeigi.orderservice.external.response.PaymentResponse;
import com.msbeigi.orderservice.model.OrderRequest;
import com.msbeigi.orderservice.model.OrderResponse;
import com.msbeigi.orderservice.external.response.ProductResponse;
import com.msbeigi.orderservice.repository.OrderRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public long placeOrder(OrderRequest orderRequest) {

        log.info("Placing order request: {}", orderRequest);

        productService.reduceQuantity(orderRequest.getProductId(), orderRequest.getQuantity());

        log.info("Creating order with status CREATED!");
        Order order = Order.builder()
                .amount(orderRequest.getTotalAmount())
                .orderStatus("CREATED")
                .productId(orderRequest.getProductId())
                .orderDate(Instant.now())
                .quantity(orderRequest.getQuantity())
                .build();
        order = orderRepository.save(order);

        log.info("Calling payment service to complete the payment!");

        PaymentRequest paymentRequest =
                PaymentRequest.builder()
                        .orderId(order.getId())
                        .paymentMode(orderRequest.getPaymentMode())
                        .amount(orderRequest.getTotalAmount())
                        .build();

        String orderStatus = null;

        try {
            paymentService.doPayment(paymentRequest);
            log.info("Payment done successfully, Changing order status!");
            orderStatus = "PLACED";
        } catch (Exception e) {
            log.error("Error occured in payment. Changing order status to PAYMENT_FAILED");
            orderStatus = "PAYMENT_FAILED";
        }

        order.setOrderStatus(orderStatus);
        orderRepository.save(order);

        log.info("Order places successfully with order id: {}", order.getId());

        //BeanUtils.copyProperties(orderRequest, );
        return order.getId();
    }

    @Override
    public OrderResponse getOrderDetailsById(long orderId) {

        log.info("Get order details for order id {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new CustomException("Order with id " + orderId +
                                " not found!", "NOT_FOUND", HttpStatus.NOT_FOUND.value()));

        log.info("Invoking product service to fetch the product id: {}", order.getProductId());
        ProductResponse productResponse =
                restTemplate.getForObject("http://PRODUCT-SERVICE/product/" + order.getProductId(),
                        ProductResponse.class);

        OrderResponse.ProductDetails productDetails =
                OrderResponse.ProductDetails
                        .builder()
                        .productName(productResponse.getProductName())
                        .productId(productResponse.getProductId())
                        .build();

        log.info("Getting payment information from payment service");
        PaymentResponse paymentResponse =
                restTemplate.getForObject("http://PAYMENT-SERVICE/payment/order/" + order.getId(),
                        PaymentResponse.class);
        OrderResponse.PaymentDetails paymentDetails =
                OrderResponse.PaymentDetails
                        .builder()
                        .paymentId(paymentResponse.getPaymentId())
                        .paymentStatus(paymentResponse.getStatus())
                        .paymentDate(paymentResponse.getPaymentDate())
                        .paymentMode(paymentResponse.getPaymentMode())
                        .build();


        log.info("Getting order details for order id {}", orderId);

        return OrderResponse.builder()
                .orderStatus(order.getOrderStatus())
                .orderDate(order.getOrderDate())
                .orderId(order.getId())
                .amount(order.getAmount())
                .productDetails(productDetails)
                .paymentDetails(paymentDetails)
                .build();

    }
}
