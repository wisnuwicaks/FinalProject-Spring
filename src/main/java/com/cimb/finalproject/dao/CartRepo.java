package com.cimb.finalproject.dao;

import com.cimb.finalproject.entity.Cart;
import com.cimb.finalproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CartRepo extends JpaRepository<Cart, Integer> {

    @Query(value = "SELECT * FROM cart WHERE user_id= ?1", nativeQuery = true)
    public Iterable<Cart> findByUserId(int userId);

    @Query(value = "SELECT * FROM cart WHERE product_id= ?1", nativeQuery = true)
    public Iterable<Cart> findByProductId(int productId);

    @Query(value = "SELECT * FROM cart WHERE product_id=?1 and size=?2 and user_id=?3", nativeQuery = true)
    public Cart findCartToDelete(int productId,String size, int userId);
}
