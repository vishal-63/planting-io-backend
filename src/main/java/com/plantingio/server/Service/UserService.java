package com.plantingio.server.Service;

import com.plantingio.server.Model.User;
import com.plantingio.server.Repo.UserRepo;
import com.plantingio.server.Utility.AuthenticationReq;
import com.plantingio.server.Utility.ChangePasswordReq;
import com.plantingio.server.Utility.JwtUtil;
import com.plantingio.server.Validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.*;

@Service
public class UserService {

    private final Validation validation;
    private final UserRepo userRepo;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Autowired
    public UserService(Validation validation, UserRepo userRepo, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.validation = validation;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public List<User> getUsers() {
        return userRepo.findAll();
    }

    public ResponseEntity<?> addUser(User user) {
        Optional<User> userOptional = userRepo.findByEmail(user.getEmail());
        if (!userOptional.isPresent()) {

            String fname = user.getFname().trim();
            String lname = user.getLname().trim();
            String email = user.getEmail().trim();
            String password = user.getPassword().trim();
            String phone = user.getPhone() + "".trim();

            if (fname != null && fname.length() > 0 &&
                    lname != null && lname.length() > 0 &&
                    validation.isEmailValid(email) &&
                    validation.isPhoneValid(phone) &&
                    validation.isPasswordValid(password)) {

                String encryptedPassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(encryptedPassword);

                user.setIs_active(true);
                userRepo.save(user);
                String jwt = jwtUtil.generateToken(email);

                return ResponseEntity.ok().body(Map.of(
                        "message", "User registered successfully",
                        "jwt", jwt));
            } else {
                throw new IllegalArgumentException("Invalid input(s)");
            }
        } else {
            throw new IllegalStateException("User already exists");
        }
    }

    public ResponseEntity loginUser(AuthenticationReq reqUser) {

        if (validation.isEmailValid(reqUser.getEmail())) {
            Optional<User> user = userRepo.findByEmail(reqUser.getEmail());

            if (user.isPresent()) {
                User existingUser = user.get();

                if (passwordEncoder.matches(reqUser.getPassword(), existingUser.getPassword())) {
                    String jwt = jwtUtil.generateToken(existingUser.getEmail());

                    return ResponseEntity.ok().body(Map.of(
                            "message", "Login Successful",
                            "jwt", jwt
                    ));

                } else {
                    throw new IllegalArgumentException("Incorrect password");
                }

            } else {
                throw new IllegalStateException("Email does not exist");
            }
        } else {
            throw new IllegalArgumentException("Invalid email address");
        }
    }

    @Transactional
    public ResponseEntity updateUser(HttpServletRequest req, User user) {
        final String authorizationHeader = req.getHeader("Authorization");
        String jwt = authorizationHeader.substring("Bearer ".length());
        String subject = jwtUtil.getSubject(jwt);
        User existingUser = userRepo.findByEmail(subject)
                .orElseThrow(() -> new IllegalStateException("User does not exist"));

        System.out.println(user.getFname());
        if (user.getFname() != null) {
            if (user.getFname().length() > 0) {

                existingUser.setFname(user.getFname());
                System.out.println(existingUser.getFname());
            } else {
                throw new IllegalArgumentException("Invalid first name!");
            }
        }

        if (user.getLname() != null) {
            if (user.getLname().length() > 0) {
                existingUser.setLname(user.getLname());
            } else {
                throw new IllegalArgumentException("Invalid last name!");
            }
        }

        if (user.getEmail() != null) {
            if (validation.isEmailValid(user.getEmail())) {
                existingUser.setEmail(user.getEmail());
            } else {
                throw new IllegalArgumentException("Invalid email!");
            }
        }


        System.out.println(user.getPhone());
        if (validation.isPhoneValid(user.getPhone() + "")) {
            existingUser.setPhone(user.getPhone());
        } else {
            throw new IllegalArgumentException("Invalid phone no!");
        }

        userRepo.save(existingUser);


        return ResponseEntity.ok().body(Map.of(
                "message", "User Info updated successfully"
        ));
    }

    public ResponseEntity getUser(HttpServletRequest req) {
        final String authorizationHeader = req.getHeader("Authorization");
        String jwt = authorizationHeader.substring("Bearer ".length());
        String email = jwtUtil.getSubject(jwt);
        Optional<User> user = userRepo.findByEmail(email);

        if (user.isPresent()) {
            return ResponseEntity.ok().body(user);
        } else {
            throw new IllegalArgumentException("Invalid user email");
        }

    }

    public ResponseEntity loginAdmin(AuthenticationReq reqAdmin) {
        Optional<User> adminOptional = userRepo.findByEmail(reqAdmin.getEmail());

        if (adminOptional.isPresent()) {
            User user = adminOptional.get();

            if (user.isIs_admin()) {
                if (passwordEncoder.matches(reqAdmin.getPassword(), user.getPassword())) {
                    String jwt = jwtUtil.generateToken(user.getEmail());

                    return ResponseEntity.ok().body(Map.of(
                            "message", "Login Successful",
                            "jwt", jwt
                    ));

                } else {
                    throw new IllegalArgumentException("Incorrect password");
                }
            } else {
                throw new IllegalArgumentException("Unauthorized request!");
            }
        } else {
            throw new IllegalStateException("Admin with given email not found!");
        }
    }

    @Transactional
    public ResponseEntity deactivateUser(int id, String authorizationHeader) {
        String email = jwtUtil.getEmail(authorizationHeader);
        int adminId = userRepo.findIdByEmail(email);
        boolean isAdmin = userRepo.findById(adminId).get().isIs_admin();
        if (isAdmin) {
            Optional<User> user = userRepo.findById(id);
            if (user.isPresent()) {
                user.get().setIs_active(false);
                userRepo.save(user.get());
                return ResponseEntity.ok().body("User deactivated");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service with id " + id + " not found!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized request!");
        }
    }

    @Transactional
    public ResponseEntity changePassword(String authorizationHeader, ChangePasswordReq changePasswordReq) {
        String email = jwtUtil.getEmail(authorizationHeader);
        User user = userRepo.findByEmail(email).get();

        if (passwordEncoder.matches(changePasswordReq.getOldPassword(), user.getPassword())) {
            if (validation.isPasswordValid(changePasswordReq.getNewPassword()) &&
                    validation.isPasswordValid(changePasswordReq.getConfirmPassword())) {
                if (changePasswordReq.getNewPassword().equals(changePasswordReq.getConfirmPassword())) {
                    user.setPassword(passwordEncoder.encode(changePasswordReq.getNewPassword()));
                    userRepo.save(user);
                    return ResponseEntity.ok().body("Password changed!");
                } else
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords must match!");
            } else
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid new password!");
        } else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Incorrect old password!");
    }

    private int otp;
    private Date expiresAt;

    private int generateNewOtp() {
        Random random = new Random();
        otp = 100000 + random.nextInt(900000);
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE, 5);
        expiresAt = cal.getTime();
        return otp;
    }

    public ResponseEntity generateOtp(String email) {

        int newOtp = generateNewOtp();
        String message = "Your One-time-password is: " + newOtp + "\nThis OTP will be valid for only 5 minutes.";
        String subject = "OTP for Forgotten Password";
        emailService.sendSimpleMessage(email, subject, message);
        return ResponseEntity.ok().body("Email sent!");

    }

    public ResponseEntity verifyOtp(int otpReq) {
        if(otp == otpReq)
            return ResponseEntity.ok().body("Otp verified!");
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid otp!");
    }

    @Transactional
    public ResponseEntity setPassword(String email, String password) {
        if(validation.isPasswordValid(password)) {
            User user = userRepo.findByEmail(email).get();
            user.setPassword(passwordEncoder.encode(password));
            userRepo.save(user);
            return ResponseEntity.ok().body("Password saved!");
        }else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid password");
    }
}
