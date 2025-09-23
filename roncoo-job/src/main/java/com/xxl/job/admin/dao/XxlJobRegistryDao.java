package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobRegistry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 执行器注册中心数据访问对象
 * 负责执行器的注册、心跳监控和状态管理
 * 支持执行器的自动发现和故障检测
 * 
 * @author xuxueli
 * @since 2016-09-30
 */
@Mapper
public interface XxlJobRegistryDao {

    /**
     * 查找已死亡（超时）的执行器注册ID列表
     * 根据超时时间判断执行器是否已经断开连接
     * 
     * @param timeout 超时时间（秒）
     * @param nowTime 当前时间
     * @return 已死亡的执行器注册ID列表
     */
    public List<Integer> findDead(@Param("timeout") int timeout,
                                  @Param("nowTime") Date nowTime);

    /**
     * 批量删除已死亡的执行器注册信息
     * 清理已经失去联系的执行器记录
     * 
     * @param ids 需要删除的注册ID列表
     * @return 影响的行数
     */
    public int removeDead(@Param("ids") List<Integer> ids);

    /**
     * 查找所有活跃的执行器注册信息
     * 获取在指定超时时间内有心跳的执行器列表
     * 
     * @param timeout 超时时间（秒）
     * @param nowTime 当前时间
     * @return 活跃的执行器注册信息列表
     */
    public List<XxlJobRegistry> findAll(@Param("timeout") int timeout,
                                        @Param("nowTime") Date nowTime);

    /**
     * 更新执行器注册信息
     * 更新已存在的执行器注册记录的心跳时间
     * 
     * @param registryGroup 注册组名（通常为'EXECUTOR'）
     * @param registryKey 注册键（执行器AppName）
     * @param registryValue 注册值（执行器地址）
     * @param updateTime 更新时间
     * @return 影响的行数
     */
    public int registryUpdate(@Param("registryGroup") String registryGroup,
                              @Param("registryKey") String registryKey,
                              @Param("registryValue") String registryValue,
                              @Param("updateTime") Date updateTime);

    /**
     * 新增执行器注册信息
     * 当执行器首次注册时创建新的注册记录
     * 
     * @param registryGroup 注册组名（通常为'EXECUTOR'）
     * @param registryKey 注册键（执行器AppName）
     * @param registryValue 注册值（执行器地址）
     * @param updateTime 创建时间
     * @return 影响的行数
     */
    public int registrySave(@Param("registryGroup") String registryGroup,
                            @Param("registryKey") String registryKey,
                            @Param("registryValue") String registryValue,
                            @Param("updateTime") Date updateTime);

    /**
     * 删除执行器注册信息
     * 当执行器下线或停止时主动清理注册信息
     * 
     * @param registryGroup 注册组名（通常为'EXECUTOR'）
     * @param registryKey 注册键（执行器AppName）
     * @param registryValue 注册值（执行器地址）
     * @return 影响的行数
     */
    public int registryDelete(@Param("registryGroup") String registryGroup,
                          @Param("registryKey") String registryKey,
                          @Param("registryValue") String registryValue);

}
