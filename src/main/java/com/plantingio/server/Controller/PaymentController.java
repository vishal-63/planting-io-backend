package com.plantingio.server.Controller;

import com.plantingio.server.Service.PaymentService;
import com.plantingio.server.Utility.CartReq;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = {"http://localhost:3000/", "http://localhost:5000/"})
public class PaymentController {

    @Value("${Stripe.apiKey}")
    String apiKey;

    private final RestTemplate restTemplate;
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(RestTemplate restTemplate, PaymentService paymentService) {
        this.restTemplate = restTemplate;
        this.paymentService = paymentService;
    }

    @PostMapping("/create-session")
    public ResponseEntity createSession(@RequestHeader("Authorization") String authorizationHeader,
                                        @RequestBody List<CartReq> orderItems) throws StripeException {
        return paymentService.createSession(apiKey, authorizationHeader, orderItems);
    }

}
