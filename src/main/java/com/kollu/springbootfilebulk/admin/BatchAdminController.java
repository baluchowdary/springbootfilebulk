package com.kollu.springbootfilebulk.admin;

import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/batch")
public class BatchAdminController {

    @Autowired
    private JobOperator jobOperator;

    //Here, We are trying to update meta table, JOB status when server crash suddenly 
    //Based on investigation on BATCH META table, we need to trigger this rest call 
    @PostMapping("/stop/{executionId}")
    public String stopJob(@PathVariable Long executionId) throws Exception {
        // Forces the job to transition to STOPPED status
        jobOperator.stop(executionId);
        return "Job " + executionId + " stop signal sent.";
    }
}
