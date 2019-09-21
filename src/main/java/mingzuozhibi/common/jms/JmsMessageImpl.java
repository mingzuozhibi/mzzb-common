package mingzuozhibi.common.jms;

import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Slf4j
@Component
public class JmsMessageImpl implements JmsMessage {

    @Autowired
    private JmsService jmsService;

    @Override
    public void info(String format, Object... args) {
        info(String.format(format, args));
    }

    @Override
    public void success(String format, Object... args) {
        success(String.format(format, args));
    }

    @Override
    public void notify(String format, Object... args) {
        notify(String.format(format, args));
    }

    @Override
    public void warning(String format, Object... args) {
        warning(String.format(format, args));
    }

    @Override
    public void danger(String format, Object... args) {
        danger(String.format(format, args));
    }

    @Override
    public void info(String message) {
        sendMsgNotLog("info", message);
        log.info("JMS -> {}: {}", "module.message", message);
    }

    @Override
    public void success(String message) {
        sendMsgNotLog("success", message);
        log.info("JMS -> {}: {}", "module.message", message);
    }

    @Override
    public void notify(String message) {
        sendMsgNotLog("notify", message);
        log.info("JMS -> {}: {}", "module.message", message);
    }

    @Override
    public void warning(String message) {
        sendMsgNotLog("warning", message);
        log.warn("JMS -> {}: {}", "module.message", message);
    }

    @Override
    public void danger(String message) {
        sendMsgNotLog("danger", message);
        log.error("JMS -> {}: {}", "module.message", message);
    }

    public void sendMsgNotLog(String type, String message) {
        jmsService.sendJson("module.message", buildMsg(type, message));
    }

    public void sendMsgAndLog(String type, String message) {
        jmsService.sendJson("module.message", buildMsg(type, message), message);
    }

    public void sendMsgAndLog(String type, String message, String record) {
        jmsService.sendJson("module.message", buildMsg(type, message), record);
    }

    private String buildMsg(String type, String text) {
        JsonObject data = new JsonObject();
        data.addProperty("type", type);
        data.addProperty("text", text);
        data.addProperty("createOn", Instant.now().toEpochMilli());
        return jmsService.buildJson(data);
    }

    @Deprecated
    public void sendMsgNoLog(String type, String message) {
        sendMsgNotLog(type, message);
    }

    @Deprecated
    public void infoAndSend(String type, String message) {
        sendMsgAndLog(type, message);
    }

    @Deprecated
    public void infoAndSend(String type, String message, String record) {
        sendMsgAndLog(type, message, record);
    }

}
