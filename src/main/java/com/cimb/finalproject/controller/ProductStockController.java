package com.cimb.finalproject.controller;

import com.cimb.finalproject.dao.ProductRepo;
import com.cimb.finalproject.dao.ProductStockRepo;
import com.cimb.finalproject.entity.Category;
import com.cimb.finalproject.entity.Product;
import com.cimb.finalproject.entity.ProductStock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/stocks")
@CrossOrigin
public class ProductStockController {

    @Autowired
    private ProductStockRepo productStockRepo;

    @Autowired
    private ProductRepo productRepo;

    @GetMapping("/all_product")
    public Iterable<ProductStock> getAllStockData (){
        return productStockRepo.findAll();
    }

    @GetMapping("/product_id/{productId}")
    public Iterable<ProductStock> getProductStockByProdId (@PathVariable int productId){
        return productStockRepo.findByProdId(productId);
    }

    @PostMapping("/add_stock/{productId}")
    public ProductStock addStock(@RequestBody ProductStock productStockData,@PathVariable int productId){
        Product findProduct = productRepo.findById(productId).get();
        if(!findProduct.equals(null)){
            productStockData.setProduct(findProduct);
            return productStockRepo.save(productStockData);
        }
        throw new RuntimeException("Product not found");
    }

    @PutMapping("/edit_stock/{productId}/{size}/{newStock}")
    @Transactional
    public ProductStock editStock(@PathVariable int productId,@PathVariable String size, @PathVariable int newStock) {
        ProductStock findProductStock = productStockRepo.findByProdIdSize(productId,size);
        Product findProduct = productRepo.findById(productId).get();
        System.out.println(size);

        if(findProductStock==null){
            ProductStock newStockData = new ProductStock();
            newStockData.setStock(newStock);
            newStockData.setSize(size);
            newStockData.setProduct(findProduct);
            return productStockRepo.save(newStockData);
        }
        findProductStock.setStock(newStock);
        return productStockRepo.save(findProductStock);
    }






    @PutMapping("/reduce_stock/{productId}/{size}/{quantity}")
    public ProductStock reduceStock(@PathVariable int productId, @PathVariable String size,@PathVariable int quantity) {
        ProductStock findProductStock = productStockRepo.findByProdIdSize(productId,size);
        findProductStock.setStock(findProductStock.getStock()-quantity);
        return productStockRepo.save(findProductStock);
    }

    @PutMapping("/delete_stock/{productId}")
    public Product deleteStock(@PathVariable int productId) {
        Product findProduct = productRepo.findById(productId).get();
        productStockRepo.deleteInBatch(findProduct.getProductStocks());
        return findProduct;
    }



}
