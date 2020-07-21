package com.cimb.finalproject.controller;


import com.cimb.finalproject.dao.PaketRepo;
import com.cimb.finalproject.dao.ProductRepo;
import com.cimb.finalproject.entity.Paket;
import com.cimb.finalproject.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/paket")
@CrossOrigin
public class PaketController {

    @Autowired
    private PaketRepo paketRepo;
    @Autowired
    private ProductRepo productRepo;


    @GetMapping("/all_paket")
    public Iterable<Paket> getAllPaket(){
        return paketRepo.findAll();
    }

    @PostMapping("/add_paket/{paket}")
    public Paket addPaket(@PathVariable String paket){
        Paket newPaket = new Paket();
        newPaket.setPaket(paket);
        return paketRepo.save(newPaket);
    }

    @PostMapping("/add_product_to_paket/{productId}/{namaPaket}")
    public Product addProductToPaket(@PathVariable int productId, @PathVariable String namaPaket){
      Paket findPaket = paketRepo.findPaketByName(namaPaket);
        Product findProduct = productRepo.findById(productId).get();
        findProduct.getPakets().add(findPaket);
       return productRepo.save(findProduct);

    }

    @PutMapping("/delete_product_from_paket/{productId}/{namaPaket}")
    public Product deleteProductFromPaket(@PathVariable int productId, @PathVariable String namaPaket){
        System.out.println(namaPaket);
        Paket findPaket = paketRepo.findPaketByName(namaPaket);
        Product findProduct = productRepo.findById(productId).get();
        findProduct.getPakets().remove(findPaket);
        return productRepo.save(findProduct);
    }









}
