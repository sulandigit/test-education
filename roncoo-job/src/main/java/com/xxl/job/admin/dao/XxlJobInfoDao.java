package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 任务信息数据访问对象
 * 负责任务信息的增删改查、调度等操作
 * 
 * @author xuxueli
 * @since 2016-01-12 18:03:45
 */
@Mapper
public interface XxlJobInfoDao {

	/**
	 * 分页查询任务信息列表
	 * 
	 * @param offset 偏移量
	 * @param pagesize 每页大小
	 * @param jobGroup 执行器主键ID（可选）
	 * @param triggerStatus 触发状态（可选）0=停止，1=运行）
	 * @param jobDesc 任务描述（模糊匹配，可选）
	 * @param executorHandler 执行JobHandler（模糊匹配，可选）
	 * @param author 作者（模糊匹配，可选）
	 * @return 任务信息列表
	 */
	public List<XxlJobInfo> pageList(@Param("offset") int offset,
									 @Param("pagesize") int pagesize,
									 @Param("jobGroup") int jobGroup,
									 @Param("triggerStatus") int triggerStatus,
									 @Param("jobDesc") String jobDesc,
									 @Param("executorHandler") String executorHandler,
									 @Param("author") String author);
	/**
	 * 分页查询任务信息总数
	 * 
	 * @param offset 偏移量
	 * @param pagesize 每页大小
	 * @param jobGroup 执行器主键ID（可选）
	 * @param triggerStatus 触发状态（可选）0=停止，1=运行）
	 * @param jobDesc 任务描述（模糊匹配，可选）
	 * @param executorHandler 执行JobHandler（模糊匹配，可选）
	 * @param author 作者（模糊匹配，可选）
	 * @return 符合条件的总记录数
	 */
	public int pageListCount(@Param("offset") int offset,
							 @Param("pagesize") int pagesize,
							 @Param("jobGroup") int jobGroup,
							 @Param("triggerStatus") int triggerStatus,
							 @Param("jobDesc") String jobDesc,
							 @Param("executorHandler") String executorHandler,
							 @Param("author") String author);
	
	/**
	 * 保存新的任务信息
	 * 
	 * @param info 任务信息实体
	 * @return 影响的行数
	 */
	public int save(XxlJobInfo info);

	/**
	 * 根据ID加载任务信息
	 * 
	 * @param id 任务ID
	 * @return 任务信息实体，若不存在则返回null
	 */
	public XxlJobInfo loadById(@Param("id") int id);
	
	/**
	 * 更新任务信息
	 * 
	 * @param xxlJobInfo 任务信息实体
	 * @return 影响的行数
	 */
	public int update(XxlJobInfo xxlJobInfo);
	
	/**
	 * 删除任务信息
	 * 
	 * @param id 任务ID
	 * @return 影响的行数
	 */
	public int delete(@Param("id") long id);

	/**
	 * 根据执行器组获取任务列表
	 * 
	 * @param jobGroup 执行器组ID
	 * @return 该组下的所有任务列表
	 */
	public List<XxlJobInfo> getJobsByGroup(@Param("jobGroup") int jobGroup);

	/**
	 * 获取所有任务的总数
	 * 
	 * @return 任务总数
	 */
	public int findAllCount();

	/**
	 * 查询需要调度的任务
	 * 根据下次触发时间查询需要执行的任务
	 * 
	 * @param maxNextTime 最大下次触发时间（时间戳）
	 * @param pagesize 每页大小，限制查询数量
	 * @return 需要调度的任务列表
	 */
	public List<XxlJobInfo> scheduleJobQuery(@Param("maxNextTime") long maxNextTime, @Param("pagesize") int pagesize );

	/**
	 * 更新任务调度信息
	 * 主要用于更新任务的下次触发时间等调度相关信息
	 * 
	 * @param xxlJobInfo 任务信息实体，包含新的调度信息
	 * @return 影响的行数
	 */
	public int scheduleUpdate(XxlJobInfo xxlJobInfo);


}
