package com.fhedu.springjdbc.Dao;

/**
 * @Author: fanghang
 * @Date: 2025-08-10
 * @Project:taobao_SpringJdbc_work
 */

import com.fhedu.springjdbc.baseClass.Transaction;
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
public class TransactionsDao implements BaseDao<Transaction, Integer> {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void insert(Transaction transaction) {
        String sql = "insert into transactions(transaction_number, order_id, buyer_account_id, " +
                "seller_account_id, platform_account_id, amount, platform_fee, seller_amount, transaction_time) " +
                "values(?, ?, ?, ?, ?, ?, ?, ?, now())";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, transaction.getTransactionNumber());
            ps.setInt(2, transaction.getOrderId());
            ps.setInt(3, transaction.getBuyerAccountId());
            ps.setInt(4, transaction.getSellerAccountId());
            ps.setInt(5, transaction.getPlatformAccountId());
            ps.setBigDecimal(6, transaction.getAmount());
            ps.setBigDecimal(7, transaction.getPlatformFee());
            ps.setBigDecimal(8, transaction.getSellerAmount());
            return ps;
        }, keyHolder);
        transaction.setId(keyHolder.getKey().intValue());
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "delete from transactions where id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public void update(Transaction transaction) {
        // 交易记录通常不允许更新，此处仅为接口完整性实现
        throw new UnsupportedOperationException("交易记录不支持更新");
    }

    @Override
    public Transaction selectById(Integer id) {
        String sql = "select id, transaction_number as transactionNumber, order_id as orderId, " +
                "buyer_account_id as buyerAccountId, seller_account_id as sellerAccountId, " +
                "platform_account_id as platformAccountId, amount, platform_fee as platformFee, " +
                "seller_amount as sellerAmount, transaction_time as transactionTime from transactions where id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Transaction.class), id);
    }

    @Override
    public List<Transaction> selectAll() {
        String sql = "select id, transaction_number as transactionNumber, order_id as orderId, " +
                "buyer_account_id as buyerAccountId, seller_account_id as sellerAccountId, " +
                "platform_account_id as platformAccountId, amount, platform_fee as platformFee, " +
                "seller_amount as sellerAmount, transaction_time as transactionTime from transactions";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Transaction.class));
    }

    // 扩展方法：按订单ID查询
    public Transaction selectByOrderId(Integer orderId) {
        String sql = "select id, transaction_number as transactionNumber, amount from transactions where order_id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Transaction.class), orderId);
    }
}