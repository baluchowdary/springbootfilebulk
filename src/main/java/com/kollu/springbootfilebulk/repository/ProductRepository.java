package com.kollu.springbootfilebulk.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.kollu.springbootfilebulk.model.Product;

@Repository
public interface ProductRepository extends MongoRepository<Product, Long> {
    // Standard CRUD methods like findAll() and deleteById() are inherited
}