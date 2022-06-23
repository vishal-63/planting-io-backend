package com.plantingio.server.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantingio.server.Model.Gardening;
import com.plantingio.server.Model.Product;
import com.plantingio.server.Service.GardeningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/service")
@CrossOrigin(origins = {"http://localhost:3000/", "http://localhost:5000/"})
public class GardeningController {
    private final GardeningService gardeningService;

    @Autowired
    public GardeningController(GardeningService serviceService) {
        this.gardeningService = serviceService;
    }

    @PostMapping("/add")
    public ResponseEntity addService(@RequestParam("service") String model,
                                     @RequestPart("file") MultipartFile file,
                                     @RequestHeader("Authorization") String authorizationHeader) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Gardening service = mapper.readValue(model, Gardening.class);
        System.out.println(service);
        return gardeningService.addService(service, file, authorizationHeader);
    }

    @GetMapping("/get-all")
    public ResponseEntity getAllServices () {
        return gardeningService.getAllServices();
    }
    @GetMapping("/get")
    public ResponseEntity<?> getServicesOfNursery(@RequestHeader("Authorization") String authorizationHeader) {
        return gardeningService.getServicesOfNursery(authorizationHeader);
    }
    @GetMapping("/get/{id}")
    public ResponseEntity getServiceById (@PathVariable int id,
                                          @RequestHeader("Authorization") String authorizationHeader) {
        return gardeningService.getServiceById(id, authorizationHeader);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity updateService(@PathVariable int id,
                                        @RequestParam("service") String model,
                                        @RequestPart(value = "file", required = false) MultipartFile file,
                                        @RequestHeader("Authorization") String authorizationHeader) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Gardening serviceReq = objectMapper.readValue(model, Gardening.class);
        return gardeningService.updateService(id, serviceReq, file, authorizationHeader);
    }

    @PutMapping(path = "/update/{id}/delete-photo")
    public ResponseEntity deleteProductPhoto(@PathVariable int id,
                                             @RequestHeader("Authorization") String authorizationHeader) {
        return gardeningService.deleteServicePhoto(id, authorizationHeader);
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity deactivateService(@PathVariable int id,
                                            @RequestHeader("Authorization") String authorizationHeader) {
        return gardeningService.deactivateService(id, authorizationHeader);
    }
}
