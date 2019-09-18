package mingzuozhibi.common;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import mingzuozhibi.common.gson.GsonFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Objects;

public class BaseController {

    private Gson gson = GsonFactory.createGson();

    public String errorMessage(String error) {
        Objects.requireNonNull(error);
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

    public String objectResult(Object data) {
        Objects.requireNonNull(data);
        return objectResult(gson.toJsonTree(data));
    }

    public String objectResult(JsonElement data) {
        Objects.requireNonNull(data);
        JsonObject root = new JsonObject();
        root.addProperty("success", true);
        root.add("data", data);
        return root.toString();
    }

    public String objectResult(Object data, Page<?> page) {
        Objects.requireNonNull(data);
        Objects.requireNonNull(page);
        return objectResult(gson.toJsonTree(data), buildPage(page));
    }

    public String objectResult(JsonElement data, JsonElement page) {
        Objects.requireNonNull(data);
        Objects.requireNonNull(page);
        JsonObject root = new JsonObject();
        root.addProperty("success", true);
        root.add("data", data);
        root.add("page", page);
        return root.toString();
    }

    public JsonElement buildPage(Page<?> page) {
        Objects.requireNonNull(page);
        Pageable pageable = page.getPageable();
        int currentPage = pageable.getPageNumber() + 1;
        int pageSize = pageable.getPageSize();
        long totalElements = page.getTotalElements();
        return buildPage(currentPage, pageSize, totalElements);
    }

    public JsonElement buildPage(long currentPage, long pageSize, long totalElements) {
        JsonObject object = new JsonObject();
        object.addProperty("pageSize", pageSize);
        object.addProperty("currentPage", currentPage);
        object.addProperty("totalElements", totalElements);
        return object;
    }

}
