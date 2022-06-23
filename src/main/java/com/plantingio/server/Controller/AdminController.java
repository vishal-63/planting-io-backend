package com.plantingio.server.Controller;

import com.plantingio.server.Service.AdminService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = {"http://localhost:3000/", "http://localhost:5000/"})
public class AdminController {

    @Value("${STRIPE_APIKEY}")
    String apiKey;

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/get-all-users")
    public ResponseEntity getAllUsers (@RequestHeader("Authorization") String authorizationHeader) {
        return adminService.getAllUsers(authorizationHeader);
    }

    @GetMapping("/get-all-nurseries")
    public ResponseEntity getAllNurseries (@RequestHeader("Authorization") String authorizationHeader) {
        return adminService.getAllNurseries (authorizationHeader);
    }

    @GetMapping("/get-all-products")
    public ResponseEntity getAllProducts (@RequestHeader("Authorization") String authorizationHeader) {
        return adminService.getAllProducts (authorizationHeader);
    }

    @GetMapping("/get-all-services")
    public ResponseEntity getAllServices (@RequestHeader("Authorization") String authorizationHeader) {
        return adminService.getAllServices (authorizationHeader);
    }

    @GetMapping("/get-all-orders")
    public ResponseEntity getAllOrders (@RequestHeader("Authorization") String authorizationHeader) {
        return adminService.getAllOrders (authorizationHeader);
    }

    @GetMapping("/get-nursery/{id}")
    public ResponseEntity getNurseryById (@RequestHeader("Authorization") String authorizationHeader,
                                          @PathVariable("id") int nurseryId) {
        return adminService.getNurseryById (authorizationHeader, nurseryId);
    }

    @GetMapping("/get-product/{id}")
    public ResponseEntity getProductById (@RequestHeader("Authorization") String authorizationHeader,
                                          @PathVariable("id") int productId) {
        return adminService.getProductById(authorizationHeader, productId);
    }

    @GetMapping("/get-service/{id}")
    public ResponseEntity getServiceById (@RequestHeader("Authorization") String authorizationHeader,
                                          @PathVariable("id") int serviceId) {
        return adminService.getServiceById(authorizationHeader, serviceId);
    }

    @GetMapping("/get-order/{id}")
    public ResponseEntity getOrderById (@RequestHeader("Authorization") String authorizationHeader,
                                          @PathVariable("id") int orderId) throws StripeException {
        return adminService.getOrderById(apiKey, authorizationHeader, orderId);
    }

    @GetMapping("/get-payments")
    public ResponseEntity getAllPayments (@RequestHeader("Authorization") String authorizationHeader) {
        return adminService.getAllPayments(authorizationHeader);
    }
}
