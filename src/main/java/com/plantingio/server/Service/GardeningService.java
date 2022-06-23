package com.plantingio.server.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantingio.server.Model.Gardening;
import com.plantingio.server.Repo.NurseryRepo;
import com.plantingio.server.Repo.GardeningRepo;
import com.plantingio.server.Repo.UserRepo;
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
public class GardeningService {
    private final GardeningRepo gardeningRepo;
    private final JwtUtil jwtUtil;
    private final NurseryRepo nurseryRepo;
    private final UserRepo userRepo;
    private final CloudinaryService cloudinaryService;

    @Autowired
    public GardeningService(GardeningRepo gardeningRepo, JwtUtil jwtUtil, NurseryRepo nurseryRepo, UserRepo userRepo, CloudinaryService cloudinaryService) {
        this.gardeningRepo = gardeningRepo;
        this.jwtUtil = jwtUtil;
        this.nurseryRepo = nurseryRepo;
        this.userRepo = userRepo;
        this.cloudinaryService = cloudinaryService;
    }

    public ResponseEntity addService(Gardening service, MultipartFile file, String authorizationHeader) {
        String nurseryEmail = jwtUtil.getEmail(authorizationHeader);
        Integer nurseryId = nurseryRepo.findIdByEmail(nurseryEmail);

        String url;

        if (service.getType() != null && service.getType().length() > 0 &&
                service.getDetails() != null && service.getDetails().length() > 30 &&
                service.getPrice() != null &&
                service.getDiscount() != null &&
                file != null && !file.isEmpty()
        ) {
            if (Arrays.asList(IMAGE_JPEG + "", IMAGE_PNG + "").contains(file.getContentType())) {
                url = cloudinaryService.uploadFile(file, "services" + service.getType());
            } else
                throw new IllegalStateException("File must be of JPG/PNG format!");

            service.setPhotoPath(url);
            service.setNurseryId(nurseryId);
            service.setIs_active(true);
            gardeningRepo.save(service);
        } else {
            throw new IllegalArgumentException("Invalid parameters!");
        }
        return ResponseEntity.ok().body(Map.of(
                "message", "Service added successfully!"
        ));
    }

    public ResponseEntity<?> getServicesOfNursery(String authorizationHeader) {
        String nurseryEmail = jwtUtil.getEmail(authorizationHeader);
        int nurseryId = nurseryRepo.findIdByEmail(nurseryEmail);
        List<Gardening> serviceList = null;
        try {
            serviceList = gardeningRepo.findByNurseryId(nurseryId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return ResponseEntity.ok().body(serviceList);
    }

    public ResponseEntity getServiceById(int id, String authorizationHeader) {
        String email = jwtUtil.getEmail(authorizationHeader);
        int nurseryId = nurseryRepo.findIdByEmail(email);
        Optional<Gardening> service = gardeningRepo.findById(id);
        if (!service.isPresent()) {
            throw new IllegalStateException("Service with id " + id + " does not exist");
        }
        if (nurseryId == service.get().getNurseryId()) {
            return ResponseEntity.ok().body(service.get());
        } else {
            throw new IllegalArgumentException("Unauthorized Request!");
        }
    }

    public ResponseEntity updateService(int id, Gardening serviceReq, MultipartFile file, String authorizationHeader) {
        String email = jwtUtil.getEmail(authorizationHeader);
        int nurseryId = nurseryRepo.findIdByEmail(email);
        Optional<Gardening> serviceOptional = gardeningRepo.findById(id);

        String url = null;
        if (serviceOptional.isPresent()) {
            Gardening service = serviceOptional.get();
            if (service.getNurseryId() == nurseryId) {

                if (serviceReq.getPrice() != null) service.setPrice(serviceReq.getPrice());
                else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid price!");

                if (serviceReq.getDiscount() != null) service.setDiscount(serviceReq.getDiscount());
                else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid discount amount!");

                if (serviceReq.getDetails() != null && serviceReq.getDetails().length() > 30 && serviceReq.getDetails().length() <= 500)
                    service.setDetails(serviceReq.getDetails());
                else return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid details!");
                System.out.println(file);
                if (file != null) {
                    System.out.println(110);
                    if (!file.isEmpty()) {
                        System.out.println(112);

                        if (Arrays.asList(IMAGE_JPEG + "", IMAGE_PNG + "").contains(file.getContentType())) {
                            url = cloudinaryService.uploadFile(file, "services/" + serviceReq.getType());

                        } else {
                            throw new IllegalStateException("File must be of JPG/PNG format!");
                        }

                    } else {
                        throw new IllegalStateException("Cannot upload empty file [" + file.getSize() + "]");
                    }

                    service.setPhotoPath(url);
                }

                gardeningRepo.save(service);
                return ResponseEntity.ok().body("Service info update successfully!");

            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized request!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Invalid service id!");
        }

    }


    @Transactional
    public ResponseEntity deleteServicePhoto(int id, String authorizationHeader) {
        String email = jwtUtil.getEmail(authorizationHeader);
        int nurseryId = nurseryRepo.findIdByEmail(email);

        Optional<Gardening> optionalService = gardeningRepo.findById(id);

        if (optionalService.isPresent()) {
            Gardening service = optionalService.get();
            if (service.getNurseryId() != nurseryId)
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "Unauthorized request!"));

            try {
                service.setPhotoPath(null);
                gardeningRepo.save(service);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

            return ResponseEntity.ok().body("Photo deleted!");
        } else {
            throw new IllegalStateException("Product with id " + id + " does not exist!");
        }
    }

    public ResponseEntity getAllServices() {
        List<Gardening> services = gardeningRepo.findAll();

        List<Map> servicesList = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for (Gardening service :
                services) {
            Map<String, Object> map = mapper.convertValue(service, Map.class);
            map.put("nurseryName", nurseryRepo.findNameById(service.getNurseryId()));
            map.put("stars", Math.round(Math.random()*(5-3.5+1) + 3.5));
            map.put("feedback", Math.round(Math.random()*(10-7+1)+7) );
            map.put("reviewCount", 0);
            servicesList.add(map);
        }

//        Map<Integer, List<ServiceResponse>> servicesPerId = services.stream().collect(Collectors.groupingBy(ServiceResponse::getNurseryId));
//
//        for (var entry :
//                servicesPerId.entrySet()) {
//            for (Gardening service :
//                    entry.getValue()) {
//                Map<String, Object> map = mapper.convertValue(service, Map.class);
//                map.put("nurseryName", nurseryRepo.findNameById(service.getNurseryId()));
//                entry.getValue().add(map);
//            }
//        }

        return ResponseEntity.ok().body(servicesList);
    }

    @Transactional
    public ResponseEntity deactivateService(int id, String authorizationHeader) {
        String email = jwtUtil.getEmail(authorizationHeader);
        int adminId = userRepo.findIdByEmail(email);
        boolean isAdmin = userRepo.findById(adminId).get().isIs_admin();
        if(isAdmin) {
            Optional<Gardening> service = gardeningRepo.findById(id);
            if(service.isPresent()) {
                service.get().setIs_active(false);
                gardeningRepo.save(service.get());
                return ResponseEntity.ok().body("Service deactivated");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Service with id " + id + " not found!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized request!");
        }
    }
}
