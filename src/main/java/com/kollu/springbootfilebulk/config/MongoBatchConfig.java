package com.kollu.springbootfilebulk.config;

import org.bson.Document;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.MongoJobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.SimpleJobOperator;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MongoJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.transaction.PlatformTransactionManager;
@Configuration
//public class MongoBatchConfig extends DefaultBatchConfiguration { kollu-This line for - MongoTransactionManager
public class MongoBatchConfig {

	// commented because no replicaSet for MongoDB - kollu-Enable below code for MongoTransactionManager
	/*
	 * @Bean public MongoTransactionManager transactionManager(MongoDatabaseFactory
	 * dbFactory) { return new MongoTransactionManager(dbFactory); }
	 */

	// Manually creating sequence no, MongoDB doesn't create auto increment secuence
	// numbers
	@Bean
	public CommandLineRunner initBatchSequences(MongoOperations mongoOperations) {
		return args -> {
			String[] sequences = { "BATCH_JOB_INSTANCE_SEQ", "BATCH_JOB_EXECUTION_SEQ", "BATCH_STEP_EXECUTION_SEQ" };

			for (String seq : sequences) {
				Query query = new Query(Criteria.where("_id").is(seq));
				if (!mongoOperations.exists(query, "BATCH_SEQUENCES")) {
					Document doc = new Document();
					doc.put("_id", seq);
					doc.put("count", 0L);
					mongoOperations.insert(doc, "BATCH_SEQUENCES");
				}
			}
		};
	}
	
	//commented below for MongoTransactionManager -kollu enable below line
	//@Autowired
    //private MongoTemplate mongoTemplate;
	

	// Updated ResourcelessTransactionManager, we are working single node region
	@Bean
	public PlatformTransactionManager transactionManager() {
		// This manager is designed for systems that don't support 
	    // traditional transactions (like standalone MongoDB)
		return new ResourcelessTransactionManager();
	}
 
	// Batch by default won't accept dot
	@Bean
	public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory factory, MongoMappingContext context) {
		DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
		MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, context);

		// This replaces dots ( . ) with double underscores ( __ ) in Map keys
		converter.setMapKeyDotReplacement("__");

		return converter;
	}
	
	// 2. JobRegistry - Manually defined so it can be injected
    @Bean
    public JobRegistry jobRegistry() {
        return new MapJobRegistry();
    }

	@Bean
	public JobRepository jobRepository(MongoTemplate mongoTemplate, PlatformTransactionManager transactionManager)
			throws Exception {
		MongoJobRepositoryFactoryBean factory = new MongoJobRepositoryFactoryBean();
		factory.setMongoOperations(mongoTemplate);
		factory.setTransactionManager(transactionManager);

		// Optional: Custom collection prefix (default is none, unlike JDBC's BATCH_)
		// factory.setCollectionPrefix("MONGO_META_");

		factory.afterPropertiesSet();
		return factory.getObject();
	}

	@Bean
	public JobLauncher jobLauncher(JobRepository jobRepository) {

		TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
		try {
			jobLauncher.setJobRepository(jobRepository);
			jobLauncher.afterPropertiesSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jobLauncher;
	}

	@Bean
	public JobExplorer jobExplorer(MongoTemplate mongoTemplate, PlatformTransactionManager transactionManager)
			throws Exception {
		MongoJobExplorerFactoryBean factory = new MongoJobExplorerFactoryBean();
		factory.setMongoOperations(mongoTemplate);
		factory.setTransactionManager(transactionManager);
		factory.afterPropertiesSet();
		return factory.getObject();
	}
	
	//Enable below code for MongoTransactionManager -kollu
    
//	@Override
//    protected JobRepository createJobRepository() throws Exception {
//        // The built-in MongoJobFactoryBean is often preferred for 
//        // managing complex initialization automatically.
//        MongoJobRepositoryFactoryBean factoryBean = new MongoJobRepositoryFactoryBean();
//        factoryBean.setMongoOperations(mongoTemplate);
//        factoryBean.setTransactionManager(transactionManager(mongoTemplate.getMongoDatabaseFactory()));
//        factoryBean.afterPropertiesSet();
//        return factoryBean.getObject();
//    }
    
	// 6. JobOperator - Now the JobRegistry will be found!
    @Bean
    public JobOperator jobOperator(JobExplorer jobExplorer, 
                                   JobLauncher jobLauncher, 
                                   JobRegistry jobRegistry, 
                                   JobRepository jobRepository) {
        SimpleJobOperator operator = new SimpleJobOperator();
        operator.setJobExplorer(jobExplorer);
        operator.setJobLauncher(jobLauncher);
        operator.setJobRegistry(jobRegistry);
        operator.setJobRepository(jobRepository);
        return operator;
    }
	
	// 7. Register Jobs automatically (Replacement for deprecated post-processor)
//    @Bean
//    public JobRegistrySmartEventListener jobRegistrySmartEventListener(JobRegistry jobRegistry) {
//        return new JobRegistrySmartEventListener(jobRegistry);
//    }
}