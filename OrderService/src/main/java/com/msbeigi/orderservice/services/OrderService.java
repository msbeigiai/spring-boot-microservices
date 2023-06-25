package com.msbeigi.orderservice.services;

import com.msbeigi.orderservice.model.OrderRequest;
import com.msbeigi.orderservice.model.OrderResponse;

public interface OrderService {
    long placeOrder(OrderRequest orderRequest);

    OrderResponse getOrderDetailsById(long orderId);
}
