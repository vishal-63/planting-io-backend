package com.plantingio.server.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantingio.server.Model.Feedback;
import com.plantingio.server.Model.Gardening;
import com.plantingio.server.Model.Product;
import com.plantingio.server.Repo.*;
import com.plantingio.server.Utility.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.*;

import static org.apache.http.entity.ContentType.IMAGE_JPEG;
import static org.apache.http.entity.ContentType.IMAGE_PNG;

@Service
public class ProductService {

    private final ProductRepo productRepo;
    private final NurseryRepo nurseryRepo;
    private final FeedbackRepo feedbackRepo;
    private final UserRepo userRepo;
    private final JwtUtil jwtUtil;
    private final CloudinaryService cloudinaryService;

    private final TestRepo testRepo;

    @Autowired
    public ProductService(ProductRepo productRepo, NurseryRepo nurseryRepo, FeedbackRepo feedbackRepo, UserRepo userRepo, JwtUtil jwtUtil, CloudinaryService cloudinaryService, TestRepo testRepo) {
        this.productRepo = productRepo;
        this.nurseryRepo = nurseryRepo;
        this.feedbackRepo = feedbackRepo;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.cloudinaryService = cloudinaryService;
        this.testRepo = testRepo;
    }

    public ResponseEntity<List<Product>> getProductsOfNursery(String authorizationHeader) {
        String nurseryEmail = jwtUtil.getEmail(authorizationHeader);
        int nurseryId = nurseryRepo.findIdByEmail(nurseryEmail);
        List<Product> productList = null;
        try {
            productList = productRepo.findByNurseryId(nurseryId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok().body(productList);
    }

    public ResponseEntity getProductById(int id, String authorizationHeader) {
        String nurseryEmail = jwtUtil.getEmail(authorizationHeader);
        int nurseryId = nurseryRepo.findIdByEmail(nurseryEmail);
        Optional<Product> product = productRepo.findById(id);
        if (!product.isPresent()) {
            throw new IllegalStateException("Product with id " + id + " does not exist");
        }
        if (nurseryId == product.get().getNurseryId()) {
            return ResponseEntity.ok().body(product.get());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized Request!");
        }
    }

    @Transactional
    public ResponseEntity updateProduct(int id, Product productReq, MultipartFile[] files, String authorizationHeader) {
        String nurseryEmail = jwtUtil.getEmail(authorizationHeader);
        int nurseryId = nurseryRepo.findIdByEmail(nurseryEmail);
        Optional<Product> productOptional = productRepo.findById(id);
        List<String> urls = new ArrayList<>();
        String url;
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            if (product.getNurseryId() != nurseryId)
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Unauthorized request!"));

            try {
                if (productReq.getName() != null && productReq.getName().length() > 0)
                    product.setName(productReq.getName());

                if (productReq.getType() != null && productReq.getType().length() > 0)
                    product.setType(productReq.getType());

                if (productReq.getPrice() != null)
                    product.setPrice(productReq.getPrice());

                if (productReq.getDiscount() != null)
                    product.setDiscount(productReq.getDiscount());

                if (productReq.getQuantity() != null)
                    product.setQuantity(productReq.getQuantity());

                if (productReq.getDetails() != null && productReq.getDetails().length() > 0)
                    product.setDetails(productReq.getDetails());

                if (files != null) {
                    for (MultipartFile file :
                            files) {
                        System.out.println(99);
                        if (!file.isEmpty()) {
                            System.out.println(101);
                            if (Arrays.asList(IMAGE_JPEG + "", IMAGE_PNG + "").contains(file.getContentType())) {
                                url = cloudinaryService.uploadFile(file, "products/" + productReq.getType());
                                urls.add(url);
                                System.out.println(105);
                            } else {
                                throw new IllegalStateException("File must be of JPG/PNG format!");
                            }
                        } else {
                            throw new IllegalStateException("Cannot upload empty file [" + file.getSize() + "]");
                        }
                    }
                }

                System.out.println(113);
                List<String> existingUrls = product.getPhotoPath();
                System.out.println(115);
                if (existingUrls != null)
                    urls.addAll(existingUrls);
                System.out.println(117);
                product.setPhotoPath(urls);
                productRepo.save(product);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new IllegalStateException("Product with id " + id + " does not exist!");
        }
        return ResponseEntity.ok().body(Map.of(
                "message", "Product info updated successfully!"
        ));
    }

    public ResponseEntity addProduct(Product product, MultipartFile[] files, String authorizationHeader) {
        String email = jwtUtil.getEmail(authorizationHeader);
        int nurseryId = nurseryRepo.findIdByEmail(email);

        List<String> urls = new ArrayList<>();
        String url;
        if (productRepo.productExistsOfNursery(product.getName(), nurseryId) == 0) {
            if (product.getName() != null && product.getName().length() > 0 &&
                    product.getType() != null && product.getType().length() > 0 &&
                    product.getDetails() != null && product.getDetails().length() > 30 &&
                    product.getPrice() != null &&
                    product.getDiscount() != null &&
                    product.getQuantity() != null
            ) {
                if (files != null) {
                    for (MultipartFile file :
                            files) {
                        System.out.println(99);
                        if (!file.isEmpty()) {
                            System.out.println(101);
                            if (Arrays.asList(IMAGE_JPEG + "", IMAGE_PNG + "").contains(file.getContentType())) {
                                url = cloudinaryService.uploadFile(file, "products/" + product.getType());
                                urls.add(url);
                                System.out.println(105);
                            } else {
                                throw new IllegalStateException("File must be of JPG/PNG format!");
                            }
                        } else {
                            throw new IllegalStateException("Cannot upload empty file [" + file.getSize() + "]");
                        }
                    }
                }
                product.setNurseryId(nurseryId);
                product.setPhotoPath(urls);
                product.setActive(true);
                productRepo.save(product);
            } else {
                throw new IllegalArgumentException("Invalid parameters!");
            }
        } else {
            throw new IllegalArgumentException("Product with name " + product.getName() + " already exists");
        }
        return ResponseEntity.ok().body(Map.of(
                "message", "Product added successfullly!"
        ));
    }

    @Transactional
    public ResponseEntity deleteProductPhoto(int id, String path, String authorizationHeader) {
        String email = jwtUtil.getEmail(authorizationHeader);
        int nurseryId = nurseryRepo.findIdByEmail(email);

        Optional<Product> optionalProduct = productRepo.findById(id);

        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            if (product.getNurseryId() != nurseryId)
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Unauthorized request!"));

            try {
                List<String> urls = product.getPhotoPath();
                urls.remove(path);
                product.setPhotoPath(urls);
                productRepo.save(product);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
            return ResponseEntity.ok().body("Photo deleted!");
        } else {
            throw new IllegalStateException("Product with id " + id + " does not exist!");
        }
    }

    public ResponseEntity getProductsByType(String type) {
        List<Product> plants = productRepo.findByType(type);

        ObjectMapper mapper = new ObjectMapper();
        List<Map<String, Object>> plantsList = new ArrayList<>();
        for (Product plant :
                plants) {
            Map<String, Object> map = mapper.convertValue(plant, Map.class);
            map.put("nurseryName", nurseryRepo.findNameById(plant.getNurseryId()));
            List<Feedback> feedbackList = feedbackRepo.findByProductId(plant.getId());
            int ratings = 0;
            for (Feedback feedback :
                    feedbackList) {
                ratings += feedback.getRating();
            }
            int reviewCount = feedbackList.size();
            if (reviewCount == 0) reviewCount = 1;

            map.put("stars", ratings / reviewCount);
            map.put("reviewCount", feedbackList.size());
            plantsList.add(map);
        }

        return ResponseEntity.ok().body(plantsList);
    }

    public ResponseEntity getProduct(int id) {
        Optional<Product> productOptional = productRepo.findById(id);

        ObjectMapper mapper = new ObjectMapper();

        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            String categoryLink;
            if (product.getType().equals("Plant")) categoryLink = "/shop-plants";
            else if (product.getType().equals("Tool")) categoryLink = "/shop-tools";
            else categoryLink = "/shop-seeds";

            Map<String, Object> map = mapper.convertValue(productOptional.get(), Map.class);
            map.put("stars", 0);
            map.put("nurseryName", nurseryRepo.findNameById(product.getNurseryId()));
            map.put("categoryLink", categoryLink);

            List<Feedback> feedbackList = feedbackRepo.findByProductId(product.getId());
            List<Map> reviewsList = new ArrayList<>();
            int ratings = 0;
            for (Feedback feedback :
                    feedbackList) {
                ratings += feedback.getRating();
                String description = feedback.getFeedbackDescription();
                if (description != null) {

                    String name = userRepo.findById(feedback.getUserId()).get().getFname() + " " + userRepo.findById(feedback.getUserId()).get().getLname();
                    Map<String, String> reviewMap = Map.of(
                            "userName", name,
                            "description", description
                    );
                    reviewsList.add(reviewMap);
                }
            }
            int starsCount = feedbackList.size();
            if (starsCount == 0) starsCount = 1;

            map.put("stars", ratings / starsCount);
            map.put("starsCount", starsCount);
            map.put("reviews", reviewsList);
            map.put("reviewCount", reviewsList.size());
            map.remove("nurseryId");
            System.out.println(map);
            return ResponseEntity.ok().body(map);
        } else
            throw new IllegalStateException("Product with id " + id + " not found!");
    }

    public ResponseEntity getRelatedProducts(String type, int id) {
        Random random = new Random();
        List<Product> productsByType = productRepo.findByType(type);
        List<Product> relatedProducts = new ArrayList<>();

        try {
            while (relatedProducts.size() < 4) {
                int randomIndex = random.nextInt(productsByType.size());
                Product product = productsByType.get(randomIndex);
                if (product.getId() != id)
                    relatedProducts.add(product);
                productsByType.remove(randomIndex);
            }

        } catch (Exception e) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

        return ResponseEntity.ok().body(relatedProducts);
    }

    @Transactional
    public ResponseEntity deactivateProduct(int id, String authorizationHeader) {
        String email = jwtUtil.getEmail(authorizationHeader);
        int adminId = userRepo.findIdByEmail(email);
        boolean isAdmin = userRepo.findById(adminId).get().isIs_admin();
        if(isAdmin) {
            Optional<Product> productOptional = productRepo.findById(id);
            if(productOptional.isPresent()) {
                productOptional.get().setActive(false);
                productRepo.save(productOptional.get());
                return ResponseEntity.ok().body("Product deactivated");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service with id " + id + " not found!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized request!");
        }
    }

    public ResponseEntity deactivateProductByNursery(int id, String authorizationHeader) {
        String email = jwtUtil.getEmail(authorizationHeader);
        int nurseryId = nurseryRepo.findIdByEmail(email);

            Optional<Product> productOptional = productRepo.findById(id);
            if(productOptional.isPresent()) {
                if(productOptional.get().getNurseryId() == nurseryId) {
                productOptional.get().setActive(false);
                productRepo.save(productOptional.get());
                return ResponseEntity.ok().body("Product deactivated");

                } else
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized Request!");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service with id " + id + " not found!");
            }

    }
}
