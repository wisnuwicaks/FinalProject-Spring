package com.cimb.finalproject.dao;

import com.cimb.finalproject.entity.Cart;
import com.cimb.finalproject.entity.Paket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaketRepo extends JpaRepository<Paket,Integer> {

    @Query(value = "SELECT * FROM paket WHERE paket= ?1", nativeQuery = true)
    public Paket findPaketByName(String namaPaket);


}
