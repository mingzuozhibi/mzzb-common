package mingzuozhibi.common;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class BaseController {

    public String errorMessage(String error) {
        JsonObject root = new JsonObject();
        root.addProperty("success", false);
        root.addProperty("message", error);
        return root.toString();
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
