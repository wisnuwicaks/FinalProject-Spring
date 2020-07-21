package com.cimb.finalproject.dao;

import com.cimb.finalproject.entity.Product;
import com.cimb.finalproject.entity.Transaction;
import com.cimb.finalproject.entity.TransactionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionDetailRepo extends JpaRepository<TransactionDetail,Integer> {

    @Query(value = "SELECT * FROM transaction_detail WHERE transaction_id= ?1", nativeQuery = true)
    public Iterable<TransactionDetail> findDetailTrxByTrxId(int trxId);

    @Query(value = "select * from transaction_detail where product_id=?1", nativeQuery = true)
    public Iterable<TransactionDetail> getTrxDetailOfProduct(int productId);


}
