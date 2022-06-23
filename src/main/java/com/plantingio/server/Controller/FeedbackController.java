package com.plantingio.server.Controller;

import com.plantingio.server.Model.Feedback;
import com.plantingio.server.Service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = {"http://localhost:3000/", "http://localhost:5000"})
public class FeedbackController {

    private final FeedbackService feedbackService;

    @Autowired
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping("/add")
    public ResponseEntity addFeedback(@RequestHeader("Authorization") String authorizationHeader,
                                      @RequestBody Feedback feedback) {
        return feedbackService.addFeedback(authorizationHeader, feedback);
    }
}
