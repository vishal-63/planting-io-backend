package com.plantingio.server.Service;

import com.plantingio.server.Model.Feedback;
import com.plantingio.server.Repo.FeedbackRepo;
import com.plantingio.server.Repo.GardeningRepo;
import com.plantingio.server.Repo.ProductRepo;
import com.plantingio.server.Repo.UserRepo;
import com.plantingio.server.Utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FeedbackService {

    private final JwtUtil jwtUtil;
    private final FeedbackRepo feedbackRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final GardeningRepo gardeningRepo;

    @Autowired
    public FeedbackService(JwtUtil jwtUtil, FeedbackRepo feedbackRepo, UserRepo userRepo, ProductRepo productRepo, GardeningRepo gardeningRepo) {
        this.jwtUtil = jwtUtil;
        this.feedbackRepo = feedbackRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.gardeningRepo = gardeningRepo;
    }

    public ResponseEntity addFeedback(String authorizationHeader, Feedback feedback) {
        String email = jwtUtil.getEmail(authorizationHeader);
        int userId = userRepo.findIdByEmail(email);

        if(userRepo.existsById(userId)) {
            feedback.setUserId(userId);
            if(feedback.getRating() > 0 && feedback.getFeedbackDescription().length() < 150)
                System.out.println(feedback.getServiceId());
                feedbackRepo.save(feedback);

            return ResponseEntity.ok().body("Feedback saved successfully!");
        } else {
            throw new IllegalStateException("User with does not exist!");
        }
    }
}
