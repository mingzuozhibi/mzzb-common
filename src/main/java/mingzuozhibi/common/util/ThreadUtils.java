package mingzuozhibi.common.util;

import lombok.extern.slf4j.Slf4j;
import mingzuozhibi.common.jms.JmsMessage;
import mingzuozhibi.common.model.Result;

@Slf4j
public abstract class ThreadUtils {

    public static void runWithDaemon(JmsMessage jmsMessage, String origin, Callback callback) {
        Thread thread = new Thread(() -> {
            try {
                callback.call();
            } catch (Throwable e) {
                jmsMessage.warning("runWithDaemon [%s] error: %s", origin, Result.formatErrorCause(e));
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public interface Callback {
        void call() throws Exception;
    }

}
