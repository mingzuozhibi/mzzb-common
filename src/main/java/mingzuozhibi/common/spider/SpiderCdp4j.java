package mingzuozhibi.common.spider;

import io.webfolder.cdp.Launcher;
import io.webfolder.cdp.session.Session;
import io.webfolder.cdp.session.SessionFactory;
import lombok.extern.slf4j.Slf4j;
import mingzuozhibi.common.model.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
public abstract class SpiderCdp4j {

    public static void doInSessionFactory(Consumer<SessionFactory> consumer) {
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        List<String> command = new ArrayList<>();
        command.add("--headless");
        command.add("--target.name=" + uuid);
        Launcher launcher = new Launcher();
        try (SessionFactory factory = launcher.launch(command)) {
            consumer.accept(factory);
        } catch (RuntimeException e) {
            log.warn(e.getMessage(), e);
        }
        killChrome(uuid);
    }

    private static void killChrome(String uuid) {
        String format = "ps -x | grep 'target.name=%s' | grep -v grep | awk '{print $1}' | xargs -t kill -9 2>&1";
        String[] cmds = {"/bin/bash", "-l", "-c", String.format(format, uuid)};
        try {
            Process process = Runtime.getRuntime().exec(cmds);
            process.waitFor();
            log.info("kill chrome " + uuid);
        } catch (IOException | InterruptedException e) {
            log.info("kill chrome " + uuid + ": " + e.getMessage(), e);
        }
    }

    private static final String USER_AGENT =
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/76.0.3809.132 Safari/537.36";

    public static Result<String> waitResult(SessionFactory factory, String url) {
        Result<String> result = new Result<>();
        for (int retry = 0; retry < 3; retry++) {
            try (Session session = factory.create()) {
                session.setUserAgent(USER_AGENT);
                session.navigate(url);
                session.waitDocumentReady(38000);
                session.wait(2000);
                result.setContent(session.getOuterHtml("body"));
                break;
            } catch (Exception e) {
                result.pushError(e);
            }
        }
        return result;
    }

    public static void threadSleep(int timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException ignored) {
        }
    }

}
