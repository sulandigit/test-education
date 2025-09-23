package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务分组数据访问对象
 * 负责执行器组的增删改查操作
 * 
 * @author xuxueli
 * @since 2016-09-30
 */
@Mapper
public interface XxlJobGroupDao {

    /**
     * 查询所有执行器分组
     * 
     * @return 所有执行器分组列表
     */
    public List<XxlJobGroup> findAll();

    /**
     * 根据地址类型查询执行器分组
     * 
     * @param addressType 地址类型（0=自动注册，1=手动录入）
     * @return 符合条件的执行器分组列表
     */
    public List<XxlJobGroup> findByAddressType(@Param("addressType") int addressType);

    /**
     * 保存新的执行器分组
     * 
     * @param xxlJobGroup 执行器分组实体
     * @return 影响的行数
     */
    public int save(XxlJobGroup xxlJobGroup);

    /**
     * 更新执行器分组信息
     * 
     * @param xxlJobGroup 执行器分组实体
     * @return 影响的行数
     */
    public int update(XxlJobGroup xxlJobGroup);

    /**
     * 删除执行器分组
     * 
     * @param id 执行器分组ID
     * @return 影响的行数
     */
    public int remove(@Param("id") int id);

    /**
     * 根据ID加载执行器分组
     * 
     * @param id 执行器分组ID
     * @return 执行器分组实体，若不存在则返回null
     */
    public XxlJobGroup load(@Param("id") int id);

    /**
     * 分页查询执行器分组列表
     * 
     * @param offset 偏移量
     * @param pagesize 每页大小
     * @param appname 应用名称（模糊匹配）
     * @param title 执行器名称（模糊匹配）
     * @return 分页查询结果列表
     */
    public List<XxlJobGroup> pageList(@Param("offset") int offset,
                                      @Param("pagesize") int pagesize,
                                      @Param("appname") String appname,
                                      @Param("title") String title);

    /**
     * 分页查询执行器分组总数
     * 
     * @param offset 偏移量
     * @param pagesize 每页大小
     * @param appname 应用名称（模糊匹配）
     * @param title 执行器名称（模糊匹配）
     * @return 符合条件的总记录数
     */
    public int pageListCount(@Param("offset") int offset,
                             @Param("pagesize") int pagesize,
                             @Param("appname") String appname,
                             @Param("title") String title);

}
