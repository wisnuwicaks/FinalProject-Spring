package com.cimb.finalproject.dao;

import com.cimb.finalproject.entity.Product;
import com.cimb.finalproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

public interface ProductRepo extends JpaRepository<Product,Integer> {
    @Query(value = "SELECT * FROM product limit ?1,?2", nativeQuery = true)
    public Iterable<Product> productLimit(int start, int much);

    @Query(value = "SELECT * FROM product p inner join product_category pc on p.id=pc.product_id where pc.category_id=?1", nativeQuery = true)
    public Iterable<Product> filterCategoryProd(int categoryId);

    @Query(value = "SELECT distinct p.id,p.product_name,p.image,p.size_available,p.price,p.sold_qty FROM product p inner join product_paket pk on p.id=pk.product_id", nativeQuery = true)
    public Iterable<Product> filterProductByPaket();

    @Query(value = "SELECT distinct FROM product p inner join product_paket pk on p.id=pk.product_id where paket_id=?1", nativeQuery = true)
    public Iterable<Product> filterProductByPaketId(int paketId);

    @Query(value = "SELECT * FROM transaction as t inner join" +
            "    transaction_detail as td on t.trx_id=td.transaction_id inner join product as p" +
            "    on p.id = td.product_id" +
            "    WHERE user_id= ?1", nativeQuery = true)
    public Iterable<Product> findProductByJoinTrx(int userId);


    @Query(value = "select p.id,p.image,p.price,p.product_name,p.size_available,p.sold_qty from product p inner join product_paket pp on p.id=pp.product_id \n" +
            "inner join paket pak on pp.paket_id=pak.id where pak.paket=?1", nativeQuery = true)
    public Iterable<Product> findProductbyPaketName(String namaPaket);

    @Query(value = "select * from product where size_available like %?1% ", nativeQuery = true)
    public Iterable<Product> findProductbySize(String size);

    @Query(value = "select * from product where size_available like %?1% ", nativeQuery = true)
    public Iterable<Product> findProductSold(String size);


}
