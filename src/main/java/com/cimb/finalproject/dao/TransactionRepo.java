package com.cimb.finalproject.dao;

import com.cimb.finalproject.entity.Cart;
import com.cimb.finalproject.entity.Transaction;
import com.cimb.finalproject.entity.TransactionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepo extends JpaRepository<Transaction,Integer> {
    @Query(value = "SELECT * FROM transaction WHERE user_id= ?1", nativeQuery = true)
    public Iterable<Transaction> findTrxByUserId(int userId);

    @Query(value = "SELECT * FROM transaction WHERE user_id= ?1 and status=?2", nativeQuery = true)
    public Iterable<Transaction> findUserTrxByStatus(int userId,String status);

    @Query(value = "SELECT * FROM transaction WHERE status=?1", nativeQuery = true)
    public Iterable<Transaction> findTrxByStatus(String status);



}
