package com.plantingio.server.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantingio.server.Model.Complaint;
import com.plantingio.server.Model.Order;
import com.plantingio.server.Model.User;
import com.plantingio.server.Repo.ComplaintRepo;
import com.plantingio.server.Repo.OrderRepo;
import com.plantingio.server.Repo.UserRepo;
import com.plantingio.server.Utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ComplaintService {

    private final JwtUtil jwtUtil;
    private final ComplaintRepo complaintRepo;
    private final UserRepo userRepo;
    private final OrderRepo orderRepo;
    private final EmailService emailService;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Autowired
    public ComplaintService(JwtUtil jwtUtil, ComplaintRepo complaintRepo, UserRepo userRepo, OrderRepo orderRepo, EmailService emailService) {
        this.jwtUtil = jwtUtil;
        this.complaintRepo = complaintRepo;
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.emailService = emailService;
    }

    private boolean authorizeAdmin(String authorizationHeader) {
        String adminEmail = jwtUtil.getEmail(authorizationHeader);
        Optional<User> adminOptional = userRepo.findByEmail(adminEmail);
        if (!adminOptional.isPresent())
            throw new IllegalStateException("Admin with given not present!");

        if (!adminOptional.get().isIs_admin()) {
            throw new IllegalArgumentException("Unauthorized request");
        }

        return true;
    }

    public ResponseEntity addService(String authorizationHeader, Complaint complaint) {
        String email = jwtUtil.getEmail(authorizationHeader);
        int userId = userRepo.findIdByEmail(email);

        Optional<Order> orderOptional = orderRepo.findById(complaint.getOrderId());
        if (orderOptional.isPresent()) {
                System.out.println(userId + "\n" + orderOptional.get().getUserId());
            if(orderOptional.get().getUserId().equals(userId)) {
                complaint.setUserId(userId);
                complaint.setIssueDate(new Date());
                complaintRepo.save(complaint);
            } else {
                throw new IllegalArgumentException("Order with id " + complaint.getOrderId() + " does not belong to requested user!");
            }
        } else {
            throw new IllegalStateException("Order with id " + complaint.getOrderId() + " not found!");
        }
        return ResponseEntity.ok().body("Complaint saved!");
    }

    public ResponseEntity getAllComplaints(String authorizationHeader) {
        if(authorizeAdmin(authorizationHeader)) {
            List<Complaint> complaintList = complaintRepo.findAll();
            ObjectMapper mapper = new ObjectMapper();

            List<Map> complaints = new ArrayList<>();
            complaintList.forEach(complaint -> {
                Map<String, Object> complaintInfo = mapper.convertValue(complaint, Map.class);
                User user = userRepo.findById(complaint.getUserId()).get();
                String userName = user.getFname() + " " + user.getLname();
                String issueDate = dateFormat.format(complaint.getIssueDate());
                complaintInfo.remove("issueDate");
                complaintInfo.put("userName", userName);
                complaintInfo.put("issueDate", issueDate);
                complaints.add(complaintInfo);
            });

            return ResponseEntity.ok().body(complaints);
        }
        return null;
    }

    public ResponseEntity sendEmail() {
            try {
                emailService.sendSimpleMessage("aasthaarora741@gmail.com", "Test email", "Hello, this is test email!");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return ResponseEntity.ok().body("Email sent successfully");
    }

    @Transactional
    public ResponseEntity replyToComplaint(String authorizationHeader, int complaintId, String reply) {
        if(authorizeAdmin(authorizationHeader)) {
            Optional<Complaint> complaintOptional = complaintRepo.findById(complaintId);
            if(complaintOptional.isPresent()) {
                Complaint complaint = complaintOptional.get();
                System.out.println(reply);
                if(!complaint.isResolved()) {
                    if(reply != null) {
                        System.out.println(complaint.getOrderId());
                        String to = userRepo.findEmailById(complaint.getUserId());
                        System.out.println(to);
                        complaint.setReply(reply);
                        complaint.setResolved(true);
                        complaintRepo.save(complaint);
                        String subject = "Reply to your complaint";
                        String message = reply;
                        emailService.sendSimpleMessage(to, subject, message);
                        return ResponseEntity.ok().body("Complaint reply saved");
                    } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Reply cannot be null");
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Complaint already resolved");
                }
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Complaint with id " + complaintId + " not found");
            }
        }
        return null;
    }
}
