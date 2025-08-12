package com.fhedu.springjdbc.Dao;

/**
 * @Author: fanghang
 * @Date: 2025-08-10
 * @Project:taobao_SpringJdbc_work
 */

import com.fhedu.springjdbc.baseClass.PlatformAccount;
import com.fhedu.springjdbc.common.BaseDao;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Repository
public class PlatformAccountDao implements BaseDao<PlatformAccount, Integer> {

    @Resource
    private JdbcTemplate jdbcTemplate;
    // 平台账户固定ID
    public static final Integer PLATFORM_ID = 1;

    // PlatformAccountDao.java
    @Override
    public void insert(PlatformAccount account) {
        // 新增：明确插入 ID（因为平台账户 ID 固定为 1）
        String sql = "insert into platform_account(id, account_name, balance, updated_at) " +
                "values(?, ?, ?, now())";
        jdbcTemplate.update(sql,
                account.getId(), // 传入 ID=1
                account.getAccountName(),
                account.getBalance()
        );
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "delete from platform_account where id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void update(PlatformAccount account) {
        String sql = "update platform_account set account_name=?, balance=?, updated_at=now() where id = ?";
        jdbcTemplate.update(sql,
                account.getAccountName(), account.getBalance(), account.getId()
        );
    }

    @Override
    public PlatformAccount selectById(Integer id) {
        String sql = "select id, account_name as accountName, balance, updated_at as updatedAt from platform_account where id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(PlatformAccount.class), id);
    }

    @Override
    public List<PlatformAccount> selectAll() {
        String sql = "select id, account_name as accountName, balance, updated_at as updatedAt from platform_account";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(PlatformAccount.class));
    }

    // 扩展方法：增加平台余额
    public void addBalance(BigDecimal fee) {
        String sql = "update platform_account set balance = balance + ?, updated_at=now() where id = ?";
        jdbcTemplate.update(sql, fee, PLATFORM_ID);
    }
}