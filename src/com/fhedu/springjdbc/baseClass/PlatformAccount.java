package com.fhedu.springjdbc.baseClass;

/**
 * @Author: fanghang
 * @Date: 2025-08-10
 * @Project:taobao_SpringJdbc_work
 */

import java.math.BigDecimal;
import java.util.Date;

public class PlatformAccount {
    private Integer id;
    private String accountName;
    private BigDecimal balance;
    private Date updatedAt;

    // 无参构造器
    public PlatformAccount() {
    }

    // 有参构造器
    public PlatformAccount(String accountName, BigDecimal balance) {
        this.accountName = accountName;
        this.balance = balance;
    }

    // getter和setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}