package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLogGlue;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * GLUE代码日志数据访问对象
 * 负责GLUE模式任务的代码版本日志管理
 * GLUE模式允许在管理台直接编写和管理任务代码
 * 
 * @author xuxueli
 * @since 2016-05-19 18:04:56
 */
@Mapper
public interface XxlJobLogGlueDao {
	
	/**
	 * 保存GLUE代码日志
	 * 记录GLUE模式任务的代码变更历史
	 * 
	 * @param xxlJobLogGlue GLUE代码日志实体
	 * @return 影响的行数
	 */
	public int save(XxlJobLogGlue xxlJobLogGlue);
	
	/**
	 * 根据任务ID查找GLUE代码日志列表
	 * 查询指定任务的所有GLUE代码版本历史
	 * 
	 * @param jobId 任务ID
	 * @return GLUE代码日志列表，按时间倒序排列
	 */
	public List<XxlJobLogGlue> findByJobId(@Param("jobId") int jobId);

	/**
	 * 清理过期GLUE代码日志
	 * 保留最新的limit条记录，删除其余的旧版本
	 * 
	 * @param jobId 任务ID
	 * @param limit 保留的记录数量
	 * @return 影响的行数
	 */
	public int removeOld(@Param("jobId") int jobId, @Param("limit") int limit);

	/**
	 * 根据任务ID删除所有GLUE代码日志
	 * 当任务被删除时，同时清理其所有的GLUE代码版本历史
	 * 
	 * @param jobId 任务ID
	 * @return 影响的行数
	 */
	public int deleteByJobId(@Param("jobId") int jobId);
	
}
