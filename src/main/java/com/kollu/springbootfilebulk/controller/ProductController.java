package com.kollu.springbootfilebulk.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) throws Exception {
        // Modern NIO Files API (JDK 11+)
        Path path = Paths.get("data.csv");
        Files.write(path, file.getBytes());

        JobParameters params = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();
        
        jobLauncher.run(job, params);
        
        // JDK 15+ Text Block for a clean response
        return """
               {
                 "status": "Success",
                 "message": "Bulk processing started for %s"
               }
               """.formatted(file.getOriginalFilename());
    }
    
    @GetMapping("/load")
    public List<Product> getAll() {
        return repository.findAll();
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
