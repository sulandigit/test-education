package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobLogReport;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 任务执行日志报告数据访问对象
 * 负责任务执行情况的统计报告数据管理
 * 提供按日统计的任务执行成功、失败率等数据
 * 
 * @author xuxueli
 * @since 2019-11-22
 */
@Mapper
public interface XxlJobLogReportDao {

	/**
	 * 保存日志报告数据
	 * 创建新的日志统计报告记录
	 * 
	 * @param xxlJobLogReport 日志报告实体
	 * @return 影响的行数
	 */
	public int save(XxlJobLogReport xxlJobLogReport);

	/**
	 * 更新日志报告数据
	 * 更新已存在的日志统计报告记录
	 * 
	 * @param xxlJobLogReport 日志报告实体
	 * @return 影响的行数
	 */
	public int update(XxlJobLogReport xxlJobLogReport);

	/**
	 * 查询指定时间范围内的日志报告
	 * 获取指定日期范围内的任务执行统计数据
	 * 
	 * @param triggerDayFrom 统计开始日期
	 * @param triggerDayTo 统计结束日期
	 * @return 日志报告列表，按日期排序
	 */
	public List<XxlJobLogReport> queryLogReport(@Param("triggerDayFrom") Date triggerDayFrom,
												@Param("triggerDayTo") Date triggerDayTo);

	/**
	 * 查询总体日志报告
	 * 获取所有时间范围内的任务执行统计汇总数据
	 * 
	 * @return 总体统计报告，包含所有日期的汇总数据
	 */
	public XxlJobLogReport queryLogReportTotal();

}
