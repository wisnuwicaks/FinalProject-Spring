package com.cimb.finalproject.controller;

import com.cimb.finalproject.dao.ProductRepo;
import com.cimb.finalproject.entity.Product;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

@RestController
public class UploadFile {
    private String uploadPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\images\\";

    @Autowired
    private ProductRepo productRepo;


    @GetMapping("/allproducts")
    public Iterable<Product> getProducts() {
        return productRepo.findAll();
    }

    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        return productRepo.save(product);
    }

    @PostMapping("/addproduct")
    public String uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("productData") String productString) throws JsonMappingException, JsonProcessingException {
        Date date = new Date();
        System.out.println(productString);
        Product product = new ObjectMapper().readValue(productString, Product.class);

        System.out.println(product.toString());
        // Register / POST product ke database, beserta dengan link ke imageLink

        String fileExtension = file.getContentType().split("/")[1];
        String newFileName = "PROD-" + date.getTime() + "." + fileExtension;

        // Get file's original name || can generate our own
        String fileName = StringUtils.cleanPath(newFileName);

        // Create path to upload destination + new file name
        Path path = Paths.get(StringUtils.cleanPath(uploadPath) + fileName);

        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/products/download/")
                .path(fileName).toUriString();

//        product.setImageLink(fileDownloadUri);

        productRepo.save(product);

        // http://localhost:8080/documents/download/PROD-123456.jpg

        return fileDownloadUri;
    }

//    @GetMapping("/download/{fileName:.+}")
//    public ResponseEntity<Object> downloadFile(@PathVariable String fileName){
//        Path path = Paths.get(uploadPath + fileName);
//        Resource resource = null;
//
//        try {
//            resource = new UrlResource(path.toUri());
//        } catch(MalformedURLException e) {
//            e.printStackTrace();
//        }
//
//        System.out.println("DOWNLOAD");
//
//        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//                .body(resource);
//    }

}
