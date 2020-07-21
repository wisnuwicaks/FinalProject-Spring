package com.cimb.finalproject.controller;

import com.cimb.finalproject.dao.ProductRepo;
import com.cimb.finalproject.dao.UserRepo;
import com.cimb.finalproject.entity.Product;
import com.cimb.finalproject.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ProductRepo productRepo;

    @GetMapping("/user_wishlist/{userId}")
    public User getUserWishlist(@PathVariable int userId){
        User findUser = userRepo.findById(userId).get();
        return findUser;
    }

    @PostMapping("/add_wishlist/{userId}/{productId}")
    public User addWishList(@PathVariable int userId, @PathVariable int productId){
        User findUser = userRepo.findById(userId).get();
        Product findProduct = productRepo.findById(productId).get();

        findUser.getProducts().add(findProduct);
        return userRepo.save(findUser);

    }

    @PutMapping("/{userId}/delete_wishlist/{productId}")
    public User removeProductFromUser(@PathVariable int userId, @PathVariable int productId){
        User findUser = userRepo.findById(userId).get();
        Product findProduct = productRepo.findById(productId).get();
        if(findUser.getProducts().contains(findProduct)){
            findUser.getProducts().remove(findProduct);
            return userRepo.save(findUser);
        }
        throw new RuntimeException("user not found!");
    }
}
