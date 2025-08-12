package com.fhedu.springjdbc.Dao;

/**
 * @Author: fanghang
 * @Date: 2025-08-10
 * @Project:taobao_SpringJdbc_work
 */

import com.fhedu.springjdbc.baseClass.BankAccount;
import com.fhedu.springjdbc.common.BaseDao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class BankAccountsDao implements BaseDao<BankAccount, Integer> {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void insert(BankAccount account) {
        String sql = "insert into bank_accounts(user_id, account_number, balance, created_at, updated_at) " +
                "values(?, ?, ?, now(), now())";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, account.getUserId());
            ps.setString(2, account.getAccountNumber());
            ps.setBigDecimal(3, account.getBalance());
            return ps;
        }, keyHolder);
        account.setId(keyHolder.getKey().intValue());
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "delete from bank_accounts where id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void update(BankAccount account) {
        String sql = "update bank_accounts set user_id=?, account_number=?, balance=?, updated_at=now() " +
                "where id = ?";
        jdbcTemplate.update(sql,
                account.getUserId(), account.getAccountNumber(),
                account.getBalance(), account.getId()
        );
    }

    @Override
    public BankAccount selectById(Integer id) {
        String sql = "select id, user_id as userId, account_number as accountNumber, balance, " +
                "created_at as createdAt, updated_at as updatedAt from bank_accounts where id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(BankAccount.class), id);
    }

    @Override
    public List<BankAccount> selectAll() {
        String sql = "select id, user_id as userId, account_number as accountNumber, balance, " +
                "created_at as createdAt, updated_at as updatedAt from bank_accounts";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(BankAccount.class));
    }

    // 扩展方法：按用户ID查询
    public BankAccount selectByUserId(Integer userId) {
        String sql = "select id, user_id as userId, account_number as accountNumber, balance from bank_accounts where user_id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(BankAccount.class), userId);
    }

    // 扩展方法：更新余额
    public void updateBalance(Integer accountId, BigDecimal amount) {
        String sql = "update bank_accounts set balance = balance + ?, updated_at=now() where id = ?";
        jdbcTemplate.update(sql, amount, accountId);
    }
}