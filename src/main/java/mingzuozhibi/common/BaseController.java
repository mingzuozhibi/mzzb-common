package mingzuozhibi.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Objects;

public class BaseController {

    public String errorMessage(String error) {
        JsonObject root = new JsonObject();
        root.addProperty("success", false);
        root.addProperty("message", error);
        return root.toString();
    }

    public String objectResult(Boolean bool) {
        Objects.requireNonNull(bool);
        return objectResult(new JsonPrimitive(bool));
    }

    public String objectResult(Number number) {
        Objects.requireNonNull(number);
        return objectResult(new JsonPrimitive(number));
    }

    public String objectResult(String content) {
        Objects.requireNonNull(content);
        return objectResult(new JsonPrimitive(content));
    }

    public String objectResult(JsonElement date) {
        JsonObject root = new JsonObject();
        root.addProperty("success", true);
        root.add("data", date);
        return root.toString();
    }

    public String objectResult(JsonElement date, JsonElement page) {
        JsonObject root = new JsonObject();
        root.addProperty("success", true);
        root.add("data", date);
        root.add("page", page);
        return root.toString();
    }

    public JsonElement buildPage(Page<?> page) {
        JsonObject object = new JsonObject();
        Pageable pageable = page.getPageable();
        object.addProperty("pageSize", pageable.getPageSize());
        object.addProperty("currentPage", pageable.getPageNumber() + 1);
        object.addProperty("totalElements", page.getTotalElements());
        return object;
    }

}
