package com.fhedu.springjdbc;

import com.fhedu.springjdbc.Dao.*;
import com.fhedu.springjdbc.baseClass.*;
import com.fhedu.springjdbc.service.ProductService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import java.math.BigDecimal;
import java.util.Objects;

public class Test {
    // 常量定义：提取测试中固定的参数
    private static final String SPRING_CONFIG_PATH = "applicationContext.xml";
    private static final int NORMAL_PURCHASE_QUANTITY = 2;
    private static final String BUYER_ROLE = "buyer";
    private static final String SELLER_ROLE = "seller";

    // Spring组件
    private static ApplicationContext context;
    private static UsersDao usersDao;
    private static BankAccountsDao bankAccountDao;
    private static ProductsDao productsDao;
    private static InventoryDao inventoryDao;
    private static PlatformAccountDao platformAccountDao;
    private static ProductService productService;

    public static void main(String[] args) {
        // 使用try-with-resources自动关闭Spring上下文，避免资源泄露
        try (ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(SPRING_CONFIG_PATH)) {
            context = ctx;
            initBeans();

            // 初始化测试数据
            System.out.println("===== 开始初始化测试数据 =====");
            Integer buyerId = initUser(BUYER_ROLE, "test_buyer", "123456", new BigDecimal("5000.00"));
            Integer sellerId = initUser(SELLER_ROLE, "test_seller", "123456", new BigDecimal("0.00"));
            initPlatformAccount();
            Integer productId = initProductAndInventory(
                    sellerId, "测试商品", "这是一个测试商品", new BigDecimal("200.00"), 10
            );
            System.out.println("===== 测试数据初始化完成 =====");

            // 执行测试用例
            testNormalPurchase(buyerId, productId);
            testInsufficientInventory(buyerId, productId);
            testInsufficientBalance(buyerId, productId);

        } catch (Exception e) {
            System.err.println("测试过程发生异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initBeans() {
        // 统一使用Objects.requireNonNull确保Bean加载成功
        usersDao = Objects.requireNonNull(context.getBean(UsersDao.class));
        bankAccountDao = Objects.requireNonNull(context.getBean(BankAccountsDao.class));
        productsDao = Objects.requireNonNull(context.getBean(ProductsDao.class));
        inventoryDao = Objects.requireNonNull(context.getBean(InventoryDao.class));
        platformAccountDao = Objects.requireNonNull(context.getBean(PlatformAccountDao.class));
        productService = Objects.requireNonNull(context.getBean(ProductService.class));
    }

    /**
     * 初始化用户及对应银行账户
     */
    private static Integer initUser(String role, String username, String password, BigDecimal initialBalance) {
        // 参数校验：避免无效输入
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("初始余额不能为负数");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRole(role);
        usersDao.insert(user);
        Integer userId = user.getId();

        BankAccount account = new BankAccount();
        account.setUserId(userId);
        account.setAccountNumber(role.equals(BUYER_ROLE) ? "6222021234567890" : "6222030987654321");
        account.setBalance(initialBalance);
        bankAccountDao.insert(account);

        System.out.printf("初始化%s用户：ID=%d, 用户名=%s, 初始余额=%.2f%n",
                role, userId, username, initialBalance);
        return userId;
    }

    /**
     * 初始化平台账户（优化：增加已存在提示）
     */
    private static void initPlatformAccount() {
        try {
            platformAccountDao.selectById(PlatformAccountDao.PLATFORM_ID);
            System.out.println("平台账户已存在，无需重复初始化：ID=" + PlatformAccountDao.PLATFORM_ID);
        } catch (Exception e) {
            PlatformAccount platformAccount = new PlatformAccount();
            platformAccount.setId(PlatformAccountDao.PLATFORM_ID); // 显式设置固定ID
            platformAccount.setAccountName("平台账户");
            platformAccount.setBalance(new BigDecimal("0.00"));
            platformAccountDao.insert(platformAccount);
            System.out.println("初始化平台账户：ID=" + PlatformAccountDao.PLATFORM_ID);
        }
    }

    /**
     * 初始化商品及库存
     */
    private static Integer initProductAndInventory(Integer sellerId, String productName,
                                                   String description, BigDecimal price, Integer stock) {
        // 库存和价格校验
        if (stock < 0) {
            throw new IllegalArgumentException("初始库存不能为负数");
        }
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("商品价格必须大于0");
        }

        Product product = new Product();
        product.setSellerId(sellerId);
        product.setName(productName);
        product.setDescription(description);
        product.setPrice(price);
        productsDao.insert(product);
        Integer productId = product.getId();

        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setQuantity(stock);
        inventoryDao.insert(inventory);

        System.out.printf("初始化商品：ID=%d, 名称=%s, 价格=%.2f, 库存=%d%n",
                productId, productName, price, stock);
        return productId;
    }

    /**
     * 测试正常购买流程（优化：增加前置查询和结果校验）
     */
    private static void testNormalPurchase(Integer buyerId, Integer productId) {
        System.out.println("\n===== 测试正常购买流程 =====");
        int purchaseQuantity = NORMAL_PURCHASE_QUANTITY;

        // 购买前状态查询（增加卖家账户查询，避免硬编码初始值）buyerAccountBefore = {BankAccount@4483}
        BankAccount buyerAccountBefore = bankAccountDao.selectByUserId(buyerId);
        Product product = productsDao.selectById(productId);
        BankAccount sellerAccountBefore = bankAccountDao.selectByUserId(product.getSellerId());
        PlatformAccount platformBefore = platformAccountDao.selectById(PlatformAccountDao.PLATFORM_ID);
        Inventory inventoryBefore = inventoryDao.selectByProductId(productId);

        try {
            Order order = productService.purchaseProduct(productId, buyerId, purchaseQuantity);
            System.out.println("购买成功，订单号：" + order.getOrderNumber());

            // 购买后状态验证
            BankAccount buyerAccountAfter = bankAccountDao.selectByUserId(buyerId);
            BankAccount sellerAccountAfter = bankAccountDao.selectByUserId(product.getSellerId());
            PlatformAccount platformAfter = platformAccountDao.selectById(PlatformAccountDao.PLATFORM_ID);
            Inventory inventoryAfter = inventoryDao.selectByProductId(productId);

            // 输出更详细的变动信息
            System.out.printf("买家余额变化：%.2f -> %.2f (减少: %.2f)%n",
                    buyerAccountBefore.getBalance(),
                    buyerAccountAfter.getBalance(),
                    buyerAccountBefore.getBalance().subtract(buyerAccountAfter.getBalance()));

            System.out.printf("卖家余额变化：%.2f -> %.2f (增加: %.2f)%n",
                    sellerAccountBefore.getBalance(),
                    sellerAccountAfter.getBalance(),
                    sellerAccountAfter.getBalance().subtract(sellerAccountBefore.getBalance()));

            System.out.printf("平台余额变化：%.2f -> %.2f (增加: %.2f)%n",
                    platformBefore.getBalance(),
                    platformAfter.getBalance(),
                    platformAfter.getBalance().subtract(platformBefore.getBalance()));

            System.out.printf("库存变化：%d -> %d (减少: %d)%n",
                    inventoryBefore.getQuantity(),
                    inventoryAfter.getQuantity(),
                    inventoryBefore.getQuantity() - inventoryAfter.getQuantity());

        } catch (RuntimeException e) { // 捕获特定异常，避免过度捕获
            System.err.println("正常购买测试失败：" + e.getMessage());
        }
    }

    /**
     * 测试库存不足场景（优化：增加参数校验）
     */
    private static void testInsufficientInventory(Integer buyerId, Integer productId) {
        System.out.println("\n===== 测试库存不足场景 =====");
        Inventory inventory = inventoryDao.selectByProductId(productId);
        if (inventory == null) {
            System.err.println("库存信息不存在，测试无法进行");
            return;
        }

        int purchaseQuantity = inventory.getQuantity() + 1; // 购买数量大于库存
        System.out.printf("尝试购买数量：%d（当前库存：%d）%n", purchaseQuantity, inventory.getQuantity());

        try {
            productService.purchaseProduct(productId, buyerId, purchaseQuantity);
            System.err.println("库存不足测试失败：未抛出预期异常");
        } catch (RuntimeException e) {
            System.out.println("库存不足测试成功，捕获预期异常：" + e.getMessage());
        }
    }

    /**
     * 测试余额不足场景（优化：处理除法异常）
     */
    private static void testInsufficientBalance(Integer buyerId, Integer productId) {
        System.out.println("\n===== 测试余额不足场景 =====");
        Product product = productsDao.selectById(productId);
        BankAccount buyerAccount = bankAccountDao.selectByUserId(buyerId);

        if (product == null || buyerAccount == null) {
            System.err.println("商品或买家账户信息不存在，测试无法进行");
            return;
        }

        // 处理价格为0的极端情况（虽然初始化时有校验，但避免运行时异常）
        if (product.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            System.err.println("商品价格异常，测试无法进行");
            return;
        }

        // 计算一个大于买家余额的购买数量（优化除法逻辑）
        int maxAffordableQuantity = buyerAccount.getBalance()
                .divide(product.getPrice(), 0, BigDecimal.ROUND_DOWN)
                .intValue();
        int purchaseQuantity = maxAffordableQuantity + 1;

        System.out.printf("买家余额：%.2f, 商品单价：%.2f, 尝试购买数量：%d（可负担最大数量：%d）%n",
                buyerAccount.getBalance(), product.getPrice(), purchaseQuantity, maxAffordableQuantity);

        try {
            productService.purchaseProduct(productId, buyerId, purchaseQuantity);
            System.err.println("余额不足测试失败：未抛出预期异常");
        } catch (RuntimeException e) {
            System.out.println("余额不足测试成功，捕获预期异常：" + e.getMessage());
        }
    }
}