package com.kollu.springbootfilebulk.util;

import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;

import com.kollu.springbootfilebulk.model.Product; // Ensure this path matches your project

public class ProductJobParameterReader extends FlatFileItemReader<Product> implements StepExecutionListener {

    public ProductJobParameterReader() {
        this.setName("productReader");
        
     // 1. SKIP THE HEADER LINE
        // This tells the reader to ignore the first line (id|name|price|category)
        this.setLinesToSkip(1);
        
        // Define how to map CSV lines to your Product object
        DefaultLineMapper<Product> lineMapper = new DefaultLineMapper<>();
        
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter("|");
        tokenizer.setNames("id", "name", "price", "category"); // Match your CSV column headers
        
        
		/*
		 * BeanWrapperFieldSetMapper<Product> fieldSetMapper = new
		 * BeanWrapperFieldSetMapper<>(); fieldSetMapper.setTargetType(Product.class);
		 */
        
        //commented above fieldSetMapper, why because i am using record class, fieldSetMapper support for normal model class
     // Manually map the fields to the Record constructor
        lineMapper.setFieldSetMapper(fieldSet -> new Product(
                fieldSet.readLong("id"),
                fieldSet.readString("name"),
                fieldSet.readDouble("price"),
                fieldSet.readString("category")
        ));
        
        lineMapper.setLineTokenizer(tokenizer);
        //lineMapper.setFieldSetMapper(fieldSetMapper);
        
        this.setLineMapper(lineMapper);
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        // This runs RIGHT before the step starts. 
        // We pull the unique path passed from the Controller.
        String path = stepExecution.getJobParameters().getString("tempFileFullPath");
        
        if (path != null) {
            this.setResource(new FileSystemResource(path));
        }
    }
}
