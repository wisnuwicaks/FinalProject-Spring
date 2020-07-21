package com.cimb.finalproject.controller;

import com.cimb.finalproject.dao.TransactionDetailRepo;
import com.cimb.finalproject.entity.TransactionDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trxdetail")
@CrossOrigin
public class TransactionDetailController {
    @Autowired
    private TransactionDetailRepo transactionDetailRepo;

    @GetMapping("/product/{productId}")
    public Iterable<TransactionDetail> getTrxDetailOfProduct(@PathVariable int productId){
        return transactionDetailRepo.getTrxDetailOfProduct(productId);
    }


}
