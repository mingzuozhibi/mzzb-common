package mingzuozhibi.common.jms;

public interface JmsMessage {

    void info(String format, Object... args);

    void success(String format, Object... args);

    void notify(String format, Object... args);

    void warning(String format, Object... args);

    void danger(String format, Object... args);

    void info(String message);

    void success(String message);

    void notify(String message);

    void warning(String message);

    void danger(String message);

    void sendMsgNotLog(String type, String message);

    void sendMsgAndLog(String type, String message);

    void sendMsgAndLog(String type, String message, String record);

    @Deprecated
    void sendMsgNoLog(String type, String message);

    @Deprecated
    void infoAndSend(String type, String message);

    @Deprecated
    void infoAndSend(String type, String message, String record);

}
