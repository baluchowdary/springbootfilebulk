package com.kollu.springbootfilebulk.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestPage<T> {

	private List<T> content;
    private int number;
    private int size;
    private long totalElements;
    
}
