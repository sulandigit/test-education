package com.roncoo.education.course.feign.interfaces;

import com.roncoo.education.course.feign.interfaces.vo.CourseViewVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * 课程信息查询 Feign 客户端接口
 * 
 * <p>本接口基于 Spring Cloud OpenFeign 实现，用于微服务间的课程信息查询调用。
 * 通过声明式的方式定义了对课程服务的远程调用接口，支持单个课程查询和批量课程查询功能。</p>
 * 
 * <p><strong>目标服务：</strong> service-course</p>
 * <p><strong>服务路径：</strong> /course/course</p>
 * 
 * <p><strong>主要功能：</strong></p>
 * <ul>
 *   <li>根据课程ID获取单个课程的详细信息</li>
 *   <li>根据课程ID列表批量获取多个课程信息</li>
 * </ul>
 * 
 * <p><strong>使用场景：</strong></p>
 * <ul>
 *   <li>用户浏览课程详情页面时获取课程信息</li>
 *   <li>课程列表页面批量加载课程基本信息</li>
 *   <li>其他微服务需要课程信息进行业务处理</li>
 * </ul>
 * 
 * @author wujing
 * @date 2022-08-27
 * @version 1.0
 * @see CourseViewVO 课程视图对象
 * @since 1.0
 */
@FeignClient(value = "service-course", path = "/course/course")
public interface IFeignCourse {

    /**
     * 根据课程主键ID获取课程详细信息
     * 
     * <p>通过课程的唯一标识ID查询课程的完整信息，包括课程基本属性、价格信息、统计数据等。
     * 该方法适用于课程详情页展示、课程信息校验等场景。</p>
     * 
     * <p><strong>调用示例：</strong></p>
     * <pre>{@code
     * CourseViewVO course = feignCourse.getById(1001L);
     * if (course != null && course.getStatusId() == 1) {
     *     // 课程存在且状态正常，进行后续业务处理
     * }
     * }</pre>
     * 
     * @param id 课程主键ID，必须为正数，不能为null
     * @return {@link CourseViewVO} 课程视图对象，包含课程的所有基本信息；
     *         如果课程不存在或已被删除，返回null
     * @throws IllegalArgumentException 当id参数为null或非正数时抛出
     * @see CourseViewVO 返回的课程视图对象详细说明
     */
    @GetMapping(value = "/get/{id}")
    CourseViewVO getById(@PathVariable(value = "id") Long id);

    /**
     * 根据课程ID列表批量获取课程信息
     * 
     * <p>通过课程ID列表批量查询多个课程的详细信息，用于提高查询效率，避免多次单独调用。
     * 该方法特别适用于课程列表页面、推荐课程展示、购物车课程信息获取等需要同时展示多个课程的场景。</p>
     * 
     * <p><strong>性能注意事项：</strong></p>
     * <ul>
     *   <li>建议单次查询的课程ID数量控制在100个以内，避免查询超时</li>
     *   <li>返回结果的顺序可能与输入ID列表顺序不一致</li>
     *   <li>如果某个课程ID对应的课程不存在，该课程不会出现在返回结果中</li>
     * </ul>
     * 
     * <p><strong>调用示例：</strong></p>
     * <pre>{@code
     * List<Long> courseIds = Arrays.asList(1001L, 1002L, 1003L);
     * List<CourseViewVO> courses = feignCourse.listByIds(courseIds);
     * // 处理返回的课程列表，注意检查返回数量是否与请求数量一致
     * }</pre>
     * 
     * @param courseIds 课程ID列表，不能为null，列表中的ID必须为正数
     * @return {@link List}<{@link CourseViewVO}> 课程视图对象列表，
     *         只包含存在且状态正常的课程信息；
     *         如果所有课程都不存在，返回空列表（非null）
     * @throws IllegalArgumentException 当courseIds为null或包含无效ID时抛出
     * @see CourseViewVO 返回列表中课程对象的详细说明
     */
    @PostMapping(value = "/listByIds")
    List<CourseViewVO> listByIds(@RequestBody List<Long> courseIds);
}
