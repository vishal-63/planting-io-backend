package com.plantingio.server.Service;

import com.plantingio.server.Model.Nursery;
import com.plantingio.server.Repo.NurseryRepo;
import com.plantingio.server.Repo.UserRepo;
import com.plantingio.server.Utility.AuthenticationReq;
import com.plantingio.server.Utility.JwtUtil;
import com.plantingio.server.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Optional;

@Service
public class NurseryService {

    private final NurseryRepo nurseryRepo;
    private final UserRepo userRepo;
    private final Validation validation;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public NurseryService(NurseryRepo nurseryRepo, UserRepo userRepo, Validation validation, JwtUtil jwtUtil, PasswordEncoder passwordEncoder, CloudinaryService cloudinaryService) {
        this.nurseryRepo = nurseryRepo;
        this.userRepo = userRepo;
        this.validation = validation;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.cloudinaryService = cloudinaryService;
    }

    public ResponseEntity addNursery(Nursery nursery) {
        if(nurseryRepo.findByEmail(nursery.getEmail()) == null) {
            String name = nursery.getName().trim();
            String email = nursery.getEmail().trim();
            String phone = nursery.getPhone()+"";
            String address = nursery.getAddress().trim();
            String city = nursery.getCity().trim();
            String state = nursery.getState().trim();
            String country = nursery.getCountry().trim();
            String pincode = nursery.getPincode()+"";
            String password = nursery.getPassword();

            if(name != null && name.length()>0 &&
                    validation.isEmailValid(email) &&
                    validation.isPhoneValid(phone) &&
                    address != null && address.length() > 0 &&
                    city != null && city.length() > 0 &&
                    pincode != null && pincode.length() == 6 &&
                    state != null && state.length() > 0 &&
                    country != null && country.length() > 0 &&
                    validation.isPasswordValid(password)
            ) {
                nursery.setPassword(passwordEncoder.encode(nursery.getPassword()));
                nursery.setIs_active(true);
                String jwt = jwtUtil.generateToken(email);
                nurseryRepo.save(nursery);
                return ResponseEntity.ok().body(Map.of(
                        "message", "Nursery registered successfully",
                        "jwt", jwt
                ));
            } else {
                throw new IllegalArgumentException("Invalid parameters!");
            }
        } else {
            throw new IllegalArgumentException("Nursery email already exists!");
        }
    }

    public ResponseEntity loginNursery(AuthenticationReq req) {
        if(validation.isEmailValid(req.getEmail())) {
            Nursery nursery = nurseryRepo.findByEmail(req.getEmail());

            if(nursery != null) {

                if(passwordEncoder.matches(req.getPassword(), nursery.getPassword())) {
                    String jwt = jwtUtil.generateToken(nursery.getEmail());

                    return ResponseEntity.ok().body(Map.of(
                            "message", "Login Successful",
                            "jwt", jwt,
                            "name", nursery.getName()
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
    public ResponseEntity uploadVerificationDoc(String type, MultipartFile file, String authorizationHeader) throws IOException {
        String token = authorizationHeader.substring("Bearer ".length());
        String email = jwtUtil.getSubject(token);
        Nursery nursery = nurseryRepo.findByEmail(email);
        if(nursery != null) {

            nursery.setDocType(type);
            nurseryRepo.save(nursery);
            return ResponseEntity.ok().body("Verification document updated successfully");
        } else {
            throw new IllegalStateException("Invalid jwt token!");
        }


    }

    public ResponseEntity<Nursery> getNurseryDetails(String authorizationHeader) {
        String token = authorizationHeader.substring("Bearer ".length());
        String email = jwtUtil.getSubject(token);
        Nursery nursery = nurseryRepo.findByEmail(email);
        if(nursery != null) {
            return ResponseEntity.ok().body(nursery);
        } else {
            throw new IllegalArgumentException("Invalid jwt token!");
        }
    }

    @Transactional
    public ResponseEntity updateNursery(Nursery nursery, String authorizationHeader) {
        String jwt = authorizationHeader.substring("Bearer ".length());
        String subject = jwtUtil.getSubject(jwt);
        Nursery existingNursery = nurseryRepo.findByEmail(subject);

        if(nursery.getEmail() != null) {
            if(validation.isEmailValid(nursery.getEmail())) {
                existingNursery.setEmail(nursery.getEmail());
            } else { throw new IllegalArgumentException("Invalid email!"); }
        }

        if(validation.isPhoneValid(nursery.getPhone()+"")) {
            existingNursery.setPhone(nursery.getPhone());
        } else { throw new IllegalArgumentException("Invalid phone no!"); }

        if (nursery.getAddress() != null) {
            if (nursery.getAddress().length() > 0) {

                existingNursery.setAddress(nursery.getAddress());
            } else {
                throw new IllegalArgumentException("Invalid address!");
            }
        }

        if (nursery.getCity() != null) {
            if (nursery.getCity().length() > 0) {

                existingNursery.setCity(nursery.getCity());
            } else {
                throw new IllegalArgumentException("Invalid city!");
            }
        }

            if (nursery.getPincode() > 99999 && nursery.getPincode() < 1000000) {

                existingNursery.setPincode(nursery.getPincode());
            } else {
                throw new IllegalArgumentException("Invalid pincode!");
            }

        if (nursery.getState() != null) {
            if (nursery.getState().length() > 0) {

                existingNursery.setState(nursery.getState());
            } else {
                throw new IllegalArgumentException("Invalid state!");
            }
        }

        if (nursery.getCountry() != null) {
            if (nursery.getCountry().length() > 0) {

                existingNursery.setCountry(nursery.getCountry());
            } else {
                throw new IllegalArgumentException("Invalid country!");
            }
        }

        nurseryRepo.save(existingNursery);


        return ResponseEntity.ok().body(Map.of(
                "message", "User Info updated successfully"
        ));
    }

    @Transactional
    public ResponseEntity uploadFile(MultipartFile file, String type, String authorizationHeader) {
        String email = jwtUtil.getEmail(authorizationHeader);
        Nursery nursery = nurseryRepo.findByEmail(email);

        if(nursery != null) {
            String url = cloudinaryService.uploadFile(file);
            nursery.setDocType(type);
            nursery.setDocPath(url);
            nurseryRepo.save(nursery);
            return ResponseEntity.created(URI.create(url)).body("File uploaded successfully!");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Requested nursery account not found!");
        }

    }

    @Transactional
    public ResponseEntity deactivateNursery(int id, String authorizationHeader) {
        String email = jwtUtil.getEmail(authorizationHeader);
        int adminId = userRepo.findIdByEmail(email);
        boolean isAdmin = userRepo.findById(adminId).get().isIs_admin();
        if(isAdmin) {
            Optional<Nursery> nurseryOptional = nurseryRepo.findById(id);
            if(nurseryOptional.isPresent()) {
                nurseryOptional.get().setIs_active(false);
                nurseryRepo.save(nurseryOptional.get());
                return ResponseEntity.ok().body("Nursery deactivated");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service with id " + id + " not found!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized request!");
        }
    }
}
