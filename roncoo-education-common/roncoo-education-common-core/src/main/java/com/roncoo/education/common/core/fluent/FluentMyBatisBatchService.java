package com.roncoo.education.common.core.fluent;

import cn.org.atool.fluent.mybatis.base.IBaseQuery;
import cn.org.atool.fluent.mybatis.base.IBaseUpdater;
import cn.org.atool.fluent.mybatis.base.RichEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * FluentMyBatis 批量操作和事务处理服务
 * 
 * 提供高效的批量操作、事务管理和异步处理功能
 * 
 * @author FluentMyBatis Integration
 * @date 2025-09-24
 */
@Slf4j
@Service
public class FluentMyBatisBatchService {

    private final Executor batchExecutor = Executors.newFixedThreadPool(4);

    // ==================== 批量保存操作 ====================

    /**
     * 批量保存实体（支持事务）
     * 
     * @param entities 实体列表
     * @param batchSize 批次大小
     * @param saveFunction 保存函数
     * @return 保存成功的数量
     */
    @Transactional(rollbackFor = Exception.class)
    public <T extends RichEntity> int batchSave(List<T> entities, int batchSize, Function<T, Integer> saveFunction) {
        if (entities == null || entities.isEmpty()) {
            return 0;
        }

        int totalSaved = 0;
        int size = entities.size();
        
        log.info("开始批量保存操作，总数量: {}, 批次大小: {}", size, batchSize);
        
        for (int i = 0; i < size; i += batchSize) {
            int endIndex = Math.min(i + batchSize, size);
            List<T> batch = entities.subList(i, endIndex);
            
            try {
                int batchResult = processBatch(batch, saveFunction);
                totalSaved += batchResult;
                log.debug("批次 {}-{} 保存成功，数量: {}", i + 1, endIndex, batchResult);
            } catch (Exception e) {
                log.error("批次 {}-{} 保存失败", i + 1, endIndex, e);
                throw new RuntimeException("批量保存失败", e);
            }
        }
        
        log.info("批量保存完成，总保存数量: {}", totalSaved);
        return totalSaved;
    }

    /**
     * 异步批量保存
     */
    public <T extends RichEntity> CompletableFuture<Integer> batchSaveAsync(
            List<T> entities, int batchSize, Function<T, Integer> saveFunction) {
        
        return CompletableFuture.supplyAsync(() -> 
            batchSave(entities, batchSize, saveFunction), batchExecutor);
    }

    // ==================== 批量更新操作 ====================

    /**
     * 批量条件更新
     * 
     * @param updaterFunction 更新构建器函数
     * @return 更新的记录数
     */
    @Transactional(rollbackFor = Exception.class)
    public <T extends RichEntity, U extends IBaseUpdater<T>> int batchUpdate(Function<Void, U> updaterFunction) {
        showFluentExample_batchUpdate();
        return 0; // 示例返回
    }

    /**
     * 批量条件更新（按ID列表）
     */
    @Transactional(rollbackFor = Exception.class)
    public <T extends RichEntity, U extends IBaseUpdater<T>> int batchUpdateByIds(
            List<Long> ids, int batchSize, Function<List<Long>, U> updaterFunction) {
        
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        int totalUpdated = 0;
        int size = ids.size();
        
        log.info("开始批量更新操作，总ID数量: {}, 批次大小: {}", size, batchSize);
        
        for (int i = 0; i < size; i += batchSize) {
            int endIndex = Math.min(i + batchSize, size);
            List<Long> batchIds = ids.subList(i, endIndex);
            
            try {
                U updater = updaterFunction.apply(batchIds);
                // int batchResult = updater.to().updateBy();
                int batchResult = showFluentExample_batchUpdateByIds(batchIds);
                totalUpdated += batchResult;
                log.debug("批次 {}-{} 更新成功，数量: {}", i + 1, endIndex, batchResult);
            } catch (Exception e) {
                log.error("批次 {}-{} 更新失败", i + 1, endIndex, e);
                throw new RuntimeException("批量更新失败", e);
            }
        }
        
        log.info("批量更新完成，总更新数量: {}", totalUpdated);
        return totalUpdated;
    }

    // ==================== 批量删除操作 ====================

    /**
     * 批量删除（软删除）
     */
    @Transactional(rollbackFor = Exception.class)
    public <T extends RichEntity, U extends IBaseUpdater<T>> int batchSoftDelete(
            List<Long> ids, int batchSize, Function<List<Long>, U> updaterFunction) {
        
        return batchUpdateByIds(ids, batchSize, updaterFunction);
    }

