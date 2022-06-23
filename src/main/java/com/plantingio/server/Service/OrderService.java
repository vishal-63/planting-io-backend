package com.plantingio.server.Service;

import com.plantingio.server.Model.*;
import com.plantingio.server.Model.Order;
import com.plantingio.server.Repo.*;
import com.plantingio.server.Utility.JwtUtil;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.Product;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class OrderService {

    private final OrderRepo orderRepo;
    private final OrderDetailsRepo orderDetailsRepo;
    private final UserRepo userRepo;
    private final CartRepo cartRepo;
    private final ProductRepo productRepo;
    private final GardeningRepo gardeningRepo;
    private final NurseryRepo nurseryRepo;
    private final FeedbackRepo feedbackRepo;
    private final PaymentService paymentService;
    private final PaymentRepo paymentRepo;
    private final JwtUtil jwtUtil;

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Autowired
    public OrderService(OrderRepo orderRepo, OrderDetailsRepo orderDetailsRepo, UserRepo userRepo, CartRepo cartRepo, ProductRepo productRepo, GardeningRepo gardeningRepo, NurseryRepo nurseryRepo, FeedbackRepo feedbackRepo, PaymentService paymentService, PaymentRepo paymentRepo, JwtUtil jwtUtil) {
        this.orderRepo = orderRepo;
        this.orderDetailsRepo = orderDetailsRepo;
        this.userRepo = userRepo;
        this.cartRepo = cartRepo;
        this.productRepo = productRepo;
        this.gardeningRepo = gardeningRepo;
        this.nurseryRepo = nurseryRepo;
        this.feedbackRepo = feedbackRepo;
        this.paymentService = paymentService;
        this.paymentRepo = paymentRepo;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity saveOrder(String apiKey, String authorizationHeader, String sessionId) throws StripeException {
        String email = jwtUtil.getEmail(authorizationHeader);
        int userId = userRepo.findIdByEmail(email);

        Stripe.apiKey = apiKey;

        List<String> productTypes = new ArrayList<>(List.of("Plant", "Tool", "Seed"));
        List<String> serviceTypes = new ArrayList<>(List.of("Garden Setup", "Garden Maintenance", "Garden Clearance"));

        if (orderRepo.existsBySessionId(sessionId))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Order with session id: " + sessionId + " already exists!");

        try {
            Session session = Session.retrieve(sessionId);
            BigDecimal subTotal = BigDecimal.valueOf(session.getAmountSubtotal()).divide(BigDecimal.valueOf(100));
            BigDecimal tax = BigDecimal.valueOf(session.getTotalDetails().getAmountTax()).divide(BigDecimal.valueOf(100));
            BigDecimal grandTotal = BigDecimal.valueOf(session.getAmountTotal()).divide(BigDecimal.valueOf(100));

            Order order = new Order();
            order.setUserId(userId);
            order.setSessionId(sessionId);
            order.setOrderDate(new Date());
            order.setOrderStatus("Ordered");
            order.setSubTotal(subTotal);
            order.setTax(tax);
            order.setGrandTotal(grandTotal);

            order = orderRepo.save(order);

            Map<String, Object> params = Map.of("limit", 10);
            LineItemCollection lineItemCollection = session.listLineItems(params);
            List<LineItem> lineItemsData = lineItemCollection.getData();
            for (LineItem item :
                    lineItemsData) {
                OrderDetails orderDetails = new OrderDetails();
                Price stripePriceObj = item.getPrice();
                String stripeProductId = stripePriceObj.getProduct();
                Product stripeProductObj = Product.retrieve(stripeProductId);
                Map<String, String> metaData = stripeProductObj.getMetadata();

                orderDetails.setOrderId(order.getId());
                orderDetails.setNurseryId(Integer.parseInt(metaData.get("nursery_id")));

                if (productTypes.contains(metaData.get("item_type")))
                    orderDetails.setProductId(Integer.parseInt(metaData.get("item_id")));
                else
                    orderDetails.setServiceId(Integer.parseInt(metaData.get("item_id")));

                orderDetails.setPrice(BigDecimal.valueOf(stripePriceObj.getUnitAmount()).divide(BigDecimal.valueOf(100)));
                orderDetails.setQuantity(Math.toIntExact(item.getQuantity()));
                orderDetailsRepo.save(orderDetails);
            }
            paymentService.savePaymentInfo(order.getId());

            Cart cart = cartRepo.findByUserId(userId).get();
            cartRepo.delete(cart);

            return ResponseEntity.ok().body("Order saved successfully!");

        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("There was an error while saving the order!");
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity getOrders(String apiKey, String authorizationHeader) throws StripeException {
        String email = jwtUtil.getEmail(authorizationHeader);
        int userId = userRepo.findIdByEmail(email);

        List<Order> ordersOfUser = orderRepo.findByUserId(userId);
        List<Map> ordersToReturn = new ArrayList<>();

        Stripe.apiKey = apiKey;
        for (Order order :
                ordersOfUser) {
            Map<String, Object> singleOrderDetails = new HashMap<>();
            String sessionId = order.getSessionId();
            Session session = Session.retrieve(sessionId);

            singleOrderDetails.put("orderId", order.getId());

            String orderDate = dateFormat.format(order.getOrderDate());
            singleOrderDetails.put("orderDate", orderDate);
            singleOrderDetails.put("orderStatus", order.getOrderStatus());
            singleOrderDetails.put("grandTotal", session.getAmountTotal() / 100.00);
            singleOrderDetails.put("subTotal", session.getAmountSubtotal() / 100.00);
            singleOrderDetails.put("tax", session.getTotalDetails().getAmountTax() / 100.00);

            Address stripeAddressObj = session.getShipping().getAddress();
            String address = stripeAddressObj.getLine1() + ", " + stripeAddressObj.getLine2() + ", " + stripeAddressObj.getCity() + "," + stripeAddressObj.getState() + " - " + stripeAddressObj.getPostalCode();
            singleOrderDetails.put("shippingAddress", address);
            singleOrderDetails.put("shippingName", session.getShipping().getName());


            List<Map> productDetailsList = new ArrayList<>();
            List<OrderDetails> orderDetailsList = orderDetailsRepo.findByOrderId(order.getId());
            for (OrderDetails orderDetail :
                    orderDetailsList) {
                Map<String, Object> productDetails = new HashMap<>();

                if (orderDetail.getProductId() != null) {
                    com.plantingio.server.Model.Product product = productRepo.findById(orderDetail.getProductId()).get();
                    productDetails.put("productId", product.getId());
                    productDetails.put("productName", product.getName());
                    productDetails.put("quantity", orderDetail.getQuantity());
                    productDetails.put("productPrice", orderDetail.getPrice());
                    productDetails.put("productImg", product.getPhotoPath().get(0));
                    productDetails.put("nurseryName", nurseryRepo.findNameById(orderDetail.getNurseryId()));
                    Feedback feedback = feedbackRepo.findByUserIdAndProductId(userId, product.getId());
                    if (feedback != null) {
                        productDetails.put("reviewStars", feedback.getRating());
                    }
                } else {
                    Gardening service = gardeningRepo.findById(orderDetail.getServiceId()).get();
                    productDetails.put("productId", service.getId());
                    productDetails.put("productName", service.getType());
                    productDetails.put("quantity", orderDetail.getQuantity());
                    productDetails.put("productPrice", orderDetail.getPrice());
                    productDetails.put("productImg", service.getPhotoPath());
                    productDetails.put("nurseryName", nurseryRepo.findNameById(orderDetail.getNurseryId()));
                    Feedback feedback = feedbackRepo.findByUserIdAndProductId(userId, service.getId());
                    if (feedback != null) {
                        productDetails.put("reviewStars", feedback.getRating());
                    }
                }
                productDetailsList.add(productDetails);
            }
            singleOrderDetails.put("products", productDetailsList);
            ordersToReturn.add(singleOrderDetails);
        }
        return ResponseEntity.ok().body(ordersToReturn);
    }

    public ResponseEntity getOrdersOfNursery(String apiKey, String authorizationHeader) {
        String email = jwtUtil.getEmail(authorizationHeader);
        int nurseryId = nurseryRepo.findIdByEmail(email);

        List<OrderDetails> ordersOfNursery = orderDetailsRepo.findByNurseryId(nurseryId);
        List<OrderDetails> orderDetailsWithUniqueOrderId = orderDetailsRepo.findRowsWithUniqueOrderId(nurseryId);

        List<Map> allOrderDetails = new ArrayList<>();

        for (OrderDetails orderDetail :
                orderDetailsWithUniqueOrderId) {
            Map<String, Object> orderItem = new HashMap<>();
            System.out.println(198);
            System.out.println(orderDetail.getOrderId());
            Order orderObj = orderRepo.findById(orderDetail.getOrderId()).get();
            System.out.println(200);
            User user = userRepo.findById(orderObj.getUserId()).get();
            String customerName = user.getFname() + " " + user.getLname();

            List<String> productsList = new ArrayList<>();
            List<OrderDetails> orderDetailsList = orderDetailsRepo.findByOrderId(orderDetail.getOrderId());
            int quantity = 0;
            String itemName = null;
            String type = null;
            System.out.println(207);
            for (OrderDetails orderDetailItem :
                    orderDetailsList) {
                System.out.println(210);
                if (orderDetailItem.getProductId() != null) {
                    com.plantingio.server.Model.Product product = productRepo.findById(orderDetailItem.getProductId()).get();
                    itemName = product.getName();
                    System.out.println(214);
                } else {
                    Gardening service = gardeningRepo.findById(orderDetailItem.getServiceId()).get();
                    itemName = service.getType();
                    type = "Service";
                    System.out.println(219);
                }
                quantity += orderDetailItem.getQuantity();
                productsList.add(itemName);
                System.out.println(223);
            }

            System.out.println(225);
            orderItem.put("orderId", orderDetail.getOrderId());
            orderItem.put("products", productsList);
            orderItem.put("type", type);
            orderItem.put("totalQuantity", quantity);
            orderItem.put("customerName", customerName);
            orderItem.put("totalAmt", orderObj.getSubTotal());
            orderItem.put("orderDate", dateFormat.format(orderObj.getOrderDate()));
            orderItem.put("orderStatus", orderObj.getOrderStatus());

            System.out.println(235);
            Payment payment = paymentRepo.findByOrderIdAndNurseryId(orderDetail.getOrderId(), orderDetail.getNurseryId());
            Calendar cal = Calendar.getInstance();
            System.out.println(payment.getOrderId());
            Date orderDate = orderObj.getOrderDate();
            System.out.println(241);
            cal.setTime(orderDate);
            System.out.println(243);
            cal.add(Calendar.DATE, 15);
            System.out.println(245);
            orderItem.put("paymentDue", dateFormat.format(cal.getTime()));
            System.out.println(247);
            orderItem.put("paymentStatus", payment.getStatus());
            System.out.println(249);
            allOrderDetails.add(orderItem);
            System.out.println(251);
        }
        return ResponseEntity.ok().body(allOrderDetails);

    }

    public ResponseEntity getOrderById(String apiKey, String authorizationHeader, int id) throws StripeException {
        String email = jwtUtil.getEmail(authorizationHeader);
        int nurseryId = nurseryRepo.findIdByEmail(email);

        Order order = orderRepo.getById(id);
        List<OrderDetails> orderDetailsList = orderDetailsRepo.findByOrderIdAndNurseryId(id, nurseryId);

        Map<String, Object> orderInfo = new HashMap<>();
        orderInfo.put("orderId", id);
        orderInfo.put("orderDate", dateFormat.format(order.getOrderDate()));

        Stripe.apiKey = apiKey;
        Session session = Session.retrieve(order.getSessionId());

        orderInfo.put("shippingName", session.getShipping().getName());
        orderInfo.put("phoneNo", session.getCustomerDetails().getPhone());
        orderInfo.put("email", session.getCustomerDetails().getEmail());
        Address stripeAddressObj = session.getShipping().getAddress();
        String address = stripeAddressObj.getLine1() + ", " + stripeAddressObj.getLine2() + ", " + stripeAddressObj.getCity() + "," + stripeAddressObj.getState() + " - " + stripeAddressObj.getPostalCode();
        orderInfo.put("shippingAddress", address);

//        this is wrong, it will send the amounts for whole order and not for one nursery
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
            if (orderDetail.getProductId() != null) {
                com.plantingio.server.Model.Product product = productRepo.findById(orderDetail.getProductId()).get();
                itemName = product.getName();
                imageUrl = product.getPhotoPath().get(0);
                pricePerUnit = product.getPrice().subtract(product.getDiscount());
            } else {
                Gardening service = gardeningRepo.findById(orderDetail.getServiceId()).get();
                itemName = service.getType();
                pricePerUnit = service.getPrice().subtract(service.getDiscount());
                imageUrl = service.getPhotoPath();
            }

            totalAmt = pricePerUnit.multiply(BigDecimal.valueOf(quantity));
            productInfo.put("itemName", itemName);
            productInfo.put("imageUrl", imageUrl);
            productInfo.put("pricePerUnit", pricePerUnit);
            productInfo.put("quantity", quantity);
            productInfo.put("totalAmt", totalAmt);

            products.add(productInfo);
        }
        orderInfo.put("products", products);

        return ResponseEntity.ok().body(orderInfo);
    }
}
