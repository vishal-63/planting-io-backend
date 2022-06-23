package com.plantingio.server.Controller;

import com.plantingio.server.Model.User;
import com.plantingio.server.Service.UserService;
import com.plantingio.server.Utility.AuthenticationReq;
import com.plantingio.server.Utility.ChangePasswordReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = {"http://localhost:3000/", "http://localhost:5000/"})
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(path = "/add")
    public ResponseEntity<?> addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> loginUser(@RequestBody AuthenticationReq reqUser) {
        return userService.loginUser(reqUser);
    }

    @PostMapping("/login-admin")
    public ResponseEntity loginAdmin(@RequestBody AuthenticationReq reqAdmin) {
        return userService.loginAdmin(reqAdmin);
    }

    @GetMapping("/get-info")
    public ResponseEntity getUser(HttpServletRequest req) {
        return userService.getUser(req);
    }

    @PutMapping(path = "/update")
    public ResponseEntity updateUser(HttpServletRequest req,
                                          @RequestBody User user) {
        return userService.updateUser(req, user);
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity deactivateUser(@PathVariable int id,
                                         @RequestHeader("Authorization") String authorizationHeader) {
        return userService.deactivateUser(id, authorizationHeader);
    }

    @PutMapping("/change-password")
    public ResponseEntity changePassword (@RequestHeader("Authorization") String authorizationHeader,
                                          @RequestBody ChangePasswordReq changePasswordReq) {
        return userService.changePassword(authorizationHeader, changePasswordReq);
    }

    @PostMapping("/generate-otp")
    public ResponseEntity generateOtp (@RequestParam("email") String email) {
        return userService.generateOtp(email);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity verifyOtp (@RequestParam("otp") int otp) {
        return userService.verifyOtp(otp);
    }

    @PutMapping("/set-new-password")
    public ResponseEntity setPassword(@RequestParam("email") String email,
                                      @RequestParam("password") String password) {
        return userService.setPassword(email, password);
    }
}
