package com.plantingio.server.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantingio.server.Model.*;
import com.plantingio.server.Repo.*;
import com.plantingio.server.Utility.JwtUtil;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Address;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.stripe.Stripe.apiKey;

@Service
public class AdminService {

    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;
    private final NurseryRepo nurseryRepo;
    private final ProductRepo productRepo;
    private final GardeningRepo gardeningRepo;
    private final OrderRepo orderRepo;
    private final OrderDetailsRepo orderDetailsRepo;
    private final PaymentRepo paymentRepo;

    @Autowired
    public AdminService(JwtUtil jwtUtil, UserRepo userRepo, NurseryRepo nurseryRepo, ProductRepo productRepo, GardeningRepo gardeningRepo, OrderRepo orderRepo, OrderDetailsRepo orderDetailsRepo, PaymentRepo paymentRepo) {
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
        this.nurseryRepo = nurseryRepo;
        this.productRepo = productRepo;
        this.gardeningRepo = gardeningRepo;
        this.orderRepo = orderRepo;
        this.orderDetailsRepo = orderDetailsRepo;
        this.paymentRepo = paymentRepo;
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

    public ResponseEntity getAllNurseries(String authorizationHeader) {
        if (authorizeAdmin(authorizationHeader)) {
            List<Nursery> nurseryList = nurseryRepo.findAll();
            return ResponseEntity.ok().body(nurseryList);
        }
        return null;
    }

    public ResponseEntity getAllUsers(String authorizationHeader) {
        if (authorizeAdmin(authorizationHeader)) {

            List<User> userList = userRepo.findAllUsers();
            return ResponseEntity.ok().body(userList);
        }
        return null;
    }

    public ResponseEntity getAllProducts(String authorizationHeader) {
        if (authorizeAdmin(authorizationHeader)) {
            ObjectMapper mapper = new ObjectMapper();
            List<Product> products = productRepo.findAll();
            List<Map> productList = new ArrayList<>();
            for (Product product :
                    products) {
                Map<String, Object> productInfo = mapper.convertValue(product, Map.class);
                String nurseryName = nurseryRepo.findNameById(product.getNurseryId());
                productInfo.put("nurseryName", nurseryName);
                productList.add(productInfo);
            }
            return ResponseEntity.ok().body(productList);
        }
        return null;
    }

    public ResponseEntity getAllServices(String authorizationHeader) {
        if (authorizeAdmin(authorizationHeader)) {
            ObjectMapper mapper = new ObjectMapper();
            List<Gardening> services = gardeningRepo.findAll();
            List<Map> serviceList = new ArrayList<>();
            for (Gardening service :
                    services) {
                Map<String, Object> serviceInfo = mapper.convertValue(service, Map.class);
                String nurseryName = nurseryRepo.findNameById(service.getNurseryId());
                serviceInfo.put("nurseryName", nurseryName);
                serviceList.add(serviceInfo);
            }
            return ResponseEntity.ok().body(serviceList);
        }
        return null;
    }

    public ResponseEntity getAllOrders(String authorizationHeader) {
        if (authorizeAdmin(authorizationHeader)) {
            ObjectMapper mapper = new ObjectMapper();
            List<Map> orderList = new ArrayList<>();

            List<Order> orders = orderRepo.findAll();
            for (Order order :
                    orders) {

                List<OrderDetails> orderDetails = orderDetailsRepo.findByOrderId(order.getId());

                for (OrderDetails orderDetail :
                        orderDetails) {
                    Map<String, Object> orderInfo = new HashMap<>();

                    String itemName = null;
                    String type = null;
                    BigDecimal price;
                    if (orderDetail.getProductId() != null) {
                        Product product = productRepo.findById(orderDetail.getProductId()).get();
                        itemName = product.getName();
                        type = product.getType();
                        price = product.getPrice().subtract(product.getDiscount());
                    } else {
                        Gardening service = gardeningRepo.findById(orderDetail.getServiceId()).get();
                        itemName = service.getType();
                        type = "Service";
                        price = service.getPrice().subtract(service.getDiscount());
                    }

                    orderInfo.put("orderId", order.getId());
                    orderInfo.put("itemName", itemName);
                    orderInfo.put("type", type);
                    orderInfo.put("nurseryName", nurseryRepo.findNameById(orderDetail.getNurseryId()));
                    User user = userRepo.findById(order.getUserId()).get();
                    String customerName = user.getFname() + " " + user.getLname();
                    orderInfo.put("customerName", customerName);
                    orderInfo.put("subTotal", price);
                    orderInfo.put("quantity", orderDetail.getQuantity());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
                    orderInfo.put("orderDate", dateFormat.format(order.getOrderDate()));
                    orderInfo.put("paymentStatus", paymentRepo.findByOrderId(order.getId()).get(0).getStatus());
                    orderInfo.put("orderStatus", order.getOrderStatus());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(order.getOrderDate());
                    cal.add(Calendar.DATE, 15);
                    orderInfo.put("paymentDue", dateFormat.format(cal.getTime()));

                    orderList.add(orderInfo);
                }
            }
            return ResponseEntity.ok().body(orderList);
        }
        return null;
    }

    public ResponseEntity getNurseryById(String authorizationHeader, int nurseryId) {

        if(authorizeAdmin(authorizationHeader)) {

            Optional<Nursery> nurseryOptional = nurseryRepo.findById(nurseryId);
            if (nurseryOptional.isPresent()) {

                Nursery nursery = nurseryOptional.get();
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> nurseryInfo = mapper.convertValue(nursery, Map.class);

                int noOfOrders = orderDetailsRepo.findRowsWithUniqueOrderId(nurseryId).size();
                nurseryInfo.put("noOfOrders", noOfOrders);

                List<Payment> payments = paymentRepo.findByNurseryId(nurseryId);
                System.out.println(payments);
                BigDecimal revenue = BigDecimal.valueOf(0.00);
                for (Payment payment :
                        payments) {
                    System.out.println(payment.getAmount() + " " + payment.getCommission());
                    revenue = revenue.add(payment.getAmount().subtract(payment.getCommission()));
                }
                nurseryInfo.put("revenue", revenue);

                List<Product> products = productRepo.findByNurseryId(nurseryId);
                nurseryInfo.put("products", products);

                List<Gardening> services= gardeningRepo.findByNurseryId(nurseryId);
                nurseryInfo.put("services", services);

                return ResponseEntity.ok().body(nurseryInfo);
            } else {
                throw new IllegalStateException("Nursery with id " + nurseryId + " not found!");
            }
        }
        return null;
    }

    public ResponseEntity getProductById(String authorizationHeader, int productId) {
        if(authorizeAdmin(authorizationHeader)) {
            Optional<Product> productOptional = productRepo.findById(productId);
            if(productOptional.isPresent()) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> productInfo = mapper.convertValue(productOptional.get(), Map.class);
                String nurseryName = nurseryRepo.findNameById(productOptional.get().getNurseryId());
                productInfo.put("nurseryName", nurseryName);

                return ResponseEntity.ok().body(productInfo);
            } else {
                throw new IllegalStateException("Product with id " + productId + " not found!");
            }
        }
        return null;
    }

