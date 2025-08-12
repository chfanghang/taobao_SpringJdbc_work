package com.fhedu.springjdbc.baseClass;

/**
 * @Author: fanghang
 * @Date: 2025-08-10
 * @Project:taobao_SpringJdbc_work
 */

import java.math.BigDecimal;
import java.util.Date;

public class BankAccount {
    private Integer id;
    private Integer userId;
    private String accountNumber;
    private BigDecimal balance;
    private Date createdAt;
    private Date updatedAt;

    // 无参构造器
    public BankAccount() {
    }

    // 有参构造器
    public BankAccount(Integer userId, String accountNumber, BigDecimal balance) {
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    // getter和setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}