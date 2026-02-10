package com.kollu.springbootfilebulk.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kollu.springbootfilebulk.model.Product;
import com.kollu.springbootfilebulk.repository.ProductRepository;

@RestController
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private JobLauncher jobLauncher;
	@Autowired
    private Job job;
	@Autowired
	private ProductRepository repository;

//    public BatchController(JobLauncher jobLauncher, Job job) {
//        this.jobLauncher = jobLauncher;
//        this.job = job;
//    }

//    @PostMapping("/upload")
//    public String handleFileUpload(@RequestParam("file") MultipartFile file) throws Exception {
//        // Modern NIO Files API (JDK 11+)
//        Path path = Paths.get("data.csv");
//        Files.write(path, file.getBytes());
//
//        JobParameters params = new JobParametersBuilder()
//                .addLong("time", System.currentTimeMillis())
//                .toJobParameters();
//        
//        jobLauncher.run(job, params);
//        
//        // JDK 15+ Text Block for a clean response
//        return """
//               {
//                 "status": "Success",
//                 "message": "Bulk processing started for %s"
//               }
//               """.formatted(file.getOriginalFilename());
//    }
	
	@PostMapping("/upload")
	public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) throws Exception {
//	    Path path = Paths.get("data.csv");
//	    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING); 
		
		// 1. Check if file is empty
	    if (file.isEmpty()) {
	        return ResponseEntity.badRequest().body("Please upload a file.");
	    }

	    // 2. Validate Content Type
	    String contentType = file.getContentType();
	    if (contentType == null || !contentType.equals("text/csv")) {
	        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	                             .body("Only CSV files are allowed.");
	    }

	    // 3. Validate File Extension
	    String fileName = file.getOriginalFilename();
	    if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
	        return ResponseEntity.badRequest().body("Invalid file extension.");
	    }

	    // Proceed with creating temp file and launching the job... 
	    Path tempFile = Files.createTempFile("uploadFile-", "-" + file.getOriginalFilename());
	    System.out.println("*********File stored at: " + tempFile.toAbsolutePath());
	    Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
		
		

	    // Launch job in the background (Assuming jobLauncher is configured for async)
	    //Here, Multiple users can upload file parallel - one thread allocate for each file upload process
	    //setting fullfilepath to read from itemreder step
	    CompletableFuture.runAsync(() -> {
	        try {
	            JobParameters params = new JobParametersBuilder()
	            		.addString("tempFileFullPath", tempFile.toAbsolutePath().toString())
	                    .addLong("time", System.currentTimeMillis())
	                    .toJobParameters();
	            jobLauncher.run(job, params);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    });
	    
	    return new ResponseEntity<String>("File Upload successfully", HttpStatus.ACCEPTED);
	}
    
    @GetMapping("/load")
    public Page<Product> getAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size) {
        return repository.findAll(PageRequest.of(page, size)); 
    }
    
 // DELETE BY ID
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
 // DELETE ALL (Bulk Clear)
    @DeleteMapping("/clear")
    public ResponseEntity<String> clearAll() {
        repository.deleteAll();
        return ResponseEntity.ok("Database Cleared");
    }
    
    
}
