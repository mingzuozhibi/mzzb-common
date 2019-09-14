package mingzuozhibi.common.jms;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
public class JmsMessage {

    @Autowired
    private JmsService jmsService;

    public void info(String format, Object... args) {
        info(String.format(format, args));
    }

    public void success(String format, Object... args) {
        success(String.format(format, args));
    }

    public void notify(String format, Object... args) {
        notify(String.format(format, args));
    }

    public void warning(String format, Object... args) {
        warning(String.format(format, args));
    }

    public void danger(String format, Object... args) {
        danger(String.format(format, args));
    }

    public void info(String message) {
        String msgData = buildMsgData("info", message);
        jmsService.sendJson("module.message", msgData);
        log.info("JMS -> {}: {}", "module.message", message);
    }

    public void success(String message) {
        String msgData = buildMsgData("success", message);
        jmsService.sendJson("module.message", msgData);
        log.info("JMS -> {}: {}", "module.message", message);
    }

    public void notify(String message) {
        String msgData = buildMsgData("notify", message);
        jmsService.sendJson("module.message", msgData);
        log.info("JMS -> {}: {}", "module.message", message);
    }

    public void warning(String message) {
        String msgData = buildMsgData("warning", message);
        jmsService.sendJson("module.message", msgData);
        log.warn("JMS -> {}: {}", "module.message", message);
    }

    public void danger(String message) {
        String msgData = buildMsgData("danger", message);
        jmsService.sendJson("module.message", msgData);
        log.error("JMS -> {}: {}", "module.message", message);
    }

    private String buildMsgData(String type, String text) {
        JsonObject data = new JsonObject();
        data.addProperty("type", type);
        data.addProperty("text", text);
        data.addProperty("createOn", Instant.now().toEpochMilli());
        return jmsService.buildJson(data);
    }

}
