package com.plantingio.server.Controller;

import com.itextpdf.text.DocumentException;
import com.plantingio.server.Utility.GeneratePDF;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;


@RestController
@RequestMapping("/api/report")
@CrossOrigin(origins = {"http://localhost:3000/", "http://localhost:5000/"})
public class ReportController {

    @GetMapping(value = "/test", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> testPdf (@RequestHeader("User-Agent") String useragent,
                                                        @RequestHeader("Accept") String accept) {
        try {
            ByteArrayInputStream byteArrayInputStream = GeneratePDF.generate();
            var headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=testreport.pdf");
            headers.add("Set-Cookie","name=vishal");
            Cookie cookie = new Cookie("name", "vishal");
//            res.addCookie(cookie);
            return new ResponseEntity<>(new InputStreamResource(byteArrayInputStream), headers, 200);
//            ResponseEntity
//                    .ok()
//                    .headers(headers)
//                    .contentType(MediaType.APPLICATION_PDF)
//                    .body(new InputStreamResource(byteArrayInputStream));
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }
}
