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
    int volume,
    String field1,
    String field2,
    String field3,
    String field4,
    String field5,
    String field6,
    String field7,
    String field8,
    String field9,
    String field10,
    String field11,
    String field12,
    String field13,
    String field14,
    String field15,
    String field16,
    String field17,
    String field18,
    String field19,
    String field20
) {}
