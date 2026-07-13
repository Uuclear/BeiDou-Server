package org.gms.model.dto;

import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.gms.exception.BaseErrorInfoInterface;
import org.gms.exception.BizExceptionEnum;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.BufferedReader;
import java.util.Optional;
import java.util.UUID;

/**
 * 统一API响应结果封装类
 * 用于封装所有Web API的返回结果，包含状态码、消息、请求ID和数据
 * @param <T> 响应数据的类型
 */
@Data
@Slf4j
public class ResultBody<T> {
    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应唯一标识ID，用于请求追踪
     */
    private String responseId;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 默认构造函数
     */
    public ResultBody() {
    }

    /**
     * 根据错误信息构造响应结果
     * @param errorInfo 错误信息接口
     */
    public ResultBody(BaseErrorInfoInterface errorInfo) {
        this.code = errorInfo.getResultCode();
        this.message = errorInfo.getResultMsg();
    }

    /**
     * 返回成功响应（无数据）
     * @param <T> 数据类型
     * @return 成功的响应结果
     */
    public static <T> ResultBody<T> success() {
        return success(null);
    }

    /**
     * 返回成功响应（带数据）
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功的响应结果
     */
    public static <T> ResultBody<T> success(T data) {
        ResultBody<T> rb = new ResultBody<>();
        rb.setResponseId(UUID.randomUUID().toString());
        rb.setCode(BizExceptionEnum.SUCCESS.getResultCode());
        rb.setMessage(BizExceptionEnum.SUCCESS.getResultMsg());
        rb.setData(data);
        return rb;
    }

    /**
     * 返回成功响应（使用请求ID和数据）
     * @param request 请求体
     * @param data 响应数据
     * @param <T> 数据类型
     * @return 成功的响应结果
     */
    public static <T> ResultBody<T> success(SubmitBody<?> request, T data) {
        ResultBody<T> rb = new ResultBody<>();
        rb.setResponseId(request.getRequestId());
        rb.setCode(BizExceptionEnum.SUCCESS.getResultCode());
        rb.setMessage(BizExceptionEnum.SUCCESS.getResultMsg());
        rb.setData(data);
        return rb;
    }

    /**
     * 返回错误响应（使用错误信息接口）
     * @param req HTTP请求对象
     * @param errorInfo 错误信息接口
     * @param <T> 数据类型
     * @return 错误的响应结果
     */
    public static <T> ResultBody<T> error(HttpServletRequest req, BaseErrorInfoInterface errorInfo) {
        return error(req, errorInfo.getResultCode(), errorInfo.getResultMsg());
    }

    /**
     * 返回错误响应（使用自定义消息）
     * @param req HTTP请求对象
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 错误的响应结果
     */
    public static <T> ResultBody<T> error(HttpServletRequest req, String message) {
        return error(req, -1, message);
    }

    /**
     * 返回错误响应（使用自定义状态码和消息）
     * 对于POST JSON请求，会尝试从请求体中解析requestId
     * @param req HTTP请求对象
     * @param code 错误状态码
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 错误的响应结果
     */
    public static <T> ResultBody<T> error(HttpServletRequest req, Integer code, String message) {
        String method = req.getMethod();
        String contentType = req.getContentType();
        ResultBody<T> rb = new ResultBody<>();
        if (RequestMethod.POST.name().equals(method) && contentType.contains("application/json")) {
            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = req.getReader()) {
                String line;
                while ((line = reader.readLine()) != null) {
                    body.append(line);
                }
            } catch (Exception e) {
                log.error("Error reading request body: {}", e.getMessage(), e);
            }
            String requestId = null;
            try {
                SubmitBody<?> request = JSONObject.parseObject(body.toString(), SubmitBody.class);
                requestId = request == null ? null : request.getRequestId();
            } catch (Exception ignore) {
            }
            rb.setResponseId(Optional.ofNullable(requestId).orElse(UUID.randomUUID().toString()));
        } else {
            rb.setResponseId(UUID.randomUUID().toString());
        }
        rb.setCode(code);
        rb.setMessage(message);
        rb.setData(null);
        return rb;
    }

    /**
     * 将响应结果转换为JSON字符串
     * @return JSON格式的字符串
     */
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
