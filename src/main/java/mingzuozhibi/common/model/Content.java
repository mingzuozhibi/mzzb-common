package mingzuozhibi.common.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mingzuozhibi.common.gson.GsonFactory;

public class Content {

    public static Content parse(Result<String> bodyResult) {
        if (!bodyResult.isUnfinished()) {
            return new Content(bodyResult.getContent());
        } else {
            JsonObject errorResult = new JsonObject();
            errorResult.addProperty("success", false);
            errorResult.addProperty("message", bodyResult.formatError());
            return new Content(errorResult.toString());
        }
    }

    private static Gson gson = GsonFactory.createGson();

    private JsonObject resultObj;

    public Content(String content) {
        parseContent(content);
    }

    private void parseContent(String content) {
        resultObj = gson.fromJson(content, JsonObject.class);
    }

    public boolean isSuccess() {
        return resultObj.get("success").getAsBoolean();
    }

    public String getMessage() {
        return resultObj.get("message").getAsString();
    }

    public JsonObject getObject() {
        return getElement().getAsJsonObject();
    }

    public JsonArray getArray() {
        return getElement().getAsJsonArray();
    }

    public JsonElement getElement() {
        return resultObj.get("data");
    }

}
