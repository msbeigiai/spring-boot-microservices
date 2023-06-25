package com.msbeigi.ProductService.services;

import com.msbeigi.ProductService.entity.Product;
import com.msbeigi.ProductService.exception.ProductServiceCustomException;
import com.msbeigi.ProductService.model.ProductRequest;
import com.msbeigi.ProductService.model.ProductResponse;
import com.msbeigi.ProductService.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public long addProduct(ProductRequest productRequest) {
        log.info("Adding product...");
        Product product =
                Product.builder()
                        .productName(productRequest.getName())
                        .price(productRequest.getPrice())
                        .quantity(productRequest.getQuantity())
                        .build();

        productRepository.save(product);

        log.info("Product with id: {} created.", product.getProductId());
        return product.getProductId();
    }

    @Override
    public ProductResponse getProductById(long productId) {
        log.info("Get the product for product id: {}", productId);
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductServiceCustomException("Product with id: " +
                        productId + " is not exists.", "PRODUCT_NOT_FOUND"));

        ProductResponse productResponse = new ProductResponse();
        BeanUtils.copyProperties(product, productResponse);

        return productResponse;
    }

    @Override
    public void reduceQuantity(long productId, long quantity) {
        log.info("Reduce quantity {} for Id {}", quantity, productId);

        Product product = productRepository.findById(productId).orElseThrow(
                () -> new ProductServiceCustomException(
                        "No product with id " + productId + " was found!",
                        "NOT_FOUND"));
        if (product.getQuantity() < quantity) {
            throw new ProductServiceCustomException(
                    "Product does not have sufficient quantity",
                    "INSUFFICIENT_QUANTITY");
        }

        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        log.info("Product quantity updated successfully");
    }
}
