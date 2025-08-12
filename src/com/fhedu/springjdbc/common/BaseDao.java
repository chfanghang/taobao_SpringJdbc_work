// com/fhedu/springjdbc/common/BaseDao.java
package com.fhedu.springjdbc.common;

import java.util.List;

/**
 * 通用CRUD接口
 * @param <T> 实体类类型
 * @param <ID> 主键类型
 */
public interface BaseDao<T, ID> {
    // 新增
    void insert(T entity);

    // 根据ID删除
    void deleteById(ID id);

    // 更新
    void update(T entity);

    // 根据ID查询
    T selectById(ID id);

    // 查询所有
    List<T> selectAll();
}