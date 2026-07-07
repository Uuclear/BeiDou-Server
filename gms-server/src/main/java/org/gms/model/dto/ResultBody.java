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
 * 统一 API 响应包装类，封装业务状态码 code、消息 message、请求追踪 ID responseId 及载荷 data。
 */
@Data
@Slf4j
public class ResultBody<T> {
    private Integer code;
    private String message;
    private String responseId;
    private T data;

    public ResultBody() {
    }

    public ResultBody(BaseErrorInfoInterface errorInfo) {
        this.code = errorInfo.getResultCode();
        this.message = errorInfo.getResultMsg();
    }

    /**
     * 构造成功响应；无参或仅 data 时自动生成 responseId，也可携带 SubmitBody 的 requestId。
     */
    public static <T> ResultBody<T> success() {
        return success(null);
    }

    /**
     * 构造成功响应；无参或仅 data 时自动生成 responseId，也可携带 SubmitBody 的 requestId。
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
     * 构造成功响应；无参或仅 data 时自动生成 responseId，也可携带 SubmitBody 的 requestId。
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
     * 根据 HttpServletRequest 与错误码构造失败响应，POST JSON 请求会尝试解析 requestId。
     */
    public static <T> ResultBody<T> error(HttpServletRequest req, BaseErrorInfoInterface errorInfo) {
        return error(req, errorInfo.getResultCode(), errorInfo.getResultMsg());
    }

    /**
     * 根据 HttpServletRequest 与错误码构造失败响应，POST JSON 请求会尝试解析 requestId。
     */
    public static <T> ResultBody<T> error(HttpServletRequest req, String message) {
        return error(req, -1, message);
    }

    /**
     * 根据 HttpServletRequest 与错误码构造失败响应，POST JSON 请求会尝试解析 requestId。
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

    @Override
    /**
     * 以 JSON 字符串形式序列化当前响应体。
     */
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
