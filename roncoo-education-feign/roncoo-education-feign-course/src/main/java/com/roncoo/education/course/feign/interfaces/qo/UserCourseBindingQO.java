package com.roncoo.education.course.feign.interfaces.qo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 用户课程绑定请求对象（Query Object）
 * 
 * <p>本类用于封装用户与课程绑定操作的请求参数，在用户成功获取课程后（购买、领取或赠送），
 * 建立用户与课程的关联关系时使用。此对象包含了绑定操作所需的所有必要信息。</p>
 * 
 * <p><strong>数据来源：</strong> 订单系统、用户操作、管理员操作</p>
 * <p><strong>传输方向：</strong> 从各业务系统到课程管理服务（请求对象）</p>
 * <p><strong>序列化支持：</strong> 实现 {@link Serializable} 接口，支持网络传输</p>
 * 
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li><strong>订单支付成功：</strong>用户成功支付课程费用后，自动调用绑定接口</li>
 *   <li><strong>免费课程获取：</strong>用户点击领取免费课程时调用</li>
 *   <li><strong>课程赠送：</strong>管理员或系统为用户赠送课程时使用</li>
 *   <li><strong>批量分配：</strong>管理员批量为用户分配课程权限</li>
 * </ul>
 * 
 * <p><strong>数据流程：</strong></p>
 * <ol>
 *   <li>业务系统收集用户、课程和购买方式信息</li>
 *   <li>封装成 UserCourseBindingQO 对象</li>
 *   <li>通过 Feign 客户端发送到课程服务</li>
 *   <li>课程服务验证数据并建立绑定关系</li>
 * </ol>
 * 
 * @author wujing
 * @date 2022-08-27
 * @version 1.0
 * @see lombok.Data 自动生成 getter/setter 方法
 * @see lombok.experimental.Accessors 支持链式调用设置属性
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class UserCourseBindingQO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识
     * 
     * <p>指向需要绑定课程的用户的唯一标识符。
     * 此用户必须是系统中已注册且状态正常的用户。</p>
     * 
     * <p><strong>业务约束：</strong></p>
     * <ul>
     *   <li>必须为正数，不能为 null</li>
     *   <li>用户必须存在且状态为激活</li>
     *   <li>用户信息由 service-user 服务管理</li>
     * </ul>
     * 
     * @see 用户信息由用户管理服务统一管理
     */
    private Long userId;

    /**
     * 课程唯一标识
     * 
     * <p>指向需要与用户绑定的课程的唯一标识符。
     * 此课程必须是系统中已存在且状态正常的课程。</p>
     * 
     * <p><strong>业务约束：</strong></p>
     * <ul>
     *   <li>必须为正数，不能为 null</li>
     *   <li>课程必须存在且状态为正常</li>
     *   <li>课程信息由 service-course 服务管理</li>
     * </ul>
     * 
     * @see CourseViewVO 课程视图对象，包含课程详细信息
     */
    private Long courseId;

    /**
     * 购买类型标识
     * 
     * <p>标识用户获取课程的方式，用于区分不同的获取渠道和计费方式。
     * 此字段对于统计分析和财务管理具有重要意义。</p>
     * 
     * <p><strong>取值范围和含义：</strong></p>
     * <ul>
     *   <li><code>1</code> - <strong>支付购买：</strong>用户通过支付金额获取课程权限</li>
     *   <li><code>2</code> - <strong>免费获取：</strong>用户免费领取或被赠送课程权限</li>
     * </ul>
     * 
     * <p><strong>业务应用场景：</strong></p>
     * <ul>
     *   <li><strong>购买类型 1：</strong>用户通过支付宝、微信等方式支付后获取</li>
     *   <li><strong>购买类型 2：</strong>免费课程、体验课程、管理员赠送、活动获取等</li>
     * </ul>
     * 
     * <p><strong>数据统计价值：</strong></p>
     * 通过此字段可以统计各类课程的付费用户数、免费用户数，为运营决策提供数据支持。
     * 
     * <p><strong>验证规则：</strong></p>
     * <ul>
     *   <li>必须为正整数，不能为 null</li>
     *   <li>只能为 1 或 2，其他值无效</li>
     *   <li>必须与实际获取方式一致</li>
     * </ul>
     * 
     * @see 与课程的 isFree 字段相关联，但不完全一致
     */
    private Integer buyType;

}
