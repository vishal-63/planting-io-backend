package com.plantingio.server.Service;

import com.plantingio.server.Model.Gardening;
import com.plantingio.server.Model.OrderDetails;
import com.plantingio.server.Model.Payment;
import com.plantingio.server.Model.Product;
import com.plantingio.server.Repo.*;
import com.plantingio.server.Utility.CartReq;
import com.plantingio.server.Utility.JwtUtil;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.TaxRate;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final GardeningRepo gardeningRepo;
    private final NurseryRepo nurseryRepo;
    private final OrderDetailsRepo orderDetailsRepo;
    private final PaymentRepo paymentRepo;

    @Autowired
    public PaymentService(JwtUtil jwtUtil, UserRepo userRepo, ProductRepo productRepo, GardeningRepo gardeningRepo, NurseryRepo nurseryRepo, OrderDetailsRepo orderDetailsRepo, PaymentRepo paymentRepo) {
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.gardeningRepo = gardeningRepo;
        this.nurseryRepo = nurseryRepo;
        this.orderDetailsRepo = orderDetailsRepo;
        this.paymentRepo = paymentRepo;
    }

    public ResponseEntity createSession(String apiKey, String authorizationHeader, List<CartReq> orderItems) {
        String email = jwtUtil.getEmail(authorizationHeader);
        int userId = userRepo.findIdByEmail(email);


        try {
            Stripe.apiKey = apiKey;

            List<Object> lineItemsList = new ArrayList<>();
            List<String> productTypes = new ArrayList<>(List.of("Plant", "Tool", "Seed"));
            List<String> serviceTypes = new ArrayList<>(List.of("Garden Setup", "Garden Maintenance", "Garden Clearance"));

            TaxRate taxRate = TaxRate.retrieve("txr_1Kjq0dSGPXvdtIQMsDYiA7aW");
            List<Object> taxList = new ArrayList<>();
            taxList.add(taxRate.getId());

            for (CartReq item :
                    orderItems) {
                String name = null;
                String imageUrl = null;
                BigDecimal price = null;
                String nurseryName = null;
                int nurseryId = 0;
                int unitAmt = 0;
                int quantity = 0;
                Map<String, Object> lineItem = new HashMap<>();
                Map<String, Object> priceData = new HashMap<>();
                Map<String, Object> productData = new HashMap<>();
                Map<String, Object> metaData = new HashMap<>();

                if (productTypes.contains(item.getType())) {
                    Product product = productRepo.findById(item.getItemId()).get();
                    name = product.getName();
                    imageUrl = product.getPhotoPath().get(0);
                    price = product.getPrice().subtract(product.getDiscount());
                    nurseryId = product.getNurseryId();
                } else {
                    Gardening service = gardeningRepo.findById(item.getItemId()).get();
                    name = service.getType();
                    imageUrl = service.getPhotoPath();
                    price = service.getPrice().subtract(service.getDiscount());
                    nurseryId = service.getNurseryId();
                }

                nurseryName = nurseryRepo.findNameById(nurseryId);
                unitAmt = price.intValue() * 100;
                quantity = item.getNoOfItems();
                metaData.put("item_id", item.getItemId());
                metaData.put("item_type", item.getType());
                metaData.put("nursery_id", nurseryId);
                metaData.put("nursery_name", nurseryName);
                productData.put("name", name);
                productData.put("images", List.of(imageUrl));
                productData.put("metadata", metaData);
                priceData.put("product_data", productData);
                priceData.put("currency", "inr");
                priceData.put("unit_amount", unitAmt);
                lineItem.put("price_data", priceData);
                lineItem.put("quantity", quantity);
                lineItem.put("tax_rates", taxList);
                lineItemsList.add(lineItem);
            }
            Map<String, Object> params = new HashMap<>();
            Map<String, Object> phoneNo = Map.of("enabled", true);
            Map<String, Object> shipping = Map.of("allowed_countries", List.of("IN"));
            params.put(
                    "success_url",
                    "http://localhost:3000/order/success?session_id={CHECKOUT_SESSION_ID}"
            );
            params.put(
                    "cancel_url",
                    "http://localhost:3000/cart"
            );
            params.put("customer_email", email);
            params.put("line_items", lineItemsList);
            params.put("phone_number_collection", phoneNo);
            params.put("shipping_address_collection", shipping);
            params.put("mode", "payment");
            Session session = Session.create(params);

            return ResponseEntity.ok().body(Map.of(
                    "paymentId", session.getPaymentIntent(),
                    "url", session.getUrl()));



        } catch (StripeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public void savePaymentInfo(Integer orderId) {
        List<OrderDetails> orderDetailsList = orderDetailsRepo.findByOrderId(orderId);
        Map<Integer, List<OrderDetails>> orderDetailsPerNursery = orderDetailsList.stream().collect(Collectors.groupingBy(OrderDetails::getNurseryId));
        for (Map.Entry<Integer, List<OrderDetails>> entry : orderDetailsPerNursery.entrySet()){
            Payment payment = new Payment();
            Integer nurseryId = entry.getKey();
            BigDecimal amount = BigDecimal.valueOf(0.00);
            List<OrderDetails> orders = entry.getValue();
            for (OrderDetails order :
                    orders) {
                amount = amount.add(order.getPrice());
            }
            payment.setNurseryId(nurseryId);
            payment.setOrderId(orderId);
            payment.setAmount(amount);
            payment.setCommission(amount.multiply(BigDecimal.valueOf(5)).divide(BigDecimal.valueOf(100)));
            payment.setStatus("Pending");
            paymentRepo.save(payment);
        }
    }
}
