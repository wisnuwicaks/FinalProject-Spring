package com.cimb.finalproject.dao;

import com.cimb.finalproject.entity.ProductStock;
import com.cimb.finalproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductStockRepo extends JpaRepository<ProductStock,Integer> {
    @Query(value = "SELECT * FROM product_stock WHERE product_id= ?1 and size=?2 ", nativeQuery = true)
    public ProductStock findByProdIdSize(int productId,String size);

    @Query(value = "SELECT * FROM product_stock WHERE product_id= ?1 ", nativeQuery = true)
    public Iterable<ProductStock> findByProdId(int productId);
}
