package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 用户管理数据访问对象
 * 负责系统用户的增删改查、身份验证等操作
 * 支持用户权限管理和角色控制
 * 
 * @author xuxueli
 * @since 2019-05-04 16:44:59
 */
@Mapper
public interface XxlJobUserDao {

	/**
	 * 分页查询用户列表
	 * 支持按用户名和角色进行过滤查询
	 * 
	 * @param offset 偏移量
	 * @param pagesize 每页大小
	 * @param username 用户名（模糊匹配，可选）
	 * @param role 用户角色（0=管理员，1=普通用户，可选）
	 * @return 用户列表
	 */
	public List<XxlJobUser> pageList(@Param("offset") int offset,
                                     @Param("pagesize") int pagesize,
                                     @Param("username") String username,
									 @Param("role") int role);
	/**
	 * 分页查询用户总数
	 * 
	 * @param offset 偏移量
	 * @param pagesize 每页大小
	 * @param username 用户名（模糊匹配，可选）
	 * @param role 用户角色（0=管理员，1=普通用户，可选）
	 * @return 符合条件的总记录数
	 */
	public int pageListCount(@Param("offset") int offset,
							 @Param("pagesize") int pagesize,
							 @Param("username") String username,
							 @Param("role") int role);

	/**
	 * 根据用户名加载用户信息
	 * 主要用于用户登录验证时的身份检查
	 * 
	 * @param username 用户名
	 * @return 用户信息实体，若不存在则返回null
	 */
	public XxlJobUser loadByUserName(@Param("username") String username);

	/**
	 * 保存新用户
	 * 创建新的系统用户账号
	 * 
	 * @param xxlJobUser 用户实体
	 * @return 影响的行数
	 */
	public int save(XxlJobUser xxlJobUser);

	/**
	 * 更新用户信息
	 * 修改用户的基本信息、密码或角色等
	 * 
	 * @param xxlJobUser 用户实体
	 * @return 影响的行数
	 */
	public int update(XxlJobUser xxlJobUser);
	
	/**
	 * 删除用户
	 * 根据用户ID删除用户账号
	 * 
	 * @param id 用户ID
	 * @return 影响的行数
	 */
	public int delete(@Param("id") int id);

}
