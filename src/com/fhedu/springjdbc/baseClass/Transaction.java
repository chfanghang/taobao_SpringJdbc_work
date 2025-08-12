package com.fhedu.springjdbc.baseClass;

/**
 * @Author: fanghang
 * @Date: 2025-08-10
 * @Project:taobao_SpringJdbc_work
 */

import java.math.BigDecimal;
import java.util.Date;

public class Transaction {
    private Integer id;
    private String transactionNumber;
    private Integer orderId;
    private Integer buyerAccountId;
    private Integer sellerAccountId;
    private Integer platformAccountId;
    private BigDecimal amount;
    private BigDecimal platformFee;
    private BigDecimal sellerAmount;
    private Date transactionTime;

    // 无参构造器
    public Transaction() {
    }

    // getter和setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getBuyerAccountId() {
        return buyerAccountId;
    }

    public void setBuyerAccountId(Integer buyerAccountId) {
        this.buyerAccountId = buyerAccountId;
    }

    public Integer getSellerAccountId() {
        return sellerAccountId;
    }

    public void setSellerAccountId(Integer sellerAccountId) {
        this.sellerAccountId = sellerAccountId;
    }

    public Integer getPlatformAccountId() {
        return platformAccountId;
    }

    public void setPlatformAccountId(Integer platformAccountId) {
        this.platformAccountId = platformAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(BigDecimal platformFee) {
        this.platformFee = platformFee;
    }

    public BigDecimal getSellerAmount() {
        return sellerAmount;
    }

    public void setSellerAmount(BigDecimal sellerAmount) {
        this.sellerAmount = sellerAmount;
    }

    public Date getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Date transactionTime) {
        this.transactionTime = transactionTime;
    }
}