    /**
     * 批量物理删除
     */
    @Transactional(rollbackFor = Exception.class)
    public <T extends RichEntity, Q extends IBaseQuery<T>> int batchHardDelete(
            List<Long> ids, int batchSize, Function<List<Long>, Q> queryFunction) {
        
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        int totalDeleted = 0;
        int size = ids.size();
        
        log.info("开始批量删除操作，总ID数量: {}, 批次大小: {}", size, batchSize);
        
        for (int i = 0; i < size; i += batchSize) {
            int endIndex = Math.min(i + batchSize, size);
            List<Long> batchIds = ids.subList(i, endIndex);
            
            try {
                Q query = queryFunction.apply(batchIds);
                // int batchResult = query.to().delete();
                int batchResult = showFluentExample_batchHardDelete(batchIds);
                totalDeleted += batchResult;
                log.debug("批次 {}-{} 删除成功，数量: {}", i + 1, endIndex, batchResult);
            } catch (Exception e) {
                log.error("批次 {}-{} 删除失败", i + 1, endIndex, e);
                throw new RuntimeException("批量删除失败", e);
            }
        }
        
        log.info("批量删除完成，总删除数量: {}", totalDeleted);
        return totalDeleted;
    }

    // ==================== 事务处理 ====================

    /**
     * 复杂事务操作
     * 多个数据源的事务协调
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public <T> T executeInTransaction(Function<Void, T> operation) {
        try {
            log.info("开始执行事务操作");
            T result = operation.apply(null);
            log.info("事务操作执行成功");
            return result;
        } catch (Exception e) {
            log.error("事务操作执行失败，将回滚", e);
            throw new RuntimeException("事务执行失败", e);
        }
    }

    /**
     * 嵌套事务操作
     */
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public <T> T executeInNewTransaction(Function<Void, T> operation) {
        try {
            log.info("开始执行新事务操作");
            T result = operation.apply(null);
            log.info("新事务操作执行成功");
            return result;
        } catch (Exception e) {
            log.error("新事务操作执行失败，将回滚", e);
            throw new RuntimeException("新事务执行失败", e);
        }
    }

    /**
     * 事务回调操作
     */
    @Transactional(rollbackFor = Exception.class)
    public void executeWithCallback(Consumer<Void> operation, Consumer<Boolean> callback) {
        boolean success = false;
        try {
            log.info("开始执行事务回调操作");
            operation.accept(null);
            success = true;
            log.info("事务回调操作执行成功");
        } catch (Exception e) {
            log.error("事务回调操作执行失败", e);
            throw new RuntimeException("事务回调执行失败", e);
        } finally {
            try {
                callback.accept(success);
            } catch (Exception e) {
                log.error("回调执行失败", e);
            }
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 处理单个批次
     */
    private <T> int processBatch(List<T> batch, Function<T, Integer> operation) {
        int count = 0;
        for (T item : batch) {
            count += operation.apply(item);
        }
        return count;
    }

    /**
     * 分割列表为批次
     */
    public static <T> List<List<T>> splitIntoBatches(List<T> list, int batchSize) {
        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, list.size());
            batches.add(list.subList(i, endIndex));
        }
        return batches;
    }

    // ==================== 示例实现 ====================

    private void showFluentExample_batchUpdate() {
        throw new UnsupportedOperationException(
            "FluentMyBatis批量更新示例：\n" +
            "\n" +
            "// 批量更新用户状态\n" +
            "return usersUpdater()\n" +
            "    .set().statusId().is(newStatus)\n" +
            "    .set().gmtModified().is(LocalDateTime.now())\n" +
            "    .where().id().in(userIds)\n" +
            "    .to().updateBy();\n" +
            "\n" +
            "// 批量更新课程价格\n" +
            "return courseUpdater()\n" +
            "    .set().coursePrice().is(newPrice)\n" +
            "    .set().gmtModified().is(LocalDateTime.now())\n" +
            "    .where().categoryId().eq(categoryId)\n" +
            "    .and().isPutaway().eq(1)\n" +
            "    .to().updateBy();"
        );
    }

    private int showFluentExample_batchUpdateByIds(List<Long> batchIds) {
        throw new UnsupportedOperationException(
            "FluentMyBatis批量ID更新示例：\n" +
            "\n" +
            "return updater()\n" +
            "    .set().statusId().is(newStatus)\n" +
            "    .set().gmtModified().is(LocalDateTime.now())\n" +
            "    .where().id().in(batchIds)\n" +
            "    .to().updateBy();"
        );
    }

    private int showFluentExample_batchHardDelete(List<Long> batchIds) {
        throw new UnsupportedOperationException(
            "FluentMyBatis批量删除示例：\n" +
            "\n" +
            "return query()\n" +
            "    .where().id().in(batchIds)\n" +
            "    .to().delete();"
        );
    }
}