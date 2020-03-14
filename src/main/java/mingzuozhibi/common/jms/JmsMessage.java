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
        sendMsgNotLog("info", message);
        log.info("JMS -> {}: {}", "module.message", message);
    }

    public void success(String message) {
        sendMsgNotLog("success", message);
        log.info("JMS -> {}: {}", "module.message", message);
    }

    public void notify(String message) {
        sendMsgNotLog("notify", message);
        log.info("JMS -> {}: {}", "module.message", message);
    }

    public void warning(String message) {
        sendMsgNotLog("warning", message);
        log.warn("JMS -> {}: {}", "module.message", message);
    }

    public void danger(String message) {
        sendMsgNotLog("danger", message);
        log.error("JMS -> {}: {}", "module.message", message);
    }

    public void sendMsgNotLog(String type, String message) {
        jmsService.sendJsonNotLog("module.message", buildMsg(type, message));
    }

    public void sendMsgAndLogger(String type, String message) {
        jmsService.sendJsonAndLogger("module.message", buildMsg(type, message), message);
    }

    public void sendMsgAndJmsLog(String type, String message) {
        String jmsLog = String.format("[%s][%s]", type, message);
        jmsService.sendJsonAndJmsLog("module.message", buildMsg(type, message), jmsLog);
    }

    public void sendMsgAndJmsLog(String type, String message, String jmsLog) {
        jmsService.sendJsonAndJmsLog("module.message", buildMsg(type, message), jmsLog);
    }

    private String buildMsg(String type, String text) {
        JsonObject data = new JsonObject();
        data.addProperty("type", type);
        data.addProperty("text", text);
        data.addProperty("createOn", Instant.now().toEpochMilli());
        return jmsService.buildJson(data);
    }

}
