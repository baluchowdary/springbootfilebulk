package com.kollu.springbootfilebulk.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
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
import com.kollu.springbootfilebulk.util.FileCleanupListener;
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
	
	 @Bean
	    public ItemProcessor<Product, Product> processor() {
	    	return item -> {
	    		String upperCaseName = item.name().toUpperCase().trim();
	    		return new Product(item.id(), upperCaseName, item.price(), item.category());
	    	};
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
    public Job importJob(JobRepository jobRepository, Step step1, FileCleanupListener step2) {
        return new JobBuilder("importJob", jobRepository)
                .start(step1)
                .listener(step2) //delete temp file step will execute after process step finish
                .build();
    }
    //here, We are using 'PlatformTransactionManager' because of single node env

    @Bean 
    public Step step1(JobRepository jobRepository, PlatformTransactionManager transactionManager,
    		ProductJobParameterReader reader, ItemProcessor<Product, Product> processor, MongoItemWriter<Product> writer) {
        return new StepBuilder("step1", jobRepository)
                .<Product, Product>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
    
//    //step2 - cleanup tempfiles
//    @Bean
//    public Step step2(JobRepository jobRepository, 
//                            PlatformTransactionManager transactionManager, 
//                            FileDeletionTasklet tasklet) {
//        return new StepBuilder("step2", jobRepository)
//                .tasklet(tasklet, transactionManager)
//                .build();
//    }

}