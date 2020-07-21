package com.cimb.finalproject.entity;

import javax.persistence.*;

@Entity
public class ProductStockGudang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "product_id")
    private Product product;

    private String size;

    private int stockGudang;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStockGudang() {
        return stockGudang;
    }

    public void setStockGudang(int stockGudang) {
        this.stockGudang = stockGudang;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }


}
