package com.roncoo.education.common.config;

/**
 * 线程上下文工具类
 * 使用 ThreadLocal 存储当前线程的用户信息
 * 主要用于在请求处理过程中传递用户ID，方便在业务层获取当前操作用户
 * 
 * 注意：使用后需要及时清理 ThreadLocal，防止内存泄漏
 * 
 * @author fengyw
 * @date 2022/1/1
 */
public final class ThreadContext {

    /**
     * 私有构造函数，防止实例化工具类
     */
    private ThreadContext() {
    }

    /**
     * 线程局部变量，存储当前线程的用户ID
     * 只有在 Admin 端和 User 端登录后才会有值
     */
    private static final ThreadLocal<String> USER_ID_LOCAL = new ThreadLocal<>();


    /**
     * 获取当前线程的用户ID
     * 
     * @return 用户ID，如果未设置可能抛出 NumberFormatException
     */
    public static Long userId() {
        return Long.valueOf(USER_ID_LOCAL.get());
    }

    /**
     * 设置当前线程的用户ID
     * 如果传入 null，则会清除当前线程的用户ID
     * 
     * @param val 用户ID字符串
     */
    public static void setUserId(String val) {
        if (val == null) {
            removeUserId();
            return;
        }
        USER_ID_LOCAL.set(val);
    }

    /**
     * 移除当前线程的用户ID
     * 防止内存泄漏，应在请求处理完成后调用
     */
    public static void removeUserId() {
        USER_ID_LOCAL.remove();
    }

}
