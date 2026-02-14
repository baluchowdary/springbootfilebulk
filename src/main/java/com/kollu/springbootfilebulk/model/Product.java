package com.kollu.springbootfilebulk.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public record Product(
    @Id  //This id tell to MongoDB to use for CSV ID as the primary key
    Long id,
    String name,
    Double price,
    String category,
    int volume
) {}
