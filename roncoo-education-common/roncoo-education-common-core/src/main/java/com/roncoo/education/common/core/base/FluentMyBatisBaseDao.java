package com.roncoo.education.common.core.base;

import cn.org.atool.fluent.mybatis.base.IBaseQuery;
import cn.org.atool.fluent.mybatis.base.IBaseUpdater;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import cn.org.atool.fluent.mybatis.base.mapper.IEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

/**
 * FluentMyBatis 基础DAO抽象类
 * 
 * 提供统一的数据访问接口，集成FluentMyBatis的Query和Updater功能
 * 
 * @param <T> 实体类型
 * @param <Q> 查询构建器类型
 * @param <U> 更新构建器类型
 * 
 * @author FluentMyBatis Integration
 * @date 2025-09-24
 */
public abstract class FluentMyBatisBaseDao<T extends RichEntity, Q extends IBaseQuery<T>, U extends IBaseUpdater<T>> {

    @Autowired
    protected IEntityMapper<T> mapper;

    /**
     * 创建查询构建器
     * 子类需要实现此方法返回具体的Query实例
     */
    protected abstract Q query();

    /**
     * 创建更新构建器
     * 子类需要实现此方法返回具体的Updater实例
     */
    protected abstract U updater();

    // ==================== 基础CRUD操作 ====================

    /**
     * 保存实体
     */
    public int save(T entity) {
        return mapper.save(entity);
    }

    /**
     * 根据ID删除
     */
    public int deleteById(Serializable id) {
        return mapper.deleteById(id);
    }

    /**
     * 更新实体（根据主键）
     */
    public int updateById(T entity) {
        return mapper.updateById(entity);
    }

    /**
     * 根据ID查询
     */
    public T getById(Serializable id) {
        return mapper.findById(id);
    }

    /**
     * 查询所有记录
     */
    public List<T> listAll() {
        return query().to().listEntity();
    }

    // ==================== FluentMyBatis增强操作 ====================

    /**
     * 条件查询列表
     */
    public List<T> listByQuery(Q queryBuilder) {
        return queryBuilder.to().listEntity();
    }

    /**
     * 条件查询单个对象
     */
    public T getByQuery(Q queryBuilder) {
        return queryBuilder.to().findOne().orElse(null);
    }

    /**
     * 条件计数
     */
    public long countByQuery(Q queryBuilder) {
        return queryBuilder.to().count();
    }

    /**
     * 条件更新
     */
    public int updateByCondition(U updaterBuilder) {
        return updaterBuilder.to().updateBy();
    }

    /**
     * 条件删除
     */
    public int deleteByQuery(Q queryBuilder) {
        return queryBuilder.to().delete();
    }

    // ==================== 分页查询 ====================

    /**
     * 分页查询
     */
    public Page<T> page(int pageCurrent, int pageSize, Q queryBuilder) {
        // 获取总数
        long total = queryBuilder.to().count();
        
        // 检查分页参数
        pageSize = PageUtil.checkPageSize(pageSize);
        pageCurrent = PageUtil.checkPageCurrent((int) total, pageSize, pageCurrent);
        
        // 设置分页参数
        queryBuilder.limit((pageCurrent - 1) * pageSize, pageSize);
        
        // 查询数据
        List<T> records = queryBuilder.to().listEntity();
        
        // 计算总页数
        int totalPage = (int) Math.ceil((double) total / pageSize);
        
        return new Page<>(total, totalPage, pageCurrent, pageSize, records);
    }

    // ==================== 批量操作 ====================

    /**
     * 批量保存
     */
    public int saveBatch(List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        for (T entity : entities) {
            count += save(entity);
        }
        return count;
    }

    /**
     * 批量删除
     */
    public int deleteBatchByIds(List<? extends Serializable> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        
        int count = 0;
        for (Serializable id : ids) {
            count += deleteById(id);
        }
        return count;
    }

    // ==================== 业务查询模板方法 ====================

    /**
     * 根据状态查询
     * 子类可以重写此方法提供具体实现
     */
    public List<T> listByStatus(Integer status) {
        // 默认实现，子类可以重写
        throw new UnsupportedOperationException("子类需要实现状态查询方法");
    }

    /**
     * 根据时间范围查询
     * 子类可以重写此方法提供具体实现
     */
    public List<T> listByTimeRange(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime) {
        // 默认实现，子类可以重写
        throw new UnsupportedOperationException("子类需要实现时间范围查询方法");
    }
}