package com.cimb.finalproject.controller;

import com.cimb.finalproject.dao.*;
import com.cimb.finalproject.entity.*;
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

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@CrossOrigin
public class ProductController {

    private String uploadPath = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\product\\";


    @Autowired
    private CartRepo cartRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private ProductStockRepo productStockRepo;


    @GetMapping("/all_products")
    public Iterable<Product> getAllProduct() {
        return productRepo.findAll();
    }

    @GetMapping("/{productId}/category/{categoryId}")
    public Product addProductToCategory(@PathVariable int productId, @PathVariable int categoryId) {
        Product findProduct = productRepo.findById(productId).get();
        Category findCate = categoryRepo.findById(categoryId).get();

        findProduct.getCategories().add(findCate);
        return productRepo.save(findProduct);
    }


    @GetMapping("/{productId}")
    public Product getProductDetail(@PathVariable int productId) {
        Product findProduct = productRepo.findById(productId).get();
        return findProduct;
    }

    @PutMapping("/{productId}/delete_category/{categoryId}")
    public Product removeCategoryFromProduct(@PathVariable int productId, @PathVariable int categoryId) {
        Product findProduct = productRepo.findById(productId).get();
        Category findCategory = categoryRepo.findById(categoryId).get();
        if (findProduct.getCategories().contains(findCategory)) {
            findProduct.getCategories().remove(findCategory);
            productRepo.save(findProduct);
        }
        return findProduct;
    }

    @PutMapping("/{productId}/delete_cart/{cartId}")
    public Product removeCartFromProduct(@PathVariable int productId, @PathVariable int cartId) {
        Product findProduct = productRepo.findById(productId).get();
        Cart findCart = cartRepo.findById(cartId).get();

        return findProduct;
    }



    @DeleteMapping("/delete/{productId}")
    public void deleteProduct(@PathVariable int productId) {

        productRepo.deleteById(productId);

    }

    @GetMapping("/limit/{start}/{much}")
    public Iterable<Product> getWithLimit(@PathVariable int start, @PathVariable int much) {
        return productRepo.productLimit(start, much);
    }

    @PostMapping("/add_product")
    public Product addProduct(@RequestParam("file") MultipartFile file, @RequestParam("productData") String productString) throws JsonMappingException, JsonProcessingException {

        Date date = new Date();

        Product product = new ObjectMapper().readValue(productString, Product.class);

        // Register / POST product ke database, beserta dengan link ke profile Picture

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

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/products/product_picture/")
                .path(fileName).toUriString();

        product.setImage(fileDownloadUri);


        return productRepo.save(product);
    }

    @GetMapping("/product_picture/{fileName:.+}")
    public ResponseEntity<Object> downloadFile(@PathVariable String fileName) {
        Path path = Paths.get(uploadPath + fileName);
        Resource resource = null;

        try {
            resource = new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @PutMapping("/edit_product/{oldCategory}")
    @Transactional
    public Product editProduct(@RequestBody Product productData, @PathVariable int oldCategory) {
        System.out.println(productData.getId());
//        Category findOldCate = categoryRepo.findByCategoryName(oldCategory);
//        Category findNewCategory = categoryRepo.findByCategoryName(newCategory);
        Category findOldCate = categoryRepo.findById(oldCategory).get();
        Product findProduct = productRepo.findById(productData.getId()).get();
        findProduct.getCategories().remove(findOldCate);
        productData.setId(findProduct.getId());
        return productRepo.save(productData);
//        return
    }

    @PutMapping("/edit_category/{productId}/{categoryName}")
    public Product editCategory(@PathVariable int productId, @PathVariable String categoryName) {
        Product findProduct = productRepo.findById(productId).get();
        Category findCate = categoryRepo.findByCategoryName(categoryName);
        findProduct.getCategories().clear();
        findProduct.getCategories().add(findCate);
        return productRepo.save(findProduct);
    }


    @GetMapping("/filter_category/{categoryId}")
    public Iterable<Product> filterCategory(@PathVariable int categoryId){
        return productRepo.filterCategoryProd(categoryId);
    }

    @GetMapping("/filter_paket")
    public Iterable<Product> filterProductPaket(){
        return productRepo.filterProductByPaket();
    }

    @GetMapping("/filter_paket/{paketId}")
    public Iterable<Product> filterProductPaketId(@PathVariable int paketId){
        return productRepo.filterProductByPaketId(paketId);
    }

    @GetMapping("/size/{sizToFilter}")
    public Iterable<Product> allProductBySize(@PathVariable String sizToFilter){
        System.out.println(sizToFilter);
        return productRepo.findProductbySize(sizToFilter);
    }

    @GetMapping("/paket_name/{paketName}")
    public Iterable<Product> allProductByPaketName(@PathVariable String paketName){
        return productRepo.findProductbyPaketName(paketName);
    }



}