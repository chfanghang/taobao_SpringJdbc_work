项目概述
本项目是一个基于 Spring JDBC 技术栈实现的模拟电商交易平台核心系统，聚焦于商品购买全流程的业务逻辑闭环与数据一致性保障。通过 Spring 框架的 IOC 容器管理组件依赖，结合 JDBC Template 实现高效数据访问，并通过声明式事务控制确保复杂交易场景下的数据准确性。项目完整模拟了买家下单、库存扣减、资金流转（买家付款、卖家收款、平台抽成）、订单与交易记录生成等核心环节，同时提供了完善的测试用例验证各类业务场景，是学习 Spring 数据访问与事务管理的典型实践案例。
核心功能与技术实现
1. 实体模型设计（baseClass 包）
实体类采用 POJO 设计，映射数据库表结构，包含完整的属性、构造器及 getter/setter 方法，支持与数据库字段的自动映射：
User：用户信息模型，包含 id、username、password、role（区分 "buyer" 和 "seller"）等核心属性，提供无参构造器及快速创建用户的有参构造器（username、password、role）。
Product：商品信息模型，关联卖家 ID（sellerId），包含 name、description、price 等属性，支持商品创建时间（createdAt）和更新时间（updatedAt）的自动记录。
Inventory：商品库存模型，核心关联 productId 与库存数量 quantity，提供通过商品 ID 和数量初始化库存的有参构造器。
BankAccount：用户银行账户模型，关联用户 ID（userId），包含 accountNumber（账号）和 balance（余额），支持通过用户 ID、账号、初始余额创建账户。
PlatformAccount：平台账户模型，固定 ID（PLATFORM_ID=1），包含 accountName 和 balance，用于收取交易手续费，提供通过账户名和余额初始化的有参构造器。
Order：订单模型，记录交易核心信息，包括 orderNumber（订单号）、buyerId、productId、quantity（购买数量）、totalAmount（总金额）、platformFee（平台手续费）、sellerAmount（卖家实际收入）及 status（状态）。
Transaction：交易记录模型，详细记录资金流转，关联 orderId、buyerAccountId、sellerAccountId、platformAccountId，包含交易金额、手续费、卖家收入等明细，生成唯一 transactionNumber 标识。
2. 数据访问层（Dao 包）
基于 Spring JDBC Template 实现数据持久化，所有 Dao 接口均继承 BaseDao（通用 CRUD），并扩展业务专属方法，确保数据操作的高效与规范：
通用能力：所有 Dao 实现 insert（插入并返回自增 ID）、deleteById、update、selectById、selectAll 方法，通过 BeanPropertyRowMapper 实现 ResultSet 到实体类的自动映射。
扩展方法：
BankAccountsDao：selectByUserId 按用户 ID 查询账户，支持快速获取买家 / 卖家的余额信息。
InventoryDao：reduceInventory 按商品 ID 扣减库存（update inventory set quantity = quantity - ? where product_id = ?），并自动更新 updated_at 时间戳。
PlatformAccountDao：addBalance 增加平台余额（update platform_account set balance = balance + ? where id = ?），关联固定平台 ID。
OrdersDao：selectByBuyerId 按买家 ID 查询订单列表，支持用户查看历史订单。
UsersDao：selectByRole 按角色（"buyer"/"seller"）查询用户，用于身份校验。
ProductsDao：selectBySellerId 按卖家 ID 查询商品，支持卖家管理商品。
TransactionsDao：selectByOrderId 按订单 ID 查询交易记录，实现订单与交易的关联追溯。
3. 业务服务层（service 包）
核心服务 ProductService 封装商品购买全流程，通过 Spring 声明式事务确保操作原子性，解决并发场景下的数据一致性问题：
事务配置：@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)，确保每个购买流程独立事务，隔离级别为可重复读，任何异常触发回滚。
平台抽成规则：固定抽成比例 10%（PLATFORM_FEE_RATE = 0.1），总金额 totalAmount = 商品单价 * 购买数量，平台手续费 platformFee = totalAmount * 0.1，卖家实际收入 sellerAmount = totalAmount - platformFee。
购买流程拆解：
参数校验：校验 productId、buyerId 非空，quantity 为正数。
基础数据查询：查询商品信息、买家 / 卖家信息、库存、账户余额等。
业务规则校验：
验证买家角色为 "buyer"；
库存充足性校验（库存数量 >= 购买数量）；
余额充足性校验（买家余额 >= 总金额）。
核心操作：
创建订单（生成唯一 orderNumber，状态设为 "completed"）；
更新账户余额（扣减买家余额、增加卖家余额、增加平台余额）；
扣减商品库存；
生成交易记录（关联订单与账户，记录资金明细）。
辅助功能：提供 getOrdersByBuyerId（查询买家订单）、getOrderById（查询单个订单）等接口，支持订单追溯。
4. 测试模块（Test 类）
通过单测验证核心业务场景，确保逻辑正确性与异常处理有效性，测试流程包含数据初始化、场景执行与结果校验：
测试数据初始化：
自动创建买家（初始余额 5000.00）、卖家（初始余额 0.00）及关联银行账户；
初始化平台账户（固定 ID=1，初始余额 0.00）；
创建测试商品（价格 200.00）及库存（初始库存 10）。
测试场景覆盖：
正常购买：验证购买后买家余额减少、卖家余额增加、平台手续费到账、库存扣减的准确性，输出详细变动明细（如 买家余额变化：5000.00 -> 4600.00 (减少: 400.00)）。
库存不足：购买数量 = 当前库存 + 1，验证是否抛出 "库存不足" 异常且事务回滚（库存、余额无变化）。
余额不足：通过计算使买家余额小于需支付金额，验证是否抛出 "余额不足" 异常且事务回滚。
测试保障：使用 try-with-resources 自动关闭 Spring 上下文，通过 Objects.requireNonNull 确保 Bean 加载成功，初始化数据时增加参数校验（如初始余额不能为负、商品价格必须大于 0）。
5. 配置与环境
数据库配置：通过 jdbc.properties 配置 MySQL 连接信息（支持 5.x 驱动）、C3P0 连接池参数（初始连接数 5，最大 20，最小 3 等）。
Spring 配置：applicationContext.xml 启用组件扫描（base-package="com.fhedu.springjdbc"）、加载数据库配置、配置 C3P0 数据源，自动管理 Dao 与 Service 组件依赖。
开发环境：Java 17、IntelliJ IDEA、Git 版本控制，日志框架 Logback 控制输出级别，Maven 管理依赖（本地仓库路径可配置）。
版本控制忽略：.gitignore 排除 IDE 配置文件（如 .idea/、out/）、编译产物、操作系统临时文件（如 .DS_Store）等。
项目价值
本项目通过模拟真实电商交易场景，完整展示了：
Spring JDBC 在数据访问中的实践（JDBC Template 简化 CRUD、BeanPropertyRowMapper 映射实体）；
声明式事务在复杂业务中的应用（隔离级别、传播行为、异常回滚）；
高并发场景下的数据一致性保障（库存扣减、资金流转的原子性）；
测试驱动开发思路（通过多场景测试验证业务逻辑）。
适合作为 Spring 框架初学者学习数据访问与事务管理的实战案例，也可为电商系统核心交易模块的设计提供参考。
