package com.kollu.springbootfilebulk.admin;

//@RestController
//@RequestMapping("/admin/batch")
public class BatchAdminController {

    //@Autowired
    //private JobOperator jobOperator;

    //Here, We are trying to update meta table, JOB status when server crash suddenly 
    //Based on investigation on BATCH META table, we need to trigger this rest call 
//    @PostMapping("/stop/{executionId}")
//    public String stopJob(@PathVariable Long executionId) throws Exception {
//        // Forces the job to transition to STOPPED status
//        jobOperator.stop(executionId);
//        return "Job " + executionId + " stop signal sent.";
//    }
}
