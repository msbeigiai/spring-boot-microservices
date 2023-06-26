package com.msbeigi.orderservice.controller;

import com.msbeigi.orderservice.model.OrderRequest;
import com.msbeigi.orderservice.model.OrderResponse;
import com.msbeigi.orderservice.services.OrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@Log4j2
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PreAuthorize("hasAuthority('Customer')")
    @PostMapping("/placeOrder")
    public ResponseEntity<Long> placeOrder(@RequestBody OrderRequest orderRequest) {
        long orderId = orderService.placeOrder(orderRequest);
        log.info("Order id has been submitted: {}", orderId);
        return new ResponseEntity<>(orderId, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('Admin') || hasAuthority('Customer')")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetailsById(@PathVariable long orderId) {
        OrderResponse orderResponse =
                orderService.getOrderDetailsById(orderId);

        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }

}
