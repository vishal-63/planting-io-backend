package com.plantingio.server.Controller;

import com.plantingio.server.Model.Nursery;
import com.plantingio.server.Service.NurseryService;
import com.plantingio.server.Utility.AuthenticationReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/nursery")
@CrossOrigin(origins = {"http://localhost:3000/", "http://localhost:5000/"})
public class NurseryController {

    private final NurseryService nurseryService;

    @Autowired
    public NurseryController(NurseryService nurseryService) {
        this.nurseryService = nurseryService;
    }

    @PostMapping(path = "/add")
    public ResponseEntity addNursery(@RequestBody Nursery nursery) { return nurseryService.addNursery(nursery); }

    @PostMapping(path = "/login")
    public ResponseEntity loginNursery(@RequestBody AuthenticationReq req) { return nurseryService.loginNursery(req); }

    @PostMapping("/upload")
    public ResponseEntity uploadPhoto(@RequestParam("file")MultipartFile file,
                                      @RequestParam("type") String type,
                                      @RequestHeader("Authorization") String authorizationHeader) {
        System.out.println(file.getOriginalFilename());
        return nurseryService.uploadFile(file, type, authorizationHeader);
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity deactivateNursery(@PathVariable int id,
                                            @RequestHeader("Authorization") String authorizationHeader) {
        return nurseryService.deactivateNursery(id, authorizationHeader);
    }

    @GetMapping(path = "/get")
    public ResponseEntity<Nursery> getNurseryDetails(@RequestHeader("Authorization") String authorizationHeader) {
        return nurseryService.getNurseryDetails(authorizationHeader);
    }

    @PutMapping(path = "/update")
    public ResponseEntity updateNursery(@RequestBody Nursery nursery, @RequestHeader("Authorization") String authorizationHeader) {
        return nurseryService.updateNursery(nursery, authorizationHeader);
    }
}
