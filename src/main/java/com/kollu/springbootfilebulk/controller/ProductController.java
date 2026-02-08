package com.kollu.springbootfilebulk.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kollu.springbootfilebulk.model.Product;
import com.kollu.springbootfilebulk.repository.ProductRepository;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository repository;

    // CREATE
    @PostMapping("/save")
    public Product addProduct(@RequestBody Product product) {
        return repository.save(product);
    }

    // READ ALL
    @GetMapping("/load")
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    // UPDATE
    @PutMapping("/update/{id}")
    public Product updateProduct(@PathVariable String id, @RequestBody Product productDetails) {
        return repository.findById(id)
            .map(existingProduct -> {
                // Create a new Record instance with updated values
                Product updatedProduct = new Product(
                    existingProduct.id(),      // Keep the original ID from the DB
                    productDetails.name(),     // New name from request
                    productDetails.price(),    // New price from request
                    productDetails.quality()   // New quality from request
                );
                return repository.save(updatedProduct);
            	//return repository.save(existingProduct.withName(productDetails.name()));
            })
            .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    // DELETE
    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable String id) {
        repository.deleteById(id);
        return "Product deleted successfully";
    }
}
