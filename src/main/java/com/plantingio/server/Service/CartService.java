package com.plantingio.server.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantingio.server.Model.Cart;
import com.plantingio.server.Model.Gardening;
import com.plantingio.server.Model.Product;
import com.plantingio.server.Repo.*;
import com.plantingio.server.Utility.CartReq;
import com.plantingio.server.Utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

@Service
public class CartService {

    private final CartRepo cartRepo;
    private final UserRepo userRepo;
    private final ProductRepo productRepo;
    private final GardeningRepo gardeningRepo;
    private final NurseryRepo nurseryRepo;
    private final JwtUtil jwtUtil;

    @Autowired
    public CartService(CartRepo cartRepo, UserRepo userRepo, ProductRepo productRepo, GardeningRepo gardeningRepo, NurseryRepo nurseryRepo, JwtUtil jwtUtil) {
        this.cartRepo = cartRepo;
        this.userRepo = userRepo;
        this.productRepo = productRepo;
        this.gardeningRepo = gardeningRepo;
        this.nurseryRepo = nurseryRepo;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public ResponseEntity addToCart(String authorizationHeader, CartReq cartReq) {

        try {
            String email = jwtUtil.getEmail(authorizationHeader);
            System.out.println(authorizationHeader);
            int userId = userRepo.findIdByEmail(email);

            if (userRepo.existsById(userId)) {

                Cart cart = new Cart();
                List<String> cartItems = new ArrayList<>();
                List<String> itemTypes = new ArrayList<>();
                List<String> noOfItems = new ArrayList<>();

//                Check if user id already present in the cart table
                Optional<Cart> optionalCart = cartRepo.findByUserId(userId);
                if (optionalCart.isPresent()) {
                    cart = optionalCart.get();
                    cartItems = cart.getCartItems();
                    itemTypes = cart.getItemType();
                    noOfItems = cart.getNoOfItems();
                }

//                    Check if product not already present in the cart
//                System.out.println(cartItems+ ", " + cartReq.getItemId() + ", " + cartItems.contains(cartReq.getItemId().toString()));
                if (!cartItems.contains(cartReq.getItemId().toString())) {

                    cartItems.add(cartReq.getItemId().toString());
                    itemTypes.add(cartReq.getType());
                    noOfItems.add(cartReq.getNoOfItems().toString());

//                      Add new item to already existing cart items
                    cart.setCartItems(cartItems);
                    cart.setItemType(itemTypes);
                    cart.setNoOfItems(noOfItems);
                    cart.setUserId(userId);
                    cartRepo.save(cart);

                    return ResponseEntity.ok().body("Item added to cart!");

                } else {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Product already present in the cart");
                }

            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    public ResponseEntity getCartItems(String authorizationHeader) {
        String email = jwtUtil.getEmail(authorizationHeader);
        int userId = userRepo.findIdByEmail(email);
        Optional<Cart> cartOptional = cartRepo.findByUserId(userId);

        List<Map> cartItems = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        int quantity;
        BigDecimal price;
        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();

            for (int i = 0; i < cart.getCartItems().size(); i++) {
                Integer itemId = Integer.valueOf(cart.getCartItems().get(i));

                if (cart.getItemType().get(i).equals("Product")) {
                    Product product = productRepo.findById(itemId).get();
                    price = product.getPrice().subtract(product.getDiscount());
                    map = mapper.convertValue(product, Map.class);
                    map.put("nurseryName", nurseryRepo.findNameById(product.getNurseryId()));
                } else {
                    Gardening service = gardeningRepo.findById(itemId).get();
                    price = service.getPrice().subtract(service.getDiscount());
                    map = mapper.convertValue(gardeningRepo.findById(itemId).get(), Map.class);
                    map.put("nurseryName", nurseryRepo.findNameById(service.getNurseryId()));
                }
                quantity = Integer.parseInt(cart.getNoOfItems().get(i));

                map.put("quantity", quantity);
                map.put("price", price);
                cartItems.add(map);
            }

            return ResponseEntity.ok().body(cartItems);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cart is empty!");
        }

    }

    @Transactional
    public ResponseEntity deleteItemFromCart(String authorizationHeader, Integer id) {
        String email = jwtUtil.getEmail(authorizationHeader);
        int userId = userRepo.findIdByEmail(email);
        System.out.println(id);
        String itemId = id.toString();
        Optional<Cart> cartOptional = cartRepo.findByUserId(userId);
        if (cartOptional.isPresent()) {
            Cart cart = cartOptional.get();
            List<String> itemIds = cart.getCartItems();
            List<String> itemType = cart.getItemType();
            List<String> noOfItems = cart.getNoOfItems();
            System.out.println(itemIds);
            if (itemIds.contains(itemId)) {
                int index = itemIds.indexOf(itemId);
                itemIds.remove(itemId);
                itemType.remove(index);
                noOfItems.remove(index);

                cart.setCartItems(itemIds);
                cart.setItemType(itemType);
                cart.setNoOfItems(noOfItems);
                cartRepo.save(cart);
                return ResponseEntity.ok().body("Item deleted from cart!");
            } else {
                throw new IllegalArgumentException("Item id not present in the cart");
            }
        } else {
            throw new IllegalStateException("Cart is empty!");
        }
    }
}
