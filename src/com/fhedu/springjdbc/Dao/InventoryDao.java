package com.fhedu.springjdbc.Dao;

/**
 * @Author: fanghang
 * @Date: 2025-08-10
 * @Project:taobao_SpringJdbc_work
 */

import com.fhedu.springjdbc.baseClass.Inventory;
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
public class InventoryDao implements BaseDao<Inventory, Integer> {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void insert(Inventory inventory) {
        String sql = "insert into inventory(product_id, quantity, updated_at) values(?, ?, now())";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, inventory.getProductId());
            ps.setInt(2, inventory.getQuantity());
            return ps;
        }, keyHolder);
        inventory.setId(keyHolder.getKey().intValue());
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "delete from inventory where id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void update(Inventory inventory) {
        String sql = "update inventory set product_id=?, quantity=?, updated_at=now() where id = ?";
        jdbcTemplate.update(sql,
                inventory.getProductId(), inventory.getQuantity(), inventory.getId()
        );
    }

    @Override
    public Inventory selectById(Integer id) {
        String sql = "select id, product_id as productId, quantity, updated_at as updatedAt from inventory where id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Inventory.class), id);
    }

    @Override
    public List<Inventory> selectAll() {
        String sql = "select id, product_id as productId, quantity, updated_at as updatedAt from inventory";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Inventory.class));
    }

    // 扩展方法：按商品ID查询
    public Inventory selectByProductId(Integer productId) {
        String sql = "select id, product_id as productId, quantity from inventory where product_id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Inventory.class), productId);
    }

    // 扩展方法：减少库存
    public void reduceInventory(Integer productId, Integer quantity) {
        String sql = "update inventory set quantity = quantity - ?, updated_at=now() where product_id = ?";
        jdbcTemplate.update(sql, quantity, productId);
    }
}