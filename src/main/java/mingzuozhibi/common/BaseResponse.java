package mingzuozhibi.common;

import com.google.gson.JsonObject;
import mingzuozhibi.common.jms.JmsMessage;
import mingzuozhibi.common.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import javax.servlet.ServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class BaseResponse {

    @Autowired
    protected JmsMessage jmsMessage;

    public void responseError(ServletResponse response, String format, Object... args) {
        responseText(response, errorMessage(String.format(format, args)));
    }

    public void responseError(ServletResponse response, String error) {
        responseText(response, errorMessage(error));
    }

    public void responseText(ServletResponse response, String content) {
        try {
            response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            response.setContentLength(bytes.length);
            response.getOutputStream().write(bytes);
            response.flushBuffer();
        } catch (IOException e) {
            jmsMessage.danger("responseText error: " + Result.formatErrorCause(e));
        }
    }

    private String errorMessage(String error) {
        Objects.requireNonNull(error);
        JsonObject root = new JsonObject();
        root.addProperty("success", false);
        root.addProperty("message", error);
        return root.toString();
    }

}
