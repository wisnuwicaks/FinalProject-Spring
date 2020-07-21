package com.cimb.finalproject.entity;



import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.List;


@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int trxId;

    private double totalPrice;
    private String status;
    private Date buyDate;
    private Date endTrxDate;
    private String shippingAddress;
    private String trfSlip;
    private String trxMessage;



    @OneToMany(fetch = FetchType.EAGER, mappedBy = "transaction", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TransactionDetail> transactionDetails;


    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
    @JoinColumn(name = "user_id")
    private User user;

    public int getTrxId() {
        return trxId;
    }

    public void setTrxId(int trxId) {
        this.trxId = trxId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(Date buyDate) {
        this.buyDate = buyDate;
    }

    public Date getEndTrxDate() {
        return endTrxDate;
    }

    public void setEndTrxDate(Date endTrxDate) {
        this.endTrxDate = endTrxDate;
    }

    public List<TransactionDetail> getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(List<TransactionDetail> transactionDetails) {
        this.transactionDetails = transactionDetails;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getTrfSlip() {
        return trfSlip;
    }

    public void setTrfSlip(String trfSlip) {
        this.trfSlip = trfSlip;
    }

    public String getTrxMessage() {
        return trxMessage;
    }

    public void setTrxMessage(String trxMessage) {
        this.trxMessage = trxMessage;
    }
}
