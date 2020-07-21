package com.cimb.finalproject.controller;


import com.cimb.finalproject.dao.ProductRepo;
import com.cimb.finalproject.dao.ProductStockGudangRepo;
import com.cimb.finalproject.entity.Product;
import com.cimb.finalproject.entity.ProductStock;
import com.cimb.finalproject.entity.ProductStockGudang;
import com.cimb.finalproject.entity.ProductStockGudang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/gudang")
@CrossOrigin
public class ProductStockGudangController {
    @Autowired
    private ProductStockGudangRepo productStockGudangRepo;

    @Autowired
    private ProductRepo productRepo;


    @GetMapping("/all_product")
    public Iterable<ProductStockGudang> getAllStockGudangData (){
        return productStockGudangRepo.findAll();
    }


    @GetMapping("/product_id/{productId}")
    public Iterable<ProductStockGudang> getProductStockGudangByProdId (@PathVariable int productId){
        return productStockGudangRepo.findByProdId(productId);
    }

    @PostMapping("/add_stock_gudang/{productId}")
    public ProductStockGudang addStockGudang(@RequestBody ProductStockGudang productStockData,@PathVariable int productId){
        Product findProduct = productRepo.findById(productId).get();
        System.out.println(productId);
        System.out.println(findProduct);

        if(!findProduct.equals(null)){
            productStockData.setProduct(findProduct);
            return productStockGudangRepo.save(productStockData);
        }
        throw new RuntimeException("Product not found");
    }

    @PutMapping("/reduce_stock/{productId}/{size}/{reduceStock}")
    @Transactional
    public ProductStockGudang reduceStockFromAddStockApp(@PathVariable int productId, @PathVariable String size,@PathVariable int reduceStock) {
        ProductStockGudang findProductStockGudang = productStockGudangRepo.findByProdIdSize(productId,size);

        System.out.println();

        findProductStockGudang.setStockGudang(findProductStockGudang.getStockGudang()-reduceStock);
        return productStockGudangRepo.save(findProductStockGudang);
    }

    @PutMapping("/add_stock/{productId}/{size}/{addedStock}")
    @Transactional
    public ProductStockGudang addStockFromDeleteStockApp(@PathVariable int productId, @PathVariable String size,@PathVariable int addedStock) {
        ProductStockGudang findProductStockGudang = productStockGudangRepo.findByProdIdSize(productId,size);

        findProductStockGudang.setStockGudang(findProductStockGudang.getStockGudang()+addedStock);
        return productStockGudangRepo.save(findProductStockGudang);
    }

    @PutMapping("/delete_stock_gudang/{productId}")
    public Product deleteStock(@PathVariable int productId) {
        Product findProduct = productRepo.findById(productId).get();
        productStockGudangRepo.deleteInBatch(findProduct.getProductStockGudangs());
        return findProduct;
    }
}
