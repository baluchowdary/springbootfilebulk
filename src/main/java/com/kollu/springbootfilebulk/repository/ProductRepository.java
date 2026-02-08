package com.kollu.springbootfilebulk.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.kollu.springbootfilebulk.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
}