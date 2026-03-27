package com.roncoo.education.course.feign.interfaces.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程信息视图对象（Value Object）
 * 
 * <p>本类用于在微服务间传输课程的完整信息，包含课程的所有基本属性、价格信息、状态信息和统计数据。
 * 主要用于课程查询接口的返回结果，支持在网络间传输和序列化。</p>
 * 
 * <p><strong>数据来源：</strong> 课程管理服务（service-course）</p>
 * <p><strong>传输方向：</strong> 从课程服务到其他微服务（响应对象）</p>
 * <p><strong>序列化支持：</strong> 实现 {@link Serializable} 接口，支持 Java 序列化和 JSON 序列化</p>
 * 
 * @author wujing
 * @date 2022-08-27
 * @version 1.0
 * @since 1.0
 */
@Data
@Accessors(chain = true)
public class CourseViewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 课程主键标识
     * 
     * <p>课程的唯一标识符，由系统自动生成的自增主键。
     * 用于在整个系统中唯一标识一个课程。</p>
     */
    private Long id;

    /**
     * 课程创建时间
     * 
     * <p>记录课程在系统中的创建时间，用于数据追溯和按时间排序。</p>
     */
    private LocalDateTime gmtCreate;

    /**
     * 课程最后修改时间
     * 
     * <p>记录课程信息的最近一次修改时间，用于版本控制和数据同步。</p>
     */
    private LocalDateTime gmtModified;

    /**
     * 课程状态标识
     * 
     * <p>表示课程当前的可用状态，影响课程是否可以被用户查看和购买。</p>
     * <ul>
     *   <li><code>1</code> - 正常状态，课程可以正常展示和购买</li>
     *   <li><code>0</code> - 禁用状态，课程不可见且不可购买</li>
     * </ul>
     */
    private Integer statusId;

    /**
     * 课程排序权重
     * 
     * <p>用于控制课程在列表中的显示顺序，数值越小排序越靠前。</p>
     */
    private Integer sort;

    /**
     * 课程讲师标识
     * 
     * <p>指向负责授课此课程的讲师的唯一标识符。</p>
     */
    private Long lecturerId;

    /**
     * 课程分类标识
     * 
     * <p>指向课程所属分类的唯一标识符，用于课程的分类管理和检索。</p>
     */
    private Long categoryId;

    /**
     * 课程名称
     * 
     * <p>课程的正式名称，用于在用户界面展示。
     * 应当简洁明了，能够准确传达课程的主要内容和价值。</p>
     */
    private String courseName;

    /**
     * 课程封面图片地址
     * 
     * <p>课程封面图片的完整 URL 地址，用于在课程列表和详情页中展示。</p>
     */
    private String courseLogo;

    /**
     * 课程简介
     * 
     * <p>课程的详细介绍和描述，包含课程内容、目标、适用人群等信息。
     * 用于帮助用户了解课程具体内容，做出购买决定。</p>
     */
    private String introduce;

    /**
     * 课程是否免费
     * 
     * <p>标识课程的收费类型，决定用户是否需要支付才能获取课程。</p>
     * <ul>
     *   <li><code>1</code> - 免费课程，用户可以直接获取</li>
     *   <li><code>0</code> - 收费课程，需要支付后才能获取</li>
     * </ul>
     */
    private Integer isFree;

    /**
     * 课程划线价（元）
     * 
     * <p>课程的市场指导价或原价，用于展示价格优惠幅度。
     * 采用 {@link BigDecimal} 类型保证计算精度，单位为人民币元。</p>
     * 
     * <p><strong>业务规则：</strong></p>
     * <ul>
     *   <li>划线价通常大于或等于实际售价</li>
     *   <li>免费课程的划线价可以为 0</li>
     *   <li>用于计算折扣幅度展示</li>
     * </ul>
     * 
     * @see #coursePrice 课程实际售价
     */
    private BigDecimal rulingPrice;

    /**
     * 课程实际售价（元）
     * 
     * <p>课程的实际销售价格，用户购买时需要支付的金额。
     * 采用 {@link BigDecimal} 类型保证计算精度，单位为人民币元。</p>
     * 
     * <p><strong>价格规则：</strong></p>
     * <ul>
     *   <li>免费课程价格为 0.00</li>
     *   <li>收费课程价格必须大于 0</li>
     *   <li>支持小数点后两位，精确到分</li>
     * </ul>
     * 
     * @see #rulingPrice 课程划线价
     * @see #isFree 免费标识，与此价格对应
     */
    private BigDecimal coursePrice;

    /**
     * 课程是否上架
     * 
     * <p>控制课程是否在前端展示和允许购买的状态标识。</p>
     * <ul>
     *   <li><code>1</code> - 已上架，课程可以被用户查看和购买</li>
     *   <li><code>0</code> - 已下架，课程不在前端展示</li>
     * </ul>
     * 
     * <p><strong>与状态字段的区别：</strong></p>
     * <ul>
     *   <li>{@link #statusId} 控制课程的有效性（禁用/正常）</li>
     *   <li>{@link #isPutaway} 控制课程的展示性（上架/下架）</li>
     * </ul>
     * 
     * @see #statusId 课程状态标识
     */
    private Integer isPutaway;

    /**
     * 课程前端显示排序
     * 
     * <p>用于控制课程在前端页面中的展示顺序，与后台管理的排序字段作用不同。</p>
     * 
     * <p><strong>排序规则：</strong></p>
     * <ul>
     *   <li>数值越小显示优先级越高</li>
     *   <li>相同数值按其他规则排序（如销量、评分等）</li>
     * </ul>
     * 
     * @see #sort 后台管理排序字段
     */
    private Integer courseSort;

    /**
     * 课程购买人数
     * 
     * <p>统计成功购买此课程的用户总数，包括付费购买和免费获取。
     * 此数据用于展示课程的受欢迎程度，影响用户的购买决策。</p>
     * 
     * <p><strong>统计规则：</strong></p>
     * <ul>
     *   <li>只统计成功完成购买流程的用户</li>
     *   <li>包括付费购买、免费领取、管理员赠送等方式</li>
     *   <li>不包括退款或取消的订单</li>
     * </ul>
     * 
     * @see #countStudy 学习人数，可能与购买人数不同
     */
    private Integer countBuy;

    /**
     * 课程学习人数
     * 
     * <p>统计实际开始学习此课程的用户数量，反映课程的真实活跃度。
     * 此数据对于评估课程质量和用户参与度具有重要参考价值。</p>
     * 
     * <p><strong>统计标准：</strong></p>
     * <ul>
     *   <li>用户点击进入课程学习页面即计入统计</li>
     *   <li>同一用户多次学习只计算一次</li>
     *   <li>定期更新，保证数据实时性</li>
     * </ul>
     * 
     * <p><strong>数据关系：</strong></p>
     * 学习人数通常小于或等于购买人数，但在有免费试学的情况下可能会出现相反情况。
     * 
     * @see #countBuy 购买人数，与学习人数的区别
     */
    private Integer countStudy;
}
