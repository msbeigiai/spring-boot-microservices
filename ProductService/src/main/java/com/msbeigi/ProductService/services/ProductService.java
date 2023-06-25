package com.msbeigi.ProductService.services;

import com.msbeigi.ProductService.model.ProductRequest;
import com.msbeigi.ProductService.model.ProductResponse;

public interface ProductService {
    long addProduct(ProductRequest productRequest);

    ProductResponse getProductById(long productId);

    void reduceQuantity(long productId, long quantity);
}
