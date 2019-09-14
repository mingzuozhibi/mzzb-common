package mingzuozhibi.common.spider;

import mingzuozhibi.common.model.Result;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.function.Consumer;

public abstract class SpiderJsoup {

    public static Result<String> waitRequest(String url) {
        return waitRequest(url, null);
    }

    public static Result<String> waitRequest(String url, Consumer<Connection> consumer) {
        Result<String> result = new Result<>();
        for (int retry = 0; retry < 3; retry++) {
            try {
                Connection connection = Jsoup.connect(url).ignoreContentType(true);
                if (consumer != null) {
                    consumer.accept(connection);
                }
                result.setContent(connection.execute().body());
                break;
            } catch (Exception e) {
                result.pushError(e);
            }
        }
        return result;
    }

}
