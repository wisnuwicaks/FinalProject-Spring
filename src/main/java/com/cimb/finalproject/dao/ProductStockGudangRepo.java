package com.cimb.finalproject.dao;

import com.cimb.finalproject.entity.ProductStock;
import com.cimb.finalproject.entity.ProductStockGudang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductStockGudangRepo extends JpaRepository<ProductStockGudang,Integer> {

    @Query(value = "SELECT * FROM product_stock_gudang WHERE product_id= ?1 ", nativeQuery = true)
    public Iterable<ProductStockGudang> findByProdId(int productId);

    @Query(value = "SELECT * FROM product_stock_gudang WHERE product_id= ?1 and size=?2 ", nativeQuery = true)
    public ProductStockGudang findByProdIdSize(int productId,String size);
}
