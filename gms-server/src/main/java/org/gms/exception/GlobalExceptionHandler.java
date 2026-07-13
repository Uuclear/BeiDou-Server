package org.gms.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.gms.model.dto.ResultBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理器
 * <p>
 * 使用Spring MVC的@ControllerAdvice注解实现全局异常拦截，
 * 统一处理应用程序中抛出的各种异常，返回标准化的错误响应。
 * </p>
 *
 * @author GMS Team
 * @since 1.0.0
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理自定义业务异常
     *
     * @param req HTTP请求对象
     * @param e   业务异常
     * @return 标准化错误响应体
     */
    @ExceptionHandler(value = BizException.class)
    @ResponseBody
    public ResultBody<Object> bizExceptionHandler(HttpServletRequest req, BizException e) {
        logger.error("发生业务异常！原因是：{}", e.getErrorMsg());
        return ResultBody.error(req, e.getErrorCode(), e.getErrorMsg());
    }

    /**
     * 处理运行时异常
     * <p>
     * IllegalArgumentException、NullPointerException、UnsupportedOperationException等都是RuntimeException的子类，
     * 这里统一捕获RuntimeException来代替逐个捕获。
     * </p>
     *
     * @param req HTTP请求对象
     * @param e   运行时异常
     * @return 标准化错误响应体
     */
    @ExceptionHandler(value = RuntimeException.class)
    @ResponseBody
    public ResultBody<Object> exceptionHandler(HttpServletRequest req, RuntimeException e) {
        logger.error("发生运行时异常！原因是:", e);
        return ResultBody.error(req, BizExceptionEnum.BODY_NOT_MATCH);
    }

    /**
     * 处理Servlet异常（请求方法不支持等）
     *
     * @param req HTTP请求对象
     * @param e   Servlet异常
     * @return 标准化错误响应体
     */
    @ExceptionHandler(value = ServletException.class)
    @ResponseBody
    public ResultBody<Object> exceptionHandler(HttpServletRequest req, ServletException e) {
        logger.error("发生请求时异常！原因是:", e);
        return ResultBody.error(req, BizExceptionEnum.REQUEST_METHOD_SUPPORT);
    }

    /**
     * 处理其他所有未捕获的异常
     *
     * @param req HTTP请求对象
     * @param e   异常
     * @return 标准化错误响应体
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResultBody<Object> exceptionHandler(HttpServletRequest req, Exception e) {
        logger.error("未知异常！原因是:", e);
        return ResultBody.error(req, BizExceptionEnum.INTERNAL_SERVER_ERROR);
    }
}
