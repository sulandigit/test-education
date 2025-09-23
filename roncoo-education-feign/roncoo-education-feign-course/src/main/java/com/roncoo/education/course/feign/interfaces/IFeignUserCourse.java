package com.roncoo.education.course.feign.interfaces;

import com.roncoo.education.course.feign.interfaces.qo.UserCourseBindingQO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户课程关联操作 Feign 客户端接口
 * 
 * <p>本接口基于 Spring Cloud OpenFeign 实现，专门用于处理用户与课程之间的关联关系操作。
 * 主要功能是在用户购买或获取课程后，建立用户与课程的绑定关系，使用户能够正常访问和学习课程内容。</p>
 * 
 * <p><strong>目标服务：</strong> service-course</p>
 * <p><strong>服务路径：</strong> /course/user/course</p>
 * 
 * <p><strong>业务场景：</strong></p>
 * <ul>
 *   <li><strong>购课流程：</strong>用户成功支付课程费用后，系统自动调用该接口建立绑定关系</li>
 *   <li><strong>免费课程：</strong>用户领取免费课程时，直接建立绑定关系</li>
 *   <li><strong>赠送课程：</strong>管理员或系统为用户赠送课程时建立关系</li>
 *   <li><strong>权限验证：</strong>用户访问课程内容时，系统通过该关系验证访问权限</li>
 * </ul>
 * 
 * <p><strong>调用时机：</strong></p>
 * <ul>
 *   <li>支付成功回调时</li>
 *   <li>免费课程领取时</li>
 *   <li>课程赠送操作时</li>
 *   <li>管理员手动分配课程时</li>
 * </ul>
 * 
 * @author wujing
 * @date 2022-08-27
 * @version 1.0
 * @see UserCourseBindingQO 用户课程绑定请求对象
 * @since 1.0
 */
@FeignClient(value = "service-course", path = "/course/user/course")
public interface IFeignUserCourse {

    /**
     * 绑定用户与课程的关联关系
     * 
     * <p>此方法用于在用户获取课程后（通过购买、领取或赠送），在数据库中建立用户与课程的映射关系。
     * 该操作是用户能够正常访问和学习课程内容的前置条件。</p>
     * 
     * <p><strong>业务逻辑：</strong></p>
     * <ul>
     *   <li>检查用户和课程是否存在且状态有效</li>
     *   <li>验证是否已存在绑定关系，避免重复绑定</li>
     *   <li>根据购买类型（支付/免费）记录不同的获取方式</li>
     *   <li>更新课程的学习人数统计</li>
     * </ul>
     * 
     * <p><strong>返回值说明：</strong></p>
     * <ul>
     *   <li><code>1</code> - 绑定成功，新建了用户课程关系</li>
     *   <li><code>0</code> - 绑定失败，可能由于数据验证失败或系统异常</li>
     *   <li><code>2</code> - 关系已存在，无需重复绑定</li>
     * </ul>
     * 
     * <p><strong>调用示例：</strong></p>
     * <pre>{@code
     * UserCourseBindingQO bindingQO = new UserCourseBindingQO()
     *     .setUserId(10001L)
     *     .setCourseId(2001L)
     *     .setBuyType(1); // 1-支付购买
     * 
     * int result = feignUserCourse.binding(bindingQO);
     * if (result == 1) {
     *     // 绑定成功，用户可以开始学习课程
     * }
     * }</pre>
     * 
     * @param qo {@link UserCourseBindingQO} 用户课程绑定请求对象，包含用户ID、课程ID和购买类型
     * @return {@code int} 绑定操作结果码：
     *         <ul>
     *           <li>1 - 绑定成功</li>
     *           <li>0 - 绑定失败</li>
     *           <li>2 - 关系已存在</li>
     *         </ul>
     * @throws IllegalArgumentException 当qo为null或必要字段缺失时抛出
     * @throws BusinessException 当用户或课程不存在、状态异常时抛出
     * @see UserCourseBindingQO 请求参数对象的详细说明
     */
    @PutMapping(value = "/binding")
    int binding(@RequestBody UserCourseBindingQO qo);
}