    public ResponseEntity getServiceById(String authorizationHeader, int serviceId) {
        if(authorizeAdmin(authorizationHeader)) {
            Optional<Gardening> serviceOptional = gardeningRepo.findById(serviceId);
            if(serviceOptional.isPresent()) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> serviceInfo = mapper.convertValue(serviceOptional.get(), Map.class);
                String nurseryName = nurseryRepo.findNameById(serviceOptional.get().getNurseryId());
                serviceInfo.put("nurseryName", nurseryName);
                List<String> photoPath = new ArrayList<>();
                photoPath.add(serviceOptional.get().getPhotoPath());
                serviceInfo.remove("photoPath");
                serviceInfo.put("photoPath", photoPath);
                return ResponseEntity.ok().body(serviceInfo);
            } else {
                throw new IllegalStateException("Product with id " + serviceId + " not found!");
            }
        }
        return null;
    }

    public ResponseEntity getOrderById(String apiKey, String authorizationHeader, int orderId) throws StripeException {
        if(authorizeAdmin(authorizationHeader)) {

            Order order = orderRepo.getById(orderId);
            List<OrderDetails> orderDetailsList = orderDetailsRepo.findByOrderId(orderId);

            Map<String, Object> orderInfo = new HashMap<>();
            orderInfo.put("orderId", orderId);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            orderInfo.put("orderDate", dateFormat.format(order.getOrderDate()));
            orderInfo.put("orderStatus", order.getOrderStatus());
            orderInfo.put("paymentStatus", paymentRepo.findByOrderId(order.getId()).get(0).getStatus());

            Stripe.apiKey = apiKey;
            Session session = Session.retrieve(order.getSessionId());

            User user = userRepo.findById(order.getUserId()).get();
            String customerName = user.getFname() + " " + user.getLname();
            orderInfo.put("userId", order.getUserId());
            orderInfo.put("userName", customerName);
            orderInfo.put("shippingName", session.getShipping().getName());
            orderInfo.put("phoneNo", session.getCustomerDetails().getPhone());
            orderInfo.put("email", session.getCustomerDetails().getEmail());
            Address stripeAddressObj = session.getShipping().getAddress();
            String address = stripeAddressObj.getLine1() + ", " + stripeAddressObj.getLine2() + ", " + stripeAddressObj.getCity() + "," + stripeAddressObj.getState() + " - " + stripeAddressObj.getPostalCode();
            orderInfo.put("shippingAddress", address);

            orderInfo.put("subTotal", order.getSubTotal());
            orderInfo.put("tax", order.getTax());
            orderInfo.put("grandTotal", order.getGrandTotal());

            List<Map> products = new ArrayList<>();
            for (OrderDetails orderDetail :
                    orderDetailsList) {

                Map<String, Object> productInfo = new HashMap<>();
                String itemName = null;
                String imageUrl = null;
                Integer quantity = orderDetail.getQuantity();
                BigDecimal pricePerUnit;
                BigDecimal totalAmt;
                System.out.println(281);
                if (orderDetail.getProductId() != null) {
                    System.out.println(283);
                    com.plantingio.server.Model.Product product = productRepo.findById(orderDetail.getProductId()).get();
                    itemName = product.getName();
                    imageUrl = product.getPhotoPath().get(0);
                    pricePerUnit = product.getPrice().subtract(product.getDiscount());
                } else {
                    System.out.println(289);
                    Gardening service = gardeningRepo.findById(orderDetail.getServiceId()).get();
                    itemName = service.getType();
                    pricePerUnit = service.getPrice().subtract(service.getDiscount());
                    imageUrl = service.getPhotoPath();
                    System.out.println(294);
                }
                System.out.println(296);
                totalAmt = pricePerUnit.multiply(BigDecimal.valueOf(quantity));
                productInfo.put("itemName", itemName);
                productInfo.put("imageUrl", imageUrl);
                productInfo.put("nurseryName", nurseryRepo.findNameById(orderDetail.getNurseryId()));
                productInfo.put("pricePerUnit", pricePerUnit);
                productInfo.put("quantity", quantity);
                productInfo.put("totalAmt", totalAmt);
                System.out.println(304);
                products.add(productInfo);
            }
            orderInfo.put("products", products);

            return ResponseEntity.ok().body(orderInfo);
        }
        return null;
    }

    public ResponseEntity getAllPayments(String authorizationHeader) {
        if(authorizeAdmin(authorizationHeader)) {
            List<Payment> paymentList = paymentRepo.findAll();
            List<Map> paymentsToReturn = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            paymentList.forEach(payment -> {
                Map<String, Object> paymentInfo = mapper.convertValue(payment, Map.class);
                String nurseryName = nurseryRepo.findNameById(payment.getNurseryId());
                BigDecimal amtToBePaid = payment.getAmount().subtract(payment.getCommission());
                paymentInfo.put("nurseryName", nurseryName);
                paymentInfo.put("amtToBePaid", amtToBePaid);
                Calendar cal = Calendar.getInstance();
                Date orderDate = orderRepo.findById(payment.getOrderId()).get().getOrderDate();
                cal.setTime(orderDate);
                cal.add(Calendar.DATE, 15);
                paymentInfo.put("paymentDue", dateFormat.format(cal.getTime()));
                paymentsToReturn.add(paymentInfo);
            });
            return ResponseEntity.ok().body(paymentsToReturn);
        }
        return null;
    }
}
