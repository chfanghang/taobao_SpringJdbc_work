package com.fhedu.springjdbc.Dao;

/**
 * @Author: fanghang
 * @Date: 2025-08-10
 * @Project:taobao_SpringJdbc_work
 */

import com.fhedu.springjdbc.baseClass.Product;
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
public class ProductsDao implements BaseDao<Product, Integer> {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void insert(Product product) {
        String sql = "insert into products(seller_id, name, description, price, created_at, updated_at) " +
                "values(?, ?, ?, ?, now(), now())";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, product.getSellerId());
            ps.setString(2, product.getName());
            ps.setString(3, product.getDescription());
            ps.setBigDecimal(4, product.getPrice());
            return ps;
        }, keyHolder);
        product.setId(keyHolder.getKey().intValue());
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "delete from products where id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void update(Product product) {
        String sql = "update products set seller_id=?, name=?, description=?, price=?, updated_at=now() " +
                "where id = ?";
        jdbcTemplate.update(sql,
                product.getSellerId(), product.getName(), product.getDescription(),
                product.getPrice(), product.getId()
        );
    }

    @Override
    public Product selectById(Integer id) {
        String sql = "select id, seller_id as sellerId, name, description, price, " +
                "created_at as createdAt, updated_at as updatedAt from products where id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Product.class), id);
    }

    @Override
    public List<Product> selectAll() {
        String sql = "select id, seller_id as sellerId, name, description, price, " +
                "created_at as createdAt, updated_at as updatedAt from products";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Product.class));
    }

    // 扩展方法：按卖家ID查询
    public List<Product> selectBySellerId(Integer sellerId) {
        String sql = "select id, name, price from products where seller_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Product.class), sellerId);
    }
}