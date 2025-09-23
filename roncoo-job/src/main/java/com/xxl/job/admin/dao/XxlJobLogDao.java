package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 任务执行日志数据访问对象
 * 负责任务执行日志的增删改查、统计分析等操作
 * 
 * @author xuxueli
 * @since 2016-01-12 18:03:06
 */
@Mapper
public interface XxlJobLogDao {

	/**
	 * 分页查询任务执行日志列表
	 * 注意：如果指定jobId则使用jobId进行精确查找，否则使用jobGroup进行范围查找
	 * 
	 * @param offset 偏移量
	 * @param pagesize 每页大小
	 * @param jobGroup 执行器组ID（当jobId不存在时使用）
	 * @param jobId 任务ID（存在时优先使用）
	 * @param triggerTimeStart 触发时间范围开始
	 * @param triggerTimeEnd 触发时间范围结束
	 * @param logStatus 日志状态（1=成功，2=失败，3=运行中）
	 * @return 任务执行日志列表
	 */
	// exist jobId not use jobGroup, not exist use jobGroup
	public List<XxlJobLog> pageList(@Param("offset") int offset,
									@Param("pagesize") int pagesize,
									@Param("jobGroup") int jobGroup,
									@Param("jobId") int jobId,
									@Param("triggerTimeStart") Date triggerTimeStart,
									@Param("triggerTimeEnd") Date triggerTimeEnd,
									@Param("logStatus") int logStatus);
	/**
	 * 分页查询任务执行日志总数
	 * 
	 * @param offset 偏移量
	 * @param pagesize 每页大小
	 * @param jobGroup 执行器组ID（当jobId不存在时使用）
	 * @param jobId 任务ID（存在时优先使用）
	 * @param triggerTimeStart 触发时间范围开始
	 * @param triggerTimeEnd 触发时间范围结束
	 * @param logStatus 日志状态（1=成功，2=失败，3=运行中）
	 * @return 符合条件的总记录数
	 */
	public int pageListCount(@Param("offset") int offset,
							 @Param("pagesize") int pagesize,
							 @Param("jobGroup") int jobGroup,
							 @Param("jobId") int jobId,
							 @Param("triggerTimeStart") Date triggerTimeStart,
							 @Param("triggerTimeEnd") Date triggerTimeEnd,
							 @Param("logStatus") int logStatus);
	
	/**
	 * 根据ID加载任务执行日志
	 * 
	 * @param id 日志ID
	 * @return 任务执行日志实体，若不存在则返回null
	 */
	public XxlJobLog load(@Param("id") long id);

	/**
	 * 保存任务执行日志
	 * 
	 * @param xxlJobLog 任务执行日志实体
	 * @return 生成的主键ID
	 */
	public long save(XxlJobLog xxlJobLog);

	/**
	 * 更新任务触发信息
	 * 更新任务的触发时间、触发结果等信息
	 * 
	 * @param xxlJobLog 包含触发信息的日志实体
	 * @return 影响的行数
	 */
	public int updateTriggerInfo(XxlJobLog xxlJobLog);

	/**
	 * 更新任务处理信息
	 * 更新任务的处理时间、处理结果、处理日志等信息
	 * 
	 * @param xxlJobLog 包含处理信息的日志实体
	 * @return 影响的行数
	 */
	public int updateHandleInfo(XxlJobLog xxlJobLog);
	
	/**
	 * 根据任务ID删除执行日志
	 * 
	 * @param jobId 任务ID
	 * @return 影响的行数
	 */
	public int delete(@Param("jobId") int jobId);

	/**
	 * 查询日志报告统计信息
	 * 统计指定时间范围内的任务执行情况
	 * 
	 * @param from 统计开始时间
	 * @param to 统计结束时间
	 * @return 包含成功数、失败数等统计信息的Map
	 */
	public Map<String, Object> findLogReport(@Param("from") Date from,
											 @Param("to") Date to);

	/**
	 * 查找需要清理的日志ID列表
	 * 根据清理策略查找过期或超量的日志
	 * 
	 * @param jobGroup 执行器组ID
	 * @param jobId 任务ID
	 * @param clearBeforeTime 清理该时间点之前的日志
	 * @param clearBeforeNum 保留最新的N条日志
	 * @param pagesize 每次清理的数量限制
	 * @return 需要清理的日志ID列表
	 */
	public List<Long> findClearLogIds(@Param("jobGroup") int jobGroup,
									  @Param("jobId") int jobId,
									  @Param("clearBeforeTime") Date clearBeforeTime,
									  @Param("clearBeforeNum") int clearBeforeNum,
									  @Param("pagesize") int pagesize);
	/**
	 * 批量清理指定的日志
	 * 
	 * @param logIds 需要删除的日志ID列表
	 * @return 影响的行数
	 */
	public int clearLog(@Param("logIds") List<Long> logIds);

	/**
	 * 查找失败的任务日志ID列表
	 * 用于失败重试或失败告警处理
	 * 
	 * @param pagesize 每次查询的数量限制
	 * @return 失败的任务日志ID列表
	 */
	public List<Long> findFailJobLogIds(@Param("pagesize") int pagesize);

	/**
	 * 更新告警状态
	 * 修改日志的告警状态，用于控制告警重复发送
	 * 
	 * @param logId 日志ID
	 * @param oldAlarmStatus 旧的告警状态
	 * @param newAlarmStatus 新的告警状态
	 * @return 影响的行数
	 */
	public int updateAlarmStatus(@Param("logId") long logId,
								 @Param("oldAlarmStatus") int oldAlarmStatus,
								 @Param("newAlarmStatus") int newAlarmStatus);

	/**
	 * 查找丢失的任务ID列表
	 * 查找触发了但没有执行结果的任务，可能是执行器宕机或网络问题导致
	 * 
	 * @param losedTime 丢失判断时间点，该时间之前触发但未执行的任务认为是丢失的
	 * @return 丢失的任务ID列表
	 */
	public List<Long> findLostJobIds(@Param("losedTime") Date losedTime);

}
