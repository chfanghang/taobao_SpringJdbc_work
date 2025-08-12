package com.fhedu.springjdbc.Dao;

/**
 * @Author: fanghang
 * @Date: 2025-08-10
 * @Project:taobao_SpringJdbc_work
 */

import com.fhedu.springjdbc.baseClass.User;
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
public class UsersDao implements BaseDao<User, Integer> {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void insert(User user) {
        String sql = "insert into users(username, password, role, real_name, phone, email, created_at, updated_at) " +
                "values(?, ?, ?, ?, ?, ?, now(), now())";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole());
            ps.setString(4, user.getRealName());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getEmail());
            return ps;
        }, keyHolder);
        // 回写自增ID
        user.setId(keyHolder.getKey().intValue());
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "delete from users where id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void update(User user) {
        String sql = "update users set username=?, password=?, role=?, real_name=?, phone=?, email=?, updated_at=now() " +
                "where id = ?";
        jdbcTemplate.update(sql,
                user.getUsername(), user.getPassword(), user.getRole(),
                user.getRealName(), user.getPhone(), user.getEmail(), user.getId()
        );
    }

    @Override
    public User selectById(Integer id) {
        String sql = "select id, username, password, role, real_name as realName, " +
                "phone, email, created_at as createdAt, updated_at as updatedAt from users where id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), id);
    }

    @Override
    public List<User> selectAll() {
        String sql = "select id, username, password, role, real_name as realName, " +
                "phone, email, created_at as createdAt, updated_at as updatedAt from users";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    // 扩展方法：按角色查询
    public List<User> selectByRole(String role) {
        String sql = "select id, username, role from users where role = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class), role);
    }
}