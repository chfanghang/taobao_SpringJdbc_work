package com.fhedu.springjdbc.baseClass;

/**
 * @Author: fanghang
 * @Date: 2025-08-10
 * @Project:taobao_SpringJdbc_work
 */

import java.math.BigDecimal;
import java.util.Date;

public class Product {
    private Integer id;
    private Integer sellerId;
    private String name;
    private String description;
    private BigDecimal price;
    private Date createdAt;
    private Date updatedAt;

    // 无参构造器
    public Product() {
    }

    // 有参构造器
    public Product(Integer sellerId, String name, String description, BigDecimal price) {
        this.sellerId = sellerId;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    // getter和setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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