package com.fhedu.springjdbc.Dao;

/**
 * @Author: fanghang
 * @Date: 2025-08-10
 * @Project:taobao_SpringJdbc_work
 */

import com.fhedu.springjdbc.baseClass.Order;
import com.fhedu.springjdbc.common.BaseDao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class OrdersDao implements BaseDao<Order, Integer> {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void insert(Order order) {
        String sql = "insert into orders(order_number, buyer_id, product_id, quantity, " +
                "total_amount, platform_fee, seller_amount, status, created_at, updated_at) " +
                "values(?, ?, ?, ?, ?, ?, ?, ?, now(), now())";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, order.getOrderNumber());
            ps.setInt(2, order.getBuyerId());
            ps.setInt(3, order.getProductId());
            ps.setInt(4, order.getQuantity());
            ps.setBigDecimal(5, order.getTotalAmount());
            ps.setBigDecimal(6, order.getPlatformFee());
            ps.setBigDecimal(7, order.getSellerAmount());
            ps.setString(8, order.getStatus());
            return ps;
        }, keyHolder);
        order.setId(keyHolder.getKey().intValue());
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "delete from orders where id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void update(Order order) {
        String sql = "update orders set order_number=?, buyer_id=?, product_id=?, quantity=?, " +
                "total_amount=?, platform_fee=?, seller_amount=?, status=?, updated_at=now() " +
                "where id = ?";
        jdbcTemplate.update(sql,
                order.getOrderNumber(), order.getBuyerId(), order.getProductId(),
                order.getQuantity(), order.getTotalAmount(), order.getPlatformFee(),
                order.getSellerAmount(), order.getStatus(), order.getId()
        );
    }

    @Override
    public Order selectById(Integer id) {
        String sql = "select id, order_number as orderNumber, buyer_id as buyerId, product_id as productId, " +
                "quantity, total_amount as totalAmount, platform_fee as platformFee, seller_amount as sellerAmount, " +
                "status, created_at as createdAt, updated_at as updatedAt from orders where id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Order.class), id);
    }

    @Override
    public List<Order> selectAll() {
        String sql = "select id, order_number as orderNumber, buyer_id as buyerId, product_id as productId, " +
                "quantity, total_amount as totalAmount, platform_fee as platformFee, seller_amount as sellerAmount, " +
                "status, created_at as createdAt, updated_at as updatedAt from orders";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Order.class));
    }

    // 扩展方法：按买家ID查询
    public List<Order> selectByBuyerId(Integer buyerId) {
        String sql = "select id, order_number as orderNumber, product_id as productId, status from orders where buyer_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Order.class), buyerId);
    }
}