package com.plantingio.server.Controller;

import com.fasterxml.classmate.members.ResolvedParameterizedMember;
import com.plantingio.server.Model.Cart;
import com.plantingio.server.Service.CartService;
import com.plantingio.server.Utility.CartReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = {"http://localhost:3000/", "http://localhost:5000/"})
public class CartController {

    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/get")
    public ResponseEntity getCartItems (@RequestHeader("Authorization") String authorizationHeader) {
        return cartService.getCartItems(authorizationHeader);
    }

    @PostMapping("/add")
    public ResponseEntity addToCart (@RequestHeader("Authorization") String authorizationHeader ,@RequestBody CartReq cartReq) {
        System.out.println(cartReq.getItemId() + " " + cartReq.getType() + " " + cartReq.getNoOfItems());
        return cartService.addToCart(authorizationHeader, cartReq);
    }

    @PutMapping("/delete/{itemId}")
    public ResponseEntity deleteItemFromCart (@RequestHeader("Authorization") String authorizationHeader, @PathVariable("itemId") Integer id) {
        return cartService.deleteItemFromCart(authorizationHeader, id);
    }
}
