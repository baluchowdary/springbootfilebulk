package com.kollu.springbootfilebulk.config;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.DuplicateJobException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import com.kollu.springbootfilebulk.model.Product;
import com.kollu.springbootfilebulk.util.ProductJobParameterReader;

@Configuration
public class BatchConfig {

//	@Bean
//    public FlatFileItemReader<Product> reader() {
//        return new FlatFileItemReaderBuilder<Product>()
//                .name("productReader")
//                .resource(new FileSystemResource("")) // Ensure this file exists
//                .linesToSkip(1)
//                .delimited()
//                .delimiter("|")
//                .names("id","name", "price", "category")
//                .targetType(Product.class)
//                .build();
//    }
	
	
	//Writing custom reader step to capture filepath from controller, By default batch won't support with NoSQL DB
	@Bean
    public ProductJobParameterReader reader() {
        return new ProductJobParameterReader();
    }
	
	/*
	 * @Bean public ItemProcessor<Product, Product> processor() { return item -> {
	 * String upperCaseName = item.name().toUpperCase().trim(); return new
	 * Product(item.id(), upperCaseName, item.price(), item.category()); }; }
	 */
	
	
	@Bean
	public ItemProcessor<Product, Product> processor() {
		return item -> {
			String upperCaseName = item.name().toUpperCase().trim();
			//return new Product(item.id(), upperCaseName, item.price(), item.category());
			return updateRecord(item, "name", upperCaseName);
		};
	}
	  

    private Product updateRecord(Product record, String fieldName, String newValue) {
		
    	try {
            RecordComponent[] components = record.getClass().getRecordComponents();
            Object[] values = new Object[components.length];

            for (int i = 0; i < components.length; i++) {
                // Check if this component is the one we want to update
                if (components[i].getName().equals(fieldName)) {
                    values[i] = newValue;
                } else {
                    // Access the current value using the record's accessor method
                    values[i] = components[i].getAccessor().invoke(record);
                }
            }

            // Identify the types of the constructor arguments
            Class<?>[] argTypes = Arrays.stream(components)
                                        .map(RecordComponent::getType)
                                        .toArray(Class<?>[]::new);

            // Call the canonical constructor to create the updated Product
            return record.getClass()
                         .getDeclaredConstructor(argTypes)
                         .newInstance(values);

        } catch (Exception e) {
            throw new RuntimeException("Failed to dynamically update Product record", e);
        }
	}

	@Bean
    public MongoItemWriter<Product> writer(MongoTemplate mongoTemplate) {
        return new MongoItemWriterBuilder<Product>()
                .template(mongoTemplate)
                .collection("products")
                .build();
    }

    // THIS IS THE BEAN THE CONTROLLER IS LOOKING FOR
    @Bean
    public Job importJob(JobRepository jobRepository, Step step1, JobRegistry jobRegistry) {
//        return new JobBuilder("importJob", jobRepository)
//                .start(step1)
//               // .listener(step2) //delete temp file step will execute after process step finish
//                .build();
    	
    	 Job job = new JobBuilder("importJob", jobRepository)
                 .start(step1)
                // .listener(step2) //delete temp file step will execute after process step finish
                 .build();
        
     // Manually push it into the registry
        ReferenceJobFactory factory = new ReferenceJobFactory(job);
        try {
			jobRegistry.register(factory);
		} catch (DuplicateJobException e) {
			e.printStackTrace();
		} 
        
        return job;
    }
    
    //here, We are using 'PlatformTransactionManager' because of single node env
    /*
	 * Default behavior is now 'upsert' (update/save) because of @Id -> It will
	 * useful to overcome data inconsistency when server crash while writing data
	 * into MongoDB
	 */
    @Bean 
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
    		ProductJobParameterReader reader, ItemProcessor<Product, Product> processor, MongoItemWriter<Product> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Product, Product>chunk(2, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
				.build();  
    }

}