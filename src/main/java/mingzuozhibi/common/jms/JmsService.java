package mingzuozhibi.common.jms;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JmsService {

    @Value("${spring.application.name}")
    private String moduleName;

    @Autowired
    private JmsTemplate template;

    @Deprecated
    public void sendJson(String destination, String json, String info) {
        template.convertAndSend(destination, json);
        log.info("JMS -> {}: {}", destination, info);
    }

    @Deprecated
    public void sendJson(String destination, String json) {
        template.convertAndSend(destination, json);
    }

    public void sendJsonAndLogger(String destination, String json, String message) {
        template.convertAndSend(destination, json);
        log.info(message);
    }

    public void sendJsonAndJmsLog(String destination, String json, String message) {
        template.convertAndSend(destination, json);
        log.info("JMS -> {}: {}", destination, message);
    }

    public void sendJsonAndJmsLog(String destination, String json) {
        template.convertAndSend(destination, json);
        log.info("JMS -> {}: {}", destination, json);
    }

    public void sendJsonNotLog(String destination, String json) {
        template.convertAndSend(destination, json);
    }

    public String buildJson(JsonElement data) {
        JsonObject root = new JsonObject();
        root.addProperty("name", moduleName);
        root.add("data", data);
        return root.toString();
    }

}
