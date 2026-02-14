package com.kollu.springbootfilebulk.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kollu.springbootfilebulk.model.JsonPage;
import com.kollu.springbootfilebulk.model.Product;
import com.kollu.springbootfilebulk.repository.ProductRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class ProductService {

	@Autowired
	private JobLauncher jobLauncher;

	@Autowired
	private Job job;

	@Autowired
	private ProductRepository productRepository;

	public String saveFIleData(MultipartFile file) throws IOException {

		// Proceed with creating temp file and launching the job...
		Path projectTempDir = Paths.get(System.getProperty("java.io.tmpdir"), "batch-uploads");
		Files.createDirectories(projectTempDir);
		Path tempFile = Files.createTempFile(projectTempDir, "uploadFile-", "-" + file.getOriginalFilename());

		// Path tempFile = Files.createTempFile("uploadFile-", "-" +
		// file.getOriginalFilename());
		System.out.println("*********File stored at: " + tempFile.toAbsolutePath());
		Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

		// Launch job in the background (Assuming jobLauncher is configured for async)
		// Here, Multiple users can upload file parallel - one thread allocate for each
		// file upload process
		// setting fullfilepath to read from itemreder step

		CompletableFuture.runAsync(() -> {
			try {
				JobParameters params = new JobParametersBuilder()
						.addString("tempFileFullPath", tempFile.toAbsolutePath().toString())
						.addLong("time", System.currentTimeMillis()).toJobParameters();
				jobLauncher.run(job, params);

			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		return "File Upload successfully";

	}

	// @CircuitBreaker(name = "redisService", fallbackMethod =
	// "getAllProductsFallback")
	@Cacheable(value = "products", key = "#page + '-' + #size")
	@CircuitBreaker(name = "redisService", fallbackMethod ="getFileDataFallback")
	//@GetMapping("/load")
	public JsonPage getFileData(int page, int size) {
		Page<Product> productPage = productRepository.findAll(PageRequest.of(page, size));
		return new JsonPage(productPage); 
	}

	// Circuit breaker with religence4J
	public JsonPage getFileDataFallback(int page, int size, Throwable e) {
		System.out.println("Error: Circuit breaker Load data block:: " + e.getMessage());
		Page<Product> productPage = productRepository.findAll(PageRequest.of(page, size));
		return new JsonPage(productPage); 
	}

	@CacheEvict(value = "products", key = "#id") // Removes the specific item from Redis
    @CircuitBreaker(name = "redisService", fallbackMethod = "deleteFallback")
	//@DeleteMapping("/delete/{id}")
	public void delete(Long id) {
		productRepository.deleteById(id);
	}
	
	public void deleteFallback(Long id, Throwable e) {
        System.err.println("Could not delete product " + id + ". Error: " + e.getMessage());
        throw new RuntimeException("System is currently busy. Delete operation failed for ID: " + id);
    }
	

	@CacheEvict(value = "products", allEntries = true) // Clears the entire "products" cache in Redis
    @CircuitBreaker(name = "redisService", fallbackMethod = "clearAllFallback")
	//@DeleteMapping("/clear")
	public String clearAll() {
		productRepository.deleteAll();
		return "Database Cleared";
	}
	
	public String clearAllFallback(Throwable e) {
        System.err.println("Clear All failed! Reason: " + e.getMessage());
        return "Maintenance Mode: Unable to clear data at this time. Please try again later.";
    }

}
