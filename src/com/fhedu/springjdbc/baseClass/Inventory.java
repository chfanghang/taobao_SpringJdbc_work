package com.fhedu.springjdbc.baseClass;

/**
 * @Author: fanghang
 * @Date: 2025-08-10
 * @Project:taobao_SpringJdbc_work
 */

import java.util.Date;

public class Inventory {
    private Integer id;
    private Integer productId;
    private Integer quantity;
    private Date updatedAt;

    // 无参构造器
    public Inventory() {
    }

    // 有参构造器
    public Inventory(Integer productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    // getter和setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}