package com.plantingio.server.Controller;

import com.plantingio.server.Service.OrderService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = {"http://localhost:3000/", "http://localhost:5000/"})
public class OrderController {

    @Value("${Stripe.apiKey}")
    String apiKey;

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/get")
    public ResponseEntity getOrders (@RequestHeader("Authorization") String authorizationHeader) throws StripeException {
        return orderService.getOrders(apiKey, authorizationHeader);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity getOrderById (@RequestHeader("Authorization") String authorizationHeader,
                                        @PathVariable("id") int id) throws StripeException {
        return orderService.getOrderById(apiKey, authorizationHeader, id);
    }

    @GetMapping("/get-nursery-orders")
    public ResponseEntity getOrdersOfNursery (@RequestHeader("Authorization") String authorizationHeader) {
        return orderService.getOrdersOfNursery(apiKey, authorizationHeader);
    }

    @PostMapping("/save/{sessionId}")
    public ResponseEntity saveOrder(@RequestHeader("Authorization") String authorizationHeader,
                                    @PathVariable("sessionId") String sessionId) throws StripeException {
        return orderService.saveOrder(apiKey, authorizationHeader, sessionId);
    }
}
