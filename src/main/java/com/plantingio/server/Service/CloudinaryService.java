package com.plantingio.server.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinaryConfig;

    @Autowired
    public CloudinaryService(Cloudinary cloudinaryConfig) {
        this.cloudinaryConfig = cloudinaryConfig;
    }

    public String uploadFile(MultipartFile file, String folder) {
        try {
            File uploadedFile = convertMultipartToFile(file);
            Map uploadResult = cloudinaryConfig.uploader().upload(uploadedFile, ObjectUtils.asMap(
                    "folder", folder
            ));
            boolean isDeleted = uploadedFile.delete();
            return  uploadResult.get("url").toString();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public String uploadFile(MultipartFile file) {
        try {
            File uploadedFile = convertMultipartToFile(file);
            Map uploadResult = cloudinaryConfig.uploader().upload(uploadedFile, ObjectUtils.emptyMap());
            boolean isDeleted = uploadedFile.delete();
            if (isDeleted)
                System.out.println("File successfully deleted");
            else
                System.out.println("File doesn't exist");
            return  uploadResult.get("url").toString();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private File convertMultipartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }
}
