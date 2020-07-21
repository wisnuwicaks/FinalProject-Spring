package com.cimb.finalproject.controller;

import com.cimb.finalproject.dao.CartRepo;
import com.cimb.finalproject.dao.ProductRepo;
import com.cimb.finalproject.dao.UserRepo;
import com.cimb.finalproject.entity.Cart;
import com.cimb.finalproject.entity.Product;
import com.cimb.finalproject.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;

@RestController
@RequestMapping("/carts")
@CrossOrigin
public class CartController {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CartRepo cartRepo;

    @GetMapping
    public Iterable<Cart> getAllCart(){
        return cartRepo.findAll();
    }

    @GetMapping("/user/{userId}")
    public Iterable<Cart> getCartByUser(@PathVariable int userId){
        return cartRepo.findByUserId(userId);
    }

    @GetMapping("/product/{productId}")
    public Iterable<Cart> getCartByProduct(@PathVariable int productId){
        return cartRepo.findByProductId(productId);
    }

    @PostMapping("/add_to_cart/{userId}/{productId}")
    @Transactional
    public Cart addToCart(@RequestBody Cart cartData,@PathVariable int userId, @PathVariable int productId){
        Product findProduct = productRepo.findById(productId).get();
        User findUser = userRepo.findById(userId).get();

        cartData.setProduct(findProduct);
        cartData.setUser(findUser);
        return cartRepo.save(cartData);
    }

    @PutMapping("/update_qty/{cartId}")
    public Cart updateCartQty (@PathVariable int cartId){
        Cart findCartData = cartRepo.findById(cartId).get();
        findCartData.setQuantity(findCartData.getQuantity()+1);
        return cartRepo.save(findCartData);
    }

    @DeleteMapping("/delete/{cartId}/{userId}")
    public void deleteCartByProdUserId(@PathVariable int cartId,@PathVariable int userId){
        Cart findCart = cartRepo.findById(cartId).get();
        User findUser = userRepo.findById(userId).get();
        findUser.getCarts().remove(findCart);
        userRepo.save(findUser);
        cartRepo.deleteById(findCart.getCartId());

    }

    @DeleteMapping("/delete/{cartId}")
    public String deleteCartById(@PathVariable int cartId){
        Cart findCart = cartRepo.findById(cartId).get();
        cartRepo.deleteById(findCart.getCartId());
        return "Berhasil Delete Cart";
    }



}
