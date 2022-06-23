package com.plantingio.server.Controller;

import com.plantingio.server.Model.Complaint;
import com.plantingio.server.Service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/complaint")
@CrossOrigin(origins = {"http://localhost:3000/", "http://localhost:5000/"})
public class ComplaintController {

    private final ComplaintService complaintService;

    @Autowired
    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @PostMapping("/add")
    public ResponseEntity addComplaint (@RequestHeader("Authorization") String authorizationHeader,
                                        @RequestBody Complaint complaint) {
        System.out.println(complaint.getOrderId());
        return complaintService.addService(authorizationHeader, complaint);
    }

    @GetMapping("/get-all")
    public ResponseEntity getAllComplaints (@RequestHeader("Authorization") String authorizationHeader) {
        return complaintService.getAllComplaints(authorizationHeader);
    }

    @GetMapping("/test-email")
    public ResponseEntity sendEmail () {
        return complaintService.sendEmail();
    }

    @PutMapping("/reply/{id}")
    public ResponseEntity replyToComplaint (@RequestHeader("Authorization") String authorizationHeader,
                                            @PathVariable("id") int complaintId,
                                            @RequestParam("reply") String reply) {
        return complaintService.replyToComplaint(authorizationHeader, complaintId, reply);
    }
}
