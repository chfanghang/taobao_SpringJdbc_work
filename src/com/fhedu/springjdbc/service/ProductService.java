package com.fhedu.springjdbc.service;

/**
 * @Author: fanghang
 * @Date: 2025-08-10
 * @Project:taobao_SpringJdbc_work
 */
import com.fhedu.springjdbc.Dao.*;
import com.fhedu.springjdbc.baseClass.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    // 平台抽成比例
    private static final BigDecimal PLATFORM_FEE_RATE = new BigDecimal("0.1");

    @Resource
    private UsersDao usersDao;
    @Resource
    private BankAccountsDao bankAccountDao;
    @Resource
    private ProductsDao productDao;
    @Resource
    private InventoryDao inventoryDao;
    @Resource
    private OrdersDao orderDao;
    @Resource
    private TransactionsDao transactionDao;
    @Resource
    private PlatformAccountDao platformAccountDao;

    /**
     * 购买商品（事务控制）
     */
    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            isolation = Isolation.REPEATABLE_READ,
            rollbackFor = Exception.class
    )
    public Order purchaseProduct(Integer productId, Integer buyerId, Integer quantity) {
        logger.info("开始购买商品：productId={}, buyerId={}, quantity={}", productId, buyerId, quantity);

        // 1. 参数校验
        validateParams(productId, buyerId, quantity);

        // 2. 查询基础数据
        Product product = productDao.selectById(productId);
        User buyer = usersDao.selectById(buyerId);
        User seller = usersDao.selectById(product.getSellerId());
        Inventory inventory = inventoryDao.selectByProductId(productId);
        BankAccount buyerAccount = bankAccountDao.selectByUserId(buyerId);
        BankAccount sellerAccount = bankAccountDao.selectByUserId(seller.getId());

        // 3. 业务规则校验
        validateBusinessRules(buyer, product, inventory, buyerAccount, quantity);

        // 4. 计算金额
        BigDecimal totalAmount = calculateAmounts(product, quantity);
        BigDecimal platformFee = totalAmount.multiply(PLATFORM_FEE_RATE);
        BigDecimal sellerAmount = totalAmount.subtract(platformFee);

        // 5. 执行核心业务操作
        Order order = createOrder(productId, buyerId, quantity, totalAmount, platformFee, sellerAmount);
        updateBalances(buyerAccount, sellerAccount, totalAmount, sellerAmount, platformFee);
        inventoryDao.reduceInventory(productId, quantity);
        createTransaction(order, buyerAccount, sellerAccount, totalAmount, platformFee, sellerAmount);

        logger.info("商品购买成功，订单号：{}", order.getOrderNumber());
        return order;
    }

    // 参数校验
    private void validateParams(Integer productId, Integer buyerId, Integer quantity) {
        if (productId == null || buyerId == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("参数错误：商品ID、买家ID不能为空，数量必须为正数");
        }
    }

    // 业务规则校验
    private void validateBusinessRules(User buyer, Product product, Inventory inventory,
                                       BankAccount buyerAccount, Integer quantity) {
        // 验证买家身份
        if (!"buyer".equals(buyer.getRole())) {
            throw new RuntimeException("用户不是买家角色");
        }
        // 验证库存
        if (inventory.getQuantity() < quantity) {
            throw new RuntimeException("库存不足：当前库存=" + inventory.getQuantity() + ", 需求=" + quantity);
        }
        // 验证余额
        BigDecimal totalAmount = product.getPrice().multiply(new BigDecimal(quantity));
        if (buyerAccount.getBalance().compareTo(totalAmount) < 0) {
            throw new RuntimeException("余额不足：当前余额=" + buyerAccount.getBalance() + ", 需支付=" + totalAmount);
        }
    }

    // 计算金额
    private BigDecimal calculateAmounts(Product product, Integer quantity) {
        return product.getPrice().multiply(new BigDecimal(quantity));
    }

    // 创建订单
    private Order createOrder(Integer productId, Integer buyerId, Integer quantity,
                              BigDecimal totalAmount, BigDecimal platformFee, BigDecimal sellerAmount) {
        Order order = new Order();
        order.setOrderNumber("ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8));
        order.setBuyerId(buyerId);
        order.setProductId(productId);
        order.setQuantity(quantity);
        order.setTotalAmount(totalAmount);
        order.setPlatformFee(platformFee);
        order.setSellerAmount(sellerAmount);
        order.setStatus("completed");
        orderDao.insert(order);
        return order;
    }

    // 更新账户余额
    private void updateBalances(BankAccount buyerAccount, BankAccount sellerAccount,
                                BigDecimal totalAmount, BigDecimal sellerAmount, BigDecimal platformFee) {
        // 扣减买家余额
        bankAccountDao.updateBalance(buyerAccount.getId(), totalAmount.negate());
        // 增加卖家余额
        bankAccountDao.updateBalance(sellerAccount.getId(), sellerAmount);
        // 增加平台余额
        platformAccountDao.addBalance(platformFee);
    }

    // 创建交易记录
    private void createTransaction(Order order, BankAccount buyerAccount, BankAccount sellerAccount,
                                   BigDecimal totalAmount, BigDecimal platformFee, BigDecimal sellerAmount) {
        Transaction transaction = new Transaction();
        transaction.setTransactionNumber("TRA" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8));
        transaction.setOrderId(order.getId());
        transaction.setBuyerAccountId(buyerAccount.getId());
        transaction.setSellerAccountId(sellerAccount.getId());
        transaction.setPlatformAccountId(platformAccountDao.PLATFORM_ID);
        transaction.setAmount(totalAmount);
        transaction.setPlatformFee(platformFee);
        transaction.setSellerAmount(sellerAmount);
        transactionDao.insert(transaction);
    }

    // 其他业务方法（查询订单、取消订单等）
    public List<Order> getOrdersByBuyerId(Integer buyerId) {
        return orderDao.selectByBuyerId(buyerId);
    }

    public Order getOrderById(Integer orderId) {
        return orderDao.selectById(orderId);
    }
